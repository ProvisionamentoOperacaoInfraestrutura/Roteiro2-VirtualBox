import random

from locust import HttpUser, between, task
from observability.locust_metrics import setup_prometheus_metrics


setup_prometheus_metrics("io_bound")


class ApiUser(HttpUser):
    wait_time = between(1, 2)

    @task
    def ler_arquivo(self):
        tamanho = random.choice([25, 50, 100, 200])

        with self.client.get(
            f"/v1/io_bound/arquivo?tamanho={tamanho}",
            name="/v1/io_bound/arquivo",
            catch_response=True,
        ) as response:
            if response.status_code != 200:
                response.failure(f"Erro HTTP {response.status_code}")
