package teste;

import modelo.*;
import negocio.Consultorio;
import excecoes.RegistroNaoEncontradoException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

public class TesteConsultorioLookup {
    public static void main(String[] args) {
        Medico m = new Medico(101, "Dra. Ana");
        Paciente p = new Paciente("11144477735", "João");
        Consulta c = new Consulta(LocalDate.now(), LocalTime.of(10, 0), 101, "11144477735");

        Consultorio cons = new Consultorio(
                Arrays.asList(m),
                Arrays.asList(p),
                Arrays.asList(c));
        cons.vincular();

        Assert.check(m.getPacientes().contains(p), "vincular liga paciente ao médico");
        Assert.check(p.getConsultas().contains(c), "vincular liga consulta ao paciente");

        try {
            Assert.check(cons.buscarMedico(101) == m, "buscarMedico encontra existente");
        } catch (RegistroNaoEncontradoException e) {
            Assert.check(false, "buscarMedico encontra existente");
        }

        try {
            cons.buscarMedico(999);
            Assert.check(false, "buscarMedico lança quando inexistente");
        } catch (RegistroNaoEncontradoException e) {
            Assert.check(true, "buscarMedico lança quando inexistente");
        }

        try {
            cons.buscarPaciente("00000000000");
            Assert.check(false, "buscarPaciente lança quando inexistente");
        } catch (RegistroNaoEncontradoException e) {
            Assert.check(true, "buscarPaciente lança quando inexistente");
        }

        Assert.fim();
    }
}
