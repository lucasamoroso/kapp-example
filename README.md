# Simple kotlin application

## Stack
- [http4k](https://www.http4k.org/documentation/) server
- [ktorm](https://www.ktorm.org/) persistence
- [arrow](https://arrow-kt.io/) functional effects
- [kotlin-logging](https://www.kotlinresources.com/library/kotlin-logging/) logger
- [hikari](https://github.com/brettwooldridge/HikariCP#-hikaricpits-fasterhikari-hikal%C4%93-origin-japanese-light-ray) data-source


## Run local
From the root of the project execute:

```bash 
docker-compose -f src/main/resources/docker-compose.yml up -d && gradle run
```

## Create new user

```curl
curl --request POST \
  --url http://localhost:8080/kapp/users \
  --header 'Content-Type: application/json' \
  --data '{
	"name":"lucas",
	"lastName":"amoroso",
	"age":15
}'
```

