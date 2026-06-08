package modelo;

import java.io.Serializable;

public abstract class Pessoa implements Serializable {
    protected String nome;

    public Pessoa(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public abstract String getResumo();
}
