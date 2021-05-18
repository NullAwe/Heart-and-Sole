package com.allen.heartandsole.account;

public interface SignInResponseHandler {
    void handle(Status status, String username);

    enum Status { NO_USER, WRONG_PASSWORD, SUCCESS }
}
