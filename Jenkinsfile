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
	}

	stages {

		stage('Checkout') {
			steps {
				git branch: 'main',
				url: 'https://github.com/AnasSaibari/blood-donation.git'
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
								sh "mvn clean verify"
							}

							stage("SonarQube ${service}") {
								withSonarQubeEnv("${SONARQUBE_ENV}") {
									sh """
                                    mvn sonar:sonar \
                                    -Dsonar.projectKey=${service} \
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
							sh """
                        docker build -t ${DOCKER_REGISTRY}/${service.toLowerCase()}:latest .
                    """

							withDockerRegistry(
								credentialsId: 'dockerhub-creds',
								url: ''
							) {
								sh "docker push ${DOCKER_REGISTRY}/${service.toLowerCase()}:latest"
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
						sh """
                        kubectl apply -n ${K8S_NAMESPACE} \
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
