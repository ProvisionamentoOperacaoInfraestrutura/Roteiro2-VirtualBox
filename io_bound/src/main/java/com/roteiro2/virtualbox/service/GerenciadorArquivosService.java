package com.roteiro2.virtualbox.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.springframework.stereotype.Service;

@Service
public class GerenciadorArquivosService {

    private static final Path ARQUIVO = Path.of("dados", "teste.bin");
    private static final int TAMANHO_MB = 100;

    private static final int O_RDONLY = 0;
    private static final int O_DIRECT = 0x4000;

    private static final int ALINHAMENTO = 4096;
    private static final int TAMANHO_BLOCO = 1024 * 1024;

    private interface LibC extends Library {
        LibC INSTANCE = Native.load("c", LibC.class);

        int open(String path, int flags);

        NativeLong read(int fd, Pointer buffer, NativeLong count);

        int close(int fd);

        int posix_memalign(
                PointerByReference pointer,
                NativeLong alignment,
                NativeLong size
        );

        void free(Pointer pointer);
    }

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

        long tamanho = Files.size(ARQUIVO);

        if (tamanho % ALINHAMENTO != 0) {
            throw new IOException(
                    "O tamanho do arquivo precisa ser múltiplo de "
                            + ALINHAMENTO + " bytes."
            );
        }

        long inicio = System.nanoTime();
        long bytesLidos = 0;

        LibC libc = LibC.INSTANCE;
        int fd = libc.open(
                ARQUIVO.toAbsolutePath().toString(),
                O_RDONLY | O_DIRECT
        );

        if (fd == -1) {
            throw new IOException(
                    "Não foi possível abrir o arquivo com O_DIRECT."
            );
        }

        PointerByReference referencia = new PointerByReference();
        Pointer buffer = null;

        try {
            int resultado = libc.posix_memalign(
                    referencia,
                    new NativeLong(ALINHAMENTO),
                    new NativeLong(TAMANHO_BLOCO)
            );

            if (resultado != 0) {
                throw new IOException(
                        "Falha ao alocar buffer alinhado: " + resultado
                );
            }

            buffer = referencia.getValue();

            while (bytesLidos < tamanho) {
                int quantidadeSolicitada = (int) Math.min(
                        TAMANHO_BLOCO,
                        tamanho - bytesLidos
                );

                NativeLong leitura = libc.read(
                        fd,
                        buffer,
                        new NativeLong(quantidadeSolicitada)
                );

                long quantidadeLida = leitura.longValue();

                if (quantidadeLida <= 0) {
                    throw new IOException(
                            "Falha na leitura direta do arquivo."
                    );
                }

                if (quantidadeLida != quantidadeSolicitada) {
                    throw new IOException(
                            "Leitura parcial inesperada: "
                                    + quantidadeLida + " bytes."
                    );
                }

                bytesLidos += quantidadeLida;
            }
        } finally {
            if (buffer != null) {
                libc.free(buffer);
            }
            libc.close(fd);
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
