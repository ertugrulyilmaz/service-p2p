# Service P2P

## What does this service do?

- This is very simple a RESTful API which demonstrate a money transfers between accounts.
- This service uses five tables for all process. Database schema is in **schema.sql** file
  - **users:** Store Application Accounts
  - **user_balances:** Store balances of accounts
  - **user_transfers:** Store transactions of money transfer between accounts.
  - **user_deposits:** Store data of deposit process
  - **user_withdraws:** Store data of withdraw process

## Technologies & Services

- **Finatra (_https://twitter.github.io/finatra/_)** is a Scala services framework built on top of TwitterServer and Finagle.
- **Scalikejdbc (_http://scalikejdbc.org/_)** is a tidy SQL-based DB access library for Scala.
- **H2 Database (_http://www.h2database.com/html/main.html_)** for embedded database.
- **Docker (_https://docker.com_)** for Dockerize service.
- **Github (_https://github.com_)** for hosting project repo.

## Requirements

- Scala 2.12.7
- Java 1.8+

## Demonstration of the API works as expected

```sh
sbt clean assembly

java -server -Xms256m -Xmx256m -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -jar target/scala-2.12/service-p2p.jar

## create users on platform
curl -X POST -H "Content-Type: application/json" http://localhost:8080/user -d '{"email": "bob@example.com"}'
curl -X POST -H "Content-Type: application/json" http://localhost:8080/user -d '{"email": "alice@example.com"}'

## check users have created
curl -H "Content-Type: application/json" http://localhost:8080/user/1
curl -H "Content-Type: application/json" http://localhost:8080/user/2

## deposit money to users from different senders
curl -X POST -H "Content-Type: application/json" http://localhost:8080/user/1/deposit -d '{"sender": "paypal", "amount": 100.50, "currency": "usd", "trx_id": "pt-123456"}'
curl -X POST -H "Content-Type: application/json" http://localhost:8080/user/2/deposit -d '{"sender": "credit-card", "amount": 201.00, "currency": "usd", "trx_id": "pt-987654"}'

## checks balances of users for each currencies
curl -H "Content-Type: application/json" http://localhost:8080/user/1/balance
curl -H "Content-Type: application/json" http://localhost:8080/user/2/balance

## checks balances of users for specific currencies
curl -H "Content-Type: application/json" http://localhost:8080/user/1/balance?currency=usd
curl -H "Content-Type: application/json" http://localhost:8080/user/2/balance?currency=usd

## confirm deposit to completed
curl -X PUT -H "Content-Type: application/json" http://localhost:8080/user/1/deposit/1/complete
curl -X PUT -H "Content-Type: application/json" http://localhost:8080/user/2/deposit/2/complete

## checks balances of users for specific currencies
curl -H "Content-Type: application/json" http://localhost:8080/user/1/balance?currency=usd
curl -H "Content-Type: application/json" http://localhost:8080/user/2/balance?currency=usd

## send money from bob to alice
curl -X POST -H "Content-Type: application/json" http://localhost:8080/user/transfer -d '{"sender_id": 1, "receiver_id": 2, "amount": 50.00, "currency": "usd", "note": "buy some foods when you are coming dinner"}'

## checks balances of users for specific currencies
curl -H "Content-Type: application/json" http://localhost:8080/user/1/balance?currency=usd
curl -H "Content-Type: application/json" http://localhost:8080/user/2/balance?currency=usd

## withdraw some money to outside of the platform
curl -X POST -H "Content-Type: application/json" http://localhost:8080/user/1/withdraw -d '{"receiver": "paypal", "amount": 20.50, "currency": "usd"}'
curl -X POST -H "Content-Type: application/json" http://localhost:8080/user/2/withdraw -d '{"receiver": "bank", "amount": 30.50, "currency": "usd"}'

## checks balances of users for specific currencies
curl -H "Content-Type: application/json" http://localhost:8080/user/1/balance?currency=usd
curl -H "Content-Type: application/json" http://localhost:8080/user/2/balance?currency=usd

## confirm withdraw to completed
curl -X PUT -H "Content-Type: application/json" http://localhost:8080/user/1/withdraw/1/complete -d '{"trx_id": "pt-445566"}'
curl -X PUT -H "Content-Type: application/json" http://localhost:8080/user/2/withdraw/2/complete -d '{"trx_id": "bt-778899"}'

## checks balances of users for specific currencies
curl -H "Content-Type: application/json" http://localhost:8080/user/1/balance?currency=usd
curl -H "Content-Type: application/json" http://localhost:8080/user/2/balance?currency=usd
```
