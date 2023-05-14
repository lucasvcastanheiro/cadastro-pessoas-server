package com.lucas.cadastropessoas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContatoDTO {

    private Long id;

    @NotEmpty
    @NotNull
    private String nome;

    @NotEmpty
    @NotNull
    private String telefone;

    @NotEmpty
    @NotNull
    @Email
    private String email;
}
