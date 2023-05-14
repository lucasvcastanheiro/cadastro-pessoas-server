package com.lucas.cadastropessoas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Calendar;

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
import com.lucas.cadastropessoas.repository.PessoaRepository;
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

        assertThrows(IllegalArgumentException.class, () -> pessoaService.cadastrar(pessoaDTO));
    }

    @ParameterizedTest
    @ValueSource(strings = { "nome", "telefone", "email" })
    @DisplayName("Quando receber uma Pessoa com algum contato sem os campos obrigatórios deve levantar um erro")
    public void quandoReceberUmaPessoaComAlgumContatoSemOsCamposObrigatóriosDeveLevantarUmErro(String campo) {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        switch (campo) {
            case "nome":
                pessoaDTO.getContatos().get(0).setNome(null);
                break;
            case "telefone":
                pessoaDTO.getContatos().get(0).setTelefone(null);
                break;
            case "email":
                pessoaDTO.getContatos().get(0).setEmail(null);
                break;
        }

        assertThrows(IllegalArgumentException.class, () -> pessoaService.cadastrar(pessoaDTO));
    }

    @Test
    @DisplayName("Quando receber uma Pessoa sem ao menos um contato deve levantar um erro")
    public void quandoReceberUmaPessoaSemAoMenosUmContatoDeveLevantarUmErro() {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        pessoaDTO.getContatos().clear();

        assertThrows(IllegalArgumentException.class, () -> pessoaService.cadastrar(pessoaDTO));
    }

    @Test
    @DisplayName("Quando receber uma Pessoa com o campo CPF inválido deve levantar um erro")
    public void quandoReceberUmaPessoaComOCampoCPFInvalidoDeveLevantarUmErro() {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        pessoaDTO.setCpf("11111111111");

        assertThrows(IllegalArgumentException.class, () -> pessoaService.cadastrar(pessoaDTO));
    }

    @Test
    @DisplayName("Quando receber uma Pessoa com uma data futura no campo dataNascimento deve levantar um erro")
    public void quandoReceberUmaPessoaComUmaDataFuturaNoCampoDataNascimentoDeveLevantarUmErro() {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        Calendar dataFutura = Calendar.getInstance();
        dataFutura.add(Calendar.DAY_OF_MONTH, 1);

        pessoaDTO.setDataNascimento(dataFutura.getTime());

        assertThrows(IllegalArgumentException.class, () -> pessoaService.cadastrar(pessoaDTO));
    }

    @Test
    @DisplayName("Quando receber uma Pessoa com algum contato com o campo email inválido deve levantar um erro")
    public void quandoReceberUmaPessoaComAlgumContatoComOCampoEmailInvalidoDeveLevantarUmErro() {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        pessoaDTO.getContatos().get(0).setEmail("emailInvalido");

        assertThrows(IllegalArgumentException.class, () -> pessoaService.cadastrar(pessoaDTO));

    }
}
