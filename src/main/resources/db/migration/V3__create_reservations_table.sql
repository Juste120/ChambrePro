CREATE TABLE IF NOT EXISTS reservations (
                                            id BIGSERIAL PRIMARY KEY,
                                            date_debut DATE NOT NULL,
                                            date_fin DATE NOT NULL,
                                            statut VARCHAR(20) NOT NULL DEFAULT 'RESERVE',
    client_id BIGINT NOT NULL,
    chambre_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Contraintes
    CONSTRAINT chk_statut CHECK (statut IN ('RESERVE', 'ANNULE', 'TERMINE')),
    CONSTRAINT chk_dates CHECK (date_debut <= date_fin),

    -- Clés étrangères
    CONSTRAINT fk_reservation_client
    FOREIGN KEY (client_id)
    REFERENCES clients(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_reservation_chambre
    FOREIGN KEY (chambre_id)
    REFERENCES chambres(id)
    ON DELETE CASCADE
    );

-- Index composites pour recherches de disponibilités
CREATE INDEX IF NOT EXISTS idx_reservation_dates
    ON reservations(date_debut, date_fin);

-- Index sur statut pour filtres rapides
CREATE INDEX IF NOT EXISTS idx_reservation_statut
    ON reservations(statut);

-- Index sur client_id pour requêtes par client
CREATE INDEX IF NOT EXISTS idx_reservation_client
    ON reservations(client_id);

-- Index sur chambre_id pour requêtes par chambre
CREATE INDEX IF NOT EXISTS idx_reservation_chambre
    ON reservations(chambre_id);

-- Commentaires
COMMENT ON TABLE reservations IS 'Table des réservations de chambres';
COMMENT ON COLUMN reservations.statut IS 'Statut : RESERVE, ANNULE ou TERMINE';
COMMENT ON CONSTRAINT chk_dates ON reservations IS 'La date de début doit être antérieure ou égale à la date de fin';
