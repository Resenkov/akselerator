package ru.dstu.work.akselerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "user_roles")
public class UserRole {

    @Setter
    @EmbeddedId
    private UserRoleId id = new UserRoleId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Setter
    @Column(name = "granted_at", nullable = false)
    private OffsetDateTime grantedAt = OffsetDateTime.now();

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by")
    private User grantedBy;

    public UserRole() {}

    public UserRole(User user, Role role, User grantedBy) {
        this.user = user;
        this.role = role;
        this.grantedBy = grantedBy;
        this.id = new UserRoleId(user.getId(), role.getId());
        this.grantedAt = OffsetDateTime.now();
    }

    public void setUser(User user) { this.user = user; if (user != null) this.id.setUserId(user.getId()); }

    public void setRole(Role role) { this.role = role; if (role != null) this.id.setRoleId(role.getId()); }

}
