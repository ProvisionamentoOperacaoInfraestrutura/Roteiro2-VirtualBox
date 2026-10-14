FROM locustio/locust:2.46.0

USER root
RUN pip install --no-cache-dir prometheus-client==0.26.0
