# Guide d'utilisation de l'API Smart Recycle

## Vue d'ensemble

Ce guide explique comment utiliser l'API Smart Recycle pour l'authentification JWT et la gestion des données WasteResult.

## Configuration

### Backend (Spring Boot)
- Port: 8080
- Base URL: `http://172.20.10.2:8080/`
- Base de données: MySQL (smart_recycle)

### Frontend (Android)
- Utilise Retrofit pour les appels API
- Gestion automatique des tokens JWT
- Stockage local des sessions

## Authentification

### 1. Inscription d'un utilisateur

```java
// Créer une requête d'inscription
SignUpRequest signUpRequest = new SignUpRequest();
signUpRequest.setFirstName("John");
signUpRequest.setLastName("Doe");
signUpRequest.setEmail("john.doe@example.com");
signUpRequest.setPassword("password123");

// Appel API
ApiService apiService = RetrofitClient.getApiService();
Call<SignUpResponse> call = apiService.signUp(signUpRequest);
```

### 2. Connexion d'un utilisateur

```java
// Utiliser la classe utilitaire
ApiClient.authenticateUser(context, "john.doe@example.com", "password123", 
    new ApiClient.OnAuthListener() {
        @Override
        public void onSuccess(SignInResponse response) {
            // Token JWT automatiquement sauvegardé
            // Utilisateur authentifié
        }

        @Override
        public void onError(String error) {
            // Gérer l'erreur
        }
    });
```

## Gestion des WasteResult

### Structure des données WasteResult

Les données WasteResult contiennent les champs suivants :

- `wasteIcon`: URL ou nom de l'icône de l'objet
- `wasteType`: Type de déchet (ex: "Plastic", "Paper", "Glass")
- `wasteCategory`: Catégorie (ex: "Recyclable", "Non-recyclable")
- `wasteDate`: Date de création
- `wastePoints`: Points gagnés pour le recyclage
- `timeAgo`: Temps écoulé depuis la création (ex: "2 minutes ago")
- `objectDescription`: Description de l'objet
- `instructions`: Instructions de recyclage

### Sauvegarde d'un WasteResult

```java
// Créer un nouveau WasteResult
WasteResult wasteResult = new WasteResult();
wasteResult.setWasteIcon("plastic_bottle_icon.png");
wasteResult.setWasteType("Plastic");
wasteResult.setWasteCategory("Recyclable");
wasteResult.setWastePoints(10);
wasteResult.setTimeAgo("2 minutes ago");
wasteResult.setObjectDescription("This is a plastic water bottle that can be recycled");
wasteResult.setInstructions("Rinse the bottle, remove the cap, and place in recycling bin");

// Sauvegarder avec authentification automatique
ApiClient.saveWasteResult(context, wasteResult, new ApiClient.OnWasteResultListener() {
    @Override
    public void onSuccess(WasteResult savedResult) {
        // WasteResult sauvegardé avec succès
        Log.d("API", "Saved with ID: " + savedResult.getId());
    }

    @Override
    public void onError(String error) {
        // Gérer l'erreur
    }
});
```

## Utilisation du ScanFragment

### Fonctionnement automatique

Le `ScanFragment` a été modifié pour automatiquement :

1. **Classifier l'image** localement avec le modèle ML
2. **Envoyer les données au backend** via l'API `/api/waste/scan`
3. **Sauvegarder dans la base de données** MySQL `smart_recycle`
4. **Naviguer vers la page de détail** avec les résultats

### Flux de données

```
Utilisateur clique sur "Analyze Object"
    ↓
Classification locale (WasteClassifier)
    ↓
Conversion image en Base64
    ↓
Envoi au backend (POST /api/waste/scan)
    ↓
Sauvegarde en base MySQL (table waste_results)
    ↓
Retour du WasteResult avec ID
    ↓
Navigation vers WasteDetailFragment
```

### Gestion de l'authentification

- **Utilisateur connecté** : Utilise l'API authentifiée avec token JWT
- **Utilisateur non connecté** : Utilise l'API publique (données sauvegardées sans utilisateur)

### Messages utilisateur

- ✅ "Analysis completed and saved!" - Succès avec sauvegarde
- ✅ "Analysis completed!" - Succès (utilisateur non connecté)
- ⚠️ "Error saving to backend" - Erreur backend mais analyse locale réussie
- ⚠️ "Network error, but analysis completed" - Erreur réseau mais analyse locale réussie

## Historique des analyses

### Fonctionnalités

L'application permet à chaque utilisateur de consulter son historique d'analyses :

1. **Accès à l'historique** : Via le menu ou navigation dédiée
2. **Affichage des analyses** : Liste chronologique des analyses effectuées
3. **Détails de chaque analyse** : Clic sur un item pour voir les détails
4. **Actualisation** : Pull-to-refresh pour mettre à jour l'historique

### Structure de l'historique

Chaque item d'historique affiche :
- **Icône du type de déchet** : Visuel du type d'objet analysé
- **Type de déchet** : Nom du type (Plastic, Paper, Glass, etc.)
- **Catégorie** : Recyclable, Non-recyclable, etc.
- **Date d'analyse** : Quand l'analyse a été effectuée
- **Points gagnés** : Points de recyclage obtenus
- **Temps écoulé** : "Il y a 2 minutes", "Il y a 1 heure", etc.

### Utilisation de l'historique

```java
// Charger l'historique
RetrofitClient.getAuthenticatedApiService(context)
    .getWasteHistory()
    .enqueue(new Callback<List<WasteResult>>() {
        @Override
        public void onResponse(Call<List<WasteResult>> call, Response<List<WasteResult>> response) {
            if (response.isSuccessful() && response.body() != null) {
                List<WasteResult> history = response.body();
                // Afficher l'historique dans la RecyclerView
                adapter.updateData(history);
            }
        }

        @Override
        public void onFailure(Call<List<WasteResult>> call, Throwable t) {
            // Gérer l'erreur réseau
        }
    });
```

### Association utilisateur-analyses

- **Table `users`** : Stocke les informations des utilisateurs
- **Table `waste_results`** : Stocke les analyses avec `user_id` (clé étrangère)
- **Relation** : Chaque analyse est liée à l'utilisateur qui l'a effectuée
- **Sécurité** : Seul l'utilisateur connecté peut voir ses propres analyses

## Gestion des sessions

### Vérifier si l'utilisateur est connecté

```java
boolean isAuthenticated = ExampleUsage.isUserAuthenticated(context);
```

### Déconnexion

```java
ExampleUsage.logoutUser(context);
```

## Endpoints API

### Authentification
- `POST /api/auth/signup` - Inscription
- `POST /api/auth/signin` - Connexion

### WasteResult
- `POST /api/waste/scan` - Scanner un déchet (classification automatique + sauvegarde)
- `POST /api/waste/save` - Sauvegarder un WasteResult complet
- `GET /api/waste/history` - Historique des scans de l'utilisateur connecté
- `GET /api/waste/{id}` - Détails d'un WasteResult

## Gestion des erreurs

### Erreurs courantes

1. **401 Unauthorized**: Token JWT invalide ou expiré
   - Solution: Reconnecter l'utilisateur

2. **403 Forbidden**: Accès refusé
   - Solution: Vérifier les permissions

3. **404 Not Found**: Ressource non trouvée
   - Solution: Vérifier l'URL et les paramètres

4. **500 Internal Server Error**: Erreur serveur
   - Solution: Vérifier les logs du serveur

### Gestion des erreurs réseau

```java
call.enqueue(new Callback<ResponseType>() {
    @Override
    public void onResponse(Call<ResponseType> call, Response<ResponseType> response) {
        if (response.isSuccessful()) {
            // Succès
        } else {
            // Erreur HTTP
            String errorMessage = "HTTP " + response.code();
            // Gérer l'erreur
        }
    }

    @Override
    public void onFailure(Call<ResponseType> call, Throwable t) {
        // Erreur réseau
        String errorMessage = "Network error: " + t.getMessage();
        // Gérer l'erreur
    }
});
```

## Exemple complet

Voir le fichier `ExampleUsage.java` pour un exemple complet d'utilisation de l'API.

## Notes importantes

1. **Sécurité**: Les tokens JWT sont automatiquement gérés par le système
2. **Base de données**: Les utilisateurs et WasteResult sont stockés en base MySQL
3. **CORS**: Le backend autorise toutes les origines pour le développement
4. **Taille des images**: Limite de 2MB pour les images uploadées
5. **Classification**: L'analyse locale fonctionne même sans connexion réseau
6. **Sauvegarde**: Les données sont automatiquement sauvegardées en base lors de l'analyse
7. **Historique**: Chaque utilisateur ne voit que ses propres analyses
8. **Performance**: L'historique est paginé pour optimiser les performances 