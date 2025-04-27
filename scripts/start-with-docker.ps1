$prompt_with_default = {
  param (
      [string]$var_name,
      [string]$default_value
  )
  $input = Read-Host "$var_name (press Enter to accept default: [$default_value])"
  if ($input -eq "") {
      return $default_value
  }
  return $input
}

Write-Host "Configuring environment variables..."
Write-Host ""
Write-Host "You need your own OpenWeatherMap API key. You can get a free one at https://home.openweathermap.org/users/sign_up"
Write-Host ""
Write-Host "If you have an existing Docker volume for this project with custom PostgreSQL credentials, please enter those values when prompted."
Write-Host ""
Write-Host "Otherwise you can customize values according to your preferences or press Enter to accept the default values shown in the [brackets]."
Write-Host "The values are saved to a .env file. When you want to launch the application again, you can just use the 'command docker-compose up' to start the containers"
Write-Host ""

$API_KEY = & $prompt_with_default "API_KEY" "Your own OpenWeatherMap API key"
$SPRING_DATASOURCE_USERNAME = & $prompt_with_default "SPRING_DATASOURCE_USERNAME" "postgres"
$SPRING_DATASOURCE_PASSWORD = & $prompt_with_default "SPRING_DATASOURCE_PASSWORD" "admin1234"
$GEOCODING_BASE_URL = & $prompt_with_default "GEOCODING_BASE_URL" "https://api.openweathermap.org/geo/1.0/direct"
$SUNRISESUNSET_BASE_URL = & $prompt_with_default "SUNRISESUNSET_BASE_URL" "https://api.sunrise-sunset.org/json"
$JWT_SECRET = & $prompt_with_default "JWT_SECRET" "Jitr5pYjU6d9ERzvRtUC3M1YST6P/O/FTqR/EK3wLpc="
$JWT_EXPIRATION_MS = & $prompt_with_default "JWT_EXPIRATION" "86400000"
$SPRING_DATASOURCE_URL = & $prompt_with_default "SPRING_DATASOURCE_URL" "jdbc:postgresql://solarwatch_db:5432/solarwatch"

$envFilePath = "../.env"
Set-Content -Path $envFilePath -Value @"
API_KEY=$API_KEY
GEOCODING_BASE_URL=$GEOCODING_BASE_URL
SUNRISESUNSET_BASE_URL=$SUNRISESUNSET_BASE_URL
JWT_SECRET=$JWT_SECRET
JWT_EXPIRATION_MS=$JWT_EXPIRATION_MS
SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD
"@

Write-Host ".env file created with your values or defaults."

Set-Location -Path ..

Write-Host "Starting Docker containers..."

docker-compose up
