#!/bin/bash

docker run -it --rm \
    -v "$HOME/.m2":/root/.m2 \
    -v "$(pwd)":/usr/src/mymaven \
    -v "$(pwd)/target:/usr/src/mymaven/target" \
    -w /usr/src/mymaven \
    ifocusit/maven-graphviz \
    mvn $1
