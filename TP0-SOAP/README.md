# TP0 - Chat SOAP

## Description

Version SOAP du projet ChatUser. Le serveur expose un service SOAP sur HTTP avec des operations `subscribe`, `unsubscribe`, `postMessage` et `getMessages`. Le client Swing interroge regulierement le serveur pour afficher les nouveaux messages.

## Service

- URL du service : `http://localhost:8081/chat`
- WSDL : `http://localhost:8081/chat?wsdl`

Operations SOAP disponibles :

- `subscribe(pseudo)`
- `unsubscribe(pseudo)`
- `postMessage(pseudo, message)`
- `getMessages(lastIndex)`

## Compilation

```bash
cd TP0-SOAP
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
