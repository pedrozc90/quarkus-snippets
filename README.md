# quarkus-snippets

## Description

Collection of template, boilerplates, snippets and samples builds in quarkus

## Setup

```bash
# create rabbitmq container
docker compose up --detach

docker exec -it rabbitmq bash
```

```bash
# create vhost "quarkus"
rabbitmqctl add_vhost quarkus

# set permissions for vhost
rabbitmqctl set_permissions -p quarkus rabbitmq ".*" ".*" ".*"
```

## License

Please, read [LICENSE](./LICENSE) file.
