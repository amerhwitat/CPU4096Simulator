#!/usr/bin/env bash
set -euo pipefail
if [[ $# -gt 0 ]]; then exec "$@"; fi
if [[ -f /app/CMakeLists.txt ]]; then cmake -S /app -B /tmp/build -DCMAKE_BUILD_TYPE=Release && cmake --build /tmp/build -j"$(nproc)"; bin=$(find /tmp/build -maxdepth 3 -type f -perm -111 | head -n1 || true); [[ -n "$bin" ]] && exec "$bin"; fi
exec bash
