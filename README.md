# Testlenium

Automated testing with Selenium through Zalenium using an ephemeral Docker platform

[![Build Status](http://jenkins.redpandaci.com/buildStatus/icon?job=red-panda-ci/testlenium/develop)](https://jenkins.redpandaci.com/job/red-panda-ci/job/testlenium/job/develop/) [![Join the chat at https://gitter.im/red-panda-ci/testlenium](https://badges.gitter.im/red-panda-ci/testlenium.svg)](https://gitter.im/red-panda-ci/testlenium?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Content #
* [Getting Started](#getting started)
   * [Set up the enviroment](#Set up the enviroment)
   * [Run Test](#Run Test)
    
## Getting Started 
### Set up the enviroment
>[Java version 1.8 or higher.] 
>[Selenium server stand alone]  
>[Docker]  
>[Chrome driver]  
>[Firefox driver]   
    
### Run Test
From comand line in your local machine  
>You can set those properties:  
>>[test]: to set the specific branch to run the test. 
>>[browser]: chrome and firefox are the abailable options. 
>>[videoLog]: to enable or disable logs in the zalenium video, true or false. 
                
There 2 ways to run the test:   

    In your local machine, selenium server standalone is needed. 
        mvn test -Dcucumber.options= [route to the especific feature].  
    For instance: 
        mvn test -Dcucumber.options=src/test/resources/features/OpenGooglePage.feature    
    
    mvn test -Dcucumber.options= "[ --tags @YourTag]" 
        For instance: 
            mvn test -Dbrowser_name="firefox" -Dvideo_log=true -Dcucumber.options=" --tags @develop" 
            
    Run test with docker 
    1. Run ./bin/build.sh to build the docker image.  
    2. Run ./bin/test.sh [Can add an epecific tag] [Browser chorme or firefox] [videoLog true or false]. 
        For instance: 
            ./bin/test.sh test firefox true 
        If you don't add a tag the test will run on develop branch.  
                         
### Report
There are 2 automated reports, generated with every tests execution.
 
 *   Zalenium dashboard a summary report with the execution videos, located in ci-scripts/reports/videos/dashboard.html from you project, 
    for more information visit [Zalenium](https://github.com/zalando/zalenium)
    
 *   NodeJs report, a summary report with the scenarios and Features results, located ci-scripts/reports/node-cucumber-report-generator/index.html, 
    more information in [RedPandaCi](https://github.com/red-panda-ci/node-cucumber-report-generator)
    

