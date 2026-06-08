package app;

import excecoes.CpfInvalidoException;
import modelo.Consulta;
import modelo.Medico;
import modelo.Paciente;
import negocio.Consultorio;
import persistencia.LeitorCsv;
import persistencia.RepositorioBinario;

import java.io.IOException;
import java.util.List;

public class ProgramaP1 {
    public static void main(String[] args) {
        String dirDados = args.length > 0 ? args[0] : "dados";
        String saidaBin = args.length > 1 ? args[1] : "dados/consultorio.bin";

        LeitorCsv leitor = new LeitorCsv();
        RepositorioBinario repo = new RepositorioBinario();

        try {
            List<Medico> medicos = leitor.lerMedicos(dirDados + "/medicos.csv");
            List<Paciente> pacientes = leitor.lerPacientes(dirDados + "/pacientes.csv");
            List<Consulta> consultas = leitor.lerConsultas(dirDados + "/consultas.csv");

            Consultorio consultorio = new Consultorio(medicos, pacientes, consultas);
            consultorio.vincular();

            repo.salvar(consultorio, saidaBin);

            System.out.println("P1 concluído: " + medicos.size() + " médicos, "
                    + pacientes.size() + " pacientes, " + consultas.size() + " consultas.");
            System.out.println("Objetos salvos em: " + saidaBin);
        } catch (CpfInvalidoException e) {
            System.err.println("Erro de validação de CPF: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro de E/S: " + e.getMessage());
        }
    }
}
