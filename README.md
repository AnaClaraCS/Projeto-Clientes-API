# Projeto-Clientes-API

## Projeto final do curso Java Arquiteto (COTI)

Este projeto é composto por três partes principais:

1. clientesApi: API desenvolvida em Spring Boot, responsável pelo cadastro, edição, exclusão e consulta de clientes. Também envia mensagens para o RabbitMQ sempre que um cliente é cadastrado.

2. email-cliente-api: Aplicação também em Spring Boot, responsável por consumir a fila do RabbitMQ e enviar o email de confirmação de cadastro do cliente.

3. clientes-app: Aplicação Angular que fornece a interface web para o usuário visualizar, cadastrar, editar e excluir clientes.


## Como Rodar

Execute na raiz do repositório:

docker compose build

docker compose up -d


## Links de Acesso

Front-end (clientes-app)	http://localhost:4200

Swagger da API (clientesApi)	http://localhost:8080/swagger-ui.html

RabbitMQ (dashboard)	http://localhost:15672