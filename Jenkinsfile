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
                    echo *#*#*#*#*#*#*#*#*#*#*#*#  Building SRC  *#*#*#*#*#*#*#*#*#*#*#*#*#*#*'''
                    sh '''mvn -B -V -ff -e clean install --strict-checksums \
                        -Djavadoc.skip -Dsource.skip'''
                    sh '''echo *#*#*#*#*#*#*#*#*#*#*#*#    Built SRC   *#*#*#*#*#*#*#*#*#*#*#*#*#*#*
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
                withCredentials([string(credentialsId: 'open-ai-payara-starter-token', variable: 'PAYARA_TOKEN')]) {
                script {
                    sh '''echo *#*#*#*#*#*#*#*#*#*#*#*#  Add OPEN_API_KEY to microprofile-config.properties  *#*#*#*#*#*#*#*#*#*#*#*#*#*#*'''
                    sh '''echo "\n OPEN_API_KEY=$PAYARA_TOKEN" >> starter-ui/src/main/resources/META-INF/microprofile-config.properties'''

                    sh '''echo *#*#*#*#*#*#*#*#*#*#*#*#  Deploying Payara Starter  *#*#*#*#*#*#*#*#*#*#*#*#*#*#*'''
                    sh '''mvn clean install payara-micro:start -f ./starter-ui/ \
                        -DcontextRoot="payara-starter" -DdeployWar=true > payara.log 2>&1 &
                    echo $! > payara.pid'''
                    sh '''echo *#*#*#*#*#*#*#*#*#*#*#*#  Testing Payara Starter  *#*#*#*#*#*#*#*#*#*#*#*#*#*#*'''
                    sh '''sleep 150'''
                    sh '''cd starter-ui'''
                    sh '''mvn verify -De2e -Dmaven.javadoc.skip=true -Pinstall-deps '''
                    sh '''echo *#*#*#*#*#*#*#*#*#*#*#*#  Stopping Payara Micro  *#*#*#*#*#*#*#*#*#*#*#*#*#*#*'''
                    sh 'kill $(cat payara.pid)'
                    }
                }
            }
        }
    }
}
