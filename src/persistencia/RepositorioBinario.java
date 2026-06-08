package persistencia;

import negocio.Consultorio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RepositorioBinario {

    public void salvar(Consultorio consultorio, String caminho) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(caminho))) {
            out.writeObject(consultorio);
        }
    }

    // req 5: declara throws e NÃO captura (repassa)
    public Consultorio carregar(String caminho) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(caminho))) {
            return (Consultorio) in.readObject();
        }
    }
}
