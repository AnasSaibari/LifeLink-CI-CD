# Exemples d'API pour tester le Chat Service

Le service tourne sur le port **9096** (base URL: `http://localhost:9096`)

## 1. Créer une Conversation

**Endpoint:** `POST /conversations`

**Description:** Crée une nouvelle conversation entre un utilisateur (sender) et un hôpital (receiver).

### Exemple avec cURL:
```bash
curl -X POST http://localhost:9096/conversations \
  -H "Content-Type: application/json" \
  -d '{
    "senderId": 1,
    "receiverId": 1
  }'
```

### Exemple avec Postman:
- **Method:** POST
- **URL:** `http://localhost:9096/conversations`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "senderId": 1,
  "receiverId": 1
}
```

### Réponse attendue (201 Created):
```json
{
  "id": 1,
  "sender": {
    "name": "John Doe"
  },
  "receiver": {
    "name": "Hospital Central"
  },
  "lastMessage": null,
  "createdAt": "2024-01-15T10:30:00"
}
```

---

## 2. Obtenir toutes les Conversations d'un Utilisateur

**Endpoint:** `GET /conversations/user/{userId}`

**Description:** Récupère toutes les conversations où l'utilisateur est soit le sender soit le receiver.

### Exemple avec cURL:
```bash
curl -X GET http://localhost:9096/conversations/user/1
```

### Exemple avec Postman:
- **Method:** GET
- **URL:** `http://localhost:9096/conversations/user/1`

### Réponse attendue (200 OK):
```json
[
  {
    "id": 1,
    "sender": {
      "name": "John Doe"
    },
    "receiver": {
      "name": "Hospital Central"
    },
    "lastMessage": "Bonjour, je voudrais prendre rendez-vous",
    "createdAt": "2024-01-15T10:30:00"
  },
  {
    "id": 2,
    "sender": {
      "name": "Jane Smith"
    },
    "receiver": {
      "name": "Hospital North"
    },
    "lastMessage": "Merci pour votre réponse",
    "createdAt": "2024-01-16T14:20:00"
  }
]
```

---

## 3. Créer un Message dans une Conversation

**Endpoint:** `POST /messages`

**Description:** Crée un nouveau message dans une conversation existante.

### Exemple avec cURL:
```bash
curl -X POST http://localhost:9096/messages \
  -H "Content-Type: application/json" \
  -d '{
    "conversationId": 1,
    "senderRole": "user",
    "text": "Bonjour, je voudrais prendre rendez-vous pour un don de sang"
  }'
```

### Exemple avec Postman:
- **Method:** POST
- **URL:** `http://localhost:9096/messages`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "conversationId": 1,
  "senderRole": "user",
  "text": "Bonjour, je voudrais prendre rendez-vous pour un don de sang"
}
```

### Exemples de senderRole:
- `"user"` - quand l'utilisateur envoie le message
- `"hospital"` - quand l'hôpital envoie le message

### Réponse attendue (201 Created):
```json
{
  "id": 1,
  "senderRole": "user",
  "text": "Bonjour, je voudrais prendre rendez-vous pour un don de sang",
  "createdAt": "2024-01-15T10:35:00"
}
```

### Autre exemple (message de l'hôpital):
```json
{
  "conversationId": 1,
  "senderRole": "hospital",
  "text": "Bonjour, nous serions ravis de vous recevoir. Quand seriez-vous disponible ?"
}
```

---

## 4. Obtenir tous les Messages d'une Conversation

**Endpoint:** `GET /messages/conversation/{conversationId}`

**Description:** Récupère tous les messages d'une conversation, triés par date de création (plus ancien au plus récent).

### Exemple avec cURL:
```bash
curl -X GET http://localhost:9096/messages/conversation/1
```

### Exemple avec Postman:
- **Method:** GET
- **URL:** `http://localhost:9096/messages/conversation/1`

### Réponse attendue (200 OK):
```json
[
  {
    "id": 1,
    "senderRole": "user",
    "text": "Bonjour, je voudrais prendre rendez-vous pour un don de sang",
    "createdAt": "2024-01-15T10:35:00"
  },
  {
    "id": 2,
    "senderRole": "hospital",
    "text": "Bonjour, nous serions ravis de vous recevoir. Quand seriez-vous disponible ?",
    "createdAt": "2024-01-15T11:20:00"
  },
  {
    "id": 3,
    "senderRole": "user",
    "text": "Je suis disponible demain après-midi",
    "createdAt": "2024-01-15T12:00:00"
  }
]
```

---

## Scénario de Test Complet

### Étape 1: Créer une conversation
```bash
curl -X POST http://localhost:9096/conversations \
  -H "Content-Type: application/json" \
  -d '{"senderId": 1, "receiverId": 1}'
```
**Note:** Sauvegardez l'`id` de la conversation retournée (ex: `1`)

### Étape 2: Envoyer un message (utilisateur)
```bash
curl -X POST http://localhost:9096/messages \
  -H "Content-Type: application/json" \
  -d '{
    "conversationId": 1,
    "senderRole": "user",
    "text": "Bonjour, je voudrais prendre rendez-vous"
  }'
```

### Étape 3: Envoyer un message (hôpital)
```bash
curl -X POST http://localhost:9096/messages \
  -H "Content-Type: application/json" \
  -d '{
    "conversationId": 1,
    "senderRole": "hospital",
    "text": "Bonjour, nous pouvons vous recevoir demain"
  }'
```

### Étape 4: Récupérer tous les messages de la conversation
```bash
curl -X GET http://localhost:9096/messages/conversation/1
```

### Étape 5: Récupérer toutes les conversations de l'utilisateur
```bash
curl -X GET http://localhost:9096/conversations/user/1
```

---

## Notes Importantes

1. **Prérequis:** Assurez-vous que les services `user-service` (port 9091) et `hospital-service` (port 9093) sont démarrés, car le chat service les appelle pour récupérer les noms des utilisateurs et hôpitaux.

2. **IDs valides:** Utilisez des IDs d'utilisateurs et d'hôpitaux qui existent réellement dans vos bases de données respectives.

3. **senderRole:** Doit être exactement `"user"` ou `"hospital"` (en minuscules).

4. **Base de données:** Le service utilise MySQL sur `localhost:3306` avec la base de données `chat_db` (créée automatiquement si elle n'existe pas).

5. **Format de date:** Les dates sont au format ISO 8601 (ex: `2024-01-15T10:30:00`).

---

## Collection Postman

Vous pouvez importer ces exemples dans Postman en créant une nouvelle collection avec les 4 requêtes suivantes:

1. **Create Conversation** - POST `/conversations`
2. **Get User Conversations** - GET `/conversations/user/{userId}`
3. **Create Message** - POST `/messages`
4. **Get Conversation Messages** - GET `/messages/conversation/{conversationId}`

