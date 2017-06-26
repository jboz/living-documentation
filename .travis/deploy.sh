#!/bin/bash

echo "running deployment script..."

function setReleaseVersion {
    echo "set $1 to release version"
    if ! mvn -f $1/pom.xml -q build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion} versions:commit; then
        echo "set release version failed"
        exit 1
    fi
}

function setNextDevVersion {
    echo "set $1 to next development version"
    if ! mvn -f $1/pom.xml -q build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.nextMinorVersion}-SNAPSHOT versions:commit; then
        echo "set next development version failed"
        exit 1
    fi
}

function deploy {
    echo "deploying version $PROJECT_VERSION of $1 to maven centrale..."
    if ! mvn -f $1/pom.xml deploy --settings $GPG_DIR/settings.xml -Prelease -DskipTests=true -B;then
        echo "maven deploy failed"
        exit 1
    fi
}

# Pull requests and commits to other branches shouldn't try to deploy, just build to verify
if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_BRANCH" != "master" ]; then
    echo "Skipping deploy; just doing a build."
    exit 0
fi

# reconnect master to origin
git checkout master

MAKE_RELEASE='false'

if [[ "${TRAVIS_COMMIT_MESSAGE}" =~ ^make\ release ]]; then
    echo "commit message indicate that a release must be create"
    MAKE_RELEASE='true'
fi

if [ "$MAKE_RELEASE" = 'true' ]; then
    # upgrade poms to final release version
    setReleaseVersion .
    setReleaseVersion livingdoc-annotations
    setReleaseVersion livingdoc-maven-plugin
else
    echo "keep snapshot version in pom.xml"
fi

echo "reading project version..."
PROJECT_VERSION=`mvn -q exec:exec -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive`

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" = 'false' ]; then
    # deploy this 2 artifacts only
    deploy livingdoc-annotations
    deploy livingdoc-maven-plugin
fi

if [ "$MAKE_RELEASE" = 'true' ]; then
    git config user.name "Travis CI"
    git config user.email "travis-ci@ifocusit.ch"
    GIT_TAG=v$PROJECT_VERSION
    echo "create git tag $GIT_TAG"
    git tag "$GIT_TAG" -a -m "Generated tag from TravisCI for build $TRAVIS_BUILD_NUMBER"

    git config --global push.followTags true # push commit and tag

    echo "preparing next version..."
    setNextDevVersion .
    setNextDevVersion livingdoc-annotations
    setNextDevVersion livingdoc-maven-plugin

    NEXT_VERSION=`mvn -q exec:exec -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive`
    echo "next development version will be $NEXT_VERSION"

    # Commit the "changes", i.e. the new version.
    git add -A .
    git commit -m "set next development version to $NEXT_VERSION"

    REPO=`git config remote.origin.url`
    echo "pushing new development version..."
    git push "https://$GITHUB_TOKEN@github.com/$TRAVIS_REPO_SLUG.git" --follow-tags

    echo "release $PROJECT_VERSION done, tag $GIT_TAG pushed, next development version $NEXT_VERSION setted"
fi
