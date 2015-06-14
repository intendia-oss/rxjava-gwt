if [ "$TRAVIS_REPO_SLUG" == "ibaca/rxjava-gwt" ] && \
   [ "$TRAVIS_JDK_VERSION" == "oraclejdk8" ] && \
   [ "$TRAVIS_PULL_REQUEST" == "false" ] && \
   [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "$GITHUB_KEY" > ~/.ssh/github.pem
  chmod 600 ~/.ssh/github.pem
  ssh-add ~/.ssh/github.pem

  git config --global user.email "rxjava-gwt@travis-ci.org"
  git config --global user.name "RxJava GWT at Travis CI"

  mvn -s ci/settings.xml site site:deploy
fi
