# SolarWatch

<details>
<summary><h2><strong>Table of Contents</strong></h2></summary>
  
- [About the Project](#about-the-project)
- [Built With](#built-with)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation Steps](#installation-steps)
- [Usage](#usage)
- [Acknowledgments](#acknowledgments)
  
</details>


## About The Project

**SolarWatch** is a web application that provides accurate **sunrise** and **sunset** times for any specified **city** and **date**. To maximize **efficiency** and reduce redundant **external API calls**, it utilizes a local **database** to **cache** frequently requested data. If the requested information is not available locally, **SolarWatch** automatically **fetches** it from an **external API** and **stores** it for future use, ensuring **fast** and **reliable** responses for subsequent requests.

![Screenshot 2025-04-29 at 12 22 31](https://github.com/user-attachments/assets/85170ce0-c1d3-441c-96a7-8e677bb3d554)

### Features

  - **City/date lookup** with accurate sunrise and sunset data
  - **Caching** via a local database to minimize external API calls
  - **Secure authentication** with role-based access

The entire application is containerized using **Docker Compose**, enabling seamless **deployment** and **scalability**.
CI pipeline via *GitHub Actions* ensures code stability by running automated tests upon push and pull requests.

  
## Built With

- **Backend:**  
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)

- **Frontend:**  
  [![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)](https://reactjs.org/)  
  [![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)](https://vitejs.dev/)  
  [![TailwindCSS](https://img.shields.io/badge/TailwindCSS-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)](https://tailwindcss.com/)

- **Database:**  
  [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)

- **Containerization:**  
  [![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)  
  [![NGINX](https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=nginx&logoColor=white)](https://www.nginx.com/)

## Getting Started

### Prerequisites

Depending on how you want to run the application, different tools are required:

**If you want to run the application using Docker, make sure you have Docker installed:**

  - #### Docker Desktop
    ➡️ [https://www.docker.com/products/docker-desktop/](https://www.docker.com/products/docker-desktop/)  

**If you plan to run the code locally without Docker, make sure you have the following installed:**

  - **Java 23**
    ➡️ [https://jdk.java.net/23/](https://jdk.java.net/23/)  

  - **Node.js 18+**
    ➡️ [https://nodejs.org/en/download/](https://nodejs.org/en/download/)  
  
  - **PostgreSQL 17+**
    ➡️ [https://www.postgresql.org/download/](https://www.postgresql.org/download/)  

  - **Maven 3.9+**
    ➡️ [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) 

### Installation Steps

To get a local copy up and running, follow these steps:

1. Open a **terminal** and navigate to the directory where you would like to save the repository.
   
2. **Clone the repository** to your machine by executing the command below in your **terminal**, then proceed with one of the installation options below.
   ```bash
   git clone https://github.com/rudnaid/solarwatch.git
   ```

To simplify setup of your **environment variables** and launching the application, **starter scripts** are provided in the `scripts` directory.

---

#### With Docker (recommended)

1. **Ensure Docker is Running**
   - Start **Docker Desktop** or the **Docker daemon** on your system.

2. **Navigate to the scripts directory**
   - The **scripts** will guide you through the initial setup of **environment variables**.
   - Open your **terminal** and navigate to the **scripts** directory located in the **root** directory of the project.

3. **Build and run the containers with the automated script**
   - **On Windows:**
     - Execute the command:
       ```bash
       ./start-with-docker.ps1
       ```
   - **On macOS/Linux:**
     - Execute the command:
       ```bash
       chmod +x start-with-docker.sh && ./start-with-docker.sh
       ```

4. **Access the Application**
   - Open your browser and visit:  
     [http://localhost:3000](http://localhost:3000)

5. **Stopping the application**
   - To stop running containers, execute this command:  
     ```bash
       docker compose down
     ```

---

#### Without Docker

1. **Create PostgreSQL database** *(only necessary if running the application for the **first time**)*
   - Open your **terminal** and type `psql` then press Enter to connect to PostgreSQL.
   - Log in with your PostgreSQL credentials (make sure the user has sufficient privileges to create databases)
   - Type `CREATE DATABASE solarwatch;` then press Enter.
   - All set! To exit PostgreSQL type `quit` and press Enter.

1. **Navigate to the scripts directory**
   - Open your **terminal** and navigate to the **scripts** directory located in the **root** directory of the project.
   - The **scripts** will guide you through the initial setup of **environment variables**.

2. **Run starter script for automated setup**
   - **On Windows:**
        - Execute the command:
          ```bash
          ./start-local.ps1
          ```
   - **On macOS/Linux:**
        - Execute the command:
          ```bash
          chmod +x start-local.sh && ./start-local.sh
          ```
            
3. **Access the Application**
   - Open your browser and visit:  
     [http://localhost:5173](http://localhost:5173)

4. **Stopping the application**
   - In your **terminal** press `Ctrl + C`

## Usage

### Retrieving Sunrise and Sunset Times

1. **Register** a new user or **log in** if you already have an account.

2. **Search for a City**
   - Enter the name of the **city** (*in English*) in the search input field.
   - Press `Enter` to submit your query.

3. **Select a Date (Optional)**
   - By default, the current date is used.
   - To choose a different date, use the date selector next to the input field **before** pressing `Enter`.

---

The application will return accurate **sunrise** and **sunset** times in UTC for the specified **city** and **date**.

## Acknowledgments

- [Sunrise-Sunset-API](https://sunrise-sunset.org/api) for the sunrise/sunset times
- [OpenWeatherMaps](https://openweathermap.org/api/geocoding-api) for the conversion of city names to latitude and longitude data
- [Best-README-Template](https://github.com/othneildrew/Best-README-Template) for inspiration
- [Shields.io](https://shields.io/) for the badges
