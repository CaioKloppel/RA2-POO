package teste;

import modelo.*;
import negocio.Consultorio;
import excecoes.RegistroNaoEncontradoException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class TesteConsultorioBuscas {
    public static void main(String[] args) throws RegistroNaoEncontradoException {
        Medico ana = new Medico(101, "Dra. Ana");
        Medico bruno = new Medico(102, "Dr. Bruno");
        Paciente joao = new Paciente("11144477735", "João");
        Paciente maria = new Paciente("52998224725", "Maria");

        LocalDate hoje = LocalDate.now();
        Consulta passada = new Consulta(hoje.minusDays(10), LocalTime.of(9, 0), 101, "11144477735");
        Consulta futura = new Consulta(hoje.plusDays(10), LocalTime.of(8, 0), 101, "11144477735");
        Consulta futuraCedo = new Consulta(hoje.plusDays(10), LocalTime.of(7, 0), 101, "52998224725");
        Consulta antiga = new Consulta(hoje.minusMonths(8), LocalTime.of(9, 0), 101, "52998224725");

        Consultorio c = new Consultorio(
                Arrays.asList(ana, bruno),
                Arrays.asList(joao, maria),
                Arrays.asList(passada, futura, futuraCedo, antiga));
        c.vincular();

        // 1. pacientes do médico
        Assert.check(c.pacientesDoMedico(101).size() == 2, "pacientesDoMedico conta 2");

        // 2. consultas do médico no período, ordenadas por data/horário
        List<Consulta> periodo = c.consultasDoMedicoNoPeriodo(101, hoje.plusDays(10), hoje.plusDays(10));
        Assert.check(periodo.size() == 2, "consultasNoPeriodo conta 2");
        Assert.check(periodo.get(0).getHorario().equals(LocalTime.of(7, 0)), "consultasNoPeriodo ordenadas por horário");

        // 3. pacientes inativos há mais de 3 meses (Maria: última c/ médico há 8 meses; João: há 10 dias)
        List<Paciente> inativos = c.pacientesInativos(101, 3);
        Assert.check(inativos.contains(maria) && !inativos.contains(joao), "pacientesInativos filtra por meses");

        // 4. médicos do paciente
        Assert.check(c.medicosDoPaciente("11144477735").size() == 1, "medicosDoPaciente conta 1");

        // 5. consultas passadas do paciente com médico
        Assert.check(c.consultasPassadasPacienteComMedico("11144477735", 101).size() == 1,
                "consultasPassadasPacienteComMedico conta 1");

        // 6. consultas futuras do paciente
        Assert.check(c.consultasFuturasDoPaciente("11144477735").size() == 1,
                "consultasFuturasDoPaciente conta 1");

        Assert.fim();
    }
}
