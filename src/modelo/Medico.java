package modelo;

import java.util.ArrayList;
import java.util.List;

public class Medico extends Pessoa implements Identificavel {
    private int codigo;
    private List<Paciente> pacientes;

    public Medico(int codigo, String nome) {
        super(nome);
        this.codigo = codigo;
        this.pacientes = new ArrayList<>();
    }

    public int getCodigo() { return codigo; }

    public List<Paciente> getPacientes() { return pacientes; }

    public void adicionarPaciente(Paciente p) {
        if (!pacientes.contains(p)) {
            pacientes.add(p);
        }
    }

    @Override
    public String getIdentificador() {
        return String.valueOf(codigo);
    }

    @Override
    public String getResumo() {
        return "Médico " + codigo + " - " + nome;
    }
}
