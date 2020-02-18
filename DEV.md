# Note pour les développeurs

## Monorepo

Dans ce repository se trouve plusieurs projet de la shère living documentation.

### livingdoc-annotations

Le projet [livingdoc-annotations](livingdoc-annotations) contient des annotation java pour faciliter/préciser la génération de la documentation.

### livingdoc-maven-plugin

Le projet [livingdoc-maven-plugin](livingdoc-maven-plugin) contient le code pour la génération de diagramme depuis maven.

### livingdoc-examples

Le projet [livingdoc-examples](livingdoc-examples) divers exemples pour la génération d'un living documentation complète suivant différents uses cases.

### livingdoc-typescript-plugin

Le projet [livingdoc-typescript-plugin](livingdoc-typescript-plugin) librairie pour générer une living documentation depuis du code typescript.

## CI/CD

PR obligatoire.

Tout merge sur le master va entrainer un déploiement des artefacts maven sur le repository central.

Pour créer une version final, il faut ajouter le message 'maven release' au message de commit du merge sur le master.
