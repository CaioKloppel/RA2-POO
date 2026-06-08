package ui;

import excecoes.RegistroNaoEncontradoException;
import negocio.Consultorio;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class PainelPaciente extends JPanel {
    private final Consultorio consultorio;
    private final RegistradorResultados registrador;
    private final JTextField campoCpf = new JTextField(14);
    private final JTextField campoCodigoMedico = new JTextField(8);
    private final JTextArea saida = new JTextArea(15, 50);

    public PainelPaciente(Consultorio consultorio, RegistradorResultados registrador) {
        this.consultorio = consultorio;
        this.registrador = registrador;
        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("CPF do paciente:"));
        form.add(campoCpf);
        form.add(new JLabel("Código do médico (busca 2):"));
        form.add(campoCodigoMedico);

        JButton btn1 = new JButton("1. Médicos do paciente");
        JButton btn2 = new JButton("2. Consultas passadas c/ médico");
        JButton btn3 = new JButton("3. Consultas futuras");
        btn1.addActionListener(e -> medicosDoPaciente());
        btn2.addActionListener(e -> consultasPassadas());
        btn3.addActionListener(e -> consultasFuturas());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btn1);
        botoes.add(btn2);
        botoes.add(btn3);

        saida.setEditable(false);
        JPanel topo = new JPanel(new BorderLayout());
        topo.add(form, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(saida), BorderLayout.CENTER);
    }

    private String lerCpf() {
        return campoCpf.getText().trim();
    }

    private void exibirERegistrar(String descricao, String parametros, List<String> linhas) {
        StringBuilder sb = new StringBuilder(descricao).append("\n");
        for (String l : linhas) {
            sb.append("  ").append(l).append("\n");
        }
        if (linhas.isEmpty()) {
            sb.append("  (nenhum resultado)\n");
        }
        saida.setText(sb.toString());
        try {
            registrador.registrarBusca(descricao, parametros, linhas);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao gravar resultados: " + e.getMessage());
        }
    }

    private void medicosDoPaciente() {
        try {
            String cpf = lerCpf();
            List<String> linhas = Formatador.formatarPessoas(consultorio.medicosDoPaciente(cpf));
            exibirERegistrar("Médicos do paciente", "cpf=" + cpf, linhas);
        } catch (RegistroNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void consultasPassadas() {
        try {
            String cpf = lerCpf();
            int codigo = Integer.parseInt(campoCodigoMedico.getText().trim());
            List<String> linhas = Formatador.formatarConsultas(
                    consultorio.consultasPassadasPacienteComMedico(cpf, codigo));
            exibirERegistrar("Consultas passadas do paciente com médico",
                    "cpf=" + cpf + ";codigo=" + codigo, linhas);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Código do médico inválido.");
        } catch (RegistroNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void consultasFuturas() {
        try {
            String cpf = lerCpf();
            List<String> linhas = Formatador.formatarConsultas(consultorio.consultasFuturasDoPaciente(cpf));
            exibirERegistrar("Consultas futuras do paciente", "cpf=" + cpf, linhas);
        } catch (RegistroNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
