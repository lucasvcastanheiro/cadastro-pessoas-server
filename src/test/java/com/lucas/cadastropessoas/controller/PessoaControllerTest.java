package com.lucas.cadastropessoas.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucas.cadastropessoas.builder.PessoaDTOBuilder;
import com.lucas.cadastropessoas.dto.PessoaDTO;
import com.lucas.cadastropessoas.exception.CampoInvalidoException;
import com.lucas.cadastropessoas.exception.PessoaNaoEncontradaException;
import com.lucas.cadastropessoas.service.PessoaService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Calendar;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PessoaControllerTest {

    private final String BASE_URL = "/pessoa";

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    private PessoaController pessoaController;

    @MockBean
    private PessoaService pessoaService;

    @BeforeAll
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(pessoaController)
                .build();
    }

    @Test
    @DisplayName("Quando um post for chamado deve criar uma pessoa")
    public void quandoUmPostForChamadoDeveCriarUmaPessoa() throws Exception {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        when(pessoaService.cadastrar(any(PessoaDTO.class))).thenReturn(pessoaDTO);

        MvcResult result = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO))
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        PessoaDTO pessoaDTORetornada = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<PessoaDTO>() {
                });

        assertEquals(pessoaDTO, pessoaDTORetornada);

        verify(pessoaService, times(1)).cadastrar(any(PessoaDTO.class));
    }

    @ParameterizedTest
    @ValueSource(strings = { "contatos", "nome", "cpf", "dataNascimento" })
    @DisplayName("Quando um post for chamado sem algum campo obrigatório da pessoa deve retornar um erro")
    public void postChamadoSemAlgumCampoObrigatorioDaPessoaDeveRetornarUmErro(String campo) throws Exception {
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

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO))
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = { "nome", "telefone", "email" })
    @DisplayName("Quando um post for chamado sem algum campo obrigatório do contato deve retornar um erro")
    public void postChamadoSemAlgumCampoObrigatorioDoContatoDeveRetornarUmErro(String campo) throws Exception {
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

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO))
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Quando um post for chamado com a pessoa sem ao menos contato deve retornar um erro")
    public void postChamadoComAPessoaSemAoMenosUmContatoDeveRetornarUmErro() throws Exception {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        pessoaDTO.getContatos().clear();

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO))
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Quando um post for chamado com o campo CPF da pessoa inválido deve retornar um erro")
    public void postChamadoComOCampoCPFDaPessoaInvalidoDeveRetornarUmErro() throws Exception {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        pessoaDTO.setCpf("11111111111");

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO))
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Quando um post for chamado com uma data futura no campo dataNascimento da pessoa deve retornar um erro")
    public void postChamadoComUmaDataFuturaNoCampoDataNascimentoDaPessoaDeveRetornarUmErro() throws Exception {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        Calendar dataFutura = Calendar.getInstance();
        dataFutura.add(Calendar.DAY_OF_MONTH, 1);

        pessoaDTO.setDataNascimento(dataFutura.getTime());

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO))
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Quando um post for chamado com o campo email do contato inválido deve retornar um erro")
    public void postChamadoComOCampoEmailDoContatoInvalidoDeveRetornarUmErro() throws Exception {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        pessoaDTO.getContatos().get(0).setEmail("emailInvalido");

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO))
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Quando um get for chamado com um ID cadastrado deve retornar uma única pessoa")
    public void getChamadoComParametroIDDeveRetornarUmaUnicaPessoa() throws Exception {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        when(pessoaService.buscarUm(any(Long.class))).thenReturn(pessoaDTO);

        mockMvc.perform(get(BASE_URL + "/" + pessoaDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(pessoaService, times(1)).buscarUm(any(Long.class));
    }

    @Test
    @DisplayName("Quando um get for chamado com um ID NÃO cadastrado um erro é retornado")
    public void getChamadoComIdNaoCadastradoRecebeUmErro() throws Exception {
        when(pessoaService.buscarUm(any(Long.class))).thenThrow(PessoaNaoEncontradaException.class);

        mockMvc.perform(get(BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(pessoaService, times(1)).buscarUm(any(Long.class));
    }

    @Test
    @DisplayName("Quando um delete for chamado com um id deve receber um OK")
    public void deleteChamadoComUmIdDeveReceberUmOK() throws Exception {
        doNothing().when(pessoaService).deletar(any(Long.class));

        mockMvc.perform(delete(BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(pessoaService, times(1)).deletar(any(Long.class));
    }

    @Test
    @DisplayName("Quando um put for chamado com campos válidos deve retornar um OK")
    public void putChamadoComCamposValidosDeveRetornarUmOK() throws Exception {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        when(pessoaService.atualizar(any(Long.class), any(PessoaDTO.class))).thenReturn(pessoaDTO);

        mockMvc.perform(put(BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(pessoaService, times(1)).atualizar(any(Long.class), any(PessoaDTO.class));
    }

    @Test
    @DisplayName("Quando um put for chamado com algum campo inválido deve retornar um ERRO")
    public void putChamadoComAlgumCampoInvalidoDeveRetornarUmERRO() throws Exception {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        when(pessoaService.atualizar(any(Long.class), any(PessoaDTO.class))).thenThrow(CampoInvalidoException.class);

        mockMvc.perform(put(BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Quando um put for chamado com um id não cadastrado deve retornar um ERRO")
    public void putChamadoComUmIdNaoCadastradoDeveRetornarUmERRO() throws Exception {
        PessoaDTO pessoaDTO = PessoaDTOBuilder.builder().build().toPessoaDTO();

        when(pessoaService.atualizar(any(Long.class), any(PessoaDTO.class)))
                .thenThrow(PessoaNaoEncontradaException.class);

        mockMvc.perform(put(BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaDTO)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}