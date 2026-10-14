from locust import HttpUser, between, task
from observability.locust_metrics import setup_prometheus_metrics


setup_prometheus_metrics("io_bound")


class ApiUser(HttpUser):
    wait_time = between(1, 2)

    @task
    def ler_arquivo(self):
        self.client.get("/v1/io_bound/arquivo", name="/v1/io_bound/arquivo")
