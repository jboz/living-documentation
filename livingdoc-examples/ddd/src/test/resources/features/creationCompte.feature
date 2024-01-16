# language: fr
Fonctionnalité: Création de compte

Scénario: On reçoit un événement de création de compte avec les infos de pièces fournies par un client, elles sont alors enregistrées
    Étant donné un client "Jean" créant un compte en fournissant 1 pièce d’identité
    Quand l'événement de création de compte est reçu
    Alors les informations concernant la pièce de "Jean" sont enregistrées
    Et "Jean" n’est donc plus sollicitable

