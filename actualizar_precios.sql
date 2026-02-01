-- Script para actualizar precios de eventos con valor 0 o NULL
-- Genera precios aleatorios realistas basados en el género del artista

UPDATE evento e
INNER JOIN evento_artista ea ON e.id = ea.evento_id
INNER JOIN artista a ON ea.artista_id = a.id
SET e.precio = CASE
    -- Pop / Dance / Electronic: 40-70€
    WHEN LOWER(a.genero) IN ('pop', 'dance', 'electronic') THEN 40 + (RAND() * 30) + IF(RAND() > 0.5, 0.99, 0.50)

    -- Hip-Hop / Rap / R&B: 38-65€
    WHEN LOWER(a.genero) IN ('hip-hop', 'rap', 'r&b', 'hip hop') THEN 38 + (RAND() * 27) + IF(RAND() > 0.5, 0.99, 0.50)

    -- Rock / Metal / Alternative: 35-60€
    WHEN LOWER(a.genero) IN ('rock', 'metal', 'alternative') THEN 35 + (RAND() * 25) + IF(RAND() > 0.5, 0.99, 0.50)

    -- Reggae / Latin / World: 32-54€
    WHEN LOWER(a.genero) IN ('reggae', 'latin', 'world') THEN 32 + (RAND() * 22) + IF(RAND() > 0.5, 0.99, 0.50)

    -- Jazz / Blues / Classical: 30-50€
    WHEN LOWER(a.genero) IN ('jazz', 'blues', 'classical') THEN 30 + (RAND() * 20) + IF(RAND() > 0.5, 0.99, 0.50)

    -- Country / Folk: 28-46€
    WHEN LOWER(a.genero) IN ('country', 'folk') THEN 28 + (RAND() * 18) + IF(RAND() > 0.5, 0.99, 0.50)

    -- General / Otros: 35-60€
    ELSE 35 + (RAND() * 25) + IF(RAND() > 0.5, 0.99, 0.50)
END
WHERE (e.precio IS NULL OR e.precio <= 5.0);

-- Verificar cuántos registros se actualizaron
SELECT COUNT(*) as 'Eventos actualizados' FROM evento WHERE precio > 5.0;

-- Mostrar algunos ejemplos de los nuevos precios
SELECT e.titulo, e.precio, a.genero
FROM evento e
INNER JOIN evento_artista ea ON e.id = ea.evento_id
INNER JOIN artista a ON ea.artista_id = a.id
WHERE e.precio > 5.0
LIMIT 20;
