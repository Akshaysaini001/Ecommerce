package com.akshay.ecommerce.entity;
import com.akshay.ecommerce.commonUsage.audits;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends audits {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String password;
    private boolean isDeleted;
    private boolean isActive;
    private boolean isExpired;
    private boolean isLocked;
    private int invalidAttemptCount = 0;
    private LocalDateTime passwordUpdateDate = LocalDateTime.now();
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name ="user_id"),
            inverseJoinColumns = @JoinColumn(name ="role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }

    public void addRole(Role role) {
        if (role == null) return;
        if (roles == null) roles = new HashSet<>();
        roles.add(role);
    }

    public void addRoles(Collection<Role> rolesToAdd) {
        if (rolesToAdd == null || rolesToAdd.isEmpty()) return;
        if (roles == null) roles = new HashSet<>();
        roles.addAll(rolesToAdd);
    }

    private LocalDateTime lockedAt;

//isme role ka methpd banado
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();
}



//
//@Getter
//@Setter
//@Entity
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;
//    private String firstName;
//    private String lastName;
//    private String middleName;
//    private String email;
//    private String password;
//    private boolean isDeleted;
//    private boolean isActive;
//    private boolean isExpired;
//    private boolean isLocked;
//    private int invalidAttemptCount =0;
//    private LocalDateTime passwordUpdateDate = LocalDateTime.now();
//    @ManyToMany
//    @JoinTable(
//          name = "user_role",
//            joinColumns = @JoinColumn(name ="user_id"),
//            inverseJoinColumns = @JoinColumn(name ="role_id")
//    )
//    private Set<Role> roles = new HashSet<>();
//    @OneToOne(mappedBy = "user")
//    private Seller seller;
//    @OneToOne(mappedBy = "user")
//    private Customer customer;
//    @OneToMany(mappedBy = "user")
//    private List<Address> addresses;
//
//}
