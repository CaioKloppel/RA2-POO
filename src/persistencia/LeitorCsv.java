package persistencia;

import excecoes.CpfInvalidoException;
import modelo.Consulta;
import modelo.Medico;
import modelo.Paciente;
import modelo.ValidadorCpf;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LeitorCsv {

    public List<Medico> lerMedicos(String caminho) throws IOException {
        List<Medico> medicos = new ArrayList<>();
        for (String[] campos : lerLinhas(caminho, 2)) {
            int codigo = Integer.parseInt(campos[0].trim());
            medicos.add(new Medico(codigo, campos[1].trim()));
        }
        return medicos;
    }

    // req 5: declara throws e NÃO captura (repassa CpfInvalidoException e IOException)
    public List<Paciente> lerPacientes(String caminho) throws IOException, CpfInvalidoException {
        List<Paciente> pacientes = new ArrayList<>();
        for (String[] campos : lerLinhas(caminho, 2)) {
            String cpf = campos[0].trim();
            ValidadorCpf.validar(cpf);
            pacientes.add(new Paciente(cpf, campos[1].trim()));
        }
        return pacientes;
    }

    public List<Consulta> lerConsultas(String caminho) throws IOException {
        List<Consulta> consultas = new ArrayList<>();
        for (String[] campos : lerLinhas(caminho, 4)) {
            LocalDate data = LocalDate.parse(campos[0].trim());
            LocalTime horario = LocalTime.parse(campos[1].trim());
            int codigoMedico = Integer.parseInt(campos[2].trim());
            String cpfPaciente = campos[3].trim();
            consultas.add(new Consulta(data, horario, codigoMedico, cpfPaciente));
        }
        return consultas;
    }

    private List<String[]> lerLinhas(String caminho, int minColunas) throws IOException {
        List<String[]> linhas = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(caminho), StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }
                String[] campos = linha.split(",", -1);
                if (campos.length < minColunas) {
                    throw new IOException("Linha CSV inválida em " + caminho + ": " + linha);
                }
                linhas.add(campos);
            }
        }
        return linhas;
    }
}
