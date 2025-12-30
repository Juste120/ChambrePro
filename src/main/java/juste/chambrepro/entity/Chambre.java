package juste.chambrepro.entity;

import jakarta.persistence.*;
import juste.chambrepro.enums.TypeChambre;
import juste.chambrepro.shared.utils.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.util.List;

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
@SuperBuilder
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

    @ElementCollection
    @CollectionTable(name = "chambre_photos", joinColumns = @JoinColumn(name = "chambre_id"))
    @Column(name = "photo_urls", length = 500)
    private List<String> photoUrls;

}
