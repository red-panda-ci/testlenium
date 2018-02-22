#!groovy

@Library('github.com/red-panda-ci/jenkins-pipeline-library@v2.6.1') _

// Initialize global config
cfg = jplConfig('testlenium', 'java' ,'', [email: 'redpandaci+testlenium@gmail.com'])

pipeline {
    agent none

    stages {
        stage ('Initialize') {
            agent { label 'docker' }
            steps  {
                jplStart(cfg)
            }
        }
        stage ('Build') {
            agent { label 'docker' }
            steps {
                sh "bin/build.sh"
            }
        }
        stage ('Test') {
            agent { label 'docker' }
            steps  {
                sh "bin/testrun.sh"
            }
            post {
                always {
                    step([$class: 'Publisher', reportFilenamePattern: '**/testng-results.xml'])

                    archiveArtifacts artifacts: 'ci-scripts/reports/videos/**/*', fingerprint: false
                    archiveArtifacts artifacts: 'ci-scripts/reports/cucumber-extentsreport/**/*', fingerprint: false
                    publishHTML (target: [
                            allowMissing: false,
                            alwaysLinkToLastBuild: false,
                            keepAll: true,
                            reportDir: 'ci-scripts/reports/videos/',
                            reportFiles: 'dashboard.html',
                            reportName: "TestResultsReport-Zalenium"
                    ])
                    publishHTML (target: [
                            allowMissing: false,
                            alwaysLinkToLastBuild: false,
                            keepAll: true,
                            reportDir: 'ci-scripts/reports/node-cucumber-report-generator',
                            reportFiles: '*.html',
                            reportName: "TestResultsReport-Cucumber"
                    ])
                }
            }
        }
        stage ('Release confirm') {
            when { expression { env.BRANCH_NAME.startsWith('release/v') || env.BRANCH_NAME.startsWith('hotfix/v') } }
            steps {
                jplPromoteBuild(cfg)
            }
        }
        stage ('Release finish') {
            agent { label 'docker' }
            when { expression { env.BRANCH_NAME.startsWith('release/v') || env.BRANCH_NAME.startsWith('hotfix/v') } }
            steps {
                jplCloseRelease(cfg)
            }
        }
    }

    post {
        always {
            jplPostBuild(cfg)
        }
    }

    options {
        timestamps()
        ansiColor('xterm')
        buildDiscarder(logRotator(artifactNumToKeepStr: '5',artifactDaysToKeepStr: '14'))
        disableConcurrentBuilds()
        skipDefaultCheckout()
        timeout(time: 1, unit: 'DAYS')
    }
}
