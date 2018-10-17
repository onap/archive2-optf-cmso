#!/bin/sh
echo "VM_ARGS="${VM_ARGS}

java -Djava.security.egd=file:/dev/./urandom  ${VM_ARGS} -Xms1024m -Xmx1024m  -jar  ./app.jar
