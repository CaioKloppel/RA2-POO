# Médicos e Pacientes — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implementar a aplicação "Médicos e Pacientes" do RA2 de POO: P1 lê CSVs e serializa objetos em binário; P2 restaura e expõe as 6 pesquisas via GUI Swing, gravando resultados em CSV.

**Architecture:** Domínio com `Pessoa` abstrata (`Medico`/`Paciente`) + interface `Identificavel`; `Consultorio` é a raiz serializável que concentra as 6 buscas; `persistencia` faz CSV→objetos e objetos↔binário; `ui` Swing dispara buscas e registra resultados. Exceções próprias para CPF inválido e registro não encontrado.

**Tech Stack:** Java (JDK herdado, módulo IntelliJ, sem build tool), Swing, serialização Java. Testes via harness próprio (`teste.Assert`) compilado com `javac`.

**Convenção de build/teste (Windows PowerShell):**
- Compilar tudo: `Get-ChildItem -Recurse -Path src -Filter *.java | ForEach-Object FullName | Set-Content out\sources.txt -Encoding utf8; javac -d out "@out\sources.txt"`
  - Mais simples e usado nos passos abaixo: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)`
- Rodar um teste: `java -cp out teste.TesteX`
- O diretório `out/` está no `.gitignore`.

---

### Task 1: Harness de testes + estrutura de pastas

**Files:**
- Create: `src/teste/Assert.java`

- [ ] **Step 1: Criar o helper de asserção**

```java
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
```

- [ ] **Step 2: Compilar para verificar o setup**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)`
Expected: compila sem erros; cria `out/teste/Assert.class`.

- [ ] **Step 3: Commit**

```bash
git add src/teste/Assert.java
git commit -m "chore: harness de testes (teste.Assert)"
```

---

### Task 2: Exceções próprias

**Files:**
- Create: `src/excecoes/CpfInvalidoException.java`
- Create: `src/excecoes/RegistroNaoEncontradoException.java`

- [ ] **Step 1: Criar `CpfInvalidoException`**

```java
package excecoes;

public class CpfInvalidoException extends Exception {
    public CpfInvalidoException(String mensagem) {
        super(mensagem);
    }
}
```

- [ ] **Step 2: Criar `RegistroNaoEncontradoException`**

```java
package excecoes;

public class RegistroNaoEncontradoException extends Exception {
    public RegistroNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
```

- [ ] **Step 3: Compilar**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)`
Expected: compila sem erros.

- [ ] **Step 4: Commit**

```bash
git add src/excecoes
git commit -m "feat: exceções CpfInvalidoException e RegistroNaoEncontradoException"
```

---

### Task 3: Validador de CPF (TDD)

Algoritmo do CPF: 11 dígitos; os 2 últimos são verificadores calculados sobre os 9 primeiros.

**Files:**
- Create: `src/modelo/ValidadorCpf.java`
- Test: `src/teste/TesteValidadorCpf.java`

- [ ] **Step 1: Escrever o teste que falha**

```java
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
```

- [ ] **Step 2: Rodar e ver falhar (não compila ainda)**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)`
Expected: FALHA de compilação — `ValidadorCpf` não existe.

- [ ] **Step 3: Implementar `ValidadorCpf`**

```java
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
```

- [ ] **Step 4: Compilar e rodar o teste**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName); java -cp out teste.TesteValidadorCpf`
Expected: `TODOS OS TESTES PASSARAM`.

- [ ] **Step 5: Commit**

```bash
git add src/modelo/ValidadorCpf.java src/teste/TesteValidadorCpf.java
git commit -m "feat: validação de CPF com dígitos verificadores"
```

---

### Task 4: Interface `Identificavel` e classe abstrata `Pessoa`

**Files:**
- Create: `src/modelo/Identificavel.java`
- Create: `src/modelo/Pessoa.java`

- [ ] **Step 1: Criar a interface `Identificavel`**

```java
package modelo;

public interface Identificavel {
    String getIdentificador();
}
```

- [ ] **Step 2: Criar a classe abstrata `Pessoa`**

```java
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
```

- [ ] **Step 3: Compilar**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)`
Expected: compila sem erros.

- [ ] **Step 4: Commit**

```bash
git add src/modelo/Identificavel.java src/modelo/Pessoa.java
git commit -m "feat: interface Identificavel e classe abstrata Pessoa"
```

---

### Task 5: `Consulta`

**Files:**
- Create: `src/modelo/Consulta.java`

- [ ] **Step 1: Criar `Consulta`**

```java
package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Consulta implements Serializable {
    private LocalDate data;
    private LocalTime horario;
    private int codigoMedico;
    private String cpfPaciente;

    public Consulta(LocalDate data, LocalTime horario, int codigoMedico, String cpfPaciente) {
        this.data = data;
        this.horario = horario;
        this.codigoMedico = codigoMedico;
        this.cpfPaciente = cpfPaciente;
    }

    public LocalDate getData() { return data; }
    public LocalTime getHorario() { return horario; }
    public int getCodigoMedico() { return codigoMedico; }
    public String getCpfPaciente() { return cpfPaciente; }

    public LocalDateTime getDataHora() {
        return LocalDateTime.of(data, horario);
    }

    @Override
    public String toString() {
        return data + " " + horario + " | médico " + codigoMedico + " | paciente " + cpfPaciente;
    }
}
```

- [ ] **Step 2: Compilar**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)`
Expected: compila sem erros.

- [ ] **Step 3: Commit**

```bash
git add src/modelo/Consulta.java
git commit -m "feat: classe Consulta"
```

---

### Task 6: `Medico` e `Paciente`

**Files:**
- Create: `src/modelo/Medico.java`
- Create: `src/modelo/Paciente.java`

- [ ] **Step 1: Criar `Medico`**

```java
package modelo;

import java.util.ArrayList;
import java.util.List;

public class Medico extends Pessoa implements Identificavel {
    private int codigo;
    private List<Paciente> pacientes;

    public Medico(int codigo, String nome) {
        super(nome);
        this.codigo = codigo;
        this.pacientes = new ArrayList<>();
    }

    public int getCodigo() { return codigo; }

    public List<Paciente> getPacientes() { return pacientes; }

    public void adicionarPaciente(Paciente p) {
        if (!pacientes.contains(p)) {
            pacientes.add(p);
        }
    }

    @Override
    public String getIdentificador() {
        return String.valueOf(codigo);
    }

    @Override
    public String getResumo() {
        return "Médico " + codigo + " - " + nome;
    }
}
```

- [ ] **Step 2: Criar `Paciente`**

```java
package modelo;

import java.util.ArrayList;
import java.util.List;

public class Paciente extends Pessoa implements Identificavel {
    private String cpf;
    private List<Consulta> consultas;

    public Paciente(String cpf, String nome) {
        super(nome);
        this.cpf = cpf;
        this.consultas = new ArrayList<>();
    }

    public String getCpf() { return cpf; }

    public List<Consulta> getConsultas() { return consultas; }

    public void adicionarConsulta(Consulta c) {
        consultas.add(c);
    }

    @Override
    public String getIdentificador() {
        return cpf;
    }

    @Override
    public String getResumo() {
        return "Paciente " + cpf + " - " + nome;
    }
}
```

- [ ] **Step 3: Compilar**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)`
Expected: compila sem erros.

- [ ] **Step 4: Commit**

```bash
git add src/modelo/Medico.java src/modelo/Paciente.java
git commit -m "feat: classes Medico e Paciente"
```

---

### Task 7: `Consultorio` — vinculação + lookups (TDD)

`Consultorio` é a raiz serializável: guarda as três listas, vincula o grafo e resolve médico/paciente por id (lançando `RegistroNaoEncontradoException`).

**Files:**
- Create: `src/negocio/Consultorio.java`
- Test: `src/teste/TesteConsultorioLookup.java`

- [ ] **Step 1: Escrever o teste que falha**

```java
package teste;

import modelo.*;
import negocio.Consultorio;
import excecoes.RegistroNaoEncontradoException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

public class TesteConsultorioLookup {
    public static void main(String[] args) {
        Medico m = new Medico(101, "Dra. Ana");
        Paciente p = new Paciente("11144477735", "João");
        Consulta c = new Consulta(LocalDate.now(), LocalTime.of(10, 0), 101, "11144477735");

        Consultorio cons = new Consultorio(
                Arrays.asList(m),
                Arrays.asList(p),
                Arrays.asList(c));
        cons.vincular();

        Assert.check(m.getPacientes().contains(p), "vincular liga paciente ao médico");
        Assert.check(p.getConsultas().contains(c), "vincular liga consulta ao paciente");

        try {
            Assert.check(cons.buscarMedico(101) == m, "buscarMedico encontra existente");
        } catch (RegistroNaoEncontradoException e) {
            Assert.check(false, "buscarMedico encontra existente");
        }

        try {
            cons.buscarMedico(999);
            Assert.check(false, "buscarMedico lança quando inexistente");
        } catch (RegistroNaoEncontradoException e) {
            Assert.check(true, "buscarMedico lança quando inexistente");
        }

        try {
            cons.buscarPaciente("00000000000");
            Assert.check(false, "buscarPaciente lança quando inexistente");
        } catch (RegistroNaoEncontradoException e) {
            Assert.check(true, "buscarPaciente lança quando inexistente");
        }

        Assert.fim();
    }
}
```

- [ ] **Step 2: Rodar e ver falhar (não compila)**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)`
Expected: FALHA de compilação — `Consultorio` não existe.

- [ ] **Step 3: Implementar `Consultorio` (vinculação + lookups; buscas vêm na Task 8)**

```java
package negocio;

import excecoes.RegistroNaoEncontradoException;
import modelo.Consulta;
import modelo.Medico;
import modelo.Paciente;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Consultorio implements Serializable {
    private List<Medico> medicos;
    private List<Paciente> pacientes;
    private List<Consulta> consultas;

    public Consultorio(List<Medico> medicos, List<Paciente> pacientes, List<Consulta> consultas) {
        this.medicos = new ArrayList<>(medicos);
        this.pacientes = new ArrayList<>(pacientes);
        this.consultas = new ArrayList<>(consultas);
    }

    public List<Medico> getMedicos() { return medicos; }
    public List<Paciente> getPacientes() { return pacientes; }
    public List<Consulta> getConsultas() { return consultas; }

    /** Liga o grafo: cada consulta entra no paciente e o paciente entra na lista do médico. */
    public void vincular() {
        for (Consulta c : consultas) {
            Medico m = acharMedico(c.getCodigoMedico());
            Paciente p = acharPaciente(c.getCpfPaciente());
            if (m == null || p == null) {
                continue;
            }
            p.adicionarConsulta(c);
            m.adicionarPaciente(p);
        }
    }

    public Medico buscarMedico(int codigo) throws RegistroNaoEncontradoException {
        Medico m = acharMedico(codigo);
        if (m == null) {
            throw new RegistroNaoEncontradoException("Médico não encontrado: " + codigo);
        }
        return m;
    }

    public Paciente buscarPaciente(String cpf) throws RegistroNaoEncontradoException {
        Paciente p = acharPaciente(cpf);
        if (p == null) {
            throw new RegistroNaoEncontradoException("Paciente não encontrado: " + cpf);
        }
        return p;
    }

    private Medico acharMedico(int codigo) {
        for (Medico m : medicos) {
            if (m.getCodigo() == codigo) {
                return m;
            }
        }
        return null;
    }

    private Paciente acharPaciente(String cpf) {
        for (Paciente p : pacientes) {
            if (p.getCpf().equals(cpf)) {
                return p;
            }
        }
        return null;
    }
}
```

- [ ] **Step 4: Compilar e rodar o teste**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName); java -cp out teste.TesteConsultorioLookup`
Expected: `TODOS OS TESTES PASSARAM`.

- [ ] **Step 5: Commit**

```bash
git add src/negocio/Consultorio.java src/teste/TesteConsultorioLookup.java
git commit -m "feat: Consultorio com vinculação e lookups"
```

---

### Task 8: As 6 pesquisas no `Consultorio` (TDD)

**Files:**
- Modify: `src/negocio/Consultorio.java`
- Test: `src/teste/TesteConsultorioBuscas.java`

- [ ] **Step 1: Escrever o teste que falha**

Datas são relativas a `LocalDate.now()` para o teste ser estável.

```java
package teste;

import modelo.*;
import negocio.Consultorio;
import excecoes.RegistroNaoEncontradoException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class TesteConsultorioBuscas {
    public static void main(String[] args) throws RegistroNaoEncontradoException {
        Medico ana = new Medico(101, "Dra. Ana");
        Medico bruno = new Medico(102, "Dr. Bruno");
        Paciente joao = new Paciente("11144477735", "João");
        Paciente maria = new Paciente("52998224725", "Maria");

        LocalDate hoje = LocalDate.now();
        Consulta passada = new Consulta(hoje.minusDays(10), LocalTime.of(9, 0), 101, "11144477735");
        Consulta futura = new Consulta(hoje.plusDays(10), LocalTime.of(8, 0), 101, "11144477735");
        Consulta futuraCedo = new Consulta(hoje.plusDays(10), LocalTime.of(7, 0), 101, "52998224725");
        Consulta antiga = new Consulta(hoje.minusMonths(8), LocalTime.of(9, 0), 101, "52998224725");

        Consultorio c = new Consultorio(
                Arrays.asList(ana, bruno),
                Arrays.asList(joao, maria),
                Arrays.asList(passada, futura, futuraCedo, antiga));
        c.vincular();

        // 1. pacientes do médico
        Assert.check(c.pacientesDoMedico(101).size() == 2, "pacientesDoMedico conta 2");

        // 2. consultas do médico no período, ordenadas por data/horário
        List<Consulta> periodo = c.consultasDoMedicoNoPeriodo(101, hoje.plusDays(10), hoje.plusDays(10));
        Assert.check(periodo.size() == 2, "consultasNoPeriodo conta 2");
        Assert.check(periodo.get(0).getHorario().equals(LocalTime.of(7, 0)), "consultasNoPeriodo ordenadas por horário");

        // 3. pacientes inativos há mais de 3 meses (Maria: última c/ médico há 8 meses; João: há 10 dias)
        List<Paciente> inativos = c.pacientesInativos(101, 3);
        Assert.check(inativos.contains(maria) && !inativos.contains(joao), "pacientesInativos filtra por meses");

        // 4. médicos do paciente
        Assert.check(c.medicosDoPaciente("11144477735").size() == 1, "medicosDoPaciente conta 1");

        // 5. consultas passadas do paciente com médico
        Assert.check(c.consultasPassadasPacienteComMedico("11144477735", 101).size() == 1,
                "consultasPassadasPacienteComMedico conta 1");

        // 6. consultas futuras do paciente
        Assert.check(c.consultasFuturasDoPaciente("11144477735").size() == 1,
                "consultasFuturasDoPaciente conta 1");

        Assert.fim();
    }
}
```

- [ ] **Step 2: Rodar e ver falhar (não compila — métodos ausentes)**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)`
Expected: FALHA de compilação — métodos de busca não existem.

- [ ] **Step 3: Adicionar os 6 métodos de busca ao `Consultorio`**

Adicionar os imports no topo e os métodos antes do último `}` da classe:

```java
// imports adicionais no topo de Consultorio.java:
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
```

```java
    // --- 6 pesquisas ---

    public List<Paciente> pacientesDoMedico(int codigo) throws RegistroNaoEncontradoException {
        return buscarMedico(codigo).getPacientes();
    }

    public List<Consulta> consultasDoMedicoNoPeriodo(int codigo, LocalDate inicio, LocalDate fim)
            throws RegistroNaoEncontradoException {
        buscarMedico(codigo); // valida existência (propaga exceção)
        return consultas.stream()
                .filter(c -> c.getCodigoMedico() == codigo)
                .filter(c -> !c.getData().isBefore(inicio) && !c.getData().isAfter(fim))
                .sorted(Comparator.comparing(Consulta::getDataHora))
                .collect(Collectors.toList());
    }

    public List<Paciente> pacientesInativos(int codigo, int meses) throws RegistroNaoEncontradoException {
        Medico m = buscarMedico(codigo);
        LocalDateTime limite = LocalDateTime.now().minusMonths(meses);
        List<Paciente> inativos = new ArrayList<>();
        for (Paciente p : m.getPacientes()) {
            LocalDateTime ultima = ultimaConsultaPassadaComMedico(p, codigo);
            if (ultima == null || ultima.isBefore(limite)) {
                inativos.add(p);
            }
        }
        return inativos;
    }

    public List<Medico> medicosDoPaciente(String cpf) throws RegistroNaoEncontradoException {
        Paciente p = buscarPaciente(cpf);
        Set<Integer> codigos = new LinkedHashSet<>();
        for (Consulta c : p.getConsultas()) {
            codigos.add(c.getCodigoMedico());
        }
        List<Medico> resultado = new ArrayList<>();
        for (Integer codigo : codigos) {
            resultado.add(buscarMedico(codigo));
        }
        return resultado;
    }

    public List<Consulta> consultasPassadasPacienteComMedico(String cpf, int codigo)
            throws RegistroNaoEncontradoException {
        Paciente p = buscarPaciente(cpf);
        buscarMedico(codigo);
        LocalDateTime agora = LocalDateTime.now();
        return p.getConsultas().stream()
                .filter(c -> c.getCodigoMedico() == codigo)
                .filter(c -> c.getDataHora().isBefore(agora))
                .sorted(Comparator.comparing(Consulta::getDataHora))
                .collect(Collectors.toList());
    }

    public List<Consulta> consultasFuturasDoPaciente(String cpf) throws RegistroNaoEncontradoException {
        Paciente p = buscarPaciente(cpf);
        LocalDateTime agora = LocalDateTime.now();
        return p.getConsultas().stream()
                .filter(c -> c.getDataHora().isAfter(agora))
                .sorted(Comparator.comparing(Consulta::getDataHora))
                .collect(Collectors.toList());
    }

    private LocalDateTime ultimaConsultaPassadaComMedico(Paciente p, int codigo) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime ultima = null;
        for (Consulta c : p.getConsultas()) {
            if (c.getCodigoMedico() == codigo && c.getDataHora().isBefore(agora)) {
                if (ultima == null || c.getDataHora().isAfter(ultima)) {
                    ultima = c.getDataHora();
                }
            }
        }
        return ultima;
    }
```

- [ ] **Step 4: Compilar e rodar o teste**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName); java -cp out teste.TesteConsultorioBuscas`
Expected: `TODOS OS TESTES PASSARAM`.

- [ ] **Step 5: Commit**

```bash
git add src/negocio/Consultorio.java src/teste/TesteConsultorioBuscas.java
git commit -m "feat: 6 pesquisas no Consultorio"
```

---

### Task 9: `LeitorCsv` (TDD) — req 5 (throws propagado)

`lerPacientes` declara `throws IOException, CpfInvalidoException` e **não** usa try-catch (repassa — req 5).

**Files:**
- Create: `src/persistencia/LeitorCsv.java`
- Test: `src/teste/TesteLeitorCsv.java`
- Create (fixture): `dados/medicos.csv`, `dados/pacientes.csv`, `dados/consultas.csv`

- [ ] **Step 1: Criar os CSVs de exemplo**

`dados/medicos.csv`:
```csv
101,Dra. Ana Lima
102,Dr. Bruno Costa
```

`dados/pacientes.csv`:
```csv
11144477735,João Souza
52998224725,Maria Oliveira
```

`dados/consultas.csv`:
```csv
2026-03-10,14:30,101,11144477735
2026-07-20,09:00,101,52998224725
2026-07-20,08:00,102,11144477735
```

- [ ] **Step 2: Escrever o teste que falha**

```java
package teste;

import modelo.Consulta;
import modelo.Medico;
import modelo.Paciente;
import persistencia.LeitorCsv;
import excecoes.CpfInvalidoException;

import java.io.IOException;
import java.util.List;

public class TesteLeitorCsv {
    public static void main(String[] args) throws IOException, CpfInvalidoException {
        LeitorCsv leitor = new LeitorCsv();

        List<Medico> medicos = leitor.lerMedicos("dados/medicos.csv");
        Assert.check(medicos.size() == 2, "lerMedicos lê 2 médicos");
        Assert.check(medicos.get(0).getCodigo() == 101, "primeiro médico é 101");

        List<Paciente> pacientes = leitor.lerPacientes("dados/pacientes.csv");
        Assert.check(pacientes.size() == 2, "lerPacientes lê 2 pacientes");
        Assert.check(pacientes.get(0).getCpf().equals("11144477735"), "primeiro paciente CPF ok");

        List<Consulta> consultas = leitor.lerConsultas("dados/consultas.csv");
        Assert.check(consultas.size() == 3, "lerConsultas lê 3 consultas");
        Assert.check(consultas.get(0).getCodigoMedico() == 101, "primeira consulta médico 101");

        Assert.fim();
    }
}
```

- [ ] **Step 3: Rodar e ver falhar (não compila)**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)`
Expected: FALHA de compilação — `LeitorCsv` não existe.

- [ ] **Step 4: Implementar `LeitorCsv`**

```java
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
```

- [ ] **Step 5: Compilar e rodar o teste**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName); java -cp out teste.TesteLeitorCsv`
Expected: `TODOS OS TESTES PASSARAM`.

- [ ] **Step 6: Commit**

```bash
git add src/persistencia/LeitorCsv.java src/teste/TesteLeitorCsv.java dados
git commit -m "feat: leitura de CSV com validação de CPF e CSVs de exemplo"
```

---

### Task 10: `RepositorioBinario` (TDD) — req 5 (segundo throws propagado)

`carregar` declara `throws IOException, ClassNotFoundException` e **não** usa try-catch (repassa — req 5).

**Files:**
- Create: `src/persistencia/RepositorioBinario.java`
- Test: `src/teste/TesteRepositorioBinario.java`

- [ ] **Step 1: Escrever o teste que falha**

```java
package teste;

import modelo.Consulta;
import modelo.Medico;
import modelo.Paciente;
import negocio.Consultorio;
import persistencia.RepositorioBinario;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

public class TesteRepositorioBinario {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Medico m = new Medico(101, "Dra. Ana");
        Paciente p = new Paciente("11144477735", "João");
        Consulta c = new Consulta(LocalDate.of(2026, 3, 10), LocalTime.of(14, 30), 101, "11144477735");
        Consultorio original = new Consultorio(Arrays.asList(m), Arrays.asList(p), Arrays.asList(c));
        original.vincular();

        RepositorioBinario repo = new RepositorioBinario();
        repo.salvar(original, "out/teste-dados.bin");
        Consultorio restaurado = repo.carregar("out/teste-dados.bin");

        Assert.check(restaurado.getMedicos().size() == 1, "round-trip preserva médicos");
        Assert.check(restaurado.getConsultas().size() == 1, "round-trip preserva consultas");
        Assert.check(restaurado.getMedicos().get(0).getPacientes().size() == 1,
                "round-trip preserva grafo vinculado");
        Assert.fim();
    }
}
```

- [ ] **Step 2: Rodar e ver falhar (não compila)**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)`
Expected: FALHA de compilação — `RepositorioBinario` não existe.

- [ ] **Step 3: Implementar `RepositorioBinario`**

```java
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
```

- [ ] **Step 4: Compilar e rodar o teste**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName); java -cp out teste.TesteRepositorioBinario`
Expected: `TODOS OS TESTES PASSARAM`.

- [ ] **Step 5: Commit**

```bash
git add src/persistencia/RepositorioBinario.java src/teste/TesteRepositorioBinario.java
git commit -m "feat: persistência binária via serialização (salvar/carregar)"
```

---

### Task 11: `ProgramaP1` — lê CSV e serializa

**Files:**
- Create: `src/app/ProgramaP1.java`

- [ ] **Step 1: Implementar `ProgramaP1`**

```java
package app;

import excecoes.CpfInvalidoException;
import modelo.Consulta;
import modelo.Medico;
import modelo.Paciente;
import negocio.Consultorio;
import persistencia.LeitorCsv;
import persistencia.RepositorioBinario;

import java.io.IOException;
import java.util.List;

public class ProgramaP1 {
    public static void main(String[] args) {
        String dirDados = args.length > 0 ? args[0] : "dados";
        String saidaBin = args.length > 1 ? args[1] : "dados/consultorio.bin";

        LeitorCsv leitor = new LeitorCsv();
        RepositorioBinario repo = new RepositorioBinario();

        try {
            List<Medico> medicos = leitor.lerMedicos(dirDados + "/medicos.csv");
            List<Paciente> pacientes = leitor.lerPacientes(dirDados + "/pacientes.csv");
            List<Consulta> consultas = leitor.lerConsultas(dirDados + "/consultas.csv");

            Consultorio consultorio = new Consultorio(medicos, pacientes, consultas);
            consultorio.vincular();

            repo.salvar(consultorio, saidaBin);

            System.out.println("P1 concluído: " + medicos.size() + " médicos, "
                    + pacientes.size() + " pacientes, " + consultas.size() + " consultas.");
            System.out.println("Objetos salvos em: " + saidaBin);
        } catch (CpfInvalidoException e) {
            System.err.println("Erro de validação de CPF: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro de E/S: " + e.getMessage());
        }
    }
}
```

- [ ] **Step 2: Compilar e executar P1 ponta a ponta**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName); java -cp out app.ProgramaP1`
Expected: imprime "P1 concluído: 2 médicos, 2 pacientes, 3 consultas." e cria `dados/consultorio.bin`.

- [ ] **Step 3: Commit**

```bash
git add src/app/ProgramaP1.java
git commit -m "feat: ProgramaP1 (CSV -> binário)"
```

---

### Task 12: `RegistradorResultados` — `resultados.csv` (req 3)

Grava resultados parciais (cada busca, com timestamp) e, ao final, o resumo.

**Files:**
- Create: `src/ui/RegistradorResultados.java`

- [ ] **Step 1: Implementar `RegistradorResultados`**

```java
package ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RegistradorResultados {
    private final String caminho;
    private int totalBuscas = 0;

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RegistradorResultados(String caminho) {
        this.caminho = caminho;
    }

    /** Resultado parcial: registra uma busca executada com timestamp, parâmetros e linhas. */
    public void registrarBusca(String descricao, String parametros, List<String> linhas) throws IOException {
        totalBuscas++;
        try (PrintWriter pw = abrir()) {
            pw.println("timestamp," + LocalDateTime.now().format(TS));
            pw.println("busca," + descricao);
            pw.println("parametros," + parametros);
            pw.println("qtd_resultados," + linhas.size());
            for (String linha : linhas) {
                pw.println("resultado," + linha.replace(",", ";"));
            }
            pw.println();
        }
    }

    /** Resultado final: resumo da sessão. */
    public void registrarResumoFinal() throws IOException {
        try (PrintWriter pw = abrir()) {
            pw.println("RESUMO FINAL");
            pw.println("timestamp," + LocalDateTime.now().format(TS));
            pw.println("total_buscas," + totalBuscas);
            pw.println();
        }
    }

    private PrintWriter abrir() throws IOException {
        return new PrintWriter(Files.newBufferedWriter(
                Paths.get(caminho),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND));
    }
}
```

- [ ] **Step 2: Compilar**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)`
Expected: compila sem erros.

- [ ] **Step 3: Commit**

```bash
git add src/ui/RegistradorResultados.java
git commit -m "feat: RegistradorResultados (resultados.csv parciais + final)"
```

---

### Task 13: GUI Swing — painéis e tela principal

Inclui a **chamada polimórfica (req 12)**: `formatarPessoas(List<? extends Pessoa>)` percorre como `List<Pessoa>` chamando `getResumo()`.

**Files:**
- Create: `src/ui/Formatador.java`
- Create: `src/ui/PainelMedico.java`
- Create: `src/ui/PainelPaciente.java`
- Create: `src/ui/TelaPrincipal.java`

- [ ] **Step 1: Criar `Formatador` (chamada polimórfica — req 12)**

```java
package ui;

import modelo.Consulta;
import modelo.Pessoa;

import java.util.ArrayList;
import java.util.List;

public final class Formatador {

    private Formatador() { }

    /** req 12: chamada polimórfica — itera List<Pessoa> chamando getResumo() sobrescrito. */
    public static List<String> formatarPessoas(List<? extends Pessoa> pessoas) {
        List<String> linhas = new ArrayList<>();
        for (Pessoa p : pessoas) {
            linhas.add(p.getResumo());
        }
        return linhas;
    }

    public static List<String> formatarConsultas(List<Consulta> consultas) {
        List<String> linhas = new ArrayList<>();
        for (Consulta c : consultas) {
            linhas.add(c.toString());
        }
        return linhas;
    }
}
```

- [ ] **Step 2: Criar `PainelMedico`**

```java
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
```

- [ ] **Step 3: Criar `PainelPaciente`**

```java
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
```

- [ ] **Step 4: Criar `TelaPrincipal`**

```java
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
```

- [ ] **Step 5: Compilar**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)`
Expected: compila sem erros.

- [ ] **Step 6: Commit**

```bash
git add src/ui/Formatador.java src/ui/PainelMedico.java src/ui/PainelPaciente.java src/ui/TelaPrincipal.java
git commit -m "feat: GUI Swing (abas médico/paciente) com chamada polimórfica"
```

---

### Task 14: `ProgramaP2` — restaura binário e abre GUI

**Files:**
- Create: `src/app/ProgramaP2.java`

- [ ] **Step 1: Implementar `ProgramaP2`**

```java
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
```

- [ ] **Step 2: Compilar e executar P2 (verificação manual)**

Run: `javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName); java -cp out app.ProgramaP2`
Expected: abre a janela Swing com abas Médico/Paciente. Testar manualmente:
- Aba Médico, código `101`, botão "1. Pacientes do médico" → lista 2 pacientes.
- Aba Médico, código `101`, período `2026-07-01`..`2026-07-31`, botão "2." → lista 2 consultas ordenadas por horário.
- Aba Paciente, CPF `11144477735`, botão "1." → lista 1 médico.
- Fechar a janela → `resultados.csv` contém as buscas com timestamp e o bloco `RESUMO FINAL`.

- [ ] **Step 3: Commit**

```bash
git add src/app/ProgramaP2.java
git commit -m "feat: ProgramaP2 (binário -> GUI Swing)"
```

---

### Task 15: Limpeza e verificação final

**Files:**
- Delete: `src/Main.java` (stub do IntelliJ, não usado)
- Create: `README.md`

- [ ] **Step 1: Remover o stub `Main.java`**

Remover o arquivo `src/Main.java` (stub do IntelliJ, não usado). Os mains reais são `app.ProgramaP1` e `app.ProgramaP2`.

- [ ] **Step 2: Criar `README.md` com instruções de execução**

```markdown
# Médicos e Pacientes — RA2 POO

## Compilar
```
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)
```

## Executar
1. P1 (gera o binário a partir dos CSVs em `dados/`):
   `java -cp out app.ProgramaP1`
2. P2 (restaura o binário e abre a GUI):
   `java -cp out app.ProgramaP2`

Resultados das buscas são gravados em `resultados.csv`.

## Testes
`java -cp out teste.TesteValidadorCpf` (e demais classes `teste.Teste*`)
```

- [ ] **Step 3: Compilar tudo e rodar todos os testes**

Run:
```
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)
java -cp out teste.TesteValidadorCpf
java -cp out teste.TesteConsultorioLookup
java -cp out teste.TesteConsultorioBuscas
java -cp out teste.TesteLeitorCsv
java -cp out teste.TesteRepositorioBinario
```
Expected: cada um imprime `TODOS OS TESTES PASSARAM`.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "chore: remove stub Main, adiciona README e verificação final"
```

---

## Checklist final dos 12 requisitos (verificar ao concluir)

| Req | Verificação |
|----|----|
| 1 | `ProgramaP1` grava `consultorio.bin`; `ProgramaP2` restaura (Tasks 11, 14) |
| 2 | GUI Swing com abas e formulários (Task 13) |
| 3 | `resultados.csv` com parciais + `RESUMO FINAL` (Tasks 12, 14) |
| 4 | `CpfInvalidoException` (`throw` na Task 3) + `RegistroNaoEncontradoException` (`throw` na Task 7) |
| 5 | `LeitorCsv.lerPacientes` e `RepositorioBinario.carregar` com `throws` sem try-catch (Tasks 9, 10) |
| 6 | pacotes `app`, `modelo`, `negocio`, `persistencia`, `excecoes`, `ui` |
| 7 | atributos `private`/`protected` (`Pessoa.nome` protegido) |
| 8 | `Pessoa` → `Medico`/`Paciente` (Tasks 4, 6) |
| 9 | `getResumo()` sobrescrito |
| 10 | `Pessoa` abstrata + `getResumo()` abstrato (Task 4) |
| 11 | `Identificavel` implementada por `Medico`/`Paciente` (Tasks 4, 6) |
| 12 | `Formatador.formatarPessoas` itera `List<Pessoa>` chamando `getResumo()` (Task 13) |
