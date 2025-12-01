package ru.dstu.work.akselerator.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

        User userEntity = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        UserDto userDto = UserMapper.toDto(userEntity);
        OrganizationDto orgDto = null;
        if (userEntity.getOrganization() != null) {
            orgDto = OrganizationMapper.toDto(userEntity.getOrganization());
        }

        LoginResponse resp = new LoginResponse(token, "Bearer", userDto, orgDto);
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

        LoginResponse resp = new LoginResponse(token, "Bearer", userDto, orgDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resp);
    }
}
