# TP0 - Chat TCP Sockets

## Description

Version TCP sockets du projet ChatUser. Le serveur utilise `ServerSocket` et chaque client utilise `Socket`.

Le serveur accepte plusieurs clients en parallele grace a un thread par connexion. Chaque message recu depuis un client est diffuse a tous les clients connectes.

## Port

Le serveur ecoute sur :

```text
localhost:8083
```

## Compilation

```bash
cd TP0-TCP
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

## Fonctionnement du protocole

1. Le client ouvre une socket TCP vers `localhost:8083`.
2. Le client envoie son pseudo sur la premiere ligne.
3. Le serveur repond `OK` ou `ERROR: ...`.
4. Chaque ligne envoyee ensuite par le client est consideree comme un message de chat.
5. Le serveur diffuse les messages a tous les clients connectes.
