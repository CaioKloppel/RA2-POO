package ui;

import modelo.Consulta;
import modelo.Pessoa;

import java.util.ArrayList;
import java.util.List;

public final class Formatador {

    private Formatador() { }

    /** req 12: chamada polimórfica — itera List<Pessoa> chamando getResumo() sobrescrito. */
    public static List<String> formatarPessoas(List<? extends Pessoa> pessoas) {
        List<String> linhas = new ArrayList<>();
        for (Pessoa p : pessoas) {
            linhas.add(p.getResumo());
        }
        return linhas;
    }

    public static List<String> formatarConsultas(List<Consulta> consultas) {
        List<String> linhas = new ArrayList<>();
        for (Consulta c : consultas) {
            linhas.add(c.toString());
        }
        return linhas;
    }
}
