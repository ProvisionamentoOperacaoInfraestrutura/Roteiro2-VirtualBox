package com.roteiro2.virtualbox.service;

import java.util.Map;
import org.springframework.stereotype.Service;


@Service
public class EstatisticaService {

    public Map<String, Object> fazEstatisticas(int n) {

        if (n < 1 || n > 20_000_000) {
            throw new IllegalArgumentException("n deve estar entre 1 e 20000000");
        }

        long inicio = System.nanoTime();
        long[] numeros = new long[n];
        long soma = 0;

        for (int i = 0; i < n; i++) {
            numeros[i] = i;
            soma += numeros[i];
        }

        long minimo = numeros[0];
        long maximo = numeros[n - 1];
        double media = (double) soma / n;
        long tempoMs = (System.nanoTime() - inicio) / 1_000_000;

        return Map.of(
                "quantidade", n,
                "soma", soma,
                "media", media,
                "minimo", minimo,
                "maximo", maximo,
                "memoria_vetor_mb", n * 8.0 / (1024 * 1024),
                "tempo_ms", tempoMs
        );
    }
}
