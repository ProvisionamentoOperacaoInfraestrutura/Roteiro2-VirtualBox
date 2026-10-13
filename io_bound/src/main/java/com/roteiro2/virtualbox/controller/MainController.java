
package com.roteiro2.virtualbox.controller;

import com.roteiro2.virtualbox.service.GerenciadorArquivosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping(
        value = "/v1/io_bound",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class MainController {
    @Autowired
    GerenciadorArquivosService gerenciadorArquivosService;


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
