package ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RegistradorResultados {
    private final String caminho;
    private int totalBuscas = 0;

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RegistradorResultados(String caminho) {
        this.caminho = caminho;
    }

    /** Resultado parcial: registra uma busca executada com timestamp, parâmetros e linhas. */
    public void registrarBusca(String descricao, String parametros, List<String> linhas) throws IOException {
        totalBuscas++;
        try (PrintWriter pw = abrir()) {
            pw.println("timestamp," + LocalDateTime.now().format(TS));
            pw.println("busca," + descricao);
            pw.println("parametros," + parametros);
            pw.println("qtd_resultados," + linhas.size());
            for (String linha : linhas) {
                pw.println("resultado," + linha.replace(",", ";"));
            }
            pw.println();
        }
    }

    /** Resultado final: resumo da sessão. */
    public void registrarResumoFinal() throws IOException {
        try (PrintWriter pw = abrir()) {
            pw.println("RESUMO FINAL");
            pw.println("timestamp," + LocalDateTime.now().format(TS));
            pw.println("total_buscas," + totalBuscas);
            pw.println();
        }
    }

    private PrintWriter abrir() throws IOException {
        return new PrintWriter(Files.newBufferedWriter(
                Paths.get(caminho),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND));
    }
}
