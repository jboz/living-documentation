#!/bin/bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    mvn deploy -f livingdoc-annotations/pom.xml --settings $GPG_DIR/settings.xml -DperformRelease=true -DskipTests=true
    mvn deploy -f livingdoc-maven-plugin/pom.xml --settings $GPG_DIR/settings.xml -DperformRelease=true -DskipTests=true
    exit $?
fi