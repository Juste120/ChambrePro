-- Ajout de la colonne tracking_id à toutes les tables

-- Table clients
ALTER TABLE clients ADD COLUMN IF NOT EXISTS tracking_id UUID DEFAULT gen_random_uuid() NOT NULL UNIQUE;

-- Table chambres
ALTER TABLE chambres ADD COLUMN IF NOT EXISTS tracking_id UUID DEFAULT gen_random_uuid() NOT NULL UNIQUE;

-- Table reservations
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS tracking_id UUID DEFAULT gen_random_uuid() NOT NULL UNIQUE;

-- Index pour recherches rapides par tracking_id
CREATE INDEX IF NOT EXISTS idx_client_tracking_id ON clients(tracking_id);
CREATE INDEX IF NOT EXISTS idx_chambre_tracking_id ON chambres(tracking_id);
CREATE INDEX IF NOT EXISTS idx_reservation_tracking_id ON reservations(tracking_id);

-- Commentaires
COMMENT ON COLUMN clients.tracking_id IS 'Identifiant unique de suivi (UUID)';
COMMENT ON COLUMN chambres.tracking_id IS 'Identifiant unique de suivi (UUID)';
COMMENT ON COLUMN reservations.tracking_id IS 'Identifiant unique de suivi (UUID)';
