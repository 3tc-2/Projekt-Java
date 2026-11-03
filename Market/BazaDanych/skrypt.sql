
-- =========================================
-- TWORZENIE BAZY DANYCH
-- =========================================

CREATE DATABASE IF NOT EXISTS Market_Baza
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE Market_Baza;


-- =========================================
-- 1. UZYTKOWNICY
-- =========================================

CREATE TABLE Uzytkownicy (
    id_uzytkownika INT AUTO_INCREMENT PRIMARY KEY,
    imie VARCHAR(30) NOT NULL,
    nazwisko VARCHAR(30) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    haslo VARCHAR(100) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;


-- =========================================
-- 2. PORTFELE
-- =========================================

CREATE TABLE Portfele (
    id_portfela INT AUTO_INCREMENT PRIMARY KEY,
    saldo INT,
    id_uzytkownika INT,

    CONSTRAINT fk_portfele_uzytkownicy
        FOREIGN KEY (id_uzytkownika)
        REFERENCES Uzytkownicy(id_uzytkownika)
) ENGINE=InnoDB;


-- =========================================
-- 3. OGLOSZENIA
-- =========================================

CREATE TABLE Ogloszenia (
    id_ogloszenia INT AUTO_INCREMENT PRIMARY KEY,
    tytul VARCHAR(30),
    opis VARCHAR(300),
    kategoria VARCHAR(30),
    cena INT,
    id_uzytkownika INT,

    CONSTRAINT fk_ogloszenia_uzytkownicy
        FOREIGN KEY (id_uzytkownika)
        REFERENCES Uzytkownicy(id_uzytkownika)
) ENGINE=InnoDB;


-- =========================================
-- 4. TRANSAKCJE BANKOWE
-- =========================================

CREATE TABLE Transakcje_Bankowe (
    id_transakcji INT AUTO_INCREMENT PRIMARY KEY,
    id_portfela INT,
    nr_konta VARCHAR(50),
    typ VARCHAR(30),
    kwota INT,
    status VARCHAR(30),
    data DATETIME,

    CONSTRAINT fk_transakcje_bankowe_portfele
        FOREIGN KEY (id_portfela)
        REFERENCES Portfele(id_portfela)
) ENGINE=InnoDB;


-- =========================================
-- 5. TRANSAKCJE PORTFELOWE
-- =========================================

CREATE TABLE Transakcje_Portfelowe (
    id_transakcji INT AUTO_INCREMENT PRIMARY KEY,
    id_nadawcy INT,
    id_odbiorcy INT,
    kwota INT,
    status VARCHAR(30),
    data DATETIME,

    CONSTRAINT fk_transakcje_portfelowe_nadawca
        FOREIGN KEY (id_nadawcy)
        REFERENCES Portfele(id_portfela),

    CONSTRAINT fk_transakcje_portfelowe_odbiorca
        FOREIGN KEY (id_odbiorcy)
        REFERENCES Portfele(id_portfela)
) ENGINE=InnoDB;


-- =========================================
-- 6. ZAMOWIENIA
-- =========================================

CREATE TABLE Zamowienia (
    id_zamowienia INT AUTO_INCREMENT PRIMARY KEY,
    id_transakcji INT,
    id_ogloszenia INT,

    CONSTRAINT fk_zamowienia_transakcje
        FOREIGN KEY (id_transakcji)
        REFERENCES Transakcje_Portfelowe(id_transakcji),

    CONSTRAINT fk_zamowienia_ogloszenia
        FOREIGN KEY (id_ogloszenia)
        REFERENCES Ogloszenia(id_ogloszenia)
) ENGINE=InnoDB;


-- =========================================
-- KONIEC SKRYPTU
-- =========================================
