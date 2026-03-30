package com.example.munichway.models;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
@Getter
@Setter
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private double balance = 100;

    @OneToMany(mappedBy = "user")
    private List<Trip> trips;

    private boolean deleted = false;

    @Column //(nullable = false)
    private String password;

    @Enumerated(jakarta.persistence.EnumType.STRING)
    @Column //(nullable = false)
    private com.example.munichway.enums.Role role = com.example.munichway.enums.Role.USER;

    public User(){
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !deleted;
    }

}
