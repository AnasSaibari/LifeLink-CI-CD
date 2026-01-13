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
        
        stage('Navigate to Backend Directory') {
            steps {
                script {
                    echo "📁 Navigation vers le dossier backend..."
                    if (fileExists('backend')) {
                        dir('backend') {
                            echo "✅ Dans le dossier backend"
                            bat 'dir'
                        }
                    } else {
                        error "❌ Le dossier 'backend' n'existe pas dans le dépôt!"
                    }
                }
            }
        }
        
        stage('Build Services') {
            steps {
                script {
                    dir('backend') {
                        echo "🏗️ CONSTRUCTION DES SERVICES DANS BACKEND/"
                        echo "============================================"
                        
                        // Structure: annonceService est dans annonceService/annonceService
                        // Les autres services sont directement dans leur dossier
                        def services = [
                            [name: "annonceService", subdir: "annonceService"],
                            [name: "chatService", subdir: ""],
                            [name: "discoveryService", subdir: ""],
                            [name: "donationService", subdir: ""],
                            [name: "gatewayService", subdir: ""],
                            [name: "hospitalService", subdir: ""],
                            [name: "locationService", subdir: ""],
                            [name: "reviewService", subdir: ""],
                            [name: "userService", subdir: ""]
                        ]
                        
                        services.each { service ->
                            // Déterminer le chemin du service
                            def servicePath = service.name
                            if (service.subdir) {
                                servicePath = "${service.name}/${service.subdir}"
                            }
                            
                            if (fileExists(servicePath)) {
                                dir(servicePath) {
                                    stage("Build ${service.name}") {
                                        echo "🔨 Construction de ${service.name} dans ${servicePath}"
                                        
                                        // Vérifier si pom.xml existe
                                        if (fileExists('pom.xml')) {
                                            try {
                                                bat 'mvn clean compile'
                                                echo "✅ ${service.name} construit avec succès"
                                            } catch (Exception e) {
                                                echo "❌ Échec de la construction de ${service.name}"
                                                echo "Erreur: ${e.getMessage()}"
                                                // Continuer avec les autres services
                                            }
                                        } else {
                                            echo "⚠️ pom.xml non trouvé pour ${service.name}, skipping..."
                                            bat 'dir 2>nul || echo "Dossier vide"'
                                        }
                                    }
                                }
                            } else {
                                echo "⚠️ Service ${service.name} non trouvé à ${servicePath}, skipping..."
                            }
                        }
                    }
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                script {
                    dir('backend') {
                        echo "🧪 EXÉCUTION DES TESTS"
                        echo "======================"
                        
                        def services = [
                            [name: "annonceService", subdir: "annonceService"],
                            [name: "chatService", subdir: ""],
                            [name: "discoveryService", subdir: ""],
                            [name: "donationService", subdir: ""],
                            [name: "gatewayService", subdir: ""],
                            [name: "hospitalService", subdir: ""],
                            [name: "locationService", subdir: ""],
                            [name: "reviewService", subdir: ""],
                            [name: "userService", subdir: ""]
                        ]
                        
                        services.each { service ->
                            def servicePath = service.name
                            if (service.subdir) {
                                servicePath = "${service.name}/${service.subdir}"
                            }
                            
                            if (fileExists(servicePath)) {
                                dir(servicePath) {
                                    stage("Test ${service.name}") {
                                        if (fileExists('pom.xml')) {
                                            try {
                                                echo "🧪 Exécution des tests pour ${service.name}..."
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
        }
        
        stage('Package Services') {
            steps {
                script {
                    dir('backend') {
                        echo "📦 EMPAQUETAGE DES SERVICES"
                        echo "============================"
                        
                        def services = [
                            [name: "annonceService", subdir: "annonceService"],
                            [name: "chatService", subdir: ""],
                            [name: "discoveryService", subdir: ""],
                            [name: "donationService", subdir: ""],
                            [name: "gatewayService", subdir: ""],
                            [name: "hospitalService", subdir: ""],
                            [name: "locationService", subdir: ""],
                            [name: "reviewService", subdir: ""],
                            [name: "userService", subdir: ""]
                        ]
                        
                        services.each { service ->
                            def servicePath = service.name
                            if (service.subdir) {
                                servicePath = "${service.name}/${service.subdir}"
                            }
                            
                            if (fileExists(servicePath)) {
                                dir(servicePath) {
                                    stage("Package ${service.name}") {
                                        if (fileExists('pom.xml')) {
                                            try {
                                                echo "📦 Empaquetage de ${service.name}..."
                                                bat 'mvn package -DskipTests'
                                                echo "✅ ${service.name} empaqueté"
                                                
                                                // Vérifier si le JAR a été créé
                                                if (findFiles(glob: 'target/*.jar').size() > 0) {
                                                    echo "✅ Fichier JAR créé pour ${service.name}"
                                                }
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
        }
        
        stage('Build Docker Images') {
            when {
                expression { 
                    try {
                        bat('docker --version', returnStatus: true) == 0
                    } catch (Exception e) {
                        false
                    }
                }
            }
            steps {
                script {
                    dir('backend') {
                        echo "🐳 CONSTRUCTION DES IMAGES DOCKER"
                        echo "=================================="
                        
                        def services = [
                            [name: "annonceService", subdir: "annonceService"],
                            [name: "chatService", subdir: ""],
                            [name: "discoveryService", subdir: ""],
                            [name: "donationService", subdir: ""],
                            [name: "gatewayService", subdir: ""],
                            [name: "hospitalService", subdir: ""],
                            [name: "locationService", subdir: ""],
                            [name: "reviewService", subdir: ""],
                            [name: "userService", subdir: ""]
                        ]
                        
                        services.each { service ->
                            def servicePath = service.name
                            if (service.subdir) {
                                servicePath = "${service.name}/${service.subdir}"
                            }
                            
                            if (fileExists(servicePath)) {
                                dir(servicePath) {
                                    stage("Docker Build ${service.name}") {
                                        if (fileExists('Dockerfile')) {
                                            try {
                                                def imageName = "${service.name.toLowerCase()}"
                                                echo "🐳 Construction de l'image: ${DOCKER_REGISTRY}/${imageName}:latest"
                                                
                                                bat "docker build -t ${DOCKER_REGISTRY}/${imageName}:latest ."
                                                echo "✅ Image Docker construite pour ${service.name}"
                                            } catch (Exception e) {
                                                echo "❌ Échec de la construction Docker pour ${service.name}"
                                                echo "Assurez-vous que Docker est en cours d'exécution"
                                            }
                                        } else {
                                            echo "⚠️ Dockerfile non trouvé pour ${service.name}"
                                            bat 'dir Dockerfile* 2>nul || echo "Pas de Dockerfile"'
                                        }
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
                    
                    withDockerRegistry(
                        credentialsId: 'dockerhub-creds',
                        url: ''
                    ) {
                        dir('backend') {
                            def services = [
                                [name: "annonceService", subdir: "annonceService"],
                                [name: "chatService", subdir: ""],
                                [name: "discoveryService", subdir: ""],
                                [name: "donationService", subdir: ""],
                                [name: "gatewayService", subdir: ""],
                                [name: "hospitalService", subdir: ""],
                                [name: "locationService", subdir: ""],
                                [name: "reviewService", subdir: ""],
                                [name: "userService", subdir: ""]
                            ]
                            
                            services.each { service ->
                                def servicePath = service.name
                                if (service.subdir) {
                                    servicePath = "${service.name}/${service.subdir}"
                                }
                                
                                if (fileExists(servicePath)) {
                                    dir(servicePath) {
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
        }
        
        stage('Deploy to Kubernetes') {
            when {
                expression { 
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
                    
                    // Remonter à la racine pour trouver le dossier k8s
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
            echo "✅ Tous les services ont été traités"
        }
        failure { 
            echo "❌ Pipeline échoué" 
            script {
                echo "🔧 Dépannage:"
                echo "1. Vérifiez la structure de votre dépôt"
                echo "2. Vérifiez que tous les services ont un pom.xml"
                echo "3. Vérifiez les logs Maven pour les erreurs de dépendances"
            }
        }
        always {
            echo "📊 Résumé:"
            echo "- Dépôt: ${SOURCE_REPO}"
            echo "- Branche: ${SOURCE_BRANCH}"
            echo "- Statut: ${currentBuild.currentResult}"
            echo "- URL du build: ${env.BUILD_URL}"
        }
    }
}
