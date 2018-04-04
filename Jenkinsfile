pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'openjdk8'
    }
    stages {
        stage('Initialize') {
            steps {
                sh '''
                     echo "PATH = ${PATH}"
                     echo "M2_HOME = ${M2_HOME}"
                 '''
            }
        }

        stage('Build and deploy') {
            steps {
                sh 'mvn deploy'
            }
        }

    }
}
