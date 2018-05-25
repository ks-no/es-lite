pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'openjdk8'
    }
    parameters {
        booleanParam(defaultValue: false, description: 'Skal prosjektet releases?', name: 'isRelease')
        string(name: "releaseVersion", defaultValue: "", description: "Hva er det nye versjonsnummeret?")
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

        stage('Build') {
            steps {
                sh 'mvn -U -B clean install'
            }
        }

        stage('Release: new version') {
            when {
                expression { params.isRelease }
            }

            steps {
                gitCheckout()
                prepareRelease params.releaseVersion
            }
        }

        stage('Deploy artifacts') {
            steps {
                sh 'mvn -U -B -Dmaven.install.skip=true deploy'
            }
        }

        stage('Release: set snapshot') {
            when {
                expression { params.isRelease }
            }

            steps {
                setSnapshot params.releaseVersion
                gitPush()
            }
        }

    }
}
