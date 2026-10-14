import os

from locust import HttpUser, between, task
from observability.locust_metrics import setup_prometheus_metrics


setup_prometheus_metrics("cpu_bound")
CPU_BOUND_LIMIT = int(os.getenv("CPU_BOUND_LIMIT", "1000000"))


class ApiUser(HttpUser):
    wait_time = between(1, 2)

    @task
    def consultar_primos(self):
        self.client.get(
            f"/v1/cpu_bound/primos?enesimo={CPU_BOUND_LIMIT}",
            name="/v1/cpu_bound/primos",
        )
