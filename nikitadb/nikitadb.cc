#include <iostream>
#include <memory>
#include <string>
#include <unordered_map>

#include <grpcpp/ext/proto_server_reflection_plugin.h>
#include <grpcpp/grpcpp.h>
#include <grpcpp/health_check_service_interface.h>

#include "user.grpc.pb.h"

using grpc::Server;
using grpc::ServerBuilder;
using grpc::ServerContext;
using grpc::Status;

struct User {
    size_t id;
    std::string first_name;
    std::string last_name;
    std::string date_of_birth;
    std::string email;
    std::string phone_number;
};

std::unordered_map<size_t, User> users = {
    {0, {0, "Nikita", "Rushmanov", "11/06/1994", "rush3nik@gmail.com", "3108497829"}}
}; // PK
size_t keyCounter = 1; // Keep track of largest key so that we know what to use when inserting.

// Logic and data behind the server's behavior.
class UserDBServiceImpl final : public user::UserDB::Service {

    Status ListUsers(ServerContext* context, const user::UserIndexRequest* request,
                     user::UserIndex* index) override {

        for(std::unordered_map<size_t, User>::const_iterator itr = users.begin();
            itr != users.end(); ++itr) {
            const User& user = itr->second;

            user::User* userMessage = index->add_users();
            userMessage->set_id(user.id);
            userMessage->set_first_name(user.first_name);
            userMessage->set_last_name(user.last_name);
        }

        return Status::OK;
    }

    Status NewUser(ServerContext* context, const user::User* request,
                     user::User* response) override {

        users[keyCounter] = {
            keyCounter,
            request->first_name(),
            request->last_name(),
            request->date_of_birth(),
            request->email(),
            request->phone_number()
        };

        *response = *request;
        response->set_id(keyCounter);

        ++keyCounter;
        return Status::OK;
    }

    Status UpdateUser(ServerContext* context, const user::User* request,
                     user::User* response) override {

        std::unordered_map<size_t, User>::iterator search = users.find(request->id());

        if(search == users.end()) {
            return Status::CANCELLED;
        }
        else {
            User& user = search->second;

            user.first_name = request->first_name();
            user.last_name = request->last_name();
            user.date_of_birth = request->date_of_birth();
            user.email = request->email();
            user.phone_number = request->phone_number();

            *response = *request;
            response->set_id(user.id);

            return Status::OK;
        }
    }
};

void RunServer() {
    std::string server_address("0.0.0.0:50051");
    UserDBServiceImpl service;

    grpc::EnableDefaultHealthCheckService(true);
    grpc::reflection::InitProtoReflectionServerBuilderPlugin();
    ServerBuilder builder;
    // Listen on the given address without any authentication mechanism.
    builder.AddListeningPort(server_address, grpc::InsecureServerCredentials());
    // Register "service" as the instance through which we'll communicate with
    // clients. In this case it corresponds to an *synchronous* service.
    builder.RegisterService(&service);
    // Finally assemble the server.
    std::unique_ptr<Server> server(builder.BuildAndStart());
    std::cout << "Server listening on " << server_address << std::endl;

    // Wait for the server to shutdown. Note that some other thread must be
    // responsible for shutting down the server for this call to ever return.
    server->Wait();
}

int main(int argc, char** argv) {
    RunServer();

    return 0;
}