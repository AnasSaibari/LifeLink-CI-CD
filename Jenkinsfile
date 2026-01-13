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
        
        stage('Verify Repository Structure') {
            steps {
                script {
                    echo "📂 Vérification de la structure du dépôt..."
                    bat 'dir'
                    
                    // Vérifier si les services sont à la racine ou dans backend/
                    def hasBackendDir = fileExists('backend')
                    echo "Backend directory exists: ${hasBackendDir}"
                    
                    if (!hasBackendDir) {
                        echo "⚠️ ATTENTION: Le dossier 'backend' n'existe pas!"
                        echo "Les services semblent être à la racine du dépôt"
                    }
                }
            }
        }
        
        stage('Build Services') {
            steps {
                script {
                    echo "🏗️ CONSTRUCTION DES SERVICES"
                    echo "============================="
                    
                    // Services à la racine selon votre screenshot GitHub
                    def services = [
                        "annonceService/annonceService",
                        "chatService",
                        "discoveryService",
                        "donationService",
                        "gatewayService",
                        "hospitalService",
                        "locationService",
                        "reviewService",
                        "userService"
                    ]
                    
                    services.each { servicePath ->
                        def serviceName = servicePath.tokenize('/').last()
                        
                        if (fileExists(servicePath)) {
                            dir(servicePath) {
                                stage("Build ${serviceName}") {
                                    echo "🔨 Construction de ${serviceName}"
                                    
                                    if (fileExists('pom.xml')) {
                                        try {
                                            bat 'mvn clean compile'
                                            echo "✅ ${serviceName} construit avec succès"
                                        } catch (Exception e) {
                                            echo "❌ Échec: ${e.getMessage()}"
                                            currentBuild.result = 'UNSTABLE'
                                        }
                                    } else {
                                        echo "⚠️ pom.xml non trouvé pour ${serviceName}"
                                    }
                                }
                            }
                        } else {
                            echo "⚠️ ${servicePath} non trouvé"
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
                        "annonceService/annonceService",
                        "chatService",
                        "discoveryService",
                        "donationService",
                        "gatewayService",
                        "hospitalService",
                        "locationService",
                        "reviewService",
                        "userService"
                    ]
                    
                    services.each { servicePath ->
                        def serviceName = servicePath.tokenize('/').last()
                        
                        if (fileExists(servicePath)) {
                            dir(servicePath) {
                                stage("Test ${serviceName}") {
                                    if (fileExists('pom.xml')) {
                                        try {
                                            bat 'mvn test'
                                            echo "✅ Tests de ${serviceName} réussis"
                                        } catch (Exception e) {
                                            echo "⚠️ Tests échoués pour ${serviceName}"
                                            currentBuild.result = 'UNSTABLE'
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
                        "annonceService/annonceService",
                        "chatService",
                        "discoveryService",
                        "donationService",
                        "gatewayService",
                        "hospitalService",
                        "locationService",
                        "reviewService",
                        "userService"
                    ]
                    
                    services.each { servicePath ->
                        def serviceName = servicePath.tokenize('/').last()
                        
                        if (fileExists(servicePath)) {
                            dir(servicePath) {
                                stage("Package ${serviceName}") {
                                    if (fileExists('pom.xml')) {
                                        try {
                                            bat 'mvn package -DskipTests'
                                            echo "✅ ${serviceName} empaqueté"
                                        } catch (Exception e) {
                                            echo "❌ Échec: ${e.getMessage()}"
                                            currentBuild.result = 'UNSTABLE'
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
                        bat(script: 'docker --version', returnStatus: true) == 0
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
                        [path: "annonceService/annonceService", name: "annonceservice"],
                        [path: "chatService", name: "chatservice"],
                        [path: "discoveryService", name: "discoveryservice"],
                        [path: "donationService", name: "donationservice"],
                        [path: "gatewayService", name: "gatewayservice"],
                        [path: "hospitalService", name: "hospitalservice"],
                        [path: "locationService", name: "locationservice"],
                        [path: "reviewService", name: "reviewservice"],
                        [path: "userService", name: "userservice"]
                    ]
                    
                    services.each { service ->
                        if (fileExists(service.path)) {
                            dir(service.path) {
                                stage("Docker Build ${service.name}") {
                                    if (fileExists('Dockerfile')) {
                                        try {
                                            bat """
                                                docker build -t ${DOCKER_REGISTRY}/${service.name}:latest .
                                            """
                                            echo "✅ Image Docker construite: ${service.name}"
                                        } catch (Exception e) {
                                            echo "❌ Échec Docker build: ${e.getMessage()}"
                                            currentBuild.result = 'UNSTABLE'
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
        
        
        
        stage('Deploy to Kubernetes') {
            when {
                expression { 
                    try {
                        bat(script: 'kubectl version --client', returnStatus: true) == 0 && fileExists('k8s')
                    } catch (Exception e) {
                        false
                    }
                }
            }
            steps {
                script {
                    echo "☸️ DÉPLOIEMENT KUBERNETES"
                    echo "========================="
                    
                    def k8sFiles = findFiles(glob: 'k8s/*.yaml')
                    
                    k8sFiles.each { file ->
                        stage("Deploy ${file.name}") {
                            try {
                                bat """
                                    kubectl apply -n ${K8S_NAMESPACE} -f ${file.path}
                                """
                                echo "✅ Déployé: ${file.name}"
                            } catch (Exception e) {
                                echo "❌ Échec déploiement: ${file.name}"
                                currentBuild.result = 'UNSTABLE'
                            }
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
        unstable {
            echo "⚠️ Pipeline terminé avec des avertissements"
        }
        failure { 
            echo "❌ Pipeline échoué" 
            script {
                echo "🔧 Actions de dépannage:"
                echo "1. Vérifiez que le plugin 'Docker Pipeline' est installé"
                echo "2. Vérifiez la structure du dépôt"
                echo "3. Assurez-vous que les credentials Docker Hub sont configurés"
                echo "4. Vérifiez que tous les services ont un pom.xml"
            }
        }
        always {
            echo "📊 Résumé du build:"
            echo "- Dépôt: ${SOURCE_REPO}"
            echo "- Branche: ${SOURCE_BRANCH}"
            echo "- Statut: ${currentBuild.currentResult}"
            echo "- URL: ${env.BUILD_URL}"
        }
    }
}
