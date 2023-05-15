package com.lucas.cadastropessoas.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.lucas.cadastropessoas.dto.PessoaDTO;
import com.lucas.cadastropessoas.entity.Pessoa;
import com.lucas.cadastropessoas.exception.CampoInvalidoException;
import com.lucas.cadastropessoas.exception.PessoaNaoEncontradaException;
import com.lucas.cadastropessoas.repository.PessoaRepository;
import com.lucas.cadastropessoas.validator.ValidarCPF;
import com.lucas.cadastropessoas.validator.ValidarDataFutura;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ModelMapper modelMapper;

    public PessoaDTO cadastrar(PessoaDTO pessoaDTO) throws CampoInvalidoException {
        if (pessoaDTO.getNome() == null || pessoaDTO.getNome().isEmpty()) {
            throw new CampoInvalidoException("nome");
        }

        if (!ValidarCPF.cpfValido(pessoaDTO.getCpf())) {
            throw new CampoInvalidoException("cpf");
        }

        if (ValidarDataFutura.dataFutura(pessoaDTO.getDataNascimento())) {
            throw new CampoInvalidoException("dataNascimento");
        }

        if (pessoaDTO.getContatos() == null || pessoaDTO.getContatos().isEmpty()) {
            throw new CampoInvalidoException("contatos");
        }

        Pessoa pessoa = toModel(pessoaDTO);
        Pessoa pessoaInserida = pessoaRepository.save(pessoa);

        return toDto(pessoaInserida);
    }

    public PessoaDTO buscarUm(Long id) throws PessoaNaoEncontradaException {
        Pessoa pessoaEncontrada = pessoaRepository.findById(id).orElseThrow(() -> new PessoaNaoEncontradaException(id));
        return toDto(pessoaEncontrada);
    }

    public Page<PessoaDTO> buscaPaginada(int pagina, int registros) {
        PageRequest paginacao = PageRequest.of(pagina, registros);

        return pessoaRepository.buscaPaginada(paginacao).map(this::toDto);
    }

    public Pessoa toModel(PessoaDTO pessoaDTO) {
        return modelMapper.map(pessoaDTO, Pessoa.class);
    }

    public PessoaDTO toDto(Pessoa pessoa) {
        return modelMapper.map(pessoa, PessoaDTO.class);
    }
}
