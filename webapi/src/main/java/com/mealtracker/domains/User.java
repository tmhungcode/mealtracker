package com.mealtracker.domains;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Transient
    private String password;

    @Column(name = "encrypted_password", nullable = false)
    private String encryptedPassword;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "role")
    @Enumerated(EnumType.ORDINAL)
    private Role role;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Embedded
    private UserSettings userSettings;

    public List<Privilege> getPrivileges() {
        return role.getPrivileges();
    }

    public boolean isEnabled() {
        return !deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", fullName='" + fullName + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
