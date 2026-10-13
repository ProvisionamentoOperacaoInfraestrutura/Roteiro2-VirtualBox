## Instalar Docker na VM

Se a VM apresentar `docker: command not found`, execute:

```bash
chmod +x install-docker.sh
./install-docker.sh
newgrp docker
```

O script instala o Docker Engine e o Docker Compose Plugin para Ubuntu ou
Debian.

## Subir as aplicações

Na raiz do projeto, execute:

```bash
chmod +x start-all.sh
./start-all.sh
```

Esse comando sobe as três aplicações, seus Prometheus e seus Grafanas.

Portas principais:

| Aplicação | API | Prometheus | Grafana |
|---|---:|---:|---:|
| CPU bound | `8081` | `9091` | `3004` |
| IO bound | `8082` | `9092` | `3002` |
| Memory bound | `8083` | `9093` | `3003` |

Para derrubar as três aplicações sem remover os volumes:

```bash
for app in cpu_bound io_bound memory_bound; do
  docker compose -f "$app/docker-compose.yml" down
done
```

## Testes com Locust

Execute apenas um comando por vez. Depois, acesse `http://localhost:8089` para
definir a quantidade de usuários e a taxa de inicialização.

### CPU bound

```bash
uvx --from locust locust \
  -f cpu_bound/locustfile.py \
  --host http://localhost:8081 \
  --web-host 0.0.0.0 \
  --web-port 8089
```

### IO bound

```bash
uvx --from locust locust \
  -f io_bound/locustfile.py \
  --host http://localhost:8082 \
  --web-host 0.0.0.0 \
  --web-port 8089
```

### Memory bound

```bash
uvx --from locust locust \
  -f memory_bound/locustfile.py \
  --host http://localhost:8083 \
  --web-host 0.0.0.0 \
  --web-port 8089
```
