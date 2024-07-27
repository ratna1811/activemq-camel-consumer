package com.learning.activemq_camel_consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class ActivemqCamelConsumerApplicationTests {

	private final Logger LOGGER = LoggerFactory.getLogger(ActivemqCamelConsumerApplicationTests.class);
	@Autowired
	private ProducerTemplate producerTemplate;

	@Autowired
	private ConsumerTemplate consumerTemplate;

	@Autowired
	private CamelContext camelContext;

	private static final Network network = Network.newNetwork();

	@SuppressWarnings("resource")
	@Container
	static GenericContainer<?> activeMQContainer = new GenericContainer<>(
			DockerImageName.parse("rmohr/activemq:latest"))
			.withNetwork(network)
			.withExposedPorts(61616)
			.waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(2)));

	@BeforeAll
	static void startActiveMQ() {
		activeMQContainer.start();
		String activeMQUrl = "tcp://" + activeMQContainer.getHost() + ":" + activeMQContainer.getMappedPort(61616);
		System.setProperty("spring.activemq.broker-url", activeMQUrl);

	}

	@BeforeEach
	void setUP() {
		assertNotNull(camelContext, "CamelContext should not be null");
		assertNotNull(producerTemplate, "ProducerTemplate should not be null");
		assertNotNull(consumerTemplate, "ConsumerTemplate should not be null");
		// Resetting and configuring the mock endpoint
		MockEndpoint mock = camelContext.getEndpoint("mock:processedMessages", MockEndpoint.class);
		mock.reset();
	}

	@Test
	void testConsumeMessage() throws InterruptedException {
		String message = "HelloActiveMQ1811";
		LOGGER.info("Sending message to queue: {}", message);
		// Create a MockEndpoint for testing
		MockEndpoint mock = camelContext.getEndpoint("mock:processedMessages", MockEndpoint.class);
		mock.expectedMessageCount(1);
		producerTemplate.sendBody("activemq:queue:myQueue", message);
		LOGGER.info("Message sent to queue.");

		mock.assertIsSatisfied();
		// Below we will not be verifying as in once the message is consumed it will be
		// deleted from the queue
		// so will not be available for our testing.
		// String consumedMessage =
		// consumerTemplate.receiveBody("activemq:queue:myQueue", 10000, String.class);
		// LOGGER.info("consumed Message----------------" + consumedMessage);

		// assertEquals(message, consumedMessage);

	}
}
