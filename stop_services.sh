#!/bin/bash

echo "Stopping all Spring Boot services (Windows)..."

cmd.exe /c "taskkill /IM java.exe /F"

echo "All services stopped."

