package com.bank.backend_bank.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {

    private String name;
    private String surname;
    private String email;
    private String password;
    private String pesel;
    private String telefon;
}