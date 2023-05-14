package com.lucas.cadastropessoas.builder;

import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;

import com.lucas.cadastropessoas.dto.ContatoDTO;
import com.lucas.cadastropessoas.dto.PessoaDTO;

import lombok.Builder;

@Builder
public class PessoaDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String nome = "Nome teste";

    @Builder.Default
    private String cpf = "42601209054";

    @Builder.Default
    private Date dataNascimento = createPastDate();

    @Builder.Default
    private List<ContatoDTO> contatos = new ArrayList<ContatoDTO>(
            Arrays.asList(ContatoDTOBuilder.builder().build().toContatoDTO()));

    public PessoaDTO toPessoaDTO() {
        return new PessoaDTO(id, nome, cpf, dataNascimento, contatos);
    }

    static Date createPastDate() {
        Calendar pastDate = Calendar.getInstance();
        pastDate.add(Calendar.DAY_OF_MONTH, -1);
        return pastDate.getTime();
    }
}
