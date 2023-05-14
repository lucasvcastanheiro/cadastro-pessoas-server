package com.lucas.cadastropessoas.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lucas.cadastropessoas.dto.PessoaDTO;
import com.lucas.cadastropessoas.entity.Pessoa;
import com.lucas.cadastropessoas.repository.PessoaRepository;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ModelMapper modelMapper;

    public PessoaDTO cadastrar(PessoaDTO pessoaDTO) {
        Pessoa pessoa = toModel(pessoaDTO);
        Pessoa pessoaInserida = pessoaRepository.save(pessoa);

        return toDto(pessoaInserida);
    }

    public Pessoa toModel(PessoaDTO pessoaDTO) {
        return modelMapper.map(pessoaDTO, Pessoa.class);
    }

    public PessoaDTO toDto(Pessoa pessoa) {
        return modelMapper.map(pessoa, PessoaDTO.class);
    }
}
