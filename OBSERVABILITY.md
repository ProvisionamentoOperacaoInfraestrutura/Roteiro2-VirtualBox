# Observabilidade dos testes Locust

Cada aplicação possui seu próprio Prometheus, Grafana e processo Locust. O dashboard
`Locust x Aplicação` correlaciona usuários e requisições do gerador de carga com as
métricas HTTP, JVM e do trabalho executado pela aplicação.

| Aplicação | API | Locust | Prometheus | Grafana |
| --- | --- | --- | --- | --- |
| CPU | http://localhost:8081 | http://localhost:8089 | http://localhost:9091 | http://localhost:3004 |
| I/O | http://localhost:8082 | http://localhost:8090 | http://localhost:9092 | http://localhost:3002 |
| Memória | http://localhost:8083 | http://localhost:8091 | http://localhost:9093 | http://localhost:3003 |

## Execução

Inicie as aplicações, os três Locust e a infraestrutura de métricas:

```bash
./start-all.sh
```

Os Locust já ficam disponíveis sem gerar carga. Os scripts abaixo servem para recriar
um Locust individualmente quando necessário:

```bash
./locust_cpu_bound.sh
./locust_io_bound.sh
./locust_memory_bound.sh
```

O teste de CPU usa o limite seguro `1000000`. Para alterar o trabalho por requisição,
defina a variável ao recriar o Locust, por exemplo:

```bash
CPU_BOUND_LIMIT=5000000 ./locust_cpu_bound.sh
```

Comece com um usuário ao experimentar um limite novo. Valores muito altos combinados
com vários usuários podem esgotar o heap, pois cada chamada mantém em memória a lista
com todos os primos encontrados.

Abra o Locust correspondente, configure usuários e taxa de subida e inicie o teste.
No Grafana correspondente, abra `Locust x Aplicação` e use o mesmo intervalo de tempo
do teste. O Prometheus coleta a cada 10 segundos (com timeout de 5 segundos) e o dashboard atualiza a cada 5 segundos.

## Como validar a correlação

- `Usuários do Locust` deve acompanhar a quantidade configurada no teste.
- `RPS: cliente x servidor` deve apresentar curvas próximas. Diferenças durante chamadas
  longas são esperadas, pois o Locust conta uma requisição quando ela termina.
- `Latência p95` compara o tempo fim a fim do Locust com o tempo dentro do Spring.
- `Trabalho em andamento` mostra chamadas ainda processando mesmo quando o RPS está em zero.
- CPU deve crescer no teste de CPU, bytes lidos no teste de I/O e heap/GC no teste de memória.
- Falhas do Locust e respostas HTTP 5xx devem aparecer no mesmo período.

O endpoint `/metrics` de cada Locust é usado pelo Prometheus. Se o cartão `Coleta do
Locust` aparecer como indisponível, confirme que o script Locust daquela aplicação está
em execução.
