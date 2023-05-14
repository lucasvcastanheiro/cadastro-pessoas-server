package com.lucas.cadastropessoas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.lucas.cadastropessoas.dto.PessoaDTO;
import com.lucas.cadastropessoas.service.PessoaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pessoa")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public PessoaDTO cadastrar(@RequestBody @Valid PessoaDTO pessoaDTO) {
        return pessoaService.cadastrar(pessoaDTO);
    }
}
