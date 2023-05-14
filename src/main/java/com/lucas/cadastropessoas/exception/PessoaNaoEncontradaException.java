package com.lucas.cadastropessoas.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PessoaNaoEncontradaException extends Exception {
    public PessoaNaoEncontradaException(Long id) {
        super(String.format("Pessoa com o ID %d não encontrada.", id));
    }
}
