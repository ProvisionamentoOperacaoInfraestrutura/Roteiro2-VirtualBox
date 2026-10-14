#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

docker compose --file "${SCRIPT_DIR}/io_bound/docker-compose.yml" \
  up --detach --force-recreate locust

echo "Locust I/O disponível em http://localhost:8090"
