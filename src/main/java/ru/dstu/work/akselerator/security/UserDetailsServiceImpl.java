package ru.dstu.work.akselerator.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ru.dstu.work.akselerator.entity.User;
import ru.dstu.work.akselerator.entity.UserRole;
import ru.dstu.work.akselerator.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Загружает пользователя по username; ожидает, что репозиторий подгружает роли (см. @EntityGraph).
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Собираем роли из связующей сущности UserRole -> Role
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(UserRole::getRole)                  // Role
                .filter(r -> r != null && r.getName() != null)
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName().toUpperCase()))
                .collect(Collectors.toList());

        // Spring Security User (uses passwordHash as password)
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .disabled(!user.isActive())
                .build();
    }
}
