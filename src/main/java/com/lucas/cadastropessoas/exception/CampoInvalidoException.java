package com.lucas.cadastropessoas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CampoInvalidoException extends Exception {
    public CampoInvalidoException(String campo) {
        super(String.format("Campo %s inválido.", campo));
    }
}
