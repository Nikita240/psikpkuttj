package com.apiserver.springboot;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

/**
 * Client for NikitaDB database.
 */
public class NikitaDBClient {
  private static final Logger logger = Logger.getLogger(NikitaDBClient.class.getName());

  private final UserDBGrpc.UserDBBlockingStub blockingStub;

  /** Construct client for accessing HelloWorld server using the existing channel. */
  public NikitaDBClient(Channel channel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
    // shut it down.

    // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
    blockingStub = UserDBGrpc.newBlockingStub(channel);
  }

  /** Return user index. */
  public UserIndex listUsers() {

    UserIndexRequest request = UserIndexRequest.newBuilder().setPage(0).build();
    return blockingStub.listUsers(request);
  }

  /** Insert new user. */
  public User newUser(Map<String, String> request) {

    User user = User.newBuilder()
                    .setFirstName(request.get("firstName"))
                    .setLastName(request.get("lastName"))
                    .setDateOfBirth(request.get("dateOfBirth"))
                    .setEmail(request.get("email"))
                    .setPhoneNumber(request.get("phoneNumber"))
                    .build();

    return blockingStub.newUser(user);
  }

  /** Update an existing user. */
  public User updateUser(Long id, Map<String, String> request) {

    User user = User.newBuilder()
                    .setId(id)
                    .setFirstName(request.get("firstName"))
                    .setLastName(request.get("lastName"))
                    .setDateOfBirth(request.get("dateOfBirth"))
                    .setEmail(request.get("email"))
                    .setPhoneNumber(request.get("phoneNumber"))
                    .build();

    return blockingStub.updateUser(user);
  }
}
