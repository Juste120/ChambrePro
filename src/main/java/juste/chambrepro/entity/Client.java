package juste.chambrepro.entity;

import jakarta.persistence.*;
import juste.chambrepro.enums.Role;
import juste.chambrepro.shared.utils.BaseEntity;
import lombok.*;

@Entity
@Table(
        name = "clients",
        indexes = {
                @Index(name = "idx_client_email", columnList = "email", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;


}
