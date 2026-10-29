import random

from locust import HttpUser, between, task
from observability.locust_metrics import setup_prometheus_metrics


setup_prometheus_metrics("memory_bound")


class ApiUser(HttpUser):
    wait_time = between(1, 2)

    @task
    def consultar_estatisticas(self):
        enesimo = random.randint(100_000, 2_000_000)

        with self.client.get(
            f"/v1/main/estatisticas?enesimo={enesimo}",
            name="/v1/main/estatisticas",
            catch_response=True,
        ) as response:
            if response.status_code != 200:
                response.failure(f"Erro HTTP {response.status_code}")
