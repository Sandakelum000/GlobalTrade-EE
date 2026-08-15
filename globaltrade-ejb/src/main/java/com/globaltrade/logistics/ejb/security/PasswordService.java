package com.globaltrade.logistics.ejb.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;

@ApplicationScoped
public class PasswordService {
    private Argon2 argon2;

    @PostConstruct
    public void init() {
        argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    }

    public String hashPassword(String password) {
        char[] passwordChars = password.toCharArray();
        try{
            return argon2.hash(
                    3,
                    65536,
                    1,
                    passwordChars
            );
        }finally {
            Arrays.fill(passwordChars, '\0');
        }
    }

    public boolean verifyPassword(String password, String hashedPassword) {
        char[] passwordChars = password.toCharArray();
        try {
            return argon2.verify(hashedPassword,passwordChars);
        }finally {
            Arrays.fill(passwordChars, '\0');
        }
    }

    @PreDestroy
    public void destroy() {
        argon2 = null;
    }
}
