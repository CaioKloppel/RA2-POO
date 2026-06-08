# Médicos e Pacientes — RA2 POO

Trabalho de Programação Orientada a Objetos (RA2).
Aplicação Java com dois programas: P1 lê CSVs e serializa objetos; P2 restaura e expõe 6 pesquisas via GUI Swing.

## Compilar
PowerShell:
    javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)

## Executar
1. P1 (gera o binário a partir dos CSVs em dados/):
   java -cp out app.ProgramaP1

2. P2 (restaura o binário e abre a GUI):
   java -cp out app.ProgramaP2

Resultados das buscas são gravados em resultados.csv.

## Testes
java -cp out teste.TesteValidadorCpf
java -cp out teste.TesteConsultorioLookup
java -cp out teste.TesteConsultorioBuscas
java -cp out teste.TesteLeitorCsv
java -cp out teste.TesteRepositorioBinario

## Estrutura de pacotes
app/          ProgramaP1, ProgramaP2
excecoes/     CpfInvalidoException, RegistroNaoEncontradoException
modelo/       Identificavel, Pessoa (abstract), Medico, Paciente, Consulta, ValidadorCpf
negocio/      Consultorio
persistencia/ LeitorCsv, RepositorioBinario
ui/           TelaPrincipal, PainelMedico, PainelPaciente, Formatador, RegistradorResultados
teste/        Assert, TesteValidadorCpf, TesteConsultorioLookup, TesteConsultorioBuscas, TesteLeitorCsv, TesteRepositorioBinario
