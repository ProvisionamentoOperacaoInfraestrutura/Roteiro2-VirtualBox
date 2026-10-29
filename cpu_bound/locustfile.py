import os
import random

from locust import HttpUser, between, task
from observability.locust_metrics import setup_prometheus_metrics


setup_prometheus_metrics("cpu_bound")
CPU_BOUND_LIMIT = int(os.getenv("CPU_BOUND_LIMIT", "1000000"))
CPU_BOUND_MIN = max(1000, CPU_BOUND_LIMIT // 10)


class ApiUser(HttpUser):
    wait_time = between(1, 2)

    @task
    def consultar_primos(self):
        enesimo = random.randint(CPU_BOUND_MIN, CPU_BOUND_LIMIT)

        with self.client.get(
            f"/v1/cpu_bound/primos?enesimo={enesimo}",
            name="/v1/cpu_bound/primos",
            catch_response=True,
        ) as response:
            if response.status_code != 200:
                response.failure(f"Erro HTTP {response.status_code}")
