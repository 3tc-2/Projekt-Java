package com.bank.backend_bank.magazynKlientow;

import com.bank.backend_bank.models.Klient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface bazaDanychKlientow extends JpaRepository<Klient, Long> {
    /*
    https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html#page-title

    doslownie zrobienie tego interface pozwolilo na wyszukiwanie klientow
     po id jest to potrzebne do wysylania przelewow nic nie musialem zrobic XD

    ten interface moze robic wiele wiecej rzeczy ale narazie nie jest to potrzebne
    */


    boolean czyNumerZajety(String numerKlienta);

    Klient findByNumerKlienta(String numerKlienta);

}