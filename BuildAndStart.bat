echo Y|docker system prune -a && mvn clean && mvn package && docker-compose up