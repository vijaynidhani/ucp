# Docker Setup for UCP Payment API

## Prerequisites
Docker Desktop must be installed on your system.

### Install Docker Desktop for Windows
1. Download Docker Desktop from: https://www.docker.com/products/docker-desktop
2. Run the installer and follow the installation wizard
3. Restart your computer if prompted
4. Start Docker Desktop from the Start menu
5. Verify installation by running: `docker --version`

## Build and Run Instructions

### Option 1: Using Docker Commands

1. **Build the Docker image:**
   ```powershell
   docker build -t ucp-payment-api:latest .
   ```

2. **Run the container:**
   ```powershell
   docker run -d -p 8080:8080 --name ucp-payment-api ucp-payment-api:latest
   ```

3. **View logs:**
   ```powershell
   docker logs -f ucp-payment-api
   ```

4. **Stop the container:**
   ```powershell
   docker stop ucp-payment-api
   ```

5. **Remove the container:**
   ```powershell
   docker rm ucp-payment-api
   ```

### Option 2: Using Docker Compose (Recommended)

1. **Start the application:**
   ```powershell
   docker-compose up -d
   ```

2. **View logs:**
   ```powershell
   docker-compose logs -f
   ```

3. **Stop the application:**
   ```powershell
   docker-compose down
   ```

4. **Rebuild and restart:**
   ```powershell
   docker-compose up -d --build
   ```

## Access the Application

Once the container is running, access the application at:
- API Base: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- H2 Console: http://localhost:8080/h2-console

## Container Information

- **Image Name:** ucp-payment-api:latest
- **Container Name:** ucp-payment-api
- **Port Mapping:** 8080:8080
- **Memory Limits:** -Xmx512m -Xms256m

## Troubleshooting

### Port Already in Use
If port 8080 is already in use, either:
- Stop the application using that port
- Or change the port mapping in docker-compose.yml: `- "8081:8080"`

### Container Won't Start
- Check logs: `docker logs ucp-payment-api`
- Verify Docker Desktop is running
- Ensure no other service is using port 8080
