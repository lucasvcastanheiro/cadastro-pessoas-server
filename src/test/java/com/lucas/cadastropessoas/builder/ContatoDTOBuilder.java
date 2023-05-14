package com.lucas.cadastropessoas.builder;

import com.lucas.cadastropessoas.dto.ContatoDTO;

import lombok.Builder;

@Builder
public class ContatoDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String nome = "Nome teste";

    @Builder.Default
    private String telefone = "9999999999";

    @Builder.Default
    private String email = "teste@email.com";

    public ContatoDTO toContatoDTO() {
        return new ContatoDTO(id, nome, telefone, email);
    }
}
