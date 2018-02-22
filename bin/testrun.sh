#!/bin/bash
# POSIX

# Credits
# - Based on soulrebel git-promote gist https://gist.github.com/soulrebel/9c47ee936cfce9dcb725
# - Parse arguments howto http://mywiki.wooledge.org/BashFAQ/035

testrun="$(basename "$0" | sed -e 's/-/ /')"
HELP="Usage: $testrun [options...]

Execute test within Zalenium.

Options:
  --testGroup                 # Test group to execute. Default \"develop\"
  --browser                   # Zalenium browser(s) wich will execute the test, default \"chrome+firefox\". Options:
                                \"chrome\"         Use default chrome browser
                                \"firefox\"        Use default firefox browser
                                \"chrome+firefox\" Use both chrome and firefox, doing two test runs one with each browser
  --gridEnvironment           # Grid environment to be used in the test. Defaults to \"local\". Options:
                                \"local\"          Use dockerized Zalenium grid environment
                                \"browserStack\"   Use Browser Stack environment
  --capabilities                # Browser stack capabilities to be used in BrowserStack environment. Default \"win\"
                                There are four targets configured in testlenium. You can add your own capabilities

                                Options:
                                \"android\"        Samsung Galaxy S8 device with Android 7.0 version
                                \"ios\"            iPhone 7 with iOS 10.3 version
                                \"win\"            Windows 10 with default Edge browser
                                \"osx\"            MacOS X with High Sierra release using Safari browser

Examples:
  $testrun --testGroup=smoke --broewser=chrome      # Execute \"smoke\" test group in chrome browser
  $testrun                                          # Execute \"develop\" test group in chrome and firefox browser
"

# Set default values
testGroup='develop'
browser='chrome+firefox'
gridEnvironment='local'
capabilities=''

# Get named parameters from system
while [ $# -gt 0 ]; do
  case "$1" in
    --testGroup=*)
      testGroup="${1#*=}"
      ;;
    --browser=*)
      browser="${1#*=}"
      ;;
    --gridEnvironment=*)
      gridEnvironment="${1#*=}"
      ;;
    --capabilities=*)
      capabilities="${1#*=}"
      ;;
    --h|\?|--help)
      echo "${HELP}"
      exit 0
      ;;
    *)
      printf "[ERROR] Invalid argument '$1 '\n"
      echo "${HELP}"
      exit 0
  esac
  shift
done

# Check parameters
if [ "${testGroup}" == "" ]
then
  echo "[ERORR]: you must specify --testGroup option"
  echo
  echo "${HELP}"
  exit 1
fi
if [ "${browser}" != "chrome" ] && [ "${browser}" != "firefox" ] && [ "${browser}" != "chrome+firefox" ]
then
  echo "[ERORR]: you must choose the browser with the right --browser option"
  echo "You should choose among 'chrome', 'firefox' and 'chrome+firefox', but you ar using '${browser}'"
  echo
  echo "${HELP}"
  exit 1
fi
if [ "${gridEnvironment}" != "local" ] && [ "${gridEnvironment}" != "browserStack" ]
then
  echo "[ERORR]: you must choose the grid environment with the right --gridEnvironment option"
  echo "You should choose between 'local' and 'browserStack', but you ar using '${gridEnvironment}'"
  echo
  echo "${HELP}"
  exit 1
fi
if [ "${gridEnvironment}" == "browserStack" ]
then
    if [ "${capabilities}" != "android" ] && [ "${capabilities}" != "ios" ] && [ "${capabilities}" != "win" ] && [ "${capabilities}" != "osx" ]
    then
        echo "[ERORR]: you must choose the Browser Stack capabilities with the right --capabilities option"
        echo "You should choose between 'android', 'ios', 'win' and 'osx', but you ar using '${capabilities}'"
        echo
        echo "${HELP}"
        exit 1
    fi
fi

# Functions
function runWithinDocker () {
    command=$1
    docker exec -t ${id} bash -c "${command}"
    returnValue=$((returnValue + $?))
}

function dockerCleanup () {
    docker stop ${docker_uuid} &> /dev/null || true
    docker rm -f ${id} ${docker_uuid} &> /dev/null || true
    docker network rm ${docker_uuid} &> /dev/null || true
}

cd $(dirname "$0")/..
bin/build.sh

timeoutSeconds=4000
returnValue=0

docker_uuid="testlenium_${testGroup}"
echo "###################################################"
echo "# Setup docker stuff with name ${docker_uuid}"
echo "# ---------"
echo "#"
grid_host=${docker_uuid}
grid_port=4444
docker run --rm -v `pwd`:/home ubuntu chown -R --reference=/home/Jenkinsfile /home # try to restore permissions
dockerCleanup                                                                      # try to clean environment from previous execution
rm -rf ci-scripts/reports/*
mkdir -p ci-scripts/reports/videos ci-scripts/reports/cucumber-extentsreport ci-scripts/reports/node-cucumber-report-generator
docker pull elgalu/selenium
docker network create ${docker_uuid}

echo -n "# Start zalenium as a container for the test run"
zalenium_id=$(docker run \
    -e docker_uuid=${docker_uuid} \
    --network ${docker_uuid} \
    --name ${docker_uuid} \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v `pwd`/ci-scripts/reports/videos:/home/seluser/videos \
    -P -d --rm --privileged dosel/zalenium start --desiredContainers 0)
returnValue=$((returnValue + $?))
echo " with id:${zalenium_id}, name:${docker_uuid}, host:${grid_host} and port:${grid_port}"
echo "Local docker info"
docker port ${docker_uuid}
sleep 10

#
echo -n "# Start test runner as a time-boxed daemon container, running for max ${timeoutSeconds} seconds"
id=$(docker run \
    --network ${docker_uuid} \
    --name ${docker_uuid}_test_runner \
    -v `pwd`:/home \
    -w /home \
    -v testlenium-cache-m2:/root/.m2 \
    -d --rm ci-scripts:testlenium sleep ${timeoutSeconds})
returnValue=$((returnValue + $?))
echo " with id ${id}"

runWithinDocker "mvn clean compile"
if [ "${browser}" == "chrome+firefox" ]
then
    echo "# Execute test with tag ${testGroup} and browser firefox."
    runWithinDocker "mvn test -Dcucumber.options=' --tags @${testGroup}' -Dbrowser_name='firefox' -Dgrid_host='${grid_host}' -Dgrid_port='${grid_port}' -Dgrid_environment='${gridEnvironment}' -Dtarget='${capabilities}'"

    echo "# Execute test with tag ${testGroup} and browser chrome."
    runWithinDocker "mvn test -Dcucumber.options=' --tags @${testGroup}' -Dbrowser_name='chrome' -Dgrid_host='${grid_host}' -Dgrid_port='${grid_port}' -Dgrid_environment='${gridEnvironment}' -Dtarget='${capabilities}'"
else
    echo "# Execute test with tag ${testGroup} and browser ${browser}."
    runWithinDocker "mvn test -Dcucumber.options=' --tags @${testGroup}' -Dbrowser_name='${browser}' -Dgrid_host='${grid_host}' -Dgrid_port='${grid_port}' -Dgrid_environment='${gridEnvironment}' -Dtarget='${capabilities}'"
fi

echo "# Waiting for videos"
docker exec ${docker_uuid} bash -c 'echo $docker_uuid > /tmp/docker_uuid'
docker exec ${docker_uuid} timeout ${timeoutSeconds} sudo bash -c 'while [ $(docker ps |grep $(cat /tmp/docker_uuid)|wc -l) -ne 2 ]
do
    sleep 5
    echo "Still waiting"
done
echo "Videos writed to disk"
sleep 5'
echo "# Restore permissions of the shared content"
runWithinDocker "chown -R --reference=bin/testrun.sh /home"

echo "# Stop jenkins daemon container"
dockerCleanup
returnValue=$((returnValue + $?))

echo "# Generate reports"
docker run --rm -v `pwd`:/workspace node:slim bash -c "cd /workspace/ci-scripts/node-cucumber-report-generator && npm install && PR=../reports/node-cucumber-report-generator/index.html JD=../reports/cucumber-extentsreport node index.js"

returnValue=$((returnValue + $?))


exit ${returnValue}
