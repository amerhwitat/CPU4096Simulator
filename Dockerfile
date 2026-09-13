FROM ubuntu:24.04
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y --no-install-recommends bash ca-certificates git gcc g++ make cmake python3 python3-pip && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY . /app
COPY docker/entrypoint.sh /usr/local/bin/repo-entrypoint
RUN chmod +x /usr/local/bin/repo-entrypoint && useradd -m -u 10001 appuser && chown -R appuser:appuser /app
USER appuser
ENTRYPOINT ["/usr/local/bin/repo-entrypoint"]
