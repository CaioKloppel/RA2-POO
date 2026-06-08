package negocio;

import excecoes.RegistroNaoEncontradoException;
import modelo.Consulta;
import modelo.Medico;
import modelo.Paciente;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    // --- 6 pesquisas ---

    public List<Paciente> pacientesDoMedico(int codigo) throws RegistroNaoEncontradoException {
        return buscarMedico(codigo).getPacientes();
    }

    public List<Consulta> consultasDoMedicoNoPeriodo(int codigo, LocalDate inicio, LocalDate fim)
            throws RegistroNaoEncontradoException {
        buscarMedico(codigo); // valida existência (propaga exceção)
        return consultas.stream()
                .filter(c -> c.getCodigoMedico() == codigo)
                .filter(c -> !c.getData().isBefore(inicio) && !c.getData().isAfter(fim))
                .sorted(Comparator.comparing(Consulta::getDataHora))
                .collect(Collectors.toList());
    }

    public List<Paciente> pacientesInativos(int codigo, int meses) throws RegistroNaoEncontradoException {
        Medico m = buscarMedico(codigo);
        LocalDateTime limite = LocalDateTime.now().minusMonths(meses);
        List<Paciente> inativos = new ArrayList<>();
        for (Paciente p : m.getPacientes()) {
            LocalDateTime ultima = ultimaConsultaPassadaComMedico(p, codigo);
            if (ultima == null || ultima.isBefore(limite)) {
                inativos.add(p);
            }
        }
        return inativos;
    }

    public List<Medico> medicosDoPaciente(String cpf) throws RegistroNaoEncontradoException {
        Paciente p = buscarPaciente(cpf);
        Set<Integer> codigos = new LinkedHashSet<>();
        for (Consulta c : p.getConsultas()) {
            codigos.add(c.getCodigoMedico());
        }
        List<Medico> resultado = new ArrayList<>();
        for (Integer codigo : codigos) {
            resultado.add(buscarMedico(codigo));
        }
        return resultado;
    }

    public List<Consulta> consultasPassadasPacienteComMedico(String cpf, int codigo)
            throws RegistroNaoEncontradoException {
        Paciente p = buscarPaciente(cpf);
        buscarMedico(codigo);
        LocalDateTime agora = LocalDateTime.now();
        return p.getConsultas().stream()
                .filter(c -> c.getCodigoMedico() == codigo)
                .filter(c -> c.getDataHora().isBefore(agora))
                .sorted(Comparator.comparing(Consulta::getDataHora))
                .collect(Collectors.toList());
    }

    public List<Consulta> consultasFuturasDoPaciente(String cpf) throws RegistroNaoEncontradoException {
        Paciente p = buscarPaciente(cpf);
        LocalDateTime agora = LocalDateTime.now();
        return p.getConsultas().stream()
                .filter(c -> c.getDataHora().isAfter(agora))
                .sorted(Comparator.comparing(Consulta::getDataHora))
                .collect(Collectors.toList());
    }

    private LocalDateTime ultimaConsultaPassadaComMedico(Paciente p, int codigo) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime ultima = null;
        for (Consulta c : p.getConsultas()) {
            if (c.getCodigoMedico() == codigo && c.getDataHora().isBefore(agora)) {
                if (ultima == null || c.getDataHora().isAfter(ultima)) {
                    ultima = c.getDataHora();
                }
            }
        }
        return ultima;
    }
}
