
CREATE TABLE IF NOT EXISTS chambres (
                                        id BIGSERIAL PRIMARY KEY,
                                        numero VARCHAR(10) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    prix_par_nuit DECIMAL(10, 2) NOT NULL,
    nombre_lits INTEGER NOT NULL,
    description TEXT,
    photo_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_type CHECK (type IN ('SIMPLE', 'DOUBLE', 'SUITE', 'FAMILIALE')),
    CONSTRAINT chk_prix_positif CHECK (prix_par_nuit > 0),
    CONSTRAINT chk_lits_positif CHECK (nombre_lits > 0)
    );

-- Index sur numero pour recherches rapides
CREATE INDEX IF NOT EXISTS idx_chambre_numero ON chambres(numero);

-- Commentaires
COMMENT ON TABLE chambres IS 'Table des chambres d''hôtel disponibles';
COMMENT ON COLUMN chambres.numero IS 'Numéro unique de la chambre';
COMMENT ON COLUMN chambres.photo_url IS 'URL de la photo stockée sur MinIO';
