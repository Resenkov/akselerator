package ru.dstu.work.akselerator.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.entity.User;
import ru.dstu.work.akselerator.entity.UserRole;
import ru.dstu.work.akselerator.repository.RoleRepository;
import ru.dstu.work.akselerator.repository.UserRepository;
import ru.dstu.work.akselerator.repository.UserRoleRepository;
import ru.dstu.work.akselerator.security.TokenIssuer;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final TokenIssuer tokenIssuer;

    public AuthController(AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          UserRoleRepository userRoleRepository,
                          TokenIssuer tokenIssuer) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.tokenIssuer = tokenIssuer;
    }

    public record RegisterRequest(String username, String email, String password) {
    }

    public record LoginRequest(String username, String password) {
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.findByUsername(req.username()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "username already exists"));
        }
        var u = new User();
        u.setUsername(req.username());
        u.setEmail(req.email());
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        var saved = userRepository.save(u); // use separate final reference below

        roleRepository.findByName("fisherman").ifPresent(role -> {
            userRoleRepository.save(new UserRole(saved, role, null));
        });

        return ResponseEntity.ok(Map.of("message", "registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        String token = tokenIssuer.issue(auth);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "tokenType", "Bearer"
        ));
    }
}