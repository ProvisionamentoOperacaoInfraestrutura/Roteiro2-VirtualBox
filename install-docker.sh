#!/usr/bin/env bash

set -euo pipefail

if ! command -v sudo >/dev/null 2>&1; then
  echo "Erro: sudo não está instalado."
  exit 1
fi

. /etc/os-release

case "${ID:-}" in
  ubuntu)
    docker_repo_os="ubuntu"
    docker_repo_suite="${UBUNTU_CODENAME:-${VERSION_CODENAME:-}}"
    ;;
  debian)
    docker_repo_os="debian"
    docker_repo_suite="${VERSION_CODENAME:-}"
    ;;
  *)
    echo "Erro: este script suporta Ubuntu e Debian. Sistema detectado: ${ID:-desconhecido}"
    exit 1
    ;;
esac

if [[ -z "${docker_repo_suite}" ]]; then
  echo "Erro: não foi possível identificar a versão do sistema."
  exit 1
fi

echo "Instalando dependências..."
sudo apt-get update
sudo apt-get install -y ca-certificates curl

echo "Configurando o repositório oficial do Docker..."
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL "https://download.docker.com/linux/${docker_repo_os}/gpg" \
  -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

sudo tee /etc/apt/sources.list.d/docker.sources > /dev/null <<EOF
Types: deb
URIs: https://download.docker.com/linux/${docker_repo_os}
Suites: ${docker_repo_suite}
Components: stable
Architectures: $(dpkg --print-architecture)
Signed-By: /etc/apt/keyrings/docker.asc
EOF

echo "Instalando Docker Engine e Docker Compose..."
sudo apt-get update
sudo apt-get install -y \
  docker-ce \
  docker-ce-cli \
  containerd.io \
  docker-buildx-plugin \
  docker-compose-plugin

sudo systemctl enable --now docker

docker_user="${SUDO_USER:-${USER}}"
sudo usermod -aG docker "${docker_user}"

echo
echo "Docker instalado com sucesso."
echo "Usuário adicionado ao grupo docker: ${docker_user}"
echo
sudo docker version --format '{{.Server.Version}}'
sudo docker compose version

if [[ "${EUID}" -ne 0 && -t 0 && -t 1 ]]; then
  echo
  echo "Abrindo uma nova sessão com o grupo docker..."
  exec newgrp docker
else
  echo "Faça logout/login ou execute 'newgrp docker' antes de usar Docker sem sudo."
fi
