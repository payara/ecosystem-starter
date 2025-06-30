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
                    sh '''echo *#*#*#*#*#*#*#*#*#*#*#*#  Building SRC  *#*#*#*#*#*#*#*#*#*#*#*#*#*#*'''
                    sh '''mvn -B -V -ff -e clean install --strict-checksums \
                        -Djavadoc.skip -Dsource.skip'''
                    sh '''echo *#*#*#*#*#*#*#*#*#*#*#*#    Built SRC   *#*#*#*#*#*#*#*#*#*#*#*#*#*#*
                    '''
                }
            }
        }
        stage('Test'){
            parallel {
                stage('Deploy Starter UI') {
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
                            echo $! > $WORKSPACE/payara.pid'''
                            }
                        }
                    }
                }
                stage('Test Starter UI') {
                    environment {
                        JAVA_HOME = tool("zulu-17")
                        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
                        MAVEN_OPTS = '-Xmx2G -Djavax.net.ssl.trustStore=${JAVA_HOME}/jre/lib/security/cacerts'
                        payaraBuildNumber = "${BUILD_NUMBER}"
                    }
                    steps {
                        script {                 
                            timeout(time: 5, unit: 'MINUTES') {
                                waitUntil(initialRecurrencePeriod: 10000){
                                    fileExists('payara.log') && readFile('payara.log').contains('Payara Micro URLs:')
                                }
                            }
                            sh '''echo *#*#*#*#*#*#*#*#*#*#*#*#  Testing Payara Starter  *#*#*#*#*#*#*#*#*#*#*#*#*#*#*'''
                            sh '''cd starter-ui'''
                            sh '''mvn verify -De2e -Dmaven.javadoc.skip=true -Pinstall-deps '''
                            }
                        }
                    post {
                        always {
                            sh 'while ps -p $(cat $WORKSPACE/payara.pid); do sudo kill -9 $(cat $WORKSPACE/payara.pid); sleep 5; done'
                        }
                    }
                }
            }
        }
    }
}
