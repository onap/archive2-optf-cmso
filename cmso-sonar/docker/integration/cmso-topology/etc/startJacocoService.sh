#!/bin/sh
apt update
apt install wget
apt install unzip  --assume-yes
wget https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/0.8.5/org.jacoco.agent-0.8.5.jar --no-check-certificate
unzip org.jacoco.agent-0.8.5.jar
cp org.jacoco.agent-0.8.5/jacocoagent.jar .
ls -l

VM_ARGS="${VM_ARGS} -javaagent:./jacocoagent.jar=destfile=/share/logs/topology.jacoco.exec,dumponexit=true,jmx=true,append=true,output=file,includes=org.onap.*"

echo "VM_ARGS=${VM_ARGS}"

java -Djava.security.egd=file:/dev/./urandom -Xms256m -Xmx1024m ${VM_ARGS} -jar ./app.jar
