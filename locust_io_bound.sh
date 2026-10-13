#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

exec uvx --from locust locust \
  -f "${SCRIPT_DIR}/io_bound/locustfile.py" \
  --host http://localhost:8082 \
  --web-host 0.0.0.0 \
  --web-port 8090
