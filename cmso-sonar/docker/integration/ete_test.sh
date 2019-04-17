#!/bin/bash
docker-compose up >up.txt 2>&1 &

### Wait for robot to finish
sleep 240
docker exec cmso-service_cmso-robot_1 ls
while [ $? -ne 1 ]; do
  sleep 60
  docker exec cmso-service_cmso-robot_1 ls
done

### Shut down java with to give time to write the jacoco_exec file
docker exec cmso-service_cmso-service_1 pkill java
docker exec cmso-service_cmso-optimizer_1 pkill java
sleep 10

### wait for  cmso  containers to exit and jacoco files written
docker exec cmso-service_cmso-service_1 ls
while [ $? -ne 1 ]; do
  sleep 60
  docker exec cmso-service_cmso-service_1 ls
done

docker exec cmso-service_cmso-optimizer_1 ls
while [ $? -ne 1 ]; do
  sleep 60
  docker exec cmso-service_cmso-optimizer_1 ls
done

cat up.txt

docker ps -a
docker-compose down
docker ps -a
