
package com.roteiro2.virtualbox.controller;

import com.roteiro2.virtualbox.service.PrimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping(
        value = "/v1/cpu_bound",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class MainController {
    @Autowired
    PrimoService primoService;


    @GetMapping("/primos")
    public ResponseEntity<?> numerosPrimos(@RequestParam int enesimo) {
        if (enesimo <= 1) {
            return ResponseEntity.badRequest().body("O enesimo deve ser maior que 1");
        }

        return ResponseEntity.ok(primoService.getNumerosPrimos(enesimo));
    }
}
