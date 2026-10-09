from locust import HttpUser, between, task


class ApiUser(HttpUser):
    wait_time = between(1, 2)

    @task(1)
    def consultar_primos(self):
        self.client.get("/v1/main/primos?enesimo=100", name="/v1/main/primos")

    @task(2)
    def consultar_estatisticas(self):
        self.client.get("/v1/main/estatisticas?enesimo=100", name="/v1/main/estatisticas",)

    @task(3)
    def ler_arquivo(self):
        self.client.get("/v1/main/arquivo", name="/v1/main/arquivo")
