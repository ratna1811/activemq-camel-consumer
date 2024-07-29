package com.learning.activemq_camel_consumer.route;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ActiveMQConsumerRoute extends RouteBuilder {
    private final Logger LOGGER = LoggerFactory.getLogger(ActiveMQConsumerRoute.class);

    @Override
    public void configure() throws Exception {

        from("activemq:queue:myQueue")
                .log("Received message: ${body}")
                .process(exchange -> {
                    String message = exchange.getIn().getBody(String.class);
                    // Process the message

                    LOGGER.info("Processing message: " + message);
                })
                .to("mock:processedMessages");

    }

}
