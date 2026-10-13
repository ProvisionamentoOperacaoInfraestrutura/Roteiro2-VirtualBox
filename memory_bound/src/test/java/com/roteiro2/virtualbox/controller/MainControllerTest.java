package com.roteiro2.virtualbox.controller;

import com.roteiro2.virtualbox.service.EstatisticaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes do MainController de memória")
class MainControllerTest {

    private static final String URI_MAIN_CONTROLLER = "/v1/main";

    private final EstatisticaService estatisticaService = mock(EstatisticaService.class);

    private MockMvc driver;

    @BeforeEach
    void configurarMockMvc() {
        MainController controller = new MainController();
        controller.estatisticaService = estatisticaService;
        driver = MockMvcBuilders.standaloneSetup(controller).build();
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

}
