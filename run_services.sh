#!/bin/bash

echo "Making all mvnw wrappers executable..."
find . -name "mvnw" -exec chmod +x {} \;

echo "Starting all services..."

# Start discoveryService
echo "Starting discoveryService..."
cd discoveryService
nohup ./mvnw spring-boot:run > discoveryService.log 2>&1 &
cd ..
echo "Waiting for discoveryService to start..."
sleep 15

# Start other services
echo "Starting userService..."
cd userService
nohup ./mvnw spring-boot:run > userService.log 2>&1 &
cd ..

echo "Starting hospitalService..."
cd hospitalService
nohup ./mvnw spring-boot:run > hospitalService.log 2>&1 &
cd ..

echo "Starting locationService..."
cd locationService
nohup ./mvnw spring-boot:run > locationService.log 2>&1 &
cd ..

echo "Starting annonceService..."
cd annonceService/annonceService
nohup ./mvnw spring-boot:run > annonceService.log 2>&1 &
cd ../..

echo "Starting reviewService..."
cd reviewService
nohup ./mvnw spring-boot:run > reviewService.log 2>&1 &
cd ..

echo "Waiting for services to register..."
sleep 30

# Start gatewayService
echo "Starting gatewayService..."
cd gatewayService
nohup ./mvnw spring-boot:run > gatewayService.log 2>&1 &
cd ..

echo "All services have been started in the background."
echo "You can check the logs in the respective service directories (e.g., discoveryService/discoveryService.log)."
echo "To stop the services, you can use a command like: pkill -f 'spring-boot:run'"
