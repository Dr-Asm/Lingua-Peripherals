import os
import sys

PROJECT_DIR = os.path.dirname(os.path.abspath(__file__))

BOM = b"\xef\xbb\xbf"

def fix_bom(path):
    fixed_count = 0
    for root, dirs, files in os.walk(path):
        if ".git" in root or "build" in root or "runs" in root:
            continue
        for name in files:
            filepath = os.path.join(root, name)
            try:
                with open(filepath, "rb") as f:
                    data = f.read()
                if data.startswith(BOM):
                    with open(filepath, "wb") as f:
                        f.write(data[3:])
                    print(f"Fixed: {os.path.relpath(filepath, path)}")
                    fixed_count += 1
            except Exception:
                pass
    return fixed_count

if __name__ == "__main__":
    count = fix_bom(PROJECT_DIR)
    if count == 0:
        print("All files are clean (no BOM).")
    else:
        print(f"Fixed {count} file(s).")
    sys.exit(0)