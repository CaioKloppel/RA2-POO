---
title: Roteiro de Apresentação — Médicos e Pacientes (RA2 POO)
date: 2026-06-15
duracao: 10 minutos
---

# Roteiro de Apresentação — "Médicos e Pacientes"

**Duração-alvo:** 10 minutos
**Formato:** ~4 min de demonstração ao vivo + ~5 min de explicação de código + ~1 min de fechamento.

> **Como usar este roteiro:** o texto em **"Fala"** pode ser lido quase na íntegra — está escrito em ritmo de apresentação oral. As linhas em _itálico entre colchetes_ `[ ... ]` são **ações** (clicar, abrir arquivo, apontar para o código), não devem ser ditas.

> **Antes de começar (deixe pronto e escondido):**
> ```powershell
> javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)
> java -cp out app.ProgramaP1      # gera dados/consultorio.bin a partir dos CSVs
> java -cp out app.ProgramaP2      # abre a GUI (deixe a janela aberta para a demo)
> ```
> Tenha abertos: a **janela da GUI**, o **editor de código** e o arquivo **`resultados.csv`** numa aba.

---

## Linha do tempo (minuto a minuto)

| Tempo | Bloco | O que mostrar |
|-------|-------|---------------|
| 0:00–1:00 | Abertura + o problema | Requisitos da especificação |
| 1:00–2:00 | Arquitetura (P1 → binário → P2) | Diagrama de pacotes / fluxo |
| 2:00–4:00 | Demo aba **Médico** (3 buscas) | GUI rodando |
| 4:00–5:00 | Demo aba **Paciente** (3 buscas) + `resultados.csv` | GUI + arquivo gerado |
| 5:00–6:00 | Modelo OO (herança, abstração, interface) | `Pessoa`, `Medico`, `Paciente` |
| 6:00–7:30 | Polimorfismo + regras de negócio | `Formatador`, `Consultorio` |
| 7:30–8:30 | Persistência + exceções | `RepositorioBinario`, `ValidadorCpf` |
| 8:30–9:30 | Camada de UI + gravação de resultados | `PainelMedico`, `RegistradorResultados` |
| 9:30–10:00 | Fechamento (checklist OO) | Slide final |

---

## Bloco 1 — Abertura (0:00–1:00)

**Fala:**
> "Boa tarde. O meu trabalho é uma aplicação que gerencia **médicos, pacientes e consultas**. A ideia é simples de enunciar, mas exercita bem orientação a objetos: eu tenho três entidades que se relacionam — um médico atende vários pacientes, um paciente tem várias consultas, e cada consulta liga um médico a um paciente numa data e horário.
>
> A especificação pede seis pesquisas. Três olhando pelo lado do **médico** e três pelo lado do **paciente**. Os dados de entrada vêm de arquivos **CSV**, e o resultado de cada pesquisa pode ser gravado num arquivo de texto. Eu implementei tudo em Java, dividido em camadas, e vou mostrar primeiro funcionando e depois o código por dentro."

_[Se houver slide com a lista das 6 buscas, mostre agora.]_ Decore esta lista:
- **Médico:** 1) pacientes do médico · 2) consultas num período, em ordem crescente de horário · 3) pacientes inativos há mais de N meses.
- **Paciente:** 1) médicos do paciente · 2) consultas passadas com um médico · 3) consultas futuras agendadas.

---

## Bloco 2 — Arquitetura (1:00–2:00)

**Fala:**
> "Antes da demonstração, a visão geral. A aplicação são na verdade **dois programas**.
>
> O primeiro, o `ProgramaP1`, é o **carregador**: ele lê os três CSVs — médicos, pacientes e consultas —, monta os objetos em memória, liga as relações entre eles e **serializa** esse conjunto inteiro num arquivo binário, o `consultorio.bin`.
>
> O segundo, o `ProgramaP2`, é o **consultor**: ele não lê CSV nenhum. Ele **desserializa** o binário, que já vem com os objetos prontos e ligados, e abre a interface gráfica onde as seis buscas acontecem. A vantagem é que os dados são lidos e validados uma única vez, no P1, e o programa de consulta sempre parte de um estado consistente."

_[Mostre o slide do fluxo, ou aponte para a estrutura de pastas.]_

```
CSVs (dados/*.csv)
      │  LeitorCsv
      ▼
  Consultorio  ──vincular()──►  grafo: paciente↔consultas, médico↔pacientes
      │  RepositorioBinario.salvar()
      ▼
dados/consultorio.bin   ◄── (P1 termina aqui)
      │  RepositorioBinario.carregar()   (P2 começa aqui)
      ▼
  TelaPrincipal (Swing) ── 6 buscas ──► resultados.csv
```

**Fala (camadas):**
> "O código está separado por responsabilidade: `modelo` tem as entidades; `negocio` tem a classe `Consultorio`, onde moram as seis buscas; `persistencia` cuida de ler CSV e da serialização; `ui` é a interface gráfica; `excecoes` são as minhas exceções próprias; e `app` tem os dois `main`. Essa separação é o que permite, por exemplo, trocar a interface sem tocar na regra de negócio."

---

## Bloco 3 — Demo aba "Médico" (2:00–4:00)

> Use exatamente estes valores — eles batem com os dados de exemplo (hoje = 2026-06-15).

**Fala (transição):**
> "Vamos ver funcionando. A janela tem duas abas, uma para cada interface. Começo pela do **médico**."

**Busca 1 — Pacientes do médico**
_[Digite `101` no campo "Código do médico" e clique em "1. Pacientes do médico".]_
> "Pergunto quais são os pacientes do médico de código 101, a Dra. Ana Lima. _[clica]_ Ele retorna dois: o João Souza e a Maria Oliveira. Repare que um deles entrou na lista porque já teve consulta realizada e o outro porque tem consulta agendada — os dois contam como pacientes do médico."

**Busca 2 — Consultas no período, em ordem crescente**
_[Digite `101`, início `2026-01-01`, fim `2026-12-31`. Clique em "2. Consultas no período".]_
> "Agora as consultas desse mesmo médico dentro de um período. Coloco o ano inteiro de 2026. _[clica]_ Vêm duas consultas, e o detalhe importante é que elas saem **ordenadas por data e horário** — a de março aparece antes da de julho. O período pode cobrir tanto passado quanto futuro; quem ordena é a própria busca."

**Busca 3 — Pacientes inativos**
_[Digite `101`, meses `4`. Clique em "3. Pacientes inativos".]_
> "A terceira é a mais interessante: pacientes que não consultam esse médico há mais de um certo tempo. Coloco 4 meses. _[clica]_ Ele retorna só a Maria. Por quê? Porque a última consulta **já realizada** do João com esse médico foi em março, ou seja, dentro da janela de 4 meses, então o João conta como ativo. A Maria só tem consulta **futura** com ele, nenhuma no passado, então é tratada como inativa. O cálculo usa sempre a última consulta passada."

---

## Bloco 4 — Demo aba "Paciente" + resultados.csv (4:00–5:00)

**Fala (transição):**
> "Trocando para a aba do **paciente**, que tem as outras três buscas."

**Busca 1 — Médicos do paciente**
_[Digite o CPF `11144477735`. Clique em "1. Médicos do paciente".]_
> "Quais médicos esse paciente, o João, já viu ou tem agendado? _[clica]_ Retorna a Dra. Ana e o Dr. Bruno — porque o João tem uma consulta com cada um."

**Busca 2 — Consultas passadas com um médico**
_[CPF `11144477735`, código do médico `101`. Clique em "2. Consultas passadas c/ médico".]_
> "Agora só o histórico do João com o médico 101. _[clica]_ Vem só a de março, que já aconteceu. Consulta futura não entra aqui."

**Busca 3 — Consultas futuras**
_[CPF `11144477735`. Clique em "3. Consultas futuras".]_
> "E o inverso: só o que está agendado para frente. _[clica]_ Aparece a consulta de julho com o Dr. Bruno."

**Fala (resultados.csv):**
_[Abra o arquivo `resultados.csv`.]_
> "E a especificação pedia poder gravar o resultado em arquivo. Toda busca que eu fiz foi registrada aqui no `resultados.csv`, com **timestamp**, os **parâmetros** que usei e as **linhas** de resultado. E quando eu fechar a janela, ele ainda anexa um **resumo final** com o total de buscas da sessão. É a parte de saída em arquivo do enunciado."

---

## Bloco 5 — Modelo OO (5:00–6:00)

**Fala (transição):**
> "Mostrei funcionando; agora o código, começando pelo modelo de objetos."

_[Abra `modelo/Pessoa.java`, `Medico.java`, `Paciente.java`.]_

**Fala:**
> "Eu percebi que médico e paciente têm coisas em comum: os dois têm nome e os dois precisam de uma forma de se descrever. Então criei uma classe **abstrata** `Pessoa`, que guarda o nome e declara um método abstrato `getResumo`. Ela é abstrata de propósito — não faz sentido instanciar uma 'pessoa' genérica.
>
> `Medico` e `Paciente` **herdam** de `Pessoa` e cada um implementa o `getResumo` do seu jeito: o médico se descreve pelo código, o paciente pelo CPF. Além disso, os dois implementam a **interface** `Identificavel`, que obriga a ter um `getIdentificador` — o médico devolve o código, o paciente devolve o CPF. E todas as entidades implementam `Serializable`, que é justamente o que habilita a persistência binária que mencionei.
>
> Sobre **encapsulamento**: os campos são todos `private`, acesso só por getter. E repare num detalhe no `adicionarPaciente` do médico: ele só adiciona se ainda não estiver na lista, para não duplicar o mesmo paciente."

```java
public abstract class Pessoa implements Serializable {
    protected String nome;
    public abstract String getResumo();      // cada subclasse decide
}

public class Medico extends Pessoa implements Identificavel {
    @Override public String getResumo()        { return "Médico "   + codigo + " - " + nome; }
    @Override public String getIdentificador()  { return String.valueOf(codigo); }
}
```

---

## Bloco 6 — Polimorfismo + regras de negócio (6:00–7:30)

_[Abra `ui/Formatador.java`.]_

**Fala (polimorfismo):**
> "Esse desenho de classe rende o **polimorfismo** aqui no `Formatador`. Esse método recebe uma lista de qualquer coisa que seja `Pessoa` e, no laço, chama `getResumo` em cada elemento. Eu **não** pergunto 'é médico ou paciente?' em lugar nenhum. Em tempo de execução, o Java despacha para a versão certa do método automaticamente. O mesmo laço formata uma lista de médicos ou uma lista de pacientes sem mudar uma linha."

```java
public static List<String> formatarPessoas(List<? extends Pessoa> pessoas) {
    List<String> linhas = new ArrayList<>();
    for (Pessoa p : pessoas) linhas.add(p.getResumo());   // despacho dinâmico
    return linhas;
}
```

_[Abra `negocio/Consultorio.java`.]_

**Fala (negócio):**
> "O coração da regra de negócio é o `Consultorio`. Quando o P1 carrega os dados, ele chama o `vincular`, que percorre todas as consultas e liga o grafo: acha o médico e o paciente de cada consulta pelo identificador e conecta os dois lados.
>
> Já as seis buscas são feitas com **streams**. Esta aqui é a das consultas no período: eu filtro pelo código do médico, filtro pelo intervalo de datas, e ordeno por data e horário. É declarativo — leio quase como a pergunta em português."

```java
public List<Consulta> consultasDoMedicoNoPeriodo(int codigo, LocalDate inicio, LocalDate fim)
        throws RegistroNaoEncontradoException {
    buscarMedico(codigo);                                   // valida existência
    return consultas.stream()
            .filter(c -> c.getCodigoMedico() == codigo)
            .filter(c -> !c.getData().isBefore(inicio) && !c.getData().isAfter(fim))
            .sorted(Comparator.comparing(Consulta::getDataHora))
            .collect(Collectors.toList());
}
```

**Fala (passado x futuro):**
> "E aquela distinção de passado e futuro que apareceu na demo? É só comparar o `getDataHora` da consulta com o `now`. Consulta antes de agora é histórico; depois de agora é agendamento."

---

## Bloco 7 — Persistência + exceções (7:30–8:30)

_[Abra `persistencia/RepositorioBinario.java`.]_

**Fala (persistência):**
> "A persistência é bem enxuta justamente por causa do `Serializable`. Salvar é um `writeObject` que grava o `Consultorio` inteiro de uma vez — com médicos, pacientes, consultas e todas as ligações. Carregar é um `readObject` que reconstrói o grafo inteiro. O P1 chama o salvar, o P2 chama o carregar."

_[Abra `modelo/ValidadorCpf.java` e `excecoes/`.]_

**Fala (exceções):**
> "Eu criei **duas exceções próprias**. A `CpfInvalidoException` é lançada pelo `ValidadorCpf`, que confere se o CPF tem 11 dígitos e valida os **dois dígitos verificadores** pelo cálculo de módulo 11. Se um CPF do arquivo for inválido, a carga no P1 é interrompida — dado ruim não entra no sistema.
>
> A segunda é a `RegistroNaoEncontradoException`, lançada quando alguém busca um médico ou paciente que não existe. E tem um ponto de design importante aqui: métodos como o `lerPacientes` e o `carregar` **declaram `throws` e não capturam** a exceção. Eles deixam o erro subir para quem chamou decidir o que fazer — no caso, a interface mostra um diálogo. Eu trato o erro onde dá para reagir a ele, não onde ele acontece."

```java
public static void validar(String cpf) throws CpfInvalidoException {
    if (cpf == null || !cpf.matches("\\d{11}"))
        throw new CpfInvalidoException("CPF deve ter 11 dígitos: " + cpf);
    // ... confere os 2 dígitos verificadores (módulo 11)
}
```

---

## Bloco 8 — UI + gravação de resultados (8:30–9:30)

_[Abra `ui/TelaPrincipal.java` e `ui/PainelMedico.java`.]_

**Fala:**
> "A interface é Swing. A `TelaPrincipal` é uma janela com abas — uma aba por interface, médico e paciente —, que é exatamente o que a especificação pede ao falar em duas interfaces separadas.
>
> Dentro de cada painel, cada botão chama um método que faz sempre quatro passos: lê os campos, chama a busca no `Consultorio`, formata o resultado e então **exibe e grava** ao mesmo tempo. E tem tratamento de entrada: se o usuário digitar um código que não é número, ou uma data fora do formato, ou buscar um médico que não existe, eu capturo a exceção e mostro um aviso em vez de quebrar.
>
> Por último, o `RegistradorResultados` abre o arquivo em modo **append** — cada busca é anexada com seus parâmetros, e ao fechar a janela um `WindowListener` dispara o resumo final. Foi o que vocês viram no `resultados.csv`."

---

## Bloco 9 — Fechamento (9:30–10:00)

**Fala:**
> "Para fechar, recapitulando o que essa aplicação exercita de orientação a objetos:"

_[Mostre o slide final / aponte os itens.]_
- ✅ **Abstração** — `Pessoa` abstrata
- ✅ **Herança** — `Medico`/`Paciente` ← `Pessoa`
- ✅ **Polimorfismo** — `getResumo()` despachado no `Formatador`
- ✅ **Encapsulamento** — campos `private` + getters
- ✅ **Interfaces** — `Identificavel`, `Serializable`
- ✅ **Exceções próprias** — `CpfInvalidoException`, `RegistroNaoEncontradoException`
- ✅ **Coleções + Streams** nas 6 buscas
- ✅ **Persistência** — serialização binária (P1 grava / P2 lê)
- ✅ **Entrada CSV** + **saída em arquivo** (`resultados.csv`)

**Fala (encerramento):**
> "Resumindo: dois programas, seis pesquisas, dados lidos de CSV, resultado gravado em arquivo, e uma arquitetura em camadas que isola modelo, regra de negócio, persistência e interface — de forma que cada parte pode mudar sem afetar as outras. Era isso, obrigado. Fico à disposição para perguntas."

---

## Perguntas prováveis (tenha respostas na ponta da língua)

- **"A especificação não pedia console?"**
  "Sim — eu implementei a interação em GUI Swing, que é um superconjunto do console: mantém as duas interfaces separadas em abas, as seis buscas e a opção de gravar em arquivo. Como a lógica de negócio está toda no `Consultorio`, independente da camada de UI, portar para console seria só reescrever o pacote `ui`, sem tocar na regra de negócio."
- **"Por que dois programas em vez de um?"**
  "Para separar a carga e validação dos dados, que acontece uma vez no P1, da consulta, que é o uso do dia a dia no P2. O binário intermediário garante que o consultor sempre parte de um estado já validado."
- **"Como você garante que o CPF é válido?"**
  "O `ValidadorCpf` confere os 11 dígitos e recalcula os dois dígitos verificadores por módulo 11. Um CPF inválido no CSV barra a carga no P1, então nunca chega a virar objeto."
- **"E se eu buscar um médico ou paciente que não existe?"**
  "A camada de negócio lança `RegistroNaoEncontradoException`, e a interface captura e mostra um diálogo explicando, em vez de quebrar."
- **"Por que usou streams nas buscas?"**
  "Porque deixam a intenção explícita — filtrar, ordenar, coletar — e o código fica próximo do enunciado em português, mais fácil de ler e de justificar."
- **"O que acontece se o arquivo binário não existir quando rodo o P2?"**
  "O P2 trata a `IOException` e avisa que não conseguiu ler o binário — o certo é rodar o P1 antes, que é o que gera o arquivo."
