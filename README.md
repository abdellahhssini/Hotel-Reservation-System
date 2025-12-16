# Hotel-Reservation-System
Hotel Reservation System – Technical Test Skypay
1) Supposons que toutes les fonctions soient regroupées dans un seul service. Est-ce une approche recommandée ? Expliquez.
Non, ce n’est pas une approche recommandée sur le plan de la conception logicielle.
Regrouper toutes les responsabilités dans un seul service viole plusieurs principes fondamentaux de l’ingénierie logicielle, notamment :

Principe de responsabilité unique (SRP – SOLID)
Un service ne devrait avoir qu’une seule responsabilité. Ici, le service gère à la fois :
- les utilisateurs
- les chambres
- les réservations
- la logique métier
- l’affichage

Faible maintenabilité
Toute modification (ex. évolution des règles de réservation) risque d’impacter d’autres fonctionnalités non liées.

Faible testabilité
Tester un service monolithique devient plus complexe car les dépendances sont fortement couplées.

Manque d’évolutivité
Si le système évolue (paiement, annulation, promotions, etc.), ce service deviendra rapidement trop volumineux.

Approche recommandée
Séparer les responsabilités en plusieurs services, par exemple :
UserService : gestion des utilisateurs
RoomService : gestion des chambres
BookingService : gestion des réservations
Cela améliore la lisibilité, la testabilité et la maintenabilité du code.


2) Dans ce design, setRoom(..) ne doit pas impacter les réservations existantes. Quelle est une autre approche possible ? Quelle est votre recommandation ?

Une autre approche consiste à versionner l’état des chambres au moment de la réservation.

Approche alternative
Lorsqu’une réservation est créée :
- copier les informations de la chambre (type, prix) dans l’entité Booking
Ainsi, même si la chambre est modifiée ultérieurement via setRoom(...), les réservations passées conservent leurs données d’origine.

Avantages
- Les réservations deviennent immutables et historiquement fiables
- Aucun effet de bord lors de la mise à jour d’une chambre
- Meilleure traçabilité et cohérence métier

Recommandation
Je recommande cette approche car :
- elle respecte le principe d’immutabilité des données métier critiques
- elle évite des incohérences financières (changement de prix après réservation)
- elle reflète un comportement réel des systèmes de réservation professionnels

👉 En résumé :
Les entités Booking doivent être autonomes et ne pas dépendre de l’état courant des chambres.
