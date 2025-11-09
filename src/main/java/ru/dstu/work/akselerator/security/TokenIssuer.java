// ru/dstu/work/akselerator/security/TokenIssuer.java
package ru.dstu.work.akselerator.security;

import org.springframework.security.core.Authentication;

public interface TokenIssuer {
    String issue(Authentication authentication);
}
