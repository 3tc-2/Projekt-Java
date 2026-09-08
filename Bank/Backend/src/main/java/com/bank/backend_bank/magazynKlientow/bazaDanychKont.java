package com.bank.backend_bank.magazynKlientow;


import com.bank.backend_bank.models.Konto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface bazaDanychKont extends JpaRepository<Konto, Long> {
    /*
    https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html#page-title

    doslownie zrobienie tego interface pozwolilo na wyszukiwanie klientow
     po id jest to potrzebne do wysylania przelewow nic nie musialem zrobic XD

    ten interface moze robic wiele wiecej rzeczy ale narazie nie jest to potrzebne
    */



    //ciekawe wlasnie przeczytalem jak sie robi lock mamy 2 opcje optimistic i pessimistic czy jakos tak
    // sadze ze jezeli to jest bank i kasa to powinnismy uzyc pesymisty moze mam racje

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT k FROM Konto k WHERE k.id = :id")
    public Optional<Konto> findByIdLock(@Param("id") Long id);

}