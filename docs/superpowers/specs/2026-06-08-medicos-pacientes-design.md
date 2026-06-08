---
title: Médicos e Pacientes — Design RA2 (POO)
date: 2026-06-08
status: aprovado
---

# Médicos e Pacientes — Design RA2

Trabalho de Programação Orientada a Objetos. Amplia a aplicação "Médicos e
Pacientes" (`MedicosPacientes.pdf`) com dois programas (P1/P2), persistência
binária, interface gráfica Swing, exceções e hierarquia OO. Abordagem **A**
(enxuta): herança via `Pessoa` abstrata; interface obrigatória = `Identificavel`.

## Objetivo

Satisfazer os 12 requisitos do enunciado mantendo o domínio original (médicos,
pacientes, consultas) e as 6 pesquisas, sem OO artificial.

## Arquitetura de pacotes

```
app/          ProgramaP1 (main), ProgramaP2 (main)
modelo/       Identificavel, Pessoa (abstract), Medico, Paciente, Consulta
negocio/      Consultorio  (grafo de objetos, raiz serializável + buscas)
persistencia/ LeitorCsv, RepositorioBinario
excecoes/     CpfInvalidoException, RegistroNaoEncontradoException
ui/           TelaPrincipal, PainelMedico, PainelPaciente, RegistradorResultados
```

São 6 pacotes (satisfaz req 6: ≥2).

## Fluxo P1 → P2

- **P1 (`app.ProgramaP1`)**: lê 3 CSVs → monta `Consultorio` → serializa em
  `dados.bin` via `ObjectOutputStream`. Apenas cria os objetos persistentes.
- **P2 (`app.ProgramaP2`)**: desserializa `dados.bin` (`ObjectInputStream`) →
  restaura o `Consultorio` em memória → abre a GUI Swing → grava resultados das
  pesquisas em `resultados.csv`.

## Formato dos arquivos CSV de entrada

| Arquivo | Colunas | Exemplo |
|---|---|---|
| `medicos.csv` | `codigo,nome` | `101,Dra. Ana Lima` |
| `pacientes.csv` | `cpf,nome` | `12345678909,João Souza` |
| `consultas.csv` | `data,horario,codigoMedico,cpfPaciente` | `2026-03-10,14:30,101,12345678909` |

- `data` em ISO `yyyy-MM-dd`; `horario` em `HH:mm`.
- CPF: 11 dígitos (9 + 2 verificadores), validado no parsing.

## Modelo (herança, abstração, interface)

```java
interface Identificavel {
    String getIdentificador();
}

abstract class Pessoa implements Serializable {
    protected String nome;
    public abstract String getResumo();   // método abstrato (req 10)
    public String getNome() { ... }
}

class Medico extends Pessoa implements Identificavel {
    private int codigo;
    private List<Paciente> pacientes;
    public String getIdentificador() { return String.valueOf(codigo); }
    public String getResumo() { /* override */ }
}

class Paciente extends Pessoa implements Identificavel {
    private String cpf;
    private List<Consulta> consultas;
    public String getIdentificador() { return cpf; }
    public String getResumo() { /* override */ }
}

class Consulta implements Serializable {
    private LocalDate data;
    private LocalTime horario;
    private int codigoMedico;
    private String cpfPaciente;
}
```

- **Generalização/especialização (req 8):** `Pessoa` → `Medico`, `Paciente`.
- **Classe abstrata + método abstrato (req 10):** `Pessoa` / `getResumo()`.
- **Interface implementada (req 11):** `Identificavel` em `Medico` e `Paciente`.
- **Encapsulamento (req 7):** atributos `private`; `nome` é `protected` em
  `Pessoa` e só é acessado por subclasses.
- **Polimorfismo + chamada polimórfica (req 9, 12):** percorrer `List<Pessoa>`
  chamando `getResumo()` sobrescrito (ex.: ao montar a saída de uma listagem).

## Exceções (req 4 e 5)

Duas classes derivadas de `Exception`, cada uma lançada com `throw` ao menos uma vez:

- `CpfInvalidoException extends Exception` — `throw` quando o CPF não tem 11
  dígitos ou os dígitos verificadores são inválidos (no parsing de `pacientes.csv`).
- `RegistroNaoEncontradoException extends Exception` — `throw` quando uma busca
  referencia código de médico ou CPF de paciente inexistente no `Consultorio`.

Dois métodos que **repassam** exceções (cláusula `throws`, sem try-catch — req 5):

- `LeitorCsv.lerPacientes(String caminho) throws IOException, CpfInvalidoException`
- `RepositorioBinario.carregar(String caminho) throws IOException, ClassNotFoundException`

## Negócio — `Consultorio`

`class Consultorio implements Serializable` é a raiz do grafo persistido:
`List<Medico>`, `List<Paciente>`, `List<Consulta>`. Concentra as 6 pesquisas.

### As 6 pesquisas

Interface do **Médico**:
1. Todos os pacientes de um médico.
2. Consultas agendadas de um médico num período `[dataInicial, dataFinal]`,
   ordenadas crescente por horário. (Período pode cobrir passado e futuro.)
3. Pacientes de um médico que não o consultam há mais de `N` meses.

Interface do **Paciente**:
1. Todos os médicos de um paciente (consultas passadas + agendadas).
2. Consultas passadas de um paciente com um médico específico (só passado).
3. Consultas futuras agendadas de um paciente (só futuro).

"Passado/futuro" são relativos a `LocalDateTime.now()` no momento da busca.
Buscas que recebem código/CPF inexistente lançam `RegistroNaoEncontradoException`.

## GUI Swing (req 2)

- `TelaPrincipal extends JFrame`: duas abas — **Médico** e **Paciente** —
  espelhando as duas interfaces do enunciado original.
- `PainelMedico` / `PainelPaciente` (`JPanel`): formulários com campos de
  entrada (código/CPF, datas, meses) e botões para cada uma das 3 buscas.
- Resultado exibido em `JTable`/`JTextArea`.
- Erros (CPF inválido, registro não encontrado) capturados na borda da UI e
  mostrados em `JOptionPane`.

## Arquivo de resultados — `resultados.csv` (req 3)

`ui.RegistradorResultados` grava em `resultados.csv`:

- **Resultados parciais:** cada busca executada é anexada com `timestamp`,
  identificação da busca, parâmetros e linhas do resultado.
- **Resultados finais:** ao fechar o P2, anexa um bloco `RESUMO FINAL` com a
  contagem total de buscas realizadas por tipo.

## Rastreamento dos 12 requisitos

| Req | Onde é satisfeito |
|----|----|
| 1 — P1/P2 + persistência binária | `app.ProgramaP1` grava `dados.bin`; `app.ProgramaP2` restaura |
| 2 — GUI | `ui.*` Swing, formulários em abas |
| 3 — arquivo de resultados | `resultados.csv` (parciais + final) |
| 4 — ≥2 Exception com `throw` | `CpfInvalidoException`, `RegistroNaoEncontradoException` |
| 5 — ≥2 métodos com `throws` propagado | `LeitorCsv.lerPacientes`, `RepositorioBinario.carregar` |
| 6 — ≥2 pacotes | 6 pacotes |
| 7 — encapsulamento | atributos `private`/`protected` |
| 8 — generalização/especialização | `Pessoa` → `Medico`/`Paciente` |
| 9 — polimorfismo | `getResumo()` sobrescrito |
| 10 — classe abstrata + método abstrato | `Pessoa` / `getResumo()` |
| 11 — interface implementada | `Identificavel` |
| 12 — chamada polimórfica | laço em `List<Pessoa>` chamando `getResumo()` |

## Fora de escopo (YAGNI)

- Edição/cadastro via GUI (o enunciado pede apenas pesquisas no P2).
- Banco de dados (persistência é via serialização binária).
- Validação de CPF além dos dígitos verificadores.
- Strategy/objetos de pesquisa (abordagem B descartada).
