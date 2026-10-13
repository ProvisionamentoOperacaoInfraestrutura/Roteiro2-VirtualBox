package com.roteiro2.virtualbox.controller;

import com.roteiro2.virtualbox.service.PrimoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes do MainController de CPU")
class MainControllerTest {

    private static final String URI_MAIN_CONTROLLER = "/v1/cpu_bound";

    private final PrimoService primoService = mock(PrimoService.class);

    private MockMvc driver;

    @BeforeEach
    void configurarMockMvc() {
        MainController controller = new MainController();
        controller.primoService = primoService;
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

}
