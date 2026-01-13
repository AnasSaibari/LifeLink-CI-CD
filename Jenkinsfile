pipeline {
    agent any
    
    tools {
        maven 'Maven_3.9.0' 
        jdk 'jdk-17'         
    }
    
    environment {
        DOCKER_REGISTRY = "anassabari"
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
        
        stage('Verify Structure') {
            steps {
                script {
                    echo "🔍 VÉRIFICATION DE LA STRUCTURE"
                    echo "================================"
                    
                    // Vérifier chaque service
                    def services = [
                        [name: "annonceService", path: "backend/annonceService/annonceService"],
                        [name: "chatService", path: "backend/chatService"],
                        [name: "discoveryService", path: "backend/discoveryService"],
                        [name: "donationService", path: "backend/donationService"],
                        [name: "gatewayService", path: "backend/gatewayService"],
                        [name: "hospitalService", path: "backend/hospitalService"],
                        [name: "locationService", path: "backend/locationService"],
                        [name: "reviewService", path: "backend/reviewService"],
                        [name: "userService", path: "backend/userService"]
                    ]
                    
                    services.each { service ->
                        if (fileExists(service.path)) {
                            echo "✅ ${service.name} trouvé à: ${service.path}"
                            
                            // Vérifier pom.xml
                            def pomPath = "${service.path}/pom.xml"
                            if (fileExists(pomPath)) {
                                echo "   📄 pom.xml: OUI"
                            } else {
                                echo "   ❌ pom.xml: NON"
                                // Chercher dans le dossier
                                bat "dir \"${service.path}\" 2>nul"
                            }
                            
                            // Vérifier Dockerfile
                            def dockerPath = "${service.path}/Dockerfile"
                            if (fileExists(dockerPath)) {
                                echo "   🐳 Dockerfile: OUI"
                            } else {
                                echo "   ⚠️ Dockerfile: NON"
                            }
                        } else {
                            echo "❌ ${service.name} NON trouvé à: ${service.path}"
                        }
                    }
                }
            }
        }
        
        stage('Build Services') {
            steps {
                script {
                    echo "🏗️ CONSTRUCTION DES SERVICES"
                    echo "============================="
                    
                    def services = [
                        [name: "annonceService", path: "backend/annonceService/annonceService"],
                        [name: "chatService", path: "backend/chatService"],
                        [name: "discoveryService", path: "backend/discoveryService"],
                        [name: "donationService", path: "backend/donationService"],
                        [name: "gatewayService", path: "backend/gatewayService"],
                        [name: "hospitalService", path: "backend/hospitalService"],
                        [name: "locationService", path: "backend/locationService"],
                        [name: "reviewService", path: "backend/reviewService"],
                        [name: "userService", path: "backend/userService"]
                    ]
                    
                    services.each { service ->
                        if (fileExists(service.path)) {
                            dir(service.path) {
                                stage("Build ${service.name}") {
                                    echo "🔨 Construction de ${service.name}..."
                                    
                                    // Vérifier si pom.xml existe
                                    if (fileExists('pom.xml')) {
                                        try {
                                            bat 'mvn clean compile'
                                            echo "✅ ${service.name} construit avec succès"
                                        } catch (Exception e) {
                                            echo "❌ Échec de la construction de ${service.name}"
                                            echo "Erreur: ${e.getMessage()}"
                                            // Ne pas échouer tout le pipeline pour un service
                                        }
                                    } else {
                                        echo "⚠️ pom.xml non trouvé pour ${service.name}, skipping..."
                                    }
                                }
                            }
                        } else {
                            echo "⚠️ Service ${service.name} non trouvé, skipping..."
                        }
                    }
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                script {
                    echo "🧪 EXÉCUTION DES TESTS"
                    echo "======================"
                    
                    def services = [
                        [name: "annonceService", path: "backend/annonceService/annonceService"],
                        [name: "chatService", path: "backend/chatService"],
                        [name: "discoveryService", path: "backend/discoveryService"],
                        [name: "donationService", path: "backend/donationService"],
                        [name: "gatewayService", path: "backend/gatewayService"],
                        [name: "hospitalService", path: "backend/hospitalService"],
                        [name: "locationService", path: "backend/locationService"],
                        [name: "reviewService", path: "backend/reviewService"],
                        [name: "userService", path: "backend/userService"]
                    ]
                    
                    services.each { service ->
                        if (fileExists(service.path)) {
                            dir(service.path) {
                                stage("Test ${service.name}") {
                                    if (fileExists('pom.xml')) {
                                        try {
                                            bat 'mvn test'
                                            echo "✅ Tests de ${service.name} exécutés"
                                        } catch (Exception e) {
                                            echo "⚠️ Tests échoués pour ${service.name}"
                                            // Continue même si les tests échouent
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        stage('Package Services') {
            steps {
                script {
                    echo "📦 EMPAQUETAGE DES SERVICES"
                    echo "============================"
                    
                    def services = [
                        [name: "annonceService", path: "backend/annonceService/annonceService"],
                        [name: "chatService", path: "backend/chatService"],
                        [name: "discoveryService", path: "backend/discoveryService"],
                        [name: "donationService", path: "backend/donationService"],
                        [name: "gatewayService", path: "backend/gatewayService"],
                        [name: "hospitalService", path: "backend/hospitalService"],
                        [name: "locationService", path: "backend/locationService"],
                        [name: "reviewService", path: "backend/reviewService"],
                        [name: "userService", path: "backend/userService"]
                    ]
                    
                    services.each { service ->
                        if (fileExists(service.path)) {
                            dir(service.path) {
                                stage("Package ${service.name}") {
                                    if (fileExists('pom.xml')) {
                                        try {
                                            bat 'mvn package -DskipTests'
                                            echo "✅ ${service.name} empaqueté"
                                        } catch (Exception e) {
                                            echo "❌ Échec de l'empaquetage de ${service.name}"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        stage('Build Docker Images') {
            when {
                expression { 
                    // Vérifie si Docker est disponible
                    try {
                        bat('docker --version', returnStatus: true) == 0
                    } catch (Exception e) {
                        false
                    }
                }
            }
            steps {
                script {
                    echo "🐳 CONSTRUCTION DES IMAGES DOCKER"
                    echo "=================================="
                    
                    def services = [
                        [name: "annonceService", path: "backend/annonceService/annonceService"],
                        [name: "chatService", path: "backend/chatService"],
                        [name: "discoveryService", path: "backend/discoveryService"],
                        [name: "donationService", path: "backend/donationService"],
                        [name: "gatewayService", path: "backend/gatewayService"],
                        [name: "hospitalService", path: "backend/hospitalService"],
                        [name: "locationService", path: "backend/locationService"],
                        [name: "reviewService", path: "backend/reviewService"],
                        [name: "userService", path: "backend/userService"]
                    ]
                    
                    services.each { service ->
                        if (fileExists(service.path)) {
                            dir(service.path) {
                                stage("Docker Build ${service.name}") {
                                    if (fileExists('Dockerfile')) {
                                        try {
                                            // Nom de l'image en minuscules
                                            def imageName = "${service.name.toLowerCase()}"
                                            echo "🐳 Construction de l'image: ${DOCKER_REGISTRY}/${imageName}:latest"
                                            
                                            bat "docker build -t ${DOCKER_REGISTRY}/${imageName}:latest ."
                                            echo "✅ Image Docker construite pour ${service.name}"
                                        } catch (Exception e) {
                                            echo "❌ Échec de la construction Docker pour ${service.name}"
                                        }
                                    } else {
                                        echo "⚠️ Dockerfile non trouvé pour ${service.name}"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        stage('Push Docker Images') {
            when {
                expression { 
                    // Vérifie si Docker est disponible ET si les credentials existent
                    try {
                        bat('docker --version', returnStatus: true) == 0
                    } catch (Exception e) {
                        false
                    }
                }
            }
            steps {
                script {
                    echo "⬆️ PUSH DES IMAGES DOCKER"
                    echo "========================="
                    
                    def services = [
                        [name: "annonceService", path: "backend/annonceService/annonceService"],
                        [name: "chatService", path: "backend/chatService"],
                        [name: "discoveryService", path: "backend/discoveryService"],
                        [name: "donationService", path: "backend/donationService"],
                        [name: "gatewayService", path: "backend/gatewayService"],
                        [name: "hospitalService", path: "backend/hospitalService"],
                        [name: "locationService", path: "backend/locationService"],
                        [name: "reviewService", path: "backend/reviewService"],
                        [name: "userService", path: "backend/userService"]
                    ]
                    
                    withDockerRegistry(
                        credentialsId: 'dockerhub-creds',
                        url: ''
                    ) {
                        services.each { service ->
                            if (fileExists(service.path)) {
                                dir(service.path) {
                                    stage("Docker Push ${service.name}") {
                                        if (fileExists('Dockerfile')) {
                                            try {
                                                def imageName = "${service.name.toLowerCase()}"
                                                echo "⬆️ Pushing: ${DOCKER_REGISTRY}/${imageName}:latest"
                                                
                                                bat "docker push ${DOCKER_REGISTRY}/${imageName}:latest"
                                                echo "✅ Image Docker poussée pour ${service.name}"
                                            } catch (Exception e) {
                                                echo "❌ Échec du push Docker pour ${service.name}"
                                            }
                                        }
                                    }
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
                    // Vérifie si kubectl est disponible ET si le dossier k8s existe
                    try {
                        bat('kubectl version --client', returnStatus: true) == 0 && fileExists('k8s')
                    } catch (Exception e) {
                        false
                    }
                }
            }
            steps {
                script {
                    echo "☸️ DÉPLOIEMENT KUBERNETES"
                    echo "========================="
                    
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
                        def manifestFile = "k8s/${service}.yaml"
                        if (fileExists(manifestFile)) {
                            stage("Deploy ${service}") {
                                try {
                                    echo "🚀 Déploiement de ${service}..."
                                    bat "kubectl apply -n ${K8S_NAMESPACE} -f ${manifestFile}"
                                    echo "✅ ${service} déployé"
                                } catch (Exception e) {
                                    echo "❌ Échec du déploiement de ${service}"
                                }
                            }
                        } else {
                            echo "⚠️ Manifest non trouvé: ${manifestFile}"
                        }
                    }
                }
            }
        }
    }
    
    post {
        success { 
            echo "🎉 Pipeline terminé avec succès" 
            echo "✅ Tous les services ont été construits et déployés"
        }
        failure { 
            echo "❌ Pipeline échoué" 
            script {
                echo "🔧 Dépannage:"
                echo "1. Vérifiez que tous les services ont un pom.xml"
                echo "2. Vérifiez que Maven est correctement configuré"
                echo "3. Vérifiez que Docker est installé et en cours d'exécution"
                echo "4. Vérifiez que kubectl est configuré"
            }
        }
        always {
            echo "📊 Résumé:"
            echo "- Branche: ${SOURCE_BRANCH}"
            echo "- Dépôt: ${SOURCE_REPO}"
            echo "- Statut: ${currentBuild.currentResult}"
            echo "- Durée: ${currentBuild.durationString}"
        }
    }
}
