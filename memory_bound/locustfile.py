from locust import HttpUser, between, task


class ApiUser(HttpUser):
    wait_time = between(1, 2)

    @task
    def consultar_estatisticas(self):
        self.client.get("/v1/main/estatisticas?enesimo=100", name="/v1/main/estatisticas")
