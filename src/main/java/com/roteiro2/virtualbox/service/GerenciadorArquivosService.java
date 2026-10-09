package com.roteiro2.virtualbox.service;

import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import org.springframework.stereotype.Service;


@Service
public class GerenciadorArquivosService {

    private static final Path ARQUIVO = Path.of("dados", "teste.bin");
    private static final int TAMANHO_MB = 100;


    public Map<String, Object> lerArquivo() throws IOException {
        Files.createDirectories(ARQUIVO.getParent());

        if (Files.notExists(ARQUIVO)) {
            byte[] bloco = new byte[1024 * 1024];

            try (var saida = Files.newOutputStream(
                    ARQUIVO, StandardOpenOption.CREATE_NEW)) {
                for (int i = 0; i < TAMANHO_MB; i++) {
                    saida.write(bloco);
                }
            }
        }

        long inicio = System.nanoTime();
        long bytesLidos = 0;

        try (var entrada = Files.newInputStream(ARQUIVO)) {
            byte[] buffer = new byte[8192];
            int quantidade;

            while ((quantidade = entrada.read(buffer)) != -1) {
                bytesLidos += quantidade;
            }
        }

        long tempoMs = (System.nanoTime() - inicio) / 1_000_000;

        return Map.of(
                "arquivo", ARQUIVO.toString(),
                "bytes_lidos", bytesLidos,
                "tamanho_mb", bytesLidos / (1024.0 * 1024),
                "tempo_ms", tempoMs
        );
    }
}
