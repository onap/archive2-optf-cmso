#!/bin/sh
apt update
apt install netcat --assume-yes
COUNTER=30
while [  $COUNTER -gt 0  ]; do
    nc -z ${DB_HOST} 3306
    if [ $? -eq 0 ]; then
        let COUNTER=0
    else
    	let COUNTER=COUNTER-1 
	sleep 10
    fi
done

echo "VM_ARGS="${VM_ARGS}

java -Djava.security.egd=file:/dev/./urandom  ${VM_ARGS} -Xms256m -Xmx1024m  -jar  ./app.jar  --spring.config.location=/share/etc/config/liquibase.properties
