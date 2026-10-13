#!/usr/bin/env bash

set -euo pipefail

if ! command -v curl >/dev/null 2>&1; then
  echo "Erro: curl não está instalado."
  echo "Execute: sudo apt update && sudo apt install -y curl"
  exit 1
fi

echo "Instalando uv, que fornece o comando uvx..."
curl -LsSf https://astral.sh/uv/install.sh | sh

uvx_bin="${HOME}/.local/bin/uvx"
if [[ ! -x "${uvx_bin}" ]]; then
  echo "Erro: uvx não foi encontrado em ${uvx_bin}."
  exit 1
fi

"${uvx_bin}" --from locust locust --version
echo "Locust instalado. Abra um novo terminal ou use os scripts locust_*.sh."
