from locust import HttpUser, between, task
from observability.locust_metrics import setup_prometheus_metrics


setup_prometheus_metrics("memory_bound")


class ApiUser(HttpUser):
    wait_time = between(1, 2)

    @task
    def consultar_estatisticas(self):
        self.client.get("/v1/main/estatisticas?enesimo=100", name="/v1/main/estatisticas")
