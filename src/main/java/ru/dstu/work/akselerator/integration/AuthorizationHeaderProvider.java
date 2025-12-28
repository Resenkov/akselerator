package ru.dstu.work.akselerator.integration;

import java.util.Optional;

public interface AuthorizationHeaderProvider {
    Optional<String> getAuthorizationHeader();
}
