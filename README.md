# Howto run Igloo app on localhost

### Start the stack using docker-compose :

> docker-compose up

It launches :

- postgreSQL bar database
- postgreSQL inventory database
- rabbitMQ
- keycloak

### Launch the 3 SpringBoot services in your favourite way

### Get an Oauth2 token from KeyCloak :

> curl -X POST http://localhost:8099/realms/bulldozer/protocol/openid-connect/token \
> -H "Content-Type: application/x-www-form-urlencoded" \
> -d "grant_type=password" \
> -d "client_id=bulldozer-client" \
> -d "client_secret=bulldozer-client-secret" \
> -d "username=marc" \
> -d "password=password"

Users available :

- EMPLOYEE : juliano/password
- ADMIN & EMPLOYEE : marc/password

### Call Igloos services using this authentication

Bar service runs on port 8081  
Inventory service runs on port 8082