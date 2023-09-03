CREATE TABLE categorie (
    id_categorie VARCHAR(50) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);

CREATE SEQUENCE seq_id_actualite
    INCREMENT 1
    MINVALUE 1 
    MAXVALUE 1000
    CYCLE;

CREATE TABLE actualite (
    id_actualite VARCHAR(50) PRIMARY KEY,
    titre VARCHAR(100) NOT NULL,
    id_categorie VARCHAR(50) REFERENCES categorie(id_categorie),
    date TIMESTAMP NOT NULL DEFAULT NOW(),
    photo VARCHAR(50) NOT NULL
);

CREATE VIEW v_actualite AS
SELECT *
FROM actualite
WHERE date <= NOW();