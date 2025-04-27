#!/bin/bash

prompt_with_default() {
    local var_name=$1
    local default_value=$2
    read -p "$var_name: press Enter to accept default: [$default_value]: " input
    echo "${input:-$default_value}"
}

trap 'echo "Exiting..."; pkill -f "spring-boot:run"; kill $SOLARWATCH_FRONTEND_PID 2>/dev/null; exit 0' INT

echo "Configuring environment variables..."
echo ""
echo "You need your own OpenWeatherMap API key. You can get a free one at https://home.openweathermap.org/users/sign_up"
echo ""
echo "For connecting to the PostgreSQL database enter your own PostgreSQL username and password when prompted"
echo ""
echo "Otherwise you can customize values according to your preferences or press Enter to accept the default values shown in the [brackets]."
echo ""

API_KEY=$(prompt_with_default "API_KEY" "Enter your OpenWeatherMap API key")
SPRING_DATASOURCE_USERNAME=$(prompt_with_default "SPRING_DATASOURCE_USERNAME" "Enter your PostgreSQL username")
SPRING_DATASOURCE_PASSWORD=$(prompt_with_default "SPRING_DATASOURCE_PASSWORD" "Enter your PostgreSQL password")
GEOCODING_BASE_URL=$(prompt_with_default "GEOCODING_BASE_URL" "https://api.openweathermap.org/geo/1.0/direct")
SUNRISESUNSET_BASE_URL=$(prompt_with_default "SUNRISESUNSET_BASE_URL" "https://api.sunrise-sunset.org/json")
JWT_SECRET=$(prompt_with_default "JWT_SECRET" "Jitr5pYjU6d9ERzvRtUC3M1YST6P/O/FTqR/EK3wLpc=")
JWT_EXPIRATION=$(prompt_with_default "JWT_EXPIRATION" "86400000")
SPRING_DATASOURCE_URL=$(prompt_with_default "SPRING_DATASOURCE_URL" "jdbc:postgresql://localhost:5432/solarwatch")

export API_KEY=$API_KEY
export GEOCODING_BASE_URL=$GEOCODING_BASE_URL
export SUNRISESUNSET_BASE_URL=$SUNRISESUNSET_BASE_URL
export JWT_SECRET=$JWT_SECRET
export JWT_EXPIRATION=$JWT_EXPIRATION
export SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL
export SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME
export SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD

echo "Starting backend..."
(cd ../backend && ./mvnw spring-boot:run) &
SOLARWATCH_BACKEND_PID=$!

echo "Starting frontend..."
(cd ../frontend && npm run dev) &
SOLARWATCH_FRONTEND_PID=$!

wait $SOLARWATCH_BACKEND_PID $SOLARWATCH_FRONTEND_PID
