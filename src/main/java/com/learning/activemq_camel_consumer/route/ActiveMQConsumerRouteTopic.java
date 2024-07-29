package com.learning.activemq_camel_consumer.route;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ActiveMQConsumerRouteTopic extends RouteBuilder {
    private static Logger LOGGER = LoggerFactory.getLogger(ActiveMQConsumerRouteTopic.class);

    @Override
    public void configure() throws Exception {

        from("activemq:topic:myTopic")
                .log("Subscriber 1 - Received message: ${body}")
                .process(exchange -> {
                    String message = exchange.getIn().getBody(String.class);
                    // Process the message

                    LOGGER.info("Subscriber 1 - Processing message: " + message);
                });

    }

}

@Component
class ActiveMQConsumer2RouteTopic extends RouteBuilder {
    private static Logger LOGGER = LoggerFactory.getLogger(ActiveMQConsumer2RouteTopic.class);

    @Override
    public void configure() throws Exception {

        from("activemq:topic:myTopic")
                .log("Subscriber 2 - Received Message: ${body}")
                .process(
                        exchange -> {
                            String message = exchange.getIn().getBody(String.class);
                            // Process the message

                            LOGGER.info("Subscriber 2 - Processing message: " + message);
                        });
    }

}
