# Setup Guide - Azure Bot Sample

## Security Notice

This project has been updated to remove hardcoded API keys and credentials. You must configure your own credentials before running the application.

## Prerequisites

- Java 21
- Maven 3.x
- Azure OpenAI account with deployed GPT-4o-mini model
- Azure Speech Services account

## Initial Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd azureBotSample
```

### 2. Configure Application Properties

Copy the example configuration file:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

### 3. Update Configuration with Your Credentials

Edit `src/main/resources/application.properties` and replace the placeholder values:

#### Azure OpenAI Configuration

```properties
# Replace YOUR_RESOURCE_NAME with your Azure OpenAI resource name
azure.openai.endpoint=https://YOUR_RESOURCE_NAME.openai.azure.com/openai/deployments/gpt-4o-mini/chat/completions?api-version=2025-01-01-preview
azure.openai.endpoint-sdk=https://YOUR_RESOURCE_NAME.openai.azure.com

# Replace with your actual API key from Azure Portal
azure.openai.api-key=YOUR_OPENAI_API_KEY_HERE

# Update if using a different deployment name
azure.openai.deployment-name=gpt-4o-mini
```

#### Azure Speech Services Configuration

```properties
# Replace with your Speech Services API key
azure.speech.subscription-key=YOUR_SPEECH_API_KEY_HERE

# Update if using a different region
azure.speech.region=southeastasia
azure.speech.endpoint=https://southeastasia.api.cognitive.microsoft.com/

# Customize voice settings (optional)
azure.speech.voice-model=en-SG-LunaNeural
azure.speech.language=en-SG
azure.speech.style=customerservice
azure.speech.role=Girl
azure.speech.style-degree=2
```

### 4. How to Get Your API Keys

#### Azure OpenAI API Key

1. Go to [Azure Portal](https://portal.azure.com)
2. Navigate to your Azure OpenAI resource
3. Click on "Keys and Endpoint" in the left menu
4. Copy one of the keys (KEY 1 or KEY 2)
5. Copy the endpoint URL

#### Azure Speech Services API Key

1. Go to [Azure Portal](https://portal.azure.com)
2. Navigate to your Speech Services resource
3. Click on "Keys and Endpoint" in the left menu
4. Copy one of the keys (KEY 1 or KEY 2)
5. Note your region (e.g., southeastasia)

### 5. Build and Run

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 6. Access the Application

Open your browser and navigate to:
- Web UI: `http://localhost:8080`
- Swagger API Docs: `http://localhost:8080/swagger-ui.html`

## Security Best Practices

### Never Commit Secrets

The `.gitignore` file is configured to exclude `application.properties` to prevent accidental commits of sensitive data.

**Files that should NEVER be committed:**
- `src/main/resources/application.properties`
- `.env` files
- Any files containing API keys or passwords

**Files that SHOULD be committed:**
- `src/main/resources/application.properties.example` (template with placeholders)
- This `SETUP.md` file

### If You Accidentally Committed Secrets

If you've already committed `application.properties` with real credentials:

1. **Immediately rotate your API keys** in Azure Portal
2. Remove the file from Git history:

```bash
# Remove from Git tracking (but keep local file)
git rm --cached src/main/resources/application.properties

# Commit the removal
git commit -m "Remove application.properties from version control"

# Push to remote
git push origin master
```

3. Verify the file is in `.gitignore`:

```bash
cat .gitignore | grep application.properties
```

### Using Environment Variables (Alternative)

For production deployments, consider using environment variables instead of `application.properties`:

```bash
export AZURE_OPENAI_API_KEY="your-key-here"
export AZURE_SPEECH_KEY="your-key-here"
```

Then update your configuration to use:
```properties
azure.openai.api-key=${AZURE_OPENAI_API_KEY}
azure.speech.subscription-key=${AZURE_SPEECH_KEY}
```

## Deployment to Azure App Service

When deploying to Azure App Service, use **Application Settings** to configure secrets:

1. Go to your App Service in Azure Portal
2. Navigate to Configuration → Application settings
3. Add new application settings:
   - `AZURE_OPENAI_API_KEY`
   - `AZURE_SPEECH_SUBSCRIPTION_KEY`
4. Reference them in your properties file using `${ENV_VAR_NAME}` syntax

## Troubleshooting

### Application Won't Start

**Error**: "Could not resolve placeholder"

**Solution**: Ensure all required properties are set in `application.properties`

### Speech Synthesis Fails

**Error**: "CANCELED: ErrorCode=ConnectionFailure"

**Solution**:
- Verify your Speech API key is correct
- Check that the region matches your Speech Services resource
- Ensure the endpoint URL is correct for your region

### OpenAI API Errors

**Error**: "401 Unauthorized"

**Solution**:
- Verify your API key is correct and not expired
- Ensure the endpoint URL matches your resource name
- Check that your deployment name matches the actual deployment in Azure

## Support

For issues related to:
- **Azure Services**: [Azure Support](https://azure.microsoft.com/support/)
- **Application Issues**: Open an issue in the repository

## Additional Resources

- [Azure OpenAI Documentation](https://learn.microsoft.com/azure/ai-services/openai/)
- [Azure Speech Services Documentation](https://learn.microsoft.com/azure/ai-services/speech-service/)
- [Spring Boot Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
