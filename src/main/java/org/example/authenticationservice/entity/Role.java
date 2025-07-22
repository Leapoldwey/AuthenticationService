package org.example.authenticationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.authenticationservice.enumer.RoleType;

import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {
    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleType name;
}
