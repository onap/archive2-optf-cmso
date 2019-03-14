#!/bin/sh

VM_ARGS="${VM_ARGS} -Dserver.local.startpath=${RESOURCES_HOME}"

echo "VM_ARGS="${VM_ARGS}

java -Djava.security.egd=file:/dev/./urandom  ${VM_ARGS} -Xms256m -Xmx1024m  -jar  ./app.jar
