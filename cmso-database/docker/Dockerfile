FROM anapsix/alpine-java:8_jdk

ENV DBUPGRADE_HOME=/opt/app/dbupgrade

VOLUME /volume/config
VOLUME /volume/logs

ADD target/docker-liquibase.jar ${DBUPGRADE_HOME}/lib/docker-liquibase.jar
ADD bin ${DBUPGRADE_HOME}/bin

ENTRYPOINT ["/bin/bash", "-x", "/opt/app/dbupgrade/bin/docker_start.sh"]