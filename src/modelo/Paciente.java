package modelo;

import java.util.ArrayList;
import java.util.List;

public class Paciente extends Pessoa implements Identificavel {
    private String cpf;
    private List<Consulta> consultas;

    public Paciente(String cpf, String nome) {
        super(nome);
        this.cpf = cpf;
        this.consultas = new ArrayList<>();
    }

    public String getCpf() { return cpf; }

    public List<Consulta> getConsultas() { return consultas; }

    public void adicionarConsulta(Consulta c) {
        consultas.add(c);
    }

    @Override
    public String getIdentificador() {
        return cpf;
    }

    @Override
    public String getResumo() {
        return "Paciente " + cpf + " - " + nome;
    }
}
