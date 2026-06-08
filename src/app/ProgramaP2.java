package app;

import negocio.Consultorio;
import persistencia.RepositorioBinario;
import ui.RegistradorResultados;
import ui.TelaPrincipal;

import javax.swing.SwingUtilities;
import java.io.IOException;

public class ProgramaP2 {
    public static void main(String[] args) {
        String entradaBin = args.length > 0 ? args[0] : "dados/consultorio.bin";
        String saidaResultados = args.length > 1 ? args[1] : "resultados.csv";

        RepositorioBinario repo = new RepositorioBinario();
        try {
            Consultorio consultorio = repo.carregar(entradaBin);
            RegistradorResultados registrador = new RegistradorResultados(saidaResultados);
            SwingUtilities.invokeLater(() ->
                    new TelaPrincipal(consultorio, registrador).setVisible(true));
        } catch (IOException e) {
            System.err.println("Erro ao ler " + entradaBin + ": " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Arquivo binário incompatível: " + e.getMessage());
        }
    }
}
