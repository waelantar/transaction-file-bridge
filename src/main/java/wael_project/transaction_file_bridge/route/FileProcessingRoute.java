package wael_project.transaction_file_bridge.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FileProcessingRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("file:{{input.directory}}?moveFailed={{error.directory}}")
                .routeId("fileProcessingRoute")
                .log("Processing file: ${file:name}");
    }
}
