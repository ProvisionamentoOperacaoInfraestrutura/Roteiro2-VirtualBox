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