---
title: Roteiro do Vídeo — Médicos e Pacientes (RA2 + RA3 POO)
date: 2026-06-15
duracao: máximo 9 minutos
integrantes: 5
---

# Roteiro do Vídeo — "Médicos e Pacientes"

**Duração total: no máximo 9 minutos**, em três partes obrigatórias:

| Parte | Conteúdo | Tempo | Quem fala |
|-------|----------|-------|-----------|
| (a) | Demonstração da aplicação em execução — **sem citar código** | 1 a 2 min | um ou mais integrantes |
| (b) | Implementação **RA2** — itens de OO da `FichaAvaliacao_RA2.pdf` | máx. 5 min | **TODOS**, tempo igualmente dividido |
| (c) | Implementação **RA3** — itens da `FichaAvaliacao_RA3.pdf` | máx. 2 min | **TODOS**, tempo igualmente dividido |

> **Equipe:** 5 integrantes. Substitua "Integrante 1..5" pelos nomes reais.
> **Divisão dos itens** (sem sobreposição entre RA2 e RA3):
>
> | Integrante | Parte (b) RA2 | Parte (c) RA3 |
> |---|---|---|
> | 1 | pacotes + encapsulamento | item 1 — P1 cria objetos e salva binário |
> | 2 | generalização/especialização (herança) | item 2 — P2 restaura do binário |
> | 3 | classe abstrata + interface | item 3 — P2 com interface gráfica |
> | 4 | polimorfismo + chamada polimórfica | item 4 — resultados **parciais** |
> | 5 | exceções próprias + propagação | item 4 — resultado **final** |
>
> **Como usar:** o texto em **"Fala"** pode ser lido quase na íntegra. Linhas em _itálico entre colchetes_ `[ ... ]` são **ações** (clicar, apontar), não devem ser ditas.

> **Preparação (antes de gravar):**
> ```powershell
> javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)
> java -cp out app.ProgramaP1      # gera dados/consultorio.bin a partir dos CSVs
> java -cp out app.ProgramaP2      # abre a GUI
> ```
> Para a parte (a), deixe a **janela da GUI** aberta. Para (b)/(c), deixe o **editor** com os arquivos prontos para mostrar.

---

# PARTE (a) — Demonstração (1 a 2 min) · SEM mencionar código

> **Regra:** proibido falar de classes, pacotes, Java ou implementação. Só o aplicativo funcionando. Pode ser apresentada por um ou mais integrantes.
> Valores abaixo batem com os dados de exemplo (hoje = 2026-06-15).

**Fala (abertura):**
> "Esta é a aplicação Médicos e Pacientes. Ela tem duas áreas: uma para o médico e outra para o paciente, cada uma com três pesquisas. Vou mostrar todas funcionando."

**Aba Médico — 3 buscas**
_[Aba "Médico". `101` em "Código do médico" → "1. Pacientes do médico".]_
> "Na visão do médico: peço todos os pacientes do médico 101. Ele lista a Maria e o João."

_[`101`, início `2026-01-01`, fim `2026-12-31` → "2. Consultas no período".]_
> "Agora as consultas desse médico num período. Repare que vêm em ordem crescente de data e horário — a de março antes da de julho."

_[`101`, meses `4` → "3. Pacientes inativos".]_
> "E os pacientes que não o consultam há mais de quatro meses. Aponta só a Maria, porque o João teve uma consulta recente com esse médico."

**Aba Paciente — 3 buscas**
_[Aba "Paciente". CPF `11144477735` → "1. Médicos do paciente".]_
> "Na visão do paciente: para o João, quais médicos ele já viu ou tem agendado? Aparecem a Dra. Ana e o Dr. Bruno."

_[CPF `11144477735`, médico `101` → "2. Consultas passadas c/ médico".]_
> "O histórico dele com um médico específico: só a consulta que já aconteceu."

_[CPF `11144477735` → "3. Consultas futuras".]_
> "E só o que está agendado para o futuro: a consulta de julho."

**Fecho da demo:**
_[Abra o arquivo `resultados.csv` gerado.]_
> "Cada pesquisa também fica registrada neste arquivo de resultados, com data, o que foi pesquisado e o que foi encontrado. Encerro a demonstração."

---

# PARTE (b) — Implementação RA2 (máx. 5 min) · TODOS falam

> Itens de Orientação a Objetos (os de infraestrutura — P1/P2, GUI, resultados — são avaliados no RA3, parte (c)).
> Divisão: **5 integrantes ≈ 1 minuto cada**. Total ≤ 5 min.

## Integrante 1 — Pacotes e encapsulamento

**Fala:**
> "Eu explico a organização e o encapsulamento. **Item: divisão em pacotes.** O código está separado por responsabilidade em seis pacotes — `modelo`, `negocio`, `persistencia`, `ui`, `excecoes` e `app` —, bem acima do mínimo. _[mostra a árvore `src/`]_
>
> **Item: encapsulamento.** Todos os atributos das entidades são `private` — `codigo`, `cpf`, `consultas` — e o acesso é só por métodos. Na superclasse `Pessoa`, o `nome` é `protected`, para ser usado pelas subclasses sem ficar público. Nada do estado interno é exposto diretamente. _[mostra `modelo/Medico.java`]_"

```java
public class Medico extends Pessoa implements Identificavel {
    private int codigo;                 // estado interno protegido
    private List<Paciente> pacientes;
    public int getCodigo() { return codigo; }   // acesso só por método
}
```

## Integrante 2 — Generalização e especialização (herança)

**Fala:**
> "Eu falo de herança. **Item: generalização e especialização.** Médico e paciente têm em comum o nome e a ideia de 'se descrever'. Por isso criei a superclasse `Pessoa`, da qual `Medico` e `Paciente` **herdam** com `extends`. O que é comum sobe para a superclasse; o que é específico — o código do médico, o CPF do paciente — fica em cada subclasse. É a relação de generalização/especialização. _[mostra `modelo/Pessoa.java`, `Medico.java`, `Paciente.java`]_"

```java
public abstract class Pessoa implements Serializable { protected String nome; }
public class Medico   extends Pessoa { private int codigo;  /* ... */ }
public class Paciente extends Pessoa { private String cpf;  /* ... */ }
```

## Integrante 3 — Classe abstrata e interface

**Fala:**
> "Eu mostro abstração e interface. **Item: classe abstrata com método abstrato.** `Pessoa` é declarada `abstract` e tem o método abstrato `getResumo()`, sem corpo. Ela não pode ser instanciada — não existe 'pessoa' genérica — e obriga cada subclasse a definir como se resume. _[mostra `modelo/Pessoa.java`]_
>
> **Item: interface implementada.** Defini a interface `Identificavel`, com `getIdentificador()`. `Medico` e `Paciente` a **implementam**: o médico devolve o código, o paciente devolve o CPF. _[mostra `modelo/Identificavel.java`]_"

```java
public interface Identificavel { String getIdentificador(); }

public abstract class Pessoa implements Serializable {
    public abstract String getResumo();        // método abstrato
}
```

## Integrante 4 — Polimorfismo e chamada polimórfica

**Fala:**
> "Eu explico o polimorfismo. **Item: polimorfismo.** Cada subclasse **sobrescreve** `getResumo()` à sua maneira — o mesmo método, comportamento diferente conforme o tipo real do objeto. _[mostra os dois `getResumo()`]_
>
> **Item: chamada polimórfica.** No `Formatador`, recebo uma lista de `Pessoa` e, num único laço, chamo `getResumo()` em cada elemento. Não há nenhum `if` perguntando o tipo: em tempo de execução o Java despacha para a versão certa. O mesmo laço formata médicos ou pacientes. _[mostra `ui/Formatador.java`]_"

```java
public static List<String> formatarPessoas(List<? extends Pessoa> pessoas) {
    List<String> linhas = new ArrayList<>();
    for (Pessoa p : pessoas) linhas.add(p.getResumo());   // despacho dinâmico
    return linhas;
}
```

## Integrante 5 — Exceções próprias e propagação

**Fala:**
> "Eu fecho com tratamento de erros. **Item: pelo menos duas exceções próprias lançadas.** Criei `CpfInvalidoException` e `RegistroNaoEncontradoException`, ambas estendendo `Exception`. A primeira é lançada com `throw` quando um CPF não tem 11 dígitos ou falha nos dígitos verificadores; a segunda, quando se busca um médico ou paciente inexistente. _[mostra `excecoes/` e `ValidadorCpf.java`]_
>
> **Item: pelo menos dois métodos que propagam exceção.** O `lerPacientes` e o `carregar` **declaram `throws` e não usam try-catch** — deixam o erro subir para quem chama tratar. _[mostra as assinaturas]_"

```java
public static void validar(String cpf) throws CpfInvalidoException {
    if (cpf == null || !cpf.matches("\\d{11}"))
        throw new CpfInvalidoException("CPF deve ter 11 dígitos: " + cpf);   // throw
}
public List<Paciente> lerPacientes(String caminho)
        throws IOException, CpfInvalidoException { /* não captura: propaga */ }
```

---

# PARTE (c) — Implementação RA3 (máx. 2 min) · TODOS falam

> Os 4 itens da `FichaAvaliacao_RA3.pdf`. Divisão: **5 integrantes ≈ 24 segundos cada** (o item 4 é dividido em "parciais" e "final"). Total ≤ 2 min.

## Integrante 1 — Item 1: P1 cria objetos persistentes e salva em binário

**Fala:**
> "Item 1 do RA3: o programa P1 cria o conjunto inicial de objetos persistentes a partir de arquivos texto e os salva em binário. O `ProgramaP1` lê os três CSVs — médicos, pacientes e consultas —, monta os objetos no `Consultorio` e grava tudo em formato binário com `ObjectOutputStream`." _[mostra `app/ProgramaP1.java`]_

```java
Consultorio consultorio = new Consultorio(medicos, pacientes, consultas);
repo.salvar(consultorio, saidaBin);          // ObjectOutputStream -> .bin
```

## Integrante 2 — Item 2: P2 restaura os objetos do binário

**Fala:**
> "Item 2: o programa P2 restaura em memória esse conjunto a partir do binário. O `ProgramaP2` não lê CSV nenhum — ele **desserializa** o arquivo binário com `ObjectInputStream` e reconstrói o `Consultorio` inteiro, com os objetos já ligados entre si." _[mostra `app/ProgramaP2.java`]_

```java
Consultorio consultorio = repo.carregar(entradaBin);   // ObjectInputStream <- .bin
```

## Integrante 3 — Item 3: P2 interage por interface gráfica

**Fala:**
> "Item 3: o P2 faz a interação com o usuário por meio de interface gráfica. A `TelaPrincipal` é uma janela Swing (`JFrame`) com duas abas — médico e paciente —, cada uma com os campos de entrada e os botões das três pesquisas." _[mostra `ui/TelaPrincipal.java` e a janela]_

```java
JTabbedPane abas = new JTabbedPane();
abas.addTab("Médico",   new PainelMedico(consultorio, registrador));
abas.addTab("Paciente", new PainelPaciente(consultorio, registrador));
```

## Integrante 4 — Item 4 (parte 1): resultados parciais

**Fala:**
> "Item 4, primeira parte: o P2 escreve os resultados **parciais** da execução num arquivo. A cada pesquisa executada, o `RegistradorResultados` anexa no `resultados.csv` uma entrada com timestamp, os parâmetros usados e as linhas encontradas." _[mostra `registrarBusca` em `ui/RegistradorResultados.java`]_

```java
public void registrarBusca(String descricao, String parametros, List<String> linhas) {
    pw.println("timestamp," + agora);    // resultado parcial, por busca
    pw.println("busca," + descricao);
    // ... parâmetros e linhas
}
```

## Integrante 5 — Item 4 (parte 2): resultado final

**Fala:**
> "Item 4, segunda parte: o P2 escreve o resultado **final**. Ao fechar a janela, um `WindowListener` dispara o resumo final, que anexa ao mesmo arquivo um bloco com o total de buscas realizadas na sessão." _[mostra `registrarResumoFinal` e o `WindowListener` em `ui/TelaPrincipal.java`]_

```java
public void registrarResumoFinal() {        // resultado final, ao encerrar
    pw.println("RESUMO FINAL");
    pw.println("total_buscas," + totalBuscas);
}
```

---

## Controle de tempo (ensaiar antes de gravar)

| Parte | Alvo | Limite |
|-------|------|--------|
| (a) Demonstração | ~1:30 | 2:00 |
| (b) RA2 — 5 × ~1:00 | ~5:00 | 5:00 |
| (c) RA3 — 5 × ~0:24 | ~2:00 | 2:00 |
| **Total** | **~8:30** | **9:00** |

## Perguntas prováveis (se houver arguição)

- **"A especificação não pedia console?"** "Implementamos em GUI Swing, que é um superconjunto do console: mantém as duas interfaces separadas, as seis buscas e a gravação em arquivo. A lógica de negócio fica isolada no `Consultorio`, então portar para console seria só reescrever a camada de UI."
- **"Por que dois programas?"** "Para separar a carga e validação dos dados (P1, uma vez) da consulta (P2, uso diário). O binário intermediário garante que o consultor parte de um estado já validado."
- **"Como garantem CPF válido?"** "O `ValidadorCpf` confere os 11 dígitos e recalcula os dois verificadores por módulo 11; CPF inválido barra a carga no P1."
