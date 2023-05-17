package com.lucas.cadastropessoas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.lucas.cadastropessoas.builder.PessoaDTOBuilder;
import com.lucas.cadastropessoas.dto.PessoaDTO;
import com.lucas.cadastropessoas.entity.Pessoa;
import com.lucas.cadastropessoas.exception.CampoInvalidoException;
import com.lucas.cadastropessoas.exception.PessoaNaoEncontradaException;
import com.lucas.cadastropessoas.repository.PessoaRepository;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PessoaServiceTest {

    @MockBean
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaService pessoaService;

    @Test
    @DisplayName("Quando receber uma Pessoa válida deve gravá-la")
    public void quandoReceberUmaPessoaValidaDeveGravar() throws Exception {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();
        Pessoa pessoa = pessoaService.toModel(pessoaDTO);

        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        PessoaDTO pessoaCriada = pessoaService.cadastrar(pessoaDTO);

        assertEquals(pessoaDTO, pessoaCriada);

        verify(pessoaRepository, times(1)).save(any(Pessoa.class));
    }

    @ParameterizedTest
    @ValueSource(strings = { "contatos", "nome", "cpf", "dataNascimento" })
    @DisplayName("Quando receber uma Pessoa sem algum campo obrigatório deve levantar um erro")
    public void quandoReceberUmaPessoaSemAlgumCampoObrigatorioDeveLevantarUmErro(String campo) {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        switch (campo) {
            case "contatos":
                pessoaDTO.setContatos(null);
                break;
            case "nome":
                pessoaDTO.setNome(null);
                break;
            case "cpf":
                pessoaDTO.setCpf(null);
                break;
            case "dataNascimento":
                pessoaDTO.setDataNascimento(null);
                break;
        }

        assertThrows(CampoInvalidoException.class, () -> pessoaService.cadastrar(pessoaDTO));
    }

    @Test
    @DisplayName("Quando receber uma Pessoa sem ao menos um contato deve levantar um erro")
    public void quandoReceberUmaPessoaSemAoMenosUmContatoDeveLevantarUmErro() {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        pessoaDTO.getContatos().clear();

        assertThrows(CampoInvalidoException.class, () -> pessoaService.cadastrar(pessoaDTO));
    }

    @Test
    @DisplayName("Quando receber uma Pessoa com o campo CPF inválido deve levantar um erro")
    public void quandoReceberUmaPessoaComOCampoCPFInvalidoDeveLevantarUmErro() {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        pessoaDTO.setCpf("12345678910");

        assertThrows(CampoInvalidoException.class, () -> pessoaService.cadastrar(pessoaDTO));
    }

    @Test
    @DisplayName("Quando receber uma Pessoa com uma data futura no campo dataNascimento deve levantar um erro")
    public void quandoReceberUmaPessoaComUmaDataFuturaNoCampoDataNascimentoDeveLevantarUmErro() {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        Calendar dataFutura = Calendar.getInstance();
        dataFutura.add(Calendar.DAY_OF_MONTH, 1);

        pessoaDTO.setDataNascimento(dataFutura.getTime());

        assertThrows(CampoInvalidoException.class, () -> pessoaService.cadastrar(pessoaDTO));
    }

    @Test
    @DisplayName("Quando receber um ID cadastrado deve retornar uma Pessoa")
    public void quandoReceberUmIdCadastradoDeveRetornarUmaPessoa() throws Exception {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();
        Pessoa pessoa = pessoaService.toModel(pessoaDTO);

        when(pessoaRepository.findById(any(Long.class))).thenReturn(Optional.of(pessoa));

        PessoaDTO pessoaDTOEncontrada = pessoaService.buscarUm(pessoaDTO.getId());

        assertEquals(pessoaDTO, pessoaDTOEncontrada);
    }

    @Test
    @DisplayName("Quando receber um ID não cadastrado deve levantar um erro")
    public void quandoReceberUmIdNaoCadastradoDeveLevantarUmErro() throws Exception {
        when(pessoaRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(PessoaNaoEncontradaException.class, () -> pessoaService.buscarUm(1L));
    }

    @Test
    @DisplayName("Quando receber um ID não cadastrado deve levantar um erro")
    public void quandoReceberUmIdDeveDeletarUmUsuario() throws Exception {
        doNothing().when(pessoaRepository).deleteById(any(Long.class));

        pessoaService.deletar(1L);

        verify(pessoaRepository, times(1)).deleteById(any(Long.class));
    }

    // @Test
    // @DisplayName("Quando receber um Id e dados validos deve atualizar a pessoa")
    // public void quandoReceberUmIdEDadosValidosDeveAtualizarAPessoa() throws
    // Exception {
    // PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();
    // Pessoa pessoaExperada = pessoaService.toModel(pessoaDTO);

    // when(this.pessoaService.buscarUm(any(Long.class))).thenReturn(pessoaDTO);
    // when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoaExperada);

    // PessoaDTO pessoaAtualizada = pessoaService.atualizar(pessoaDTO.getId(),
    // pessoaDTO);

    // assertEquals(pessoaDTO, pessoaAtualizada);

    // verify(pessoaRepository, times(1)).save(any(Pessoa.class));
    // verify(pessoaService, times(1)).buscarUm(any(Long.class));
    // }
}
