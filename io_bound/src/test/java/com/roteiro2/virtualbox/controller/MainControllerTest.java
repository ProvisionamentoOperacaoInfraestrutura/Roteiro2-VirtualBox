package com.roteiro2.virtualbox.controller;

import com.roteiro2.virtualbox.service.GerenciadorArquivosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes do MainController de I/O")
class MainControllerTest {

    private static final String URI_MAIN_CONTROLLER = "/v1/io_bound";

    private final GerenciadorArquivosService gerenciadorArquivosService = mock(GerenciadorArquivosService.class);

    private MockMvc driver;

    @BeforeEach
    void configurarMockMvc() {
        MainController controller = new MainController();
        controller.gerenciadorArquivosService = gerenciadorArquivosService;
        driver = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void quandoLeArquivoComSucessoRetornaAsInformacoes() throws Exception {
        when(gerenciadorArquivosService.lerArquivo()).thenReturn(Map.<String, Object>of(
                "arquivo", "dados/teste.bin",
                "bytes_lidos", 1024,
                "tamanho_mb", 0.0009765625,
                "tempo_ms", 1
        ));

        driver.perform(get(URI_MAIN_CONTROLLER + "/arquivo")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.arquivo").value("dados/teste.bin"))
                .andExpect(jsonPath("$.bytes_lidos").value(1024))
                .andExpect(jsonPath("$.tamanho_mb").value(0.0009765625));

        verify(gerenciadorArquivosService).lerArquivo();
    }

    @Test
    void quandoFalhaAoLerArquivoRetornaErroInterno() throws Exception {
        when(gerenciadorArquivosService.lerArquivo()).thenThrow(new IOException("falha"));

        driver.perform(get(URI_MAIN_CONTROLLER + "/arquivo")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.erro").value("Falha ao ler o arquivo"));
    }
}
