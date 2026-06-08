package teste;

import modelo.Consulta;
import modelo.Medico;
import modelo.Paciente;
import negocio.Consultorio;
import persistencia.RepositorioBinario;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

public class TesteRepositorioBinario {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Medico m = new Medico(101, "Dra. Ana");
        Paciente p = new Paciente("11144477735", "João");
        Consulta c = new Consulta(LocalDate.of(2026, 3, 10), LocalTime.of(14, 30), 101, "11144477735");
        Consultorio original = new Consultorio(Arrays.asList(m), Arrays.asList(p), Arrays.asList(c));
        original.vincular();

        RepositorioBinario repo = new RepositorioBinario();
        repo.salvar(original, "out/teste-dados.bin");
        Consultorio restaurado = repo.carregar("out/teste-dados.bin");

        Assert.check(restaurado.getMedicos().size() == 1, "round-trip preserva médicos");
        Assert.check(restaurado.getConsultas().size() == 1, "round-trip preserva consultas");
        Assert.check(restaurado.getMedicos().get(0).getPacientes().size() == 1,
                "round-trip preserva grafo vinculado");
        Assert.fim();
    }
}
