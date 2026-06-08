package modelo;

import excecoes.CpfInvalidoException;

public final class ValidadorCpf {

    private ValidadorCpf() { }

    public static void validar(String cpf) throws CpfInvalidoException {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new CpfInvalidoException("CPF deve ter exatamente 11 dígitos numéricos: " + cpf);
        }
        int d1 = digitoVerificador(cpf, 9, 10);
        int d2 = digitoVerificador(cpf, 10, 11);
        if (d1 != (cpf.charAt(9) - '0') || d2 != (cpf.charAt(10) - '0')) {
            throw new CpfInvalidoException("Dígitos verificadores inválidos: " + cpf);
        }
    }

    private static int digitoVerificador(String cpf, int qtdDigitos, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;
        for (int i = 0; i < qtdDigitos; i++) {
            soma += (cpf.charAt(i) - '0') * peso;
            peso--;
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }
}
