package ru.dstu.work.akselerator.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.dstu.work.akselerator.entity.Role;
import ru.dstu.work.akselerator.entity.User;
import ru.dstu.work.akselerator.entity.UserRole;
import ru.dstu.work.akselerator.repository.RoleRepository;
import ru.dstu.work.akselerator.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder encoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        ensureRole("admin", "Суперадмин системы");
        ensureRole("fisherman", "Рыбак (вносит уловы)");
        ensureRole("inspector", "Инспектор (верифицирует отчёты)");

        var adminOpt = userRepository.findByUsername("superadmin");
        User u = adminOpt.orElseGet(() -> {
            User x = new User();
            x.setUsername("superadmin");
            x.setEmail("admin@fishlog.ru");
            x.setActive(true);
            return x;
        });
        // если пароль не хэширован — хэшируем
        if (u.getPasswordHash() == null || !u.getPasswordHash().startsWith("$2")) {
            u.setPasswordHash(encoder.encode("admin123"));
        }
        // гарантируем роль admin
        Role admin = roleRepository.findByName("admin").orElseThrow();
        boolean hasAdmin = u.getRoles().stream()
                .anyMatch(ur -> ur.getRole() != null && "admin".equalsIgnoreCase(ur.getRole().getName()));
        if (!hasAdmin) {
            UserRole link = new UserRole();
            link.setUser(u);
            link.setRole(admin);
            u.getRoles().add(link);
        }
        userRepository.save(u);
    }

    private void ensureRole(String name, String description) {
        roleRepository.findByName(name).orElseGet(() -> roleRepository.save(new Role(name, description)));
    }
}
