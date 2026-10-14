"""Prometheus metrics shared by the three Locust test suites."""

import time

from locust import events
from prometheus_client import CONTENT_TYPE_LATEST, Counter, Gauge, Histogram, generate_latest


REQUESTS = Counter(
    "locust_requests",
    "Requests completed by Locust.",
    ("application", "method", "name", "outcome", "status"),
)
REQUEST_DURATION = Histogram(
    "locust_request_duration_seconds",
    "End-to-end response time measured by Locust.",
    ("application", "method", "name", "outcome"),
    buckets=(0.005, 0.01, 0.025, 0.05, 0.1, 0.25, 0.5, 1, 2.5, 5, 10, 30, 60, 120, 300),
)
RESPONSE_BYTES = Counter(
    "locust_response_bytes",
    "Response bytes received by Locust.",
    ("application", "name"),
)
USERS = Gauge("locust_users", "Current simulated users.", ("application",))
TARGET_USERS = Gauge("locust_target_users", "Target simulated users.", ("application",))
TEST_RUNNING = Gauge("locust_test_running", "Whether a Locust test is running.", ("application",))
TEST_START = Gauge(
    "locust_test_start_timestamp_seconds",
    "Unix timestamp of the latest test start.",
    ("application",),
)
TEST_STARTS = Counter("locust_test_starts", "Number of Locust test runs.", ("application",))

_application = None
_environment = None


def setup_prometheus_metrics(application):
    global _application

    if _application is not None:
        if _application != application:
            raise RuntimeError("Locust Prometheus metrics already configured")
        return

    _application = application
    USERS.labels(application).set(0)
    TARGET_USERS.labels(application).set(0)
    TEST_RUNNING.labels(application).set(0)


@events.init.add_listener
def on_locust_init(environment, **_kwargs):
    global _environment
    _environment = environment

    if environment.web_ui is None:
        return

    def prometheus_metrics():
        runner = _environment.runner
        if runner is not None and _application is not None:
            current_users = getattr(runner, "user_count", 0)
            target_users = getattr(runner, "target_user_count", current_users)
            state = getattr(runner, "state", "stopped")
            USERS.labels(_application).set(current_users)
            TARGET_USERS.labels(_application).set(target_users)
            TEST_RUNNING.labels(_application).set(state in {"running", "spawning"})

        return generate_latest(), 200, {"Content-Type": CONTENT_TYPE_LATEST}

    environment.web_ui.app.add_url_rule(
        "/metrics",
        endpoint="prometheus_metrics",
        view_func=prometheus_metrics,
    )


@events.test_start.add_listener
def on_test_start(**_kwargs):
    if _application is None:
        return
    TEST_RUNNING.labels(_application).set(1)
    TEST_START.labels(_application).set(time.time())
    TEST_STARTS.labels(_application).inc()


@events.test_stop.add_listener
def on_test_stop(**_kwargs):
    if _application is None:
        return
    USERS.labels(_application).set(0)
    TARGET_USERS.labels(_application).set(0)
    TEST_RUNNING.labels(_application).set(0)


@events.request.add_listener
def on_request(
    request_type,
    name,
    response_time,
    response_length,
    exception,
    response=None,
    **_kwargs,
):
    if _application is None:
        return

    outcome = "failure" if exception is not None else "success"
    status = str(getattr(response, "status_code", 0)) if response is not None else "exception"
    request_name = name or "unknown"
    method = request_type or "unknown"

    REQUESTS.labels(_application, method, request_name, outcome, status).inc()
    if response_time is not None:
        REQUEST_DURATION.labels(_application, method, request_name, outcome).observe(
            response_time / 1000.0
        )
    if response_length:
        RESPONSE_BYTES.labels(_application, request_name).inc(response_length)
