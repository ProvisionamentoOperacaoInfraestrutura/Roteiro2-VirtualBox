## Instalar Docker na VM

Se a VM apresentar `docker: command not found`, execute:

```bash
chmod +x install-docker.sh
./install-docker.sh
```

O script instala o Docker Engine e o Docker Compose Plugin para Ubuntu ou
Debian e abre uma nova sessão com o grupo `docker`. Se a execução não for
interativa, execute `newgrp docker` manualmente depois.

## Acessar fora da VM VirtualBox

Para acessar as aplicações pelo computador hospedeiro, configure o
redirecionamento de portas no VirtualBox:

1. Desligue a máquina virtual.
2. Abra **Configurações → Rede → Adaptador 1**.
3. Selecione **NAT → Avançado → Redirecionamento de Portas**.
4. Adicione as regras abaixo usando o protocolo `TCP`.
5. Deixe **Host IP** e **Guest IP** vazios.

### Grafanas

| Nome | Porta do host | Porta da VM |
|---|---:|---:|
| grafana-cpu | 3004 | 3004 |
| grafana-io | 3002 | 3002 |
| grafana-memory | 3003 | 3003 |

### APIs

| Nome | Porta do host | Porta da VM |
|---|---:|---:|
| api-cpu | 8081 | 8081 |
| api-io | 8082 | 8082 |
| api-memory | 8083 | 8083 |

Opcionalmente, para acessar os Prometheus, adicione também:

| Nome | Porta do host | Porta da VM |
|---|---:|---:|
| prometheus-cpu | 9091 | 9091 |
| prometheus-io | 9092 | 9092 |
| prometheus-memory | 9093 | 9093 |

Após iniciar a VM e as aplicações, acesse pelo computador hospedeiro:

```text
Grafana CPU:    http://localhost:3004
Grafana IO:     http://localhost:3002
Grafana Memory: http://localhost:3003
API CPU:        http://localhost:8081
API IO:         http://localhost:8082
API Memory:     http://localhost:8083
```

Se os Locusts estiverem rodando dentro da VM, redirecione também as portas
`8089`, `8090` e `8091` para as mesmas portas e acesse:

```text
CPU:    http://localhost:8089
IO:     http://localhost:8090
Memory: http://localhost:8091
```

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

Os três testes podem ser executados simultaneamente. Em cada interface,
defina a quantidade de usuários e a taxa de inicialização.

### CPU bound

```bash
./locust_cpu_bound.sh
```

Acesse `http://localhost:8089`.

### IO bound

```bash
./locust_io_bound.sh
```

Acesse `http://localhost:8090`.

### Memory bound

```bash
./locust_memory_bound.sh
```

Acesse `http://localhost:8091`.
