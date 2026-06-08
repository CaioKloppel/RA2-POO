package ui;

import excecoes.RegistroNaoEncontradoException;
import negocio.Consultorio;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PainelMedico extends JPanel {
    private final Consultorio consultorio;
    private final RegistradorResultados registrador;
    private final JTextField campoCodigo = new JTextField(8);
    private final JTextField campoInicio = new JTextField(10);
    private final JTextField campoFim = new JTextField(10);
    private final JTextField campoMeses = new JTextField(4);
    private final JTextArea saida = new JTextArea(15, 50);

    public PainelMedico(Consultorio consultorio, RegistradorResultados registrador) {
        this.consultorio = consultorio;
        this.registrador = registrador;
        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("Código do médico:"));
        form.add(campoCodigo);
        form.add(new JLabel("Período início (yyyy-MM-dd):"));
        form.add(campoInicio);
        form.add(new JLabel("Período fim (yyyy-MM-dd):"));
        form.add(campoFim);
        form.add(new JLabel("Inatividade (meses):"));
        form.add(campoMeses);

        JButton btn1 = new JButton("1. Pacientes do médico");
        JButton btn2 = new JButton("2. Consultas no período");
        JButton btn3 = new JButton("3. Pacientes inativos");
        btn1.addActionListener(e -> pacientesDoMedico());
        btn2.addActionListener(e -> consultasNoPeriodo());
        btn3.addActionListener(e -> pacientesInativos());

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

    private int lerCodigo() {
        return Integer.parseInt(campoCodigo.getText().trim());
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

    private void pacientesDoMedico() {
        try {
            int codigo = lerCodigo();
            List<String> linhas = Formatador.formatarPessoas(consultorio.pacientesDoMedico(codigo));
            exibirERegistrar("Pacientes do médico", "codigo=" + codigo, linhas);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Código inválido.");
        } catch (RegistroNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void consultasNoPeriodo() {
        try {
            int codigo = lerCodigo();
            LocalDate inicio = LocalDate.parse(campoInicio.getText().trim());
            LocalDate fim = LocalDate.parse(campoFim.getText().trim());
            List<String> linhas = Formatador.formatarConsultas(
                    consultorio.consultasDoMedicoNoPeriodo(codigo, inicio, fim));
            exibirERegistrar("Consultas do médico no período",
                    "codigo=" + codigo + ";inicio=" + inicio + ";fim=" + fim, linhas);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Código inválido.");
        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida (use yyyy-MM-dd).");
        } catch (RegistroNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void pacientesInativos() {
        try {
            int codigo = lerCodigo();
            int meses = Integer.parseInt(campoMeses.getText().trim());
            List<String> linhas = Formatador.formatarPessoas(consultorio.pacientesInativos(codigo, meses));
            exibirERegistrar("Pacientes inativos", "codigo=" + codigo + ";meses=" + meses, linhas);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Código ou meses inválido.");
        } catch (RegistroNaoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
