package juste.chambrepro.entity;

import jakarta.persistence.*;
import juste.chambrepro.enums.TypeChambre;
import juste.chambrepro.shared.utils.BaseEntity;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "chambres",
        indexes = {
                @Index(name = "idx_chambre_numero", columnList = "numero")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chambre extends BaseEntity {

    @Column(nullable = false, unique = true, length = 10)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeChambre type;

    @Column(name = "prix_par_nuit", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixParNuit;

    @Column(name = "nombre_lits", nullable = false)
    private Integer nombreLits;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

}
