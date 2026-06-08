package negocio;

import excecoes.RegistroNaoEncontradoException;
import modelo.Consulta;
import modelo.Medico;
import modelo.Paciente;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Consultorio implements Serializable {
    private List<Medico> medicos;
    private List<Paciente> pacientes;
    private List<Consulta> consultas;

    public Consultorio(List<Medico> medicos, List<Paciente> pacientes, List<Consulta> consultas) {
        this.medicos = new ArrayList<>(medicos);
        this.pacientes = new ArrayList<>(pacientes);
        this.consultas = new ArrayList<>(consultas);
    }

    public List<Medico> getMedicos() { return medicos; }
    public List<Paciente> getPacientes() { return pacientes; }
    public List<Consulta> getConsultas() { return consultas; }

    /** Liga o grafo: cada consulta entra no paciente e o paciente entra na lista do médico. */
    public void vincular() {
        for (Consulta c : consultas) {
            Medico m = acharMedico(c.getCodigoMedico());
            Paciente p = acharPaciente(c.getCpfPaciente());
            if (m == null || p == null) {
                continue;
            }
            p.adicionarConsulta(c);
            m.adicionarPaciente(p);
        }
    }

    public Medico buscarMedico(int codigo) throws RegistroNaoEncontradoException {
        Medico m = acharMedico(codigo);
        if (m == null) {
            throw new RegistroNaoEncontradoException("Médico não encontrado: " + codigo);
        }
        return m;
    }

    public Paciente buscarPaciente(String cpf) throws RegistroNaoEncontradoException {
        Paciente p = acharPaciente(cpf);
        if (p == null) {
            throw new RegistroNaoEncontradoException("Paciente não encontrado: " + cpf);
        }
        return p;
    }

    private Medico acharMedico(int codigo) {
        for (Medico m : medicos) {
            if (m.getCodigo() == codigo) {
                return m;
            }
        }
        return null;
    }

    private Paciente acharPaciente(String cpf) {
        for (Paciente p : pacientes) {
            if (p.getCpf().equals(cpf)) {
                return p;
            }
        }
        return null;
    }
}
