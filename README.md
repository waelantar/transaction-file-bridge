# Transaction File Bridge

A Spring Boot application that processes transaction files and uploads them to MinIO object storage. The application provides a web interface for uploading files and monitoring their processing status.

## Features
- File upload via web interface
- Automatic processing of CSV files
- Integration with MinIO for object storage
- Health monitoring endpoints
- Responsive web interface built with Thymeleaf
- Automated deployment to OpenShift via GitHub Actions

## Prerequisites
- Java 17
- Maven 3.6+
- Docker
- OpenShift account
- Docker Hub account


## Setup and Installation (Local)

### 1. Clone the Repository
```bash
git clone https://github.com/waelantar/transaction-file-bridge.git  
cd transaction-file-bridge
```

### 2. Build the Application
```bash
mvn clean package -DskipTests
```

### 3. Run with Docker
```bash
docker build -t transaction-file-bridge:latest .
docker run -p 8080:8080 transaction-file-bridge:latest
```

### 4. Run with Maven
```bash
mvn spring-boot:run
```
The application will be available at http://localhost:8080

## Deploying to OpenShift

### 1. Set Up OpenShift Project
```bash
oc new-project transaction-file-bridge
```

### 2. Configure GitHub Secrets
Configure the following secrets in your GitHub repository:
- `DOCKERHUB_USERNAME`: Your Docker Hub username
- `DOCKERHUB_TOKEN`: Your Docker Hub access token
- `OPENSHIFT_SERVER`: Your OpenShift server URL
- `OPENSHIFT_TOKEN`: Your OpenShift access token

### 3. Deploy Using GitHub Actions
Push your changes to the main branch. The GitHub Actions workflow will automatically:
- Build the application
- Create a Docker image
- Push the image to Docker Hub
- Deploy the application to OpenShift

### 4. Manual Deployment (Optional)
If you prefer to deploy manually:
```bash
# Apply all OpenShift manifests
oc apply -f openshiftScripts/

# Wait for deployments to be ready
oc rollout status deployment/minio
oc rollout status deployment/transaction-file-bridge
```

## Using the Application

### 1. Access the Application
After deployment, get the application URL:
```bash
oc get route transaction-file-bridge -o jsonpath='{.spec.host}'
```
Open the URL in your browser.

### 2. Upload Files
- Navigate to the Upload page (`/upload`)
- Select a CSV file to upload
- Click "Upload File"
- The file will be processed automatically and moved to the output directory

### 3. View Files
Navigate to the Files page (`/files`) to view files in the input directory.

### 4. Health Check
Navigate to the Health page (`/health`) to check the application's health status.

### 5. Access MinIO Console
Get the MinIO console URL:
```bash
oc get route minio-console -o jsonpath='{.spec.host}'
```
Open the URL in your browser and log in with:
- Username: `minioadmin`
- Password: `minioadmin`

## Thymeleaf Templates

### index.html
Landing page with navigation to other sections.

### upload.html
File upload form with:
- File selection input
- Upload button
- Success/error messages
- File information display

### files.html
File listing page with:
- Table of files in the input directory
- File information (name, size, last modified)
- Refresh button
- Navigation to upload page

### health.html
Health status page with:
- Application health status
- System information
- MinIO connection status

## GitHub Actions Pipeline
The project includes a GitHub Actions workflow that automates the build and deployment process:

### Build Job
- Checks out the repository
- Sets up JDK 17
- Caches Maven packages
- Builds the application with Maven
- Logs in to Docker Hub
- Builds and pushes the Docker image
- Outputs image information

### Deploy Job
- Installs OpenShift CLI
- Logs in to OpenShift
- Applies OpenShift manifests
- Updates the application image
- Checks deployment status
- Rolls back on failure
- Verifies deployment

### Notify Job
- Sends a notification about the deployment status.

## Configuration

### Environment Variables
The application can be configured using the following environment variables:
- `INPUT_PATH`: Path to the input directory (default: `/app/input`)
- `OUTPUT_PATH`: Path to the output directory (default: `/app/output`)
- `ERROR_PATH`: Path to the error directory (default: `/app/error`)
- `MINIO_ENDPOINT`: MinIO server URL (default: `http://localhost:9000`)
- `MINIO_ACCESS_KEY`: MinIO access key (default: `minioadmin`)
- `MINIO_SECRET_KEY`: MinIO secret key (default: `minioadmin`)
- `MINIO_BUCKET_NAME`: MinIO bucket name (default: `transactions`)

### OpenShift Configuration
The OpenShift manifests include:
- MinIO deployment and service
- Application deployment and service
- Routes for external access

## Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Commit your changes
6. Push to the branch
7. Create a Pull Request

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Support
For support, please open an issue in the GitHub repository or contact the development team.
