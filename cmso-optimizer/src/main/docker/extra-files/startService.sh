#!/bin/sh

AUTHENTICATION=${AUTHENTICATION:-proprietary-auth}

VM_ARGS="${VM_ARGS} -Dserver.local.startpath=${RESOURCES_HOME}"

if [ "${JAVA_TRUSTSTORE}" != '']
then
   VM_ARGS="${VM_ARGS} -Djavax.net.ssl.trustStore=${JAVA_TRUSTSTORE}"
fi

if [ "${JAVA_TRUSTSTORE_PASSWORD}" != '']
then
   VM_ARGS="${VM_ARGS} -Djavax.net.ssl.trustStorePassword=${JAVA_TRUSTSTORE_PASSWORD}"
fi

if [ "${SSL_KEYSTORE}" != '' ]
then
   VM_ARGS="${VM_ARGS} -Dserver.ssl.key-store=${SSL_KEYSTORE}"
fi

if [ "${SSL_KEYSTORE_PASSWORD}" != '' ]
then
   VM_ARGS="${VM_ARGS} -Dserver.ssl.key-password=${SSL_KEYSTORE_PASSWORD}"
fi

echo "VM_ARGS="${VM_ARGS}

java -Djava.security.egd=file:/dev/./urandom  ${VM_ARGS} -Xms256m -Xmx1024m  -jar  ./app.jar --spring.profiles.active=${AUTHENTICATION}
