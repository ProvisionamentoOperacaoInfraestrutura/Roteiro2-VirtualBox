package com.roteiro2.virtualbox.service;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class PrimoService {

    public List<Integer> getNumerosPrimos(int limite) {
        if (limite < 1) {
            throw new IllegalArgumentException("Limite no puede ser negativo");
        }


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
        return numerosPrimos;
    }
}
