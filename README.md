# projet-tp-0

Projet académique réalisé dans le cadre du module de systèmes distribués et services web.

Le dépôt présente une même application de discussion, **ChatUser**, implémentée avec plusieurs mécanismes de communication distribuée. L’objectif est de comparer les architectures, les protocoles, les formats d’échange et les modes de validation.

## Informations

- Enseignant : Pr. Ibrahima Fall
- Étudiants : Salif Biaye, Ndeye Astou Diagouraga, Mouhamadou Tidiane Seck, Sountou Sakho
- Dépôt GitHub : https://github.com/salifbiaye/projet-tp-0
- Rapport : `rapport-i-fall-complet.docx`

## Structure du projet

| Dossier | Contenu |
| --- | --- |
| `TP0/` | Version Java RMI de l’application ChatUser. |
| `TP0-XMLRPC/` | Version XML-RPC avec appels de procédures distantes sur HTTP/XML. |
| `TP0-SOAP/` | Version SOAP avec enveloppes XML et service WSDL. |
| `TP0-REST/` | Version REST avec endpoints HTTP et échanges JSON. |
| `TP0-TCP/` | Variante bonus avec sockets TCP, `ServerSocket` et threads clients. |
| `ProjetAnt/` | Exemple d’automatisation Java avec Apache Ant. |
| `fop/` et `projet/` | Exemples XML, XSL-FO et génération PDF avec Apache FOP. |
| `images/` | Captures d’exécution insérées dans le rapport Word. |
| `rapport/` | Ancienne base de rapport utilisée comme source documentaire. |

## Technologies étudiées

- Java RMI : objets distants, registre RMI, callbacks.
- XML-RPC : appels de procédures distantes via HTTP et XML.
- SOAP : enveloppes XML, opérations de service et WSDL.
- REST : endpoints HTTP, méthodes `GET`/`POST` et JSON.
- TCP sockets : communication bas niveau par flux texte.
- Apache Ant : compilation, documentation, archive et exécution automatisées.
- Apache FOP : transformation XSL-FO vers PDF.

## Exécution rapide

Chaque version principale suit la même logique : compiler, démarrer le serveur, puis lancer un ou plusieurs clients.

### Java RMI

```powershell
cd TP0
ant compile
ant run-server
ant run-client -Dpseudo=Alice
```

### XML-RPC

```powershell
cd TP0-XMLRPC
ant compile
ant run-server
ant run-client
```

### SOAP

```powershell
cd TP0-SOAP
ant compile
ant run-server
ant run-client
```

Le WSDL est disponible à l’adresse :

```text
http://localhost:8081/chat?wsdl
```

### REST

```powershell
cd TP0-REST
ant compile
ant run-server
ant run-client
```

Endpoints REST principaux :

```text
POST http://localhost:8082/chat/subscribe
POST http://localhost:8082/chat/message
GET  http://localhost:8082/chat/messages?lastIndex=0
POST http://localhost:8082/chat/unsubscribe
```

### Bonus TCP

```powershell
cd TP0-TCP
ant compile
ant run-server
ant run-client
```

## Scénario de validation

1. Démarrer le serveur de la version choisie.
2. Lancer deux clients avec des pseudos différents.
3. Envoyer un message depuis le premier client.
4. Vérifier que le second client reçoit le message.
5. Répondre depuis le second client.
6. Vérifier que le premier client reçoit la réponse.
7. Fermer un client et vérifier que le serveur continue de fonctionner.

## Rapport

Le rapport Word `rapport-i-fall-complet.docx` documente :

- l’architecture générale de ChatUser ;
- les versions RMI, XML-RPC, SOAP et REST ;
- la variante TCP en bonus ;
- le guide d’exécution et de validation ;
- les annexes Ant et FOP ;
- les schémas techniques et emplacements pour captures d’exécution.

Les captures utilisées dans le rapport sont conservées dans `images/` afin que le document reste vérifiable et que les preuves d’exécution soient disponibles dans le dépôt.

## Nettoyage

Les fichiers compilés, archives, documentations générées, captures temporaires et artefacts de rendu sont exclus par `.gitignore` afin de garder le dépôt lisible.
