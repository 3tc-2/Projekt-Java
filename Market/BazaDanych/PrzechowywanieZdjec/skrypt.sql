USE Market_Baza;

CREATE TABLE IF NOT EXISTS Zdjecia (
    id_zdjecia INT AUTO_INCREMENT PRIMARY KEY,
    id_ogloszenia INT NOT NULL,
    typ VARCHAR(50) NOT NULL,
    zdjecie LONGBLOB NOT NULL,

    CONSTRAINT fk_zdjecia_ogloszenia
        FOREIGN KEY (id_ogloszenia)
        REFERENCES Ogloszenia(id_ogloszenia)
        ON DELETE CASCADE
) ENGINE=InnoDB;
