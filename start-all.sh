#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

for app in cpu_bound io_bound memory_bound; do
  echo "Subindo ${app}..."
  docker compose \
    --file "${SCRIPT_DIR}/${app}/docker-compose.yml" \
    up --detach --build
done

echo "As três aplicações foram iniciadas."
