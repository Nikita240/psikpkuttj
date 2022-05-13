## Prerequisites

  - Install docker.
  - Install docker-compose.
  - Make sure BuildKit is enabled.
  - Make sure BuildKit is enabled for docker-compose.

[docker installation guide](https://docs.docker.com/engine/install/ubuntu/)

[docker-compose installation guide](https://docs.docker.com/compose/install/)

To enable BuildKit, add these 3 environment variables to your .bashrc or equivalent:

```sh
export DOCKER_BUILDKIT=1 # Enable BuildKit
export COMPOSE_DOCKER_CLI_BUILD=1 # Enable BuildKit for docker-compose
```

## Running

Build:

```sh
docker-compose build
```

Run:

```sh
docker-compose up
```

## Usage

Here are the implemented endpoints:

  - `GET /users`
  - `POST /users`
  - `PUT /users/{id}`

Input data shall be passed in as json in the request body.

Here are some sample requests:

```sh
# request:
curl --location --request GET 'localhost:8080/users'
# return:
{
  "users": [{
    "id": "0",
    "firstName": "Nikita",
    "lastName": "Rushmanov"
  }]
}

# request:
curl --location --request POST 'localhost:8080/users' \
--header 'Content-Type: application/json' \
--data-raw '{
    "firstName": "John",
    "lastName": "Smith",
    "dateOfBirth": "11/06/1994",
    "email": "johnsmith@coolguy.com",
    "phoneNumber": "9999999999"
}'
# return:
{
  "id": "1",
  "firstName": "John",
  "lastName": "Smith",
  "dateOfBirth": "11/06/1994",
  "email": "johnsmith@coolguy.com",
  "phoneNumber": "9999999999"
}

# request:
curl --location --request PUT 'localhost:8080/users/1' \
--header 'Content-Type: application/json' \
--data-raw '{
    "firstName": "John",
    "lastName": "Smith",
    "dateOfBirth": "11/06/1994",
    "email": "igotanewemail@lol.gg",
    "phoneNumber": "9999999999"
}'
# response:
{
  "id": "1",
  "firstName": "John",
  "lastName": "Smith",
  "dateOfBirth": "11/06/1994",
  "email": "igotanewemail@lol.gg",
  "phoneNumber": "9999999999"
}

# request:
curl --location --request GET 'localhost:8080/users'
# return:
{
  "users": [{
    "id": "1",
    "firstName": "John",
    "lastName": "Smith"
  }, {
    "id": "0",
    "firstName": "Nikita",
    "lastName": "Rushmanov"
  }]
}
```