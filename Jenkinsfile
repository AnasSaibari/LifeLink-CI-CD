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
        GITHUB_CREDENTIALS = 'github-credentials'
        SOURCE_REPO = 'https://github.com/AnasSaibari/LifeLink-CI-CD.git'
        SOURCE_BRANCH = 'detached'
    }
    
    stages {
        stage('Checkout Code') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "${SOURCE_BRANCH}"]],
                    extensions: [],
                    userRemoteConfigs: [[
                        url: "${SOURCE_REPO}",
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
                        def serviceDir = (service == "annonceService") ? "${service}/${service}" : "backend/${service}"
                        
                        dir(serviceDir) {
                            stage("Build ${service}") {
                                bat "mvn clean verify"
                            }
                            
                            stage("SonarQube ${service}") {
                                withSonarQubeEnv("${SONARQUBE_ENV}") {
                                    // Version 1: Utiliser le goal complet
                                    bat """
                                    mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar ^
                                    -Dsonar.projectKey=${service} ^
                                    -Dsonar.projectName=${service}
                                    """
                                    
                                    // Alternative si ça ne fonctionne pas:
                                    // bat """
                                    // mvn sonar:sonar ^
                                    // -Dsonar.projectKey=${service} ^
                                    // -Dsonar.projectName=${service} ^
                                    // -Dsonar.host.url=http://localhost:9000 ^
                                    // -Dsonar.login=your_token
                                    // """
                                }
                            }
                        }
                    }
                }
            }
        }
        
        stage('Build & Push Docker Images') {
            when {
                expression { 
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
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
                        def serviceDir = (service == "annonceService") ? "${service}/${service}" : service
                        
                        dir(serviceDir) {
                            script {
                                // Vérifie si Dockerfile existe
                                if (fileExists('Dockerfile')) {
                                    echo "🐳 Building Docker image for ${service}"
                                    bat "docker build -t ${DOCKER_REGISTRY}/${service.toLowerCase()}:latest ."
                                    
                                    withDockerRegistry(
                                        credentialsId: 'dockerhub-creds',
                                        url: ''
                                    ) {
                                        bat "docker push ${DOCKER_REGISTRY}/${service.toLowerCase()}:latest"
                                    }
                                } else {
                                    echo "⚠️ Dockerfile not found in ${serviceDir}, skipping Docker build"
                                }
                            }
                        }
                    }
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            when {
                expression { 
                    (currentBuild.result == null || currentBuild.result == 'SUCCESS') && 
                    fileExists('k8s')
                }
            }
            steps {
                script {
                    def k8sServices = [
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
                    
                    k8sServices.each { service ->
                        def manifestFile = "k8s/${service.toLowerCase()}.yaml"
                        if (fileExists(manifestFile)) {
                            echo "🚀 Deploying ${service} to Kubernetes"
                            bat """
                            kubectl apply -n ${K8S_NAMESPACE} ^
                            -f ${manifestFile}
                            """
                        } else {
                            echo "⚠️ Kubernetes manifest not found: ${manifestFile}"
                        }
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
            script {
                echo "Dernière erreur: ${currentBuild.currentResult}"
                echo "Pour résoudre l'erreur SonarQube, vérifiez que:"
                echo "1. Le serveur SonarQube est accessible"
                echo "2. Le plugin est configuré dans les pom.xml OU"
                echo "3. Utilisez l'alternative ci-dessous"
            }
        }
        always {
            echo "📊 Build Status: ${currentBuild.currentResult}"
        }
    }
}
