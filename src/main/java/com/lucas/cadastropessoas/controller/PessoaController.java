package com.lucas.cadastropessoas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.lucas.cadastropessoas.dto.PessoaDTO;
import com.lucas.cadastropessoas.exception.CampoInvalidoException;
import com.lucas.cadastropessoas.exception.PessoaNaoEncontradaException;
import com.lucas.cadastropessoas.service.PessoaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pessoa")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public PessoaDTO cadastrar(@RequestBody @Valid PessoaDTO pessoaDTO) throws CampoInvalidoException {
        return pessoaService.cadastrar(pessoaDTO);
    }

    @GetMapping("/{id}")
    public PessoaDTO buscarUm(@PathVariable Long id) throws PessoaNaoEncontradaException {
        return pessoaService.buscarUm(id);
    }

    @GetMapping("/paginar")
    public Page<PessoaDTO> buscaPaginada(
            @RequestParam(value = "registros", required = false, defaultValue = "5") int registros,
            @RequestParam(value = "pagina", required = false, defaultValue = "0") int pagina) {
        return pessoaService.buscaPaginada(pagina, registros);
    }
}
