package com.example.demo.service;

import com.example.demo.models.User;
import com.example.demo.models.role.Role;
import com.example.demo.repository.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.stereotype.Service;
import user.UserServiceGrpc;
import user.UserServiceOuterClass;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@GRpcService
public class UserService extends UserServiceGrpc.UserServiceImplBase {
    private final UserRepository repository;

    @Override
    public void createUser(UserServiceOuterClass.CreateUserRequest request,
                           StreamObserver<UserServiceOuterClass.CreateUserResponse> responseObserver) throws NullPointerException{
        if (request.getFirstName().isEmpty() || request.getLastName().isEmpty() ||
                request.getEmail().isEmpty() || request.getPassword().isEmpty()){
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Empty field")
                    .asRuntimeException());
            return;
        }
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.getRoles().add(Role.ROLE_USER);

        repository.save(user);

        UserServiceOuterClass.CreateUserResponse.Builder responseBuilder = UserServiceOuterClass.CreateUserResponse.newBuilder();

        responseBuilder.setId(user.getId().toString());

        responseObserver.onNext(responseBuilder.build());

        responseObserver.onCompleted();
    }

    @Override
    public void getUser(UserServiceOuterClass.GetUserRequest request,
                        StreamObserver<UserServiceOuterClass.GetUserResponse> responseObserver) {
        User user = repository.findById(UUID.fromString(request.getUserId()))
                .orElse(null);

        if (user == null){
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Incorrect id")
                    .asRuntimeException());
            return;
        }

        UserServiceOuterClass.GetUserResponse.Builder responseBuilder = UserServiceOuterClass.GetUserResponse.newBuilder();

        responseBuilder.setUser(UserServiceOuterClass.User.newBuilder()
                .setId(user.getId().toString())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .build());

        responseObserver.onNext(responseBuilder.build());

        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UserServiceOuterClass.UpdateUserRequest request,
                           StreamObserver<UserServiceOuterClass.UpdateUserResponse> responseObserver) {
        User user = repository.findById(UUID.fromString(request.getUserId()))
                .orElseThrow(null);

        if (user == null){
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Incorrect id")
                    .asRuntimeException());
            return;
        }

        copyCredentials(user, request);

        repository.save(user);

        UserServiceOuterClass.UpdateUserResponse.Builder responseBuilder = UserServiceOuterClass.UpdateUserResponse.newBuilder();

        responseBuilder.setMessage("User updated");

        responseObserver.onNext(responseBuilder.build());

        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(UserServiceOuterClass.DeleteUserRequest request,
                           StreamObserver<UserServiceOuterClass.DeleteUserResponse> responseObserver) {
        if (request.getUserId().isEmpty() || repository.existsById(UUID.fromString(request.getUserId()))){
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Incorrect id")
                    .asRuntimeException());
            return;
        }

        repository.deleteById(UUID.fromString(request.getUserId()));

        UserServiceOuterClass.DeleteUserResponse.Builder responseBuilder = UserServiceOuterClass.DeleteUserResponse.newBuilder();

        responseBuilder.setMessage("User deleted");

        responseObserver.onNext(responseBuilder.build());

        responseObserver.onCompleted();
    }

    private void copyCredentials(User user, UserServiceOuterClass.UpdateUserRequest request){
        if (!request.getFirstName().isEmpty()){
            user.setFirstName(request.getFirstName());
        }
        if (!request.getLastName().isEmpty()){
            user.setLastName(request.getLastName());
        }
        if (!request.getEmail().isEmpty()){
            user.setEmail(request.getEmail());
        }
    }
}
