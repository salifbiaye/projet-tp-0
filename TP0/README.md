# TP0 - Java RMI Chat

## Description
Logiciel de chat utilisant la technologie RMI permettant à plusieurs utilisateurs de discuter simultanément.

## Structure du projet

```
TP0/
├── interface/          # Interfaces distantes
│   ├── ChatRoom.java
│   └── ChatUser.java
├── impl/              # Implémentations
│   ├── ChatRoomImpl.java
│   └── ChatUserImpl.java
├── server/            # Serveur
│   └── ChatServer.java
└── client/            # Client
    └── ChatClient.java
```

## Compilation

```bash
cd TP0
javac interface/*.java impl/*.java server/*.java client/*.java
```

## Exécution

### 1. Démarrer le serveur
```bash
java server.ChatServer
```

### 2. Lancer des clients (dans des terminaux séparés)
```bash
java client.ChatClient Alice
java client.ChatClient Bob
java client.ChatClient Charlie
```

## Utilisation

- Tapez vos messages et appuyez sur Entrée pour les envoyer
- Tapez 'quit' pour quitter la salle de discussion
- Tous les messages sont diffusés à tous les utilisateurs connectés

## Fonctionnalités

- Connexion/déconnexion des utilisateurs
- Diffusion des messages à tous les participants
- Notifications d'arrivée et de départ
- Gestion automatique des déconnexions
