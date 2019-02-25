#!/bin/bash
docker-compose up >up.txt 2>&1 &

### Wait for robot to finish
sleep 60
docker exec -it cmso-service_cmso-robot_1 ls
while [ $? -ne 1 ]; do
  sleep 60
  docker exec -it cmso-service_cmso-robot_1 ls
done

### Shut down java with to give time to write the jacoco_exec file
docker exec cmso-service_cmso-service_1 pkill java
sleep 10

### wait for  cmso-service container to exit
docker exec -it cmso-service_cmso-service_1 ls
while [ $? -ne 1 ]; do
  sleep 60
  docker exec -it cmso-service_cmso-service_1 ls
done

docker ps -a
docker-compose down
docker ps -a
