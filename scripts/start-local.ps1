function prompt_with_default {
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

$ScriptBlock = {
  Write-Host "Exiting..."
  Stop-Process -Name "java" -Force
  Stop-Process -Name "npm" -Force
  exit 0
}

$null = Register-EngineEvent -SourceIdentifier "ConsoleCtrlCheck" -Action $ScriptBlock

Write-Host "Configuring environment variables..."
Write-Host ""
Write-Host "You need your own OpenWeatherMap API key. You can get a free one at https://home.openweathermap.org/users/sign_up"
Write-Host ""
Write-Host "For connecting to the PostgreSQL database enter your own PostgreSQL username and password when prompted"
Write-Host ""
Write-Host "Otherwise you can customize values according to your preferences or press Enter to accept the default values shown in the [brackets]."
Write-Host ""

$API_KEY = prompt_with_default "API_KEY" "Enter your OpenWeatherMap API key"
$SPRING_DATASOURCE_USERNAME = prompt_with_default "SPRING_DATASOURCE_USERNAME" "Enter your PostgreSQL username"
$SPRING_DATASOURCE_PASSWORD = prompt_with_default "SPRING_DATASOURCE_PASSWORD" "Enter your PostgreSQL password"
$GEOCODING_BASE_URL = prompt_with_default "GEOCODING_BASE_URL" "https://api.openweathermap.org/geo/1.0/direct"
$SUNRISESUNSET_BASE_URL = prompt_with_default "SUNRISESUNSET_BASE_URL" "https://api.sunrise-sunset.org/json"
$JWT_SECRET = prompt_with_default "JWT_SECRET" "Jitr5pYjU6d9ERzvRtUC3M1YST6P/O/FTqR/EK3wLpc="
$JWT_EXPIRATION = prompt_with_default "JWT_EXPIRATION" "86400000"
$SPRING_DATASOURCE_URL = prompt_with_default "SPRING_DATASOURCE_URL" "jdbc:postgresql://localhost:5432/solarwatch"

$envFilePath = "../.env"
@"
API_KEY=$API_KEY
GEOCODING_BASE_URL=$GEOCODING_BASE_URL
SUNRISESUNSET_BASE_URL=$SUNRISESUNSET_BASE_URL
JWT_SECRET=$JWT_SECRET
JWT_EXPIRATION=$JWT_EXPIRATION
SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD
"@ | Set-Content -Path $envFilePath

Write-Host ".env file created with your values or defaults."

Set-Location -Path ..

Write-Host "Starting backend..."
Start-Process "powershell.exe" -ArgumentList "cd ../backend; ./mvnw spring-boot:run" -NoNewWindow -PassThru
Start-Sleep -Seconds 5

Write-Host "Starting frontend..."
Start-Process "powershell.exe" -ArgumentList "cd ../frontend; npm run dev" -NoNewWindow -PassThru


Write-Host "Press Ctrl+C to stop both services..."
Wait-Event -SourceIdentifier "ConsoleCtrlCheck"
