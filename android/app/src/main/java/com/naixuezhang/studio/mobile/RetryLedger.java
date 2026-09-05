package com.naixuezhang.studio.mobile;

import java.util.ArrayList;
import java.util.List;

/** Per-copy outcomes. Saving an image ends generation even if post-processing fails. */
final class RetryLedger {
    private enum State { PENDING, IN_FLIGHT, GENERATED, FAILED, UNKNOWN }
    private final State[] states;

    RetryLedger(int count) {
        states = new State[count];
        java.util.Arrays.fill(states, State.PENDING);
    }

    synchronized void requested(int index) { states[index] = State.IN_FLIGHT; }
    synchronized void generated(int index) { states[index] = State.GENERATED; }

    synchronized void failed(int index, boolean uncertain) {
        if (states[index] != State.GENERATED) {
            states[index] = uncertain ? State.UNKNOWN : State.FAILED;
        }
    }

    synchronized List<Integer> remaining(boolean retryUnknown) {
        List<Integer> remaining = new ArrayList<>();
        for (int i = 0; i < states.length; i++) {
            if (states[i] == State.IN_FLIGHT) {
                throw new IllegalStateException("请求仍在处理，请等待当前任务结束后再补做");
            }
            if (states[i] == State.PENDING || states[i] == State.FAILED
                    || (retryUnknown && states[i] == State.UNKNOWN)) remaining.add(i);
        }
        return remaining;
    }

    synchronized boolean hasUnknown() {
        for (State state : states) if (state == State.UNKNOWN) return true;
        return false;
    }
}
