package ru.dstu.work.akselerator.controller;

import jakarta.validation.Valid;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.*;
import ru.dstu.work.akselerator.entity.*;
import ru.dstu.work.akselerator.mapper.OrganizationMapper;
import ru.dstu.work.akselerator.mapper.UserMapper;
import ru.dstu.work.akselerator.repository.*;
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
    private final OrganizationRepository organizationRepository;

    public AuthController(AuthenticationManager authManager,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider tokenProvider,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          OrganizationRepository organizationRepository) {
        this.authManager = authManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
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
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(rq.getUsername(), rq.getPassword())
        );
        var principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        List<String> roles = principal.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();

        String token = tokenProvider.createToken(principal.getUsername(), roles);

        LoginResponse response = new LoginResponse(token, "Bearer", UserMapper.toDto(u), null, roles);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest rq) {
        Authentication authentication;
        try {
            authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(rq.getUsername(), rq.getPassword())
            );
        } catch (BadCredentialsException ex) {
            LoginResponse errorResp = new LoginResponse(null, null, null, null, null, false, false);
            errorResp.setValid(false);
            errorResp.setMessage("Неверный логин или пароль");
            errorResp.setError("invalid_credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
        }
        var principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        List<String> roles = principal.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();
        String token = tokenProvider.createToken(principal.getUsername(), roles);

        User userEntity = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        UserDto userDto = UserMapper.toDto(userEntity);
        OrganizationDto orgDto = null;
        if (userEntity.getOrganization() != null) {
            orgDto = OrganizationMapper.toDto(userEntity.getOrganization());
        }

        LoginResponse resp = new LoginResponse(token, "Bearer", userDto, orgDto, roles);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register-company")
    public ResponseEntity<?> registerCompany(@Valid @RequestBody CompanyRegistrationRequest rq) {

        // 1. Проверка username
        if (userRepository.existsByUsername(rq.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error", "username_taken",
                            "message", "Пользователь с таким логином уже существует"
                    ));
        }

        // 2. Проверка email
        if (userRepository.existsByEmail(rq.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error", "email_taken",
                            "message", "Пользователь с таким email уже существует"
                    ));
        }

        // 3. Проверка организации по INN (если INN передаётся)
        if (rq.getInn() != null && !rq.getInn().isBlank()
                && organizationRepository.existsByInn(rq.getInn())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error", "organization_exists",
                            "message", "Организация с таким ИНН уже зарегистрирована"
                    ));
        }

        // 4. Дальше — как раньше: создаём организацию, пользователя, выдаём токен

        Organization org = new Organization();
        org.setName(rq.getOrgName());
        org.setOrgType(rq.getOrgType());
        org.setInn(rq.getInn());

        Organization savedOrg = organizationRepository.save(org);

        User user = new User();
        user.setUsername(rq.getUsername());
        user.setEmail(rq.getEmail());
        user.setPasswordHash(passwordEncoder.encode(rq.getPassword()));
        user.setActive(true);
        user.setOrganization(savedOrg);

        Role ownerRole = roleRepository.findByName("fisherman")
                .orElseThrow(() -> new IllegalStateException("Role 'fisherman' not found"));
        UserRole link = new UserRole();
        link.setUser(user);
        link.setRole(ownerRole);
        user.getRoles().add(link);

        User savedUser = userRepository.save(user);

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(rq.getUsername(), rq.getPassword())
        );
        var principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        List<String> roles = principal.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();

        String token = tokenProvider.createToken(principal.getUsername(), roles);

        UserDto userDto = UserMapper.toDto(savedUser);
        OrganizationDto orgDto = OrganizationMapper.toDto(savedOrg);

        LoginResponse resp = new LoginResponse(token, "Bearer", userDto, orgDto, roles);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resp);
    }

    @GetMapping("/token-info")
    public ResponseEntity<?> tokenInfo(@RequestHeader(value = "Authorization", required = false) String authorization,
                                       @RequestParam(value = "token", required = false) String tokenParam) {
        String token = extractToken(authorization, tokenParam);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "token_missing", "message", "Provide token via Authorization header or 'token' query param"));
        }

        boolean expired;
        Claims claims;
        try {
            expired = tokenProvider.isExpired(token);
            claims = tokenProvider.parseClaimsAllowExpired(token);
        } catch (JwtException | IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "expired", false, "error", "invalid_token"));
        }

        String username = claims.getSubject();
        List<String> roles = tokenProvider.getRoles(claims);

        User userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found for token subject"));

        UserDto userDto = UserMapper.toDto(userEntity);
        OrganizationDto orgDto = null;
        if (userEntity.getOrganization() != null) {
            orgDto = OrganizationMapper.toDto(userEntity.getOrganization());
        }

        LoginResponse resp = new LoginResponse(token, "Bearer", userDto, orgDto, roles, expired, !expired);
        return ResponseEntity.ok(resp);
    }

    private String extractToken(String authorizationHeader, String tokenParam) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        if (tokenParam != null && !tokenParam.isBlank()) {
            return tokenParam;
        }
        return null;
    }
}
