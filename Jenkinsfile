pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                bat '"C:\\Users\\iot-a2\\apache-maven-3.10.0\\bin\\mvn.cmd" clean test'
            }
        }

        stage('Package') {
            steps {
                bat '"C:\\Users\\iot-a2\\apache-maven-3.10.0\\bin\\mvn.cmd" package'
            }
        }
    }

    post {
        always {
            junit 'course-registration-app/target/surefire-reports/TEST-*.xml'
        }

        success {
            archiveArtifacts artifacts: 'course-registration-app/target/*.jar',
                             fingerprint: true
        }

        failure {
            echo 'Pipeline failed. Review the build output and test reports.'
        }
    }
}
