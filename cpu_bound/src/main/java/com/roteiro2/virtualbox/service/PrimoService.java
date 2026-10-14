package com.roteiro2.virtualbox.service;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class PrimoService {

    private final MeterRegistry meterRegistry;
    private final Timer duration;
    private final LongTaskTimer activeOperations;
    private final DistributionSummary inputSize;
    private final Counter outputUnits;
    private final Counter failures;

    public PrimoService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.duration = Timer.builder("workload.operation.duration")
                .description("Tempo da operação principal da aplicação")
                .tag("type", "cpu")
                .publishPercentileHistogram()
                .register(meterRegistry);
        this.activeOperations = LongTaskTimer.builder("workload.operation.active")
                .description("Operações principais atualmente em execução")
                .tag("type", "cpu")
                .register(meterRegistry);
        this.inputSize = DistributionSummary.builder("workload.input.size")
                .description("Limite recebido pelo cálculo de números primos")
                .tag("type", "cpu")
                .register(meterRegistry);
        this.outputUnits = Counter.builder("workload.output.units")
                .description("Números primos produzidos")
                .tag("type", "cpu")
                .tag("unit", "primes")
                .register(meterRegistry);
        this.failures = Counter.builder("workload.operation.failures")
                .description("Falhas da operação principal")
                .tag("type", "cpu")
                .register(meterRegistry);
    }

    public List<Integer> getNumerosPrimos(int limite) {
        if (limite < 1) {
            throw new IllegalArgumentException("Limite no puede ser negativo");
        }


        inputSize.record(limite);
        Timer.Sample durationSample = Timer.start(meterRegistry);
        LongTaskTimer.Sample activeSample = activeOperations.start();

        try {
            List<Integer> numerosPrimos = new ArrayList<>();

            for (int numero = 2; numero <= limite; numero++) {
                boolean primo = true;

                for (int i = 2; i <= Math.sqrt(numero); i++) {
                    if (numero % i == 0) {
                        primo = false;
                        break;
                    }
                }

                if (primo) {
                    numerosPrimos.add(numero);
                }
            }

            outputUnits.increment(numerosPrimos.size());
            return numerosPrimos;
        } catch (RuntimeException | Error error) {
            failures.increment();
            throw error;
        } finally {
            activeSample.stop();
            durationSample.stop(duration);
        }
    }
}
