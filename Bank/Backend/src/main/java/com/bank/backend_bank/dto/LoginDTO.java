package com.bank.backend_bank.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LoginDTO {

    private String password;
    private String pesel;
    private String numerKlienta;

}
