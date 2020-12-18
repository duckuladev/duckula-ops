#task and data, not exist ops 
#FROM openjdk:8-jdk-alpine
# the limit resource set 320M*80%=256M
FROM rjzjh/jdk8:1
# me
MAINTAINER andy "rjzjh@163.com"
#
LABEL version="3.0"
LABEL location="Shang Hai" author="andy" role="Ops"


RUN addgroup -S duckula && adduser -S -G duckula duckula
RUN set -ex; \
    mkdir -p /data/duckula-data; \
    mkdir -p /opt/duckula/bin; \
    mkdir -p /alidata1/admin; \
    apk update upgrade; \
    apk add --no-cache --virtual .must  \
         'su-exec>=0.2' \
          mysql-client \
          bash; \
    apk add --no-cache --virtual .duckuladepent  \
          tar; 
COPY ./duckula-ops.jar  /opt/duckula
COPY ./bin/*  /opt/duckula/bin/
COPY ./export/config.yaml  /
COPY ./export/jmx_prometheus_javaagent-0.12.0.jar  /
RUN  chmod +x -R /opt/duckula/bin; \
    for path in \
		 /opt/duckula \
		 /data/duckula-data \
		 /alidata1/admin \
	; do \
		mkdir -p "$path"; \
		chown -R duckula:duckula "$path"; \
	done; 
VOLUME /opt/userconfig/
WORKDIR /opt/duckula
ENV PATH /opt/duckula/bin:$PATH

ENV DUCKULA3_VERSION="3.0.0"
ENV DUCKULA3_DATA="/data/duckula-data" \
    DUCKULA3_HOME="/opt/duckula" \
    metricspath="/data/duckula-data/logs/metrics" 

COPY ./docker-entrypoint/docker-entrypoint.sh  /
RUN set -ex; \
    chmod  +x  /docker-entrypoint.sh; \
    chmod  777  /config.yaml; \
    chmod 444  /jmx_prometheus_javaagent-0.12.0.jar; 
#2723:jmx,jmxexport:2780,2113:debug,8080:web
EXPOSE 2723 2780 2113 8080
#不允许修改入口
#ENTRYPOINT ["/docker-entrypoint.sh"]
#CMD ["docker-run.sh","ops","2723"]
ENTRYPOINT ["/docker-entrypoint.sh","docker-run.sh","ops","2723"]