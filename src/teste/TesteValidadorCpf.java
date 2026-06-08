package teste;

import modelo.ValidadorCpf;
import excecoes.CpfInvalidoException;

public class TesteValidadorCpf {
    public static void main(String[] args) {
        // CPF válido conhecido
        try {
            ValidadorCpf.validar("11144477735");
            Assert.check(true, "CPF válido aceito");
        } catch (CpfInvalidoException e) {
            Assert.check(false, "CPF válido aceito");
        }

        // Quantidade errada de dígitos
        try {
            ValidadorCpf.validar("123");
            Assert.check(false, "CPF curto rejeitado");
        } catch (CpfInvalidoException e) {
            Assert.check(true, "CPF curto rejeitado");
        }

        // Dígito verificador errado
        try {
            ValidadorCpf.validar("11144477700");
            Assert.check(false, "DV inválido rejeitado");
        } catch (CpfInvalidoException e) {
            Assert.check(true, "DV inválido rejeitado");
        }

        // Caracteres não numéricos
        try {
            ValidadorCpf.validar("111444777ab");
            Assert.check(false, "CPF não numérico rejeitado");
        } catch (CpfInvalidoException e) {
            Assert.check(true, "CPF não numérico rejeitado");
        }

        Assert.fim();
    }
}
