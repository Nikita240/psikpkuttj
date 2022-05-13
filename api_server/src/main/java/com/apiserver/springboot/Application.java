package com.apiserver.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@SpringBootApplication
@RestController
public class Application {

	@RequestMapping("/")
	public String home() {
		return "Hello World";
	}

	public static void main(String[] args) {
		// Access a service running on the local machine on port 50051
		String target = "nikitadb:50051";

		// Create a communication channel to the server, known as a Channel. Channels are thread-safe
		// and reusable. It is common to create channels at the beginning of your application and reuse
		// them until the application shuts down.
		ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
			// Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
			// needing certificates.
			.usePlaintext()
			.build();
		try {
			HelloWorldClient client = new HelloWorldClient(channel);
			client.greet("nikita");
		} finally {
			// ManagedChannels use resources like threads and TCP connections. To prevent leaking these
			// resources the channel should be shut down when it will no longer be used. If it may be used
			// again leave it running.
			// channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
		}

		SpringApplication.run(Application.class, args);
	}

}
