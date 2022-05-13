package com.apiserver.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.Map;

@SpringBootApplication
@RestController
public class Application {

	private static NikitaDBClient client;

	@GetMapping("/users")
	public String userIndex()  throws InvalidProtocolBufferException {
		UserIndex users = client.listUsers();
		return JsonFormat.printer().print(users);
	}

	@PostMapping("/users")
	public String newUser(@RequestBody Map<String, String> payload) throws InvalidProtocolBufferException {
		User user = client.newUser(payload);
		return JsonFormat.printer().print(user);
	}

	@PutMapping("/users/{id}")
	public String updateUser(@RequestBody Map<String, String> payload, @PathVariable Long id) throws InvalidProtocolBufferException {
		User user = client.updateUser(id, payload);
		return JsonFormat.printer().print(user);
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

		client = new NikitaDBClient(channel);

		SpringApplication.run(Application.class, args);
	}

}
