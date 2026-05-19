# TP0 - Chat REST

## Description

Version REST du projet ChatUser. Le serveur expose des endpoints HTTP qui echangent du JSON. Le client Swing reprend le meme principe que la version XML-RPC : il envoie les messages au serveur et recupere les nouveaux messages par polling.

## Endpoints

- `POST http://localhost:8082/chat/subscribe`
  - Corps : `{"pseudo":"Alice"}`
- `POST http://localhost:8082/chat/message`
  - Corps : `{"pseudo":"Alice","message":"Bonjour"}`
- `GET http://localhost:8082/chat/messages?lastIndex=0`
- `POST http://localhost:8082/chat/unsubscribe`
  - Corps : `{"pseudo":"Alice"}`

## Compilation

```bash
cd TP0-REST
ant compile
```

## Execution

Demarrer le serveur :

```bash
ant run-server
```

Dans un autre terminal, lancer un ou plusieurs clients :

```bash
ant run-client
```

## Archive complete

```bash
ant all
```

Les fichiers JAR sont generes dans `archive/`.
