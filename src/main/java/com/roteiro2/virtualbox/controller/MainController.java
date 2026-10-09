
package com.roteiro2.virtualbox.controller;

import com.roteiro2.virtualbox.service.EstatisticaService;
import com.roteiro2.virtualbox.service.GerenciadorArquivosService;
import com.roteiro2.virtualbox.service.PrimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping(
        value = "/v1/main",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class MainController {
    @Autowired
    PrimoService primoService;
    @Autowired
    EstatisticaService estatisticaService;
    @Autowired
    GerenciadorArquivosService gerenciadorArquivosService;


    @GetMapping("/primos")
    public ResponseEntity<?> numerosPrimos(@RequestParam int enesimo) {
        if (enesimo <= 1) {
            return ResponseEntity.badRequest().body("O enesimo deve ser maior que 1");
        }

        return ResponseEntity.ok(primoService.getNumerosPrimos(enesimo));
    }


    @GetMapping("/estatisticas")
    public ResponseEntity<?> estatisticas(@RequestParam int enesimo) {
        if (enesimo <= 1) {
            return ResponseEntity.badRequest().body("O enesimo deve ser maior que 1");
        }

        return ResponseEntity.ok(estatisticaService.fazEstatisticas(enesimo));
    }


    @GetMapping("/arquivo")
    public ResponseEntity<?> lerArquivo() {
        try {
            return ResponseEntity.ok(gerenciadorArquivosService.lerArquivo());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("erro", "Falha ao ler o arquivo")
            );
        }
    }
}
