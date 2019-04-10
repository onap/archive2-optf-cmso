#!/bin/sh

AUTHENTICATION=${AUTHENTICATION:-proprietary-auth}
JAVA_TRUSTSTORE=${JAVA_TRUSTSTORE:-etc/config/truststoreONAPall.jks}

VM_ARGS="${VM_ARGS} -Dserver.local.startpath=${RESOURCES_HOME}"
VM_ARGS="${VM_ARGS} -Djavax.net.ssl.trustStore==${JAVA_TRUSTSTORE}"

echo "VM_ARGS="${VM_ARGS}

java -Djava.security.egd=file:/dev/./urandom  ${VM_ARGS} -Xms256m -Xmx1024m  -jar  ./app.jar --spring.profiles.active=${AUTHENTICATION}
