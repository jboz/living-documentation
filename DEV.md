# Note pour les développeurs

## Monorepo

Dans ce repository se trouve plusieurs projets de la sphère living documentation.

### livingdoc-annotations

Le projet [livingdoc-annotations](livingdoc-annotations) contient des annotations java pour faciliter/préciser la génération de la documentation.

### livingdoc-maven-plugin

Le projet [livingdoc-maven-plugin](livingdoc-maven-plugin) contient le code pour la génération de la documentation depuis maven.

### livingdoc-examples

Le projet [livingdoc-examples](livingdoc-examples) divers exemples pour la génération d'un living documentation complète suivant différents uses cases.

### livingdoc-typescript-plugin

Le projet [livingdoc-typescript-plugin](livingdoc-typescript-plugin) librairie pour générer une living documentation depuis du code typescript.

## CI/CD

PR obligatoire.

## Tips

### generate image from html

copy svg content into generated html

```bash
yay -S wkhtmltox-bin
cd sources/living-documentation/livingdoc-examples/microservice
wkhtmltoimage --format png http://localhost:63342/living-documentation/livingdoc-examples/microservice/target/docs/index.html images/example_microservice_index.html.png
```
