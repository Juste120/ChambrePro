-- This script ensures the 'chambre_photos' table is correctly set up for the @ElementCollection

-- Step 1: Add a temporary column to hold the old photo_url data if it exists.
ALTER TABLE chambres ADD COLUMN IF NOT EXISTS temp_photo_url VARCHAR(500);

-- Step 2: If the original photo_url column exists, copy its data to the temporary column.
DO $$
BEGIN
   IF EXISTS(SELECT 1 FROM information_schema.columns WHERE table_name='chambres' AND column_name='photo_url') THEN
      UPDATE chambres SET temp_photo_url = photo_url;
   END IF;
END $$;

-- Step 3: Drop the old photo_url column if it exists, to avoid conflicts.
ALTER TABLE chambres DROP COLUMN IF EXISTS photo_url;

-- Step 4: Drop the chambre_photos table if it exists, to start fresh.
DROP TABLE IF EXISTS chambre_photos;

-- Step 5: Create the chambre_photos table with the schema Hibernate expects.
CREATE TABLE chambre_photos (
    chambre_id BIGINT NOT NULL,
    photo_urls VARCHAR(255),
    CONSTRAINT fk_chambre_photos_to_chambre FOREIGN KEY (chambre_id) REFERENCES chambres (id) ON DELETE CASCADE
);

-- Step 6: Migrate the data from the temporary column into the new collection table.
INSERT INTO chambre_photos (chambre_id, photo_urls)
SELECT id, temp_photo_url FROM chambres WHERE temp_photo_url IS NOT NULL AND temp_photo_url <> '';

-- Step 7: Remove the temporary column as it's no longer needed.
ALTER TABLE chambres DROP COLUMN IF EXISTS temp_photo_url;
