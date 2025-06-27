#!groovy

pipeline {

    agent {
        label 'general-purpose'
    }
    tools {
        maven "maven-3.6.3"
    }
    stages {

        stage('Build Payara-Starter') {
            environment {
                JAVA_HOME = tool("zulu-17")
                PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
                MAVEN_OPTS = '-Xmx2G -Djavax.net.ssl.trustStore=${JAVA_HOME}/jre/lib/security/cacerts'
                payaraBuildNumber = "${BUILD_NUMBER}"
            }
            steps {
                script {
                    sh '''
                    echo *#*#*#*#*#*#*#*#*#*#*#*#  Building SRC  *#*#*#*#*#*#*#*#*#*#*#*#*#*#*
                    mvn -B -V -ff -e clean install --strict-checksums \
                        -Djavadoc.skip -Dsource.skip
                    echo *#*#*#*#*#*#*#*#*#*#*#*#    Built SRC   *#*#*#*#*#*#*#*#*#*#*#*#*#*#*
                    '''
                }
            }
        }
        stage('Test Payara Starter') {
            environment {
                JAVA_HOME = tool("zulu-17")
                PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
                MAVEN_OPTS = '-Xmx2G -Djavax.net.ssl.trustStore=${JAVA_HOME}/jre/lib/security/cacerts'
                payaraBuildNumber = "${BUILD_NUMBER}"
            }
            steps {
                script {
                    sh '''
                    echo *#*#*#*#*#*#*#*#*#*#*#*#  Deploying Payara Starter  *#*#*#*#*#*#*#*#*#*#*#*#*#*#*
                    mvn clean install payara-micro:start -f ./starter-ui/ \
                        -DcontextRoot="payara-starter" -DdeployWar=true
                    echo *#*#*#*#*#*#*#*#*#*#*#*#  Testing Payara Starter  *#*#*#*#*#*#*#*#*#*#*#*#*#*#*
                    mvn clean verify -f ./starter-ui/ -De2e
                    '''
                }
            }
        }
    }
}
