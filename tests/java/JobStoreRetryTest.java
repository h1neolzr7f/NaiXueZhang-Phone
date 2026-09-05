package com.naixuezhang.studio.mobile;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

/** Runs the production scheduler with local, deterministic provider/storage doubles. */
public final class JobStoreRetryTest {
    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    static JSONObject waitFor(JobStore jobs, String id) throws Exception {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (System.nanoTime() < deadline) {
            JSONObject job = jobs.get(id);
            if (job.optBoolean("terminal") && job.optInt("running") == 0) return job;
            Thread.sleep(5);
        }
        throw new AssertionError("job did not finish");
    }

    static JobStore store(NaiGenerator provider, PipelineStore pipeline) {
        return new JobStore(provider, new ImageStore(), pipeline, null, null, null);
    }

    static JSONArray pages(int count) throws Exception {
        JSONArray pages = new JSONArray();
        for (int i = 0; i < count; i++) {
            pages.put(new JSONObject().put("page_index", i)
                .put("comment", new JSONObject().put("seed", 100 + i * 100)));
        }
        return pages;
    }

    static void partialRetryAndDuplicateTap() throws Exception {
        NaiGenerator provider = new NaiGenerator();
        provider.failSeed = 201;
        JobStore jobs = store(provider, new PipelineStore());
        String id = jobs.startPages(pages(2), true, 2, null).getString("task_id");
        waitFor(jobs, id);
        check(provider.calls.size() == 4, "initial batch");
        provider.failSeed = -1;
        JSONObject child = jobs.retry(id);
        String childId = child.getString("task_id");
        check(child.getInt("total") == 1, "retry only one failed copy");
        check(jobs.retry(id).getString("task_id").equals(childId), "same parent returns same child");
        waitFor(jobs, childId);
        check(provider.calls.size() == 5, "no completed copy repeated");
        check(provider.calls.get(4) == 201, "copy offset/seed preserved");
    }

    static void unknownRequiresConfirmation() throws Exception {
        NaiGenerator provider = new NaiGenerator();
        provider.failSeed = 100;
        provider.uncertain = true;
        JobStore jobs = store(provider, new PipelineStore());
        String id = jobs.startPages(pages(1), true, 1, null).getString("task_id");
        JSONObject ended = waitFor(jobs, id);
        check(ended.optBoolean("billing_uncertain"), "unknown outcome exposed");
        try { jobs.retry(id); throw new AssertionError("unconfirmed retry accepted"); }
        catch (IllegalStateException expected) { }
        check(provider.calls.size() == 1, "no unconfirmed provider call");
        provider.failSeed = -1;
        waitFor(jobs, jobs.retry(id, true).getString("task_id"));
        check(provider.calls.size() == 2, "explicit retry sent once");
    }

    static void postProcessingFailureKeepsGeneratedImage() throws Exception {
        NaiGenerator provider = new NaiGenerator();
        PipelineStore pipeline = new PipelineStore();
        pipeline.fail = true;
        JobStore jobs = store(provider, pipeline);
        String id = jobs.startPages(pages(1), true, 1, null).getString("task_id");
        waitFor(jobs, id);
        try { jobs.retry(id, true); throw new AssertionError("saved image regenerated"); }
        catch (IllegalStateException expected) { }
        check(provider.calls.size() == 1, "post-processing does not repeat generation");
    }

    static void cancellationWaitsForInflightRequest() throws Exception {
        NaiGenerator provider = new NaiGenerator();
        provider.release = new CountDownLatch(1);
        JobStore jobs = store(provider, new PipelineStore());
        String id = jobs.startPages(pages(1), true, 1, null).getString("task_id");
        check(provider.entered.await(5, TimeUnit.SECONDS), "request started");
        jobs.cancel(id);
        try { jobs.retry(id, true); throw new AssertionError("retry while in flight"); }
        catch (IllegalStateException expected) { }
        provider.release.countDown();
        waitFor(jobs, id);
        check(provider.calls.size() == 1, "inflight request not duplicated");
    }

    public static void main(String[] args) {
        try {
            partialRetryAndDuplicateTap();
            unknownRequiresConfirmation();
            postProcessingFailureKeepsGeneratedImage();
            cancellationWaitsForInflightRequest();
            System.out.println("JobStore retry: 4 scenarios passed");
            System.exit(0);
        } catch (Throwable error) {
            error.printStackTrace();
            System.exit(1);
        }
    }
}

final class NaiGenerator {
    final List<Integer> calls = new CopyOnWriteArrayList<>();
    volatile int failSeed = -1;
    volatile boolean uncertain;
    final CountDownLatch entered = new CountDownLatch(1);
    CountDownLatch release;
    int concurrency() { return 1; }
    byte[] generatePng(JSONObject page, boolean forceFree, BooleanSupplier cancelled) throws Exception {
        int seed = page.getInt("seed");
        calls.add(seed);
        entered.countDown();
        if (release != null && !release.await(5, TimeUnit.SECONDS)) throw new AssertionError("release timeout");
        if (seed == failSeed) throw new NaiError("simulated failure", uncertain, "test");
        return new byte[] {1};
    }
    static final class NaiError extends Exception {
        final boolean billingUncertain;
        NaiError(String message, boolean uncertain, String code) { super(message); billingUncertain = uncertain; }
    }
}
final class ImageStore {
    String save(String id, byte[] image, boolean ignored) { return id; }
    void saveFinal(String id, byte[] image) { }
    void exportOne(String id, byte[] image) { }
}
final class PipelineStore {
    boolean fail;
    boolean upscaleEnabled() { return fail; }
    boolean metadataEnabled() { return false; }
    boolean mosaicEnabled() { return false; }
    boolean autoAfterGenerate() { return false; }
    String mosaicMethod() { return "none"; }
    String summary() { return "test"; }
    int estimateMs() { return 0; }
    byte[] processWithoutMosaic(byte[] image) { throw new IllegalStateException("local processing failed"); }
    byte[] processMosaicOnly(byte[] image) { return image; }
}
final class OutputCatalog { void add(String id, JSONObject source) { } }
final class FavoriteStore { void importGeneratedPage(String id, int index, JSONObject record, byte[] png, int total) { } }
final class GalleryStore {
    void create(String id, String title, JSONObject source) { }
    void addImage(String id, String imageId, String url, JSONObject source) { }
}
