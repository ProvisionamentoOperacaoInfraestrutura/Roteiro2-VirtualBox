package com.roteiro2.virtualbox.controller;

import com.roteiro2.virtualbox.service.EstatisticaService;
import com.roteiro2.virtualbox.service.GerenciadorArquivosService;
import com.roteiro2.virtualbox.service.PrimoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes do MainController")
class MainControllerTest {

    private static final String URI_MAIN_CONTROLLER = "/v1/main";

    private final PrimoService primoService = mock(PrimoService.class);
    private final EstatisticaService estatisticaService = mock(EstatisticaService.class);
    private final GerenciadorArquivosService gerenciadorArquivosService = mock(GerenciadorArquivosService.class);

    private MockMvc driver;

    @BeforeEach
    void configurarMockMvc() {
        MainController controller = new MainController();
        controller.primoService = primoService;
        controller.estatisticaService = estatisticaService;
        controller.gerenciadorArquivosService = gerenciadorArquivosService;
        driver = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void quandoBuscamosPrimosValidosRetornaLista() throws Exception {
        when(primoService.getNumerosPrimos(10)).thenReturn(List.of(2, 3, 5, 7));

        driver.perform(get(URI_MAIN_CONTROLLER + "/primos")
                        .param("enesimo", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[2,3,5,7]"));

        verify(primoService).getNumerosPrimos(10);
    }

    @Test
    void quandoBuscamosPrimosComValorInvalidoRetornaBadRequest() throws Exception {
        driver.perform(get(URI_MAIN_CONTROLLER + "/primos").param("enesimo", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("O enesimo deve ser maior que 1"));

        verifyNoInteractions(primoService);
    }

    @Test
    void quandoBuscamosEstatisticasValidasRetornaOsDados() throws Exception {
        when(estatisticaService.fazEstatisticas(4)).thenReturn(Map.<String, Object>of(
                "quantidade", 4,
                "soma", 6,
                "media", 1.5,
                "minimo", 0,
                "maximo", 3,
                "memoria_vetor_mb", 0.00003,
                "tempo_ms", 2
        ));

        driver.perform(get(URI_MAIN_CONTROLLER + "/estatisticas")
                        .param("enesimo", "4")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade").value(4))
                .andExpect(jsonPath("$.soma").value(6))
                .andExpect(jsonPath("$.media").value(1.5))
                .andExpect(jsonPath("$.minimo").value(0))
                .andExpect(jsonPath("$.maximo").value(3));

        verify(estatisticaService).fazEstatisticas(4);
    }

    @Test
    void quandoBuscamosEstatisticasComValorInvalidoRetornaBadRequest() throws Exception {
        driver.perform(get(URI_MAIN_CONTROLLER + "/estatisticas").param("enesimo", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("O enesimo deve ser maior que 1"));

        verifyNoInteractions(estatisticaService);
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
