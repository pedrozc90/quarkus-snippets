# Publisher

## Rabbitmq

```bash
# start rabbitmq contianer
docker compose up --detach
```

```bash
# start rabbitmq contianer
docker exec rabbitmq bash -c "rabbitmqctl add_vhost quarkus"
docker exec rabbitmq bash -c 'rabbitmqctl set_permissions -p quarkus rabbitmq ".*" ".*" ".*"'
```
