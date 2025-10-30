package ru.dstu.work.akselerator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.*;
import ru.dstu.work.akselerator.entity.User;
import ru.dstu.work.akselerator.entity.UserRole;
import ru.dstu.work.akselerator.repository.UserRepository;
import ru.dstu.work.akselerator.repository.RoleRepository;
import ru.dstu.work.akselerator.security.JwtTokenProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager,
                          JwtTokenProvider tokenProvider,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated @RequestBody RegisterRequest rq) {
        if (userRepository.findByUsername(rq.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "username_taken"));
        }

        User u = new User();
        u.setUsername(rq.getUsername());
        u.setEmail(rq.getEmail());
        u.setPasswordHash(passwordEncoder.encode(rq.getPassword()));
        u.setActive(true);

        if (u.getRoles() == null) {
            u.setRoles(new HashSet<>());
        }

        roleRepository.findByName("fisherman").ifPresent(role -> {
            UserRole ur = new UserRole();
            ur.setUser(u);
            ur.setRole(role);
            u.getRoles().add(ur);
        });

        userRepository.save(u);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest rq) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(rq.getUsername(), rq.getPassword())
        );

        // build token
        var userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());

        String token = tokenProvider.createToken(userDetails.getUsername(), roles);
        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
    }
}
