# Java Azure Function consuming events from Azure Event Hub

# Add local.settings.json with your event hub access and Job storage Conecction String

With the next JSON structure:
```yaml
{
  "IsEncrypted": false,
  "Values": {
    "AzureWebJobsStorage": "",
    "EventHubConnectionString": "",
    "FUNCTIONS_WORKER_RUNTIME": "java"
  }
}

# Run Locally

## `mvn package`
## `mvn mvn azure-functions:run`


