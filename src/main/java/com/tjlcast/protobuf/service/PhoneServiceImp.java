package com.tjlcast.protobuf.service;

import com.tjlcast.protobuf.Myworld.AddPhoneToUserRequest ;
import com.tjlcast.protobuf.Myworld.AddPhoneToUserResponse ;
import com.tjlcast.protobuf.PhoneServiceGrpc;
import io.grpc.stub.StreamObserver;

/**
 * Created by tangjialiang on 2018/1/16.
 *
 */
public class PhoneServiceImp extends PhoneServiceGrpc.PhoneServiceImplBase {

    @Override
    public void addPhoneToUser(AddPhoneToUserRequest request, StreamObserver<AddPhoneToUserResponse> responseObserver) {
        // TODO Auto-generated method stub
        AddPhoneToUserResponse response = null;
        if(request.getPhoneNumber().length() == 11 ){
            System.out.printf("uid = %s , phone type is %s, nubmer is %s\n", request.getUid(), request.getPhoneType(), request.getPhoneNumber());
            response = AddPhoneToUserResponse.newBuilder().setResult(true).build();
        }else{
            System.out.printf("The phone nubmer %s is wrong!\n",request.getPhoneNumber());
            response = AddPhoneToUserResponse.newBuilder().setResult(false).build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
