#!/bin/sh
apt update
apt install wget
apt install unzip  --assume-yes
n=0
while true;
do
  ((n++))
  wget https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/0.8.5/org.jacoco.agent-0.8.5.jar
  if [[ $? = 0 || $n -gt 5 ]]; then break; fi;
done
unzip org.jacoco.agent-0.8.5.jar
cp org.jacoco.agent-0.8.5/jacocoagent.jar .
ls -l

VM_ARGS="${VM_ARGS} -javaagent:./jacocoagent.jar=destfile=/share/logs/optimizer.jacoco.exec,dumponexit=true,jmx=true,append=true,output=file,includes=org.onap.*"

echo "VM_ARGS=${VM_ARGS}"

java -Djava.security.egd=file:/dev/./urandom -Xms256m -Xmx1024m ${VM_ARGS} -jar ./app.jar
