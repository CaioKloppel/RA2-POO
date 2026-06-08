package teste;

public class Assert {
    private static int falhas = 0;

    public static void check(boolean cond, String msg) {
        if (cond) {
            System.out.println("PASS: " + msg);
        } else {
            System.out.println("FALHA: " + msg);
            falhas++;
        }
    }

    public static void fim() {
        if (falhas > 0) {
            System.out.println(falhas + " FALHA(S)");
            System.exit(1);
        }
        System.out.println("TODOS OS TESTES PASSARAM");
    }
}
