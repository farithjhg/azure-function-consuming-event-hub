package org.easa.eccairs.indexation;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    public static final String TOPIC_IN  = "eccairs";
    public static final String EVENT_HUB_CONNECTION_STRING = "EventHubConnectionString";
    public static final String TOPIC_OUT = "eccairs-indexing-farith";
    public static final String FN_APP_NAME = "fn-farith";

    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }

    @FunctionName(FN_APP_NAME)
    public void processSensorData(
            @EventHubTrigger(
                    name = "event_in",
                    eventHubName = TOPIC_IN,
                    cardinality = Cardinality.ONE,
                    connection = EVENT_HUB_CONNECTION_STRING)
                    ConsumerMessage consumerMessage,
            @EventHubOutput(name = "event_out",
                    eventHubName = TOPIC_OUT,
                    connection = EVENT_HUB_CONNECTION_STRING)
                    OutputBinding<ConsumerMessage> outputBinding,
            final ExecutionContext context) {
        context.getLogger().info("Java Event Hub trigger function executed.");
        context.getLogger().info("Length:" + consumerMessage.getMessage().length());
        context.getLogger().info("Payload:" + consumerMessage.getMessage());
        outputBinding.setValue(consumerMessage);
    }

    @FunctionName("sendTime")
    @EventHubOutput(name = "event", eventHubName = TOPIC_IN, connection = EVENT_HUB_CONNECTION_STRING)
    public ConsumerMessage sendTime(
            @TimerTrigger(name = "sendTimeTrigger", schedule = "0 */5 * * * *") String timerInfo,
            final ExecutionContext context)  {
        context.getLogger().info("TimerTrigger executed: " + timerInfo);
        String localTime = LocalDateTime.now().toString();
        return new ConsumerMessage("Time: "+ localTime);
    }
}
