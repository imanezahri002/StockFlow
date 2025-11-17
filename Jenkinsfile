pipeline {
  agent any

  tools {
    jdk 'JDK-17'
    maven 'maven'
  }

  environment {
    MAVEN_OPTS = "-Xmx1g"
  }

  options {
    timestamps()
    ansiColor('xterm')
    timeout(time: 60, unit: 'MINUTES')
    buildDiscarder(logRotator(numToKeepStr: '25'))
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
        sh 'ls -la'
        sh 'chmod +x mvnw'
      }
    }

    stage('Build (compile)') {
      steps {
        sh "./mvnw -B -DskipTests=true clean compile"
      }
    }

    stage('Unit Tests') {
      steps {
        sh "./mvnw -B test"
      }
    }

    stage('JaCoCo Report') {
      steps {
        sh "./mvnw -B jacoco:report"
      }
    }

    stage('SonarQube Analysis') {
      steps {
        script {
          withSonarQubeEnv('smartSupply') {
            sh """
              ./mvnw sonar:sonar \
                -Dsonar.projectKey=smartSupply \
                -Dsonar.host.url=http://localhost:9000 \
                -Dsonar.token=squ_0d1dc894e83a4919dc02974e65ee5e051c60c9ea
            """
          }
        }
      }
    }

    stage('Package') {
      steps {
        sh "./mvnw -B -DskipTests=true package"
      }
    }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
      archiveArtifacts artifacts: 'target/*.jar, target/site/jacoco/**', allowEmptyArchive: true
      cleanWs()
    }

    success {
      echo "Build succeeded: ${env.BUILD_URL}"
    }

    failure {
      echo "Build failed: ${env.BUILD_URL}"
    }
  }
}
