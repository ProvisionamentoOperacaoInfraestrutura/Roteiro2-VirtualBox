#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

docker compose --file "${SCRIPT_DIR}/cpu_bound/docker-compose.yml" \
  up --detach --force-recreate locust

echo "Locust CPU disponível em http://localhost:8089"
