package org.example.authenticationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false, unique = true)
    private String token;
    @Column(name = "expiry_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;
    private boolean revoked;
}
