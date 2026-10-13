-- Table des demandes de diplômes
CREATE TABLE IF NOT EXISTS demandes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    type_diplome TEXT NOT NULL CHECK (type_diplome IN ('CEPE', 'BEPC', 'BACC')),
    nom_complet TEXT NOT NULL,
    numero_examen TEXT NOT NULL UNIQUE,
    annee_examen INTEGER NOT NULL,
    lieu_examen TEXT NOT NULL,
    cin TEXT,
    date_demande DATE NOT NULL DEFAULT CURRENT_DATE,
    date_reception DATE,
    date_livraison DATE,
    statut TEXT NOT NULL DEFAULT 'EN_COURS' CHECK (statut IN ('EN_COURS', 'RECU', 'LIVRE')),
    verrouille BOOLEAN NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_demandes_type ON demandes(type_diplome);
CREATE INDEX IF NOT EXISTS idx_demandes_statut ON demandes(statut);
CREATE INDEX IF NOT EXISTS idx_demandes_numero ON demandes(numero_examen);
CREATE INDEX IF NOT EXISTS idx_demandes_dates ON demandes(date_demande, date_reception, date_livraison);

-- Table des statistiques (pour le dashboard)
CREATE TABLE IF NOT EXISTS statistiques_mensuelles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    annee INTEGER NOT NULL,
    mois INTEGER NOT NULL,
    total_demandes INTEGER DEFAULT 0,
    demandes_en_cours INTEGER DEFAULT 0,
    demandes_recues INTEGER DEFAULT 0,
    demandes_livrees INTEGER DEFAULT 0,
    UNIQUE(annee, mois)
);

-- Données initiales pour les tests
INSERT OR IGNORE INTO demandes (type_diplome, nom_complet, numero_examen, annee_examen, lieu_examen, cin, date_demande, statut) VALUES
('CEPE', 'Jean Dupont', 'CEPE2024001', 2024, 'Ecole Primaire Antananarivo', '101234567890', '2024-01-15', 'EN_COURS'),
('CEPE', 'Marie Rakoto', 'CEPE2024002', 2024, 'Ecole Primaire Antsirabe', '101234567891', '2024-01-16', 'RECU'),
('CEPE', 'Pierre Randria', 'CEPE2024003', 2024, 'Ecole Primaire Toamasina', '101234567892', '2024-01-17', 'LIVRE'),
('BEPC', 'Sophie Rabe', 'BEPC2024001', 2024, 'Lycée Antananarivo', '101234567893', '2024-01-18', 'EN_COURS'),
('BEPC', 'Luc Andria', 'BEPC2024002', 2024, 'Lycée Fianarantsoa', '101234567894', '2024-01-19', 'RECU');

-- Déclencheur pour mettre à jour automatiquement le verrouillage
CREATE TRIGGER IF NOT EXISTS verrouillage_automatique
AFTER UPDATE OF statut ON demandes
FOR EACH ROW
WHEN NEW.statut = 'LIVRE' AND OLD.statut != 'LIVRE'
BEGIN
    UPDATE demandes 
    SET verrouille = 1, 
        date_livraison = CURRENT_DATE,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.id;
END;

-- Déclencheur pour la date de réception
CREATE TRIGGER IF NOT EXISTS date_reception_automatique
AFTER UPDATE OF statut ON demandes
FOR EACH ROW
WHEN NEW.statut = 'RECU' AND OLD.statut != 'RECU'
BEGIN
    UPDATE demandes 
    SET date_reception = CURRENT_DATE,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.id;
END;