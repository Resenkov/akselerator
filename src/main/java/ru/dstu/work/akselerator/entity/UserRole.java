package ru.dstu.work.akselerator.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_roles")
public class UserRole {

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

    @Column(name = "granted_at", nullable = false)
    private OffsetDateTime grantedAt = OffsetDateTime.now();

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

    public UserRoleId getId() { return id; }
    public void setId(UserRoleId id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; if (user != null) this.id.setUserId(user.getId()); }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; if (role != null) this.id.setRoleId(role.getId()); }
    public OffsetDateTime getGrantedAt() { return grantedAt; }
    public void setGrantedAt(OffsetDateTime grantedAt) { this.grantedAt = grantedAt; }
    public User getGrantedBy() { return grantedBy; }
    public void setGrantedBy(User grantedBy) { this.grantedBy = grantedBy; }
}
