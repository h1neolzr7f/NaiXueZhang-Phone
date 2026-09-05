"""Restore public build assets from a pinned, verified source revision."""
from __future__ import annotations

import argparse
import hashlib
import json
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def matches(data: bytes, record: dict) -> bool:
    digest = hashlib.sha1(b"blob " + str(len(data)).encode() + b"\0" + data).hexdigest()
    return len(data) == record["size"] and digest == record["sha"]


def restore(include_model: bool = False) -> None:
    manifest = json.loads((ROOT / "scripts/phone_assets.json").read_text(encoding="utf-8"))
    for record in manifest["files"]:
        if record["optional"] and not include_model:
            continue
        path = ROOT / record["path"]
        if path.is_file():
            if matches(path.read_bytes(), record):
                continue
            raise RuntimeError(f"Existing asset differs; preserve or move it before restoring: {record['path']}")
        url = f"https://raw.githubusercontent.com/{manifest['repository']}/{manifest['ref']}/{record['path']}"
        with urllib.request.urlopen(url, timeout=120) as response:
            data = response.read(record["size"] + 1)
        if not matches(data, record):
            raise RuntimeError(f"Asset integrity check failed: {record['path']}")
        path.parent.mkdir(parents=True, exist_ok=True)
        temporary = path.with_suffix(path.suffix + ".download")
        try:
            temporary.write_bytes(data)
            temporary.replace(path)
        finally:
            temporary.unlink(missing_ok=True)
        print(f"Restored {record['path']}", flush=True)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--model", action="store_true", help="Also restore the optional ONNX model")
    restore(parser.parse_args().model)
