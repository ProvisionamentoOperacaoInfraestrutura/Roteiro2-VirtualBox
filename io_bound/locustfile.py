from locust import HttpUser, between, task


class ApiUser(HttpUser):
    wait_time = between(1, 2)

    @task
    def ler_arquivo(self):
        self.client.get("/v1/io_bound/arquivo", name="/v1/io_bound/arquivo")
