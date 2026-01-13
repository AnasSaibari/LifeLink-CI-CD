pipeline {
    agent any
    
    tools {
        maven 'Maven_3.9.0' 
        jdk 'jdk-17'         
    }
    
    environment {
        DOCKER_REGISTRY = "anassabari"
        SONARQUBE_ENV = "SonarQube"
        K8S_NAMESPACE = "default"
        GITHUB_CREDENTIALS = 'github-credentials' // À configurer dans Jenkins
        SOURCE_REPO = 'https://github.com/AnasSaibari/LifeLink-CI-CD.git'
        SOURCE_BRANCH = 'detached'
    }
    
    stages {
        stage('Checkout Code') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "${SOURCE_BRANCH}"]], // Utilisez la variable
                    extensions: [],
                    userRemoteConfigs: [[
                        url: "${SOURCE_REPO}", // Utilisez la variable
                        credentialsId: "${GITHUB_CREDENTIALS}"
                    ]]
                ])
            }
        }
        
        stage('Build, Test & SonarQube') {
            steps {
                script {
                    def services = [
                        "annonceService",
                        "chatService", 
                        "discoveryService",
                        "donationService",
                        "gatewayService",
                        "hospitalService",
                        "locationService",
                        "reviewService",
                        "userService"
                    ]
                    
                    services.each { service ->
                        dir("backend/${service}") {
                            stage("Build ${service}") {
                                // Utiliser bat pour Windows au lieu de sh
                                bat "mvn clean verify"
                            }
                            
                            stage("SonarQube ${service}") {
                                withSonarQubeEnv("${SONARQUBE_ENV}") {
                                    // Utiliser ^ pour les continuations de ligne sur Windows
                                    bat """
                                    mvn sonar:sonar ^
                                    -Dsonar.projectKey=${service} ^
                                    -Dsonar.projectName=${service}
                                    """
                                }
                            }
                        }
                    }
                }
            }
        }
        
        stage('Build & Push Docker Images') {
            steps {
                script {
                    def services = [
                        "annonceService",
                        "chatService",
                        "discoveryService",
                        "donationService",
                        "gatewayService",
                        "hospitalService",
                        "locationService",
                        "reviewService",
                        "userService"
                    ]
                    
                    services.each { service ->
                        def serviceDir = "${service}"
                        
                        // Cas particulier pour annonceService
                        if (service == "annonceService") {
                            serviceDir = "${service}/${service}"
                        }
                        
                        dir(serviceDir) {
                            // Utiliser bat pour Windows
                            bat """
                            docker build -t ${DOCKER_REGISTRY}/${service.toLowerCase()}:latest .
                            """
                            
                            withDockerRegistry(
                                credentialsId: 'dockerhub-creds',
                                url: ''
                            ) {
                                bat "docker push ${DOCKER_REGISTRY}/${service.toLowerCase()}:latest"
                            }
                        }
                    }
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                script {
                    def services = [
                        "annonce-donation-services",
                        "annonce-service",
                        "chat-service",
                        "discovery-service",
                        "donation-service",
                        "gateway-service",
                        "hospital-service",
                        "location-service",
                        "review-service",
                        "user-service"
                    ]
                    
                    services.each { service ->
                        // Utiliser bat pour Windows
                        bat """
                        kubectl apply -n ${K8S_NAMESPACE} ^
                        -f k8s/${service.toLowerCase()}.yaml
                        """
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo "🎉 Pipeline terminé avec succès"
        }
        failure {
            echo "❌ Pipeline échoué"
        }
    }
}
