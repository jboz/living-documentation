project?=$(pwd)
goals?=clean package
mvnRepository?=$(HOME)/.m2

build:
	docker build -t jboz/maven-graphviz .

run:
	docker run -it --rm \
    -v $(mvnRepository):/root/.m2 \
    -v $(project):/usr/src/mymaven \
    -w /usr/src/mymaven \
    jboz/maven-graphviz \
    mvn $(goals)

push:
	docker push jboz/maven-graphviz
