from locust import HttpUser, between, task


class ApiUser(HttpUser):
    wait_time = between(1, 2)

    @task
    def consultar_primos(self):
        self.client.get("/v1/cpu_bound/primos?enesimo=100", name="/v1/cpu_bound/primos")
