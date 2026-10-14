package com.roteiro2.virtualbox.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.Map;
import org.springframework.stereotype.Service;


@Service
public class EstatisticaService {

    private final MeterRegistry meterRegistry;
    private final Timer duration;
    private final LongTaskTimer activeOperations;
    private final DistributionSummary inputSize;
    private final Counter outputUnits;
    private final Counter failures;

    public EstatisticaService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.duration = Timer.builder("workload.operation.duration")
                .description("Tempo da operação principal da aplicação")
                .tag("type", "memory")
                .publishPercentileHistogram()
                .register(meterRegistry);
        this.activeOperations = LongTaskTimer.builder("workload.operation.active")
                .description("Operações principais atualmente em execução")
                .tag("type", "memory")
                .register(meterRegistry);
        this.inputSize = DistributionSummary.builder("workload.input.size")
                .description("Quantidade de elementos solicitados")
                .tag("type", "memory")
                .register(meterRegistry);
        this.outputUnits = Counter.builder("workload.output.units")
                .description("Bytes alocados pela operação principal")
                .tag("type", "memory")
                .tag("unit", "bytes")
                .register(meterRegistry);
        this.failures = Counter.builder("workload.operation.failures")
                .description("Falhas da operação principal")
                .tag("type", "memory")
                .register(meterRegistry);
    }

    public Map<String, Object> fazEstatisticas(int n) {

        if (n < 1 || n > 20_000_000) {
            throw new IllegalArgumentException("n deve estar entre 1 e 20000000");
        }

        inputSize.record(n);
        Timer.Sample durationSample = Timer.start(meterRegistry);
        LongTaskTimer.Sample activeSample = activeOperations.start();

        try {
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
            double bytesAlocados = n * 8.0;
            outputUnits.increment(bytesAlocados);

            return Map.of(
                    "quantidade", n,
                    "soma", soma,
                    "media", media,
                    "minimo", minimo,
                    "maximo", maximo,
                    "memoria_vetor_mb", bytesAlocados / (1024 * 1024),
                    "tempo_ms", tempoMs
            );
        } catch (RuntimeException | Error error) {
            failures.increment();
            throw error;
        } finally {
            activeSample.stop();
            durationSample.stop(duration);
        }
    }
}
