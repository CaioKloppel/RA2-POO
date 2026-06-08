package teste;

import modelo.Consulta;
import modelo.Medico;
import modelo.Paciente;
import persistencia.LeitorCsv;
import excecoes.CpfInvalidoException;

import java.io.IOException;
import java.util.List;

public class TesteLeitorCsv {
    public static void main(String[] args) throws IOException, CpfInvalidoException {
        LeitorCsv leitor = new LeitorCsv();

        List<Medico> medicos = leitor.lerMedicos("dados/medicos.csv");
        Assert.check(medicos.size() == 2, "lerMedicos lê 2 médicos");
        Assert.check(medicos.get(0).getCodigo() == 101, "primeiro médico é 101");

        List<Paciente> pacientes = leitor.lerPacientes("dados/pacientes.csv");
        Assert.check(pacientes.size() == 2, "lerPacientes lê 2 pacientes");
        Assert.check(pacientes.get(0).getCpf().equals("11144477735"), "primeiro paciente CPF ok");

        List<Consulta> consultas = leitor.lerConsultas("dados/consultas.csv");
        Assert.check(consultas.size() == 3, "lerConsultas lê 3 consultas");
        Assert.check(consultas.get(0).getCodigoMedico() == 101, "primeira consulta médico 101");

        Assert.fim();
    }
}
