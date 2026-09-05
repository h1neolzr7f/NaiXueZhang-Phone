"""Compile and run the real Java queue with test provider/storage implementations."""
from pathlib import Path
import hashlib
import os
import shutil
import subprocess
import tempfile
import urllib.request

ROOT = Path(__file__).resolve().parents[1]
JSON_SHA256 = "3cf6cd6892e32e2b4c1c39e0f52f5248a2f5b37646fdfbb79a66b46b618414ed"


def main():
    java = str(Path(os.environ["JAVA_HOME"]) / "bin/java") if os.environ.get("JAVA_HOME") else shutil.which("java")
    if not java:
        raise RuntimeError("Java 17 JDK is required")
    with tempfile.TemporaryDirectory() as temp:
        directory = Path(temp)
        with urllib.request.urlopen("https://repo.maven.apache.org/maven2/org/json/json/20240303/json-20240303.jar", timeout=60) as response:
            data = response.read()
        if hashlib.sha256(data).hexdigest() != JSON_SHA256:
            raise RuntimeError("JSON test dependency checksum mismatch")
        jar = directory / "json.jar"
        jar.write_bytes(data)
        classes = directory / "classes"
        classes.mkdir()
        src = ROOT / "android/app/src/main/java/com/naixuezhang/studio/mobile"
        subprocess.run([java, "com.sun.tools.javac.Main", "-cp", str(jar), "-d", str(classes),
                        *[str(src / name) for name in ["JobStore.java", "RetryLedger.java", "JsonUtil.java"]],
                        str(ROOT / "tests/java/JobStoreRetryTest.java")], check=True)
        subprocess.run([java, "-cp", os.pathsep.join([str(jar), str(classes)]),
                        "com.naixuezhang.studio.mobile.JobStoreRetryTest"], check=True, timeout=30)


if __name__ == "__main__":
    main()
