package com.lucas.cadastropessoas.dto;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PessoaDTO {

    private Long id;

    @NotNull
    @NotEmpty
    private String nome;

    @NotNull
    @NotEmpty
    @CPF
    private String cpf;

    @NotNull
    @DateTimeFormat
    @Past
    private Date dataNascimento;

    @NotNull
    @NotEmpty
    @Valid
    private List<ContatoDTO> contatos;
}
