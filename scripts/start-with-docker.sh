#!/bin/bash

prompt_with_default() {
    local var_name=$1
    local default_value=$2
    read -p "$var_name: press Enter to accept default: [$default_value]: " input
    echo "${input:-$default_value}"
}

echo "Configuring environment variables..."
echo ""
echo "You need your own OpenWeatherMap API key. You can get a free one at https://home.openweathermap.org/users/sign_up"
echo ""
echo "If you have an existing Docker volume for this project with custom PostgreSQL credentials, please enter those values when prompted."
echo ""
echo "Otherwise you can customize values according to your preferences or press Enter to accept the default values shown in the [brackets]."
echo "The values are saved to a .env file. When you want to launch the application again, you can just use the 'command docker compose up' to start the containers"
echo ""

API_KEY=$(prompt_with_default "API_KEY" "Your own OpenWeatherMap API key")
SPRING_DATASOURCE_USERNAME=$(prompt_with_default "SPRING_DATASOURCE_USERNAME" "postgres")
SPRING_DATASOURCE_PASSWORD=$(prompt_with_default "SPRING_DATASOURCE_PASSWORD" "admin1234")
GEOCODING_BASE_URL=$(prompt_with_default "GEOCODING_BASE_URL" "https://api.openweathermap.org/geo/1.0/direct")
SUNRISESUNSET_BASE_URL=$(prompt_with_default "SUNRISESUNSET_BASE_URL" "https://api.sunrise-sunset.org/json")
JWT_SECRET=$(prompt_with_default "JWT_SECRET" "Jitr5pYjU6d9ERzvRtUC3M1YST6P/O/FTqR/EK3wLpc=")
JWT_EXPIRATION_MS=$(prompt_with_default "JWT_EXPIRATION" "86400000")
SPRING_DATASOURCE_URL=$(prompt_with_default "SPRING_DATASOURCE_URL" "jdbc:postgresql://solarwatch_db:5432/solarwatch")


cat > ../.env <<EOL

API_KEY=${API_KEY}
GEOCODING_BASE_URL=${GEOCODING_BASE_URL}
SUNRISESUNSET_BASE_URL=${SUNRISESUNSET_BASE_URL}

JWT_SECRET=${JWT_SECRET}
JWT_EXPIRATION_MS=${JWT_EXPIRATION_MS}

SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}
SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
EOL

echo ".env file created with your values or defaults."

cd .. 

echo "Starting Docker containers..."

docker compose up
