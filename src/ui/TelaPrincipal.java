package ui;

import negocio.Consultorio;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal(Consultorio consultorio, RegistradorResultados registrador) {
        super("Médicos e Pacientes — P2");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Médico", new PainelMedico(consultorio, registrador));
        abas.addTab("Paciente", new PainelPaciente(consultorio, registrador));
        add(abas);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    registrador.registrarResumoFinal();
                } catch (IOException ex) {
                    System.err.println("Erro ao gravar resumo final: " + ex.getMessage());
                }
            }
        });

        pack();
        setLocationRelativeTo(null);
    }
}
