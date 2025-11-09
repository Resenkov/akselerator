package ru.dstu.work.akselerator.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.LoginRequest;
import ru.dstu.work.akselerator.dto.LoginResponse;
import ru.dstu.work.akselerator.dto.RegisterRequest;
import ru.dstu.work.akselerator.entity.Role;
import ru.dstu.work.akselerator.entity.User;
import ru.dstu.work.akselerator.entity.UserRole;
import ru.dstu.work.akselerator.mapper.UserMapper;
import ru.dstu.work.akselerator.repository.RoleRepository;
import ru.dstu.work.akselerator.repository.UserRepository;
import ru.dstu.work.akselerator.security.JwtTokenProvider;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AuthController(AuthenticationManager authManager,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider tokenProvider,
                          UserRepository userRepository,
                          RoleRepository roleRepository) {
        this.authManager = authManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest rq) {
        if (userRepository.findByUsername(rq.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "username_taken"));
        }

        User u = new User();
        u.setUsername(rq.getUsername());
        u.setEmail(rq.getEmail());
        u.setPasswordHash(passwordEncoder.encode(rq.getPassword()));
        u.setActive(true);

        Role fisherman = roleRepository.findByName("fisherman")
                .orElseThrow(() -> new IllegalStateException("Role 'fisherman' not found"));
        UserRole link = new UserRole();
        link.setUser(u);
        link.setRole(fisherman);
        u.getRoles().add(link);

        userRepository.save(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(u));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest rq) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(rq.getUsername(), rq.getPassword())
        );
        var principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        List<String> roles = principal.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();
        String token = tokenProvider.createToken(principal.getUsername(), roles);
        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
    }
}
