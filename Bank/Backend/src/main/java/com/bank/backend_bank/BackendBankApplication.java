package com.bank.backend_bank;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class BackendBankApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BackendBankApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Scanner scanner = new Scanner(System.in);

		System.out.println("To jest test");

		String test = scanner.nextLine();
		System.out.println(test);
	}
}