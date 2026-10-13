#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
uvx_bin="$(command -v uvx 2>/dev/null || true)"
uvx_bin="${uvx_bin:-${HOME}/.local/bin/uvx}"

if [[ ! -x "${uvx_bin}" ]]; then
  echo "uvx não encontrado. Execute ./install-locust.sh primeiro."
  exit 1
fi

exec "${uvx_bin}" --from locust locust \
  -f "${SCRIPT_DIR}/cpu_bound/locustfile.py" \
  --host http://localhost:8081 \
  --web-host 0.0.0.0 \
  --web-port 8089
