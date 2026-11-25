USE Market_Baza;

CREATE TABLE IF NOT EXISTS Zdjecia (
    id_zdjecia INT AUTO_INCREMENT PRIMARY KEY,
    id_ogloszenia INT NOT NULL,
    adres VARCHAR(255) NOT NULL,

    FOREIGN KEY (id_ogloszenia)
        REFERENCES Ogloszenia(id_ogloszenia)
        ON DELETE CASCADE
) ENGINE=InnoDB;
