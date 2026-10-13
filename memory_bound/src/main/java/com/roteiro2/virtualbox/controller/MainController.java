
package com.roteiro2.virtualbox.controller;

import com.roteiro2.virtualbox.service.EstatisticaService;
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
    EstatisticaService estatisticaService;


    @GetMapping("/estatisticas")
    public ResponseEntity<?> estatisticas(@RequestParam int enesimo) {
        if (enesimo <= 1) {
            return ResponseEntity.badRequest().body("O enesimo deve ser maior que 1");
        }

        return ResponseEntity.ok(estatisticaService.fazEstatisticas(enesimo));
    }
}
