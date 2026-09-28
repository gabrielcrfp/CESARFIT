# Descricao do dominio e linguagem onipresente

## Visao geral

A GymFlow e uma academia de bairro que hoje controla alunos em planilhas. O sistema
substitui esse controle: a **recepcao** cadastra o **aluno**, vende um **plano**,
recebe a **mensalidade** e controla as **matriculas**; o **professor** prescreve a
**ficha de treino**; o aluno registra a **execucao** de cada **item de treino**.

## Termos (usados identicamente na conversa, no codigo e nos testes)

| Termo | Significado no dominio | Onde aparece no codigo |
|---|---|---|
| Aluno | Pessoa fisica vinculada a academia, com CPF unico e um objetivo declarado | `domain.matricula.model.Aluno` |
| Objetivo | Meta do aluno: hipertrofia, emagrecimento ou condicionamento | `Objetivo` |
| Plano | Produto vendido: nome, valor mensal e modalidade | `domain.matricula.plano.PlanoBase` |
| Plano | Somente dois: Plano Semanal (7 dias, R$ 50,00) e Plano Mensal (1 mes, R$ 99,00) | `Modalidade` |
| Matricula | Vinculo vigente entre aluno e plano, com inicio, fim e status | `Matricula` |
| Vigencia | Periodo em que a matricula autoriza o acesso | `Matricula.estaVigenteEm` |
| Mensalidade | Valor do plano contratado, recalculado sempre que o plano muda | `Matricula.valorMensal` |
| Politica de preco | Regra que define o valor de cada plano (Semanal, Mensal) | `politica.PoliticaDePreco` |
| Pagamento | Quitacao de uma mensalidade | `Pagamento` |
| Ficha de treino | Conjunto ordenado de itens prescritos para um periodo | `treino.model.FichaDeTreino` |
| Item de treino | Exercicio com series, repeticoes e descanso | `ItemDeTreino` |
| Item pendente | Item ainda nao concluido pelo aluno | iterador `IteradorDePendentes` |

## Regras de negocio principais

1. Idade minima para matricula: 16 anos.
2. CPF e unico por aluno.
3. Um aluno nao pode ter duas matriculas vigentes ao mesmo tempo.
4. A mensalidade e a **menor** entre as politicas aplicaveis ao aluno.
5. Plano Semanal custa R$ 50,00 e Plano Mensal custa R$ 99,00; ao trocar o plano o valor e recalculado e salvo.
6. Pagamento menor que a mensalidade vigente e recusado.
7. Matricula cancelada nao aceita pagamento nem renovacao.
8. Sem matricula ativa o aluno nao recebe ficha de treino e aluno com matricula ativa nao pode ser excluido.
9. Reentrada em menos de 5 minutos e bloqueada.
10. Ficha de treino so e gerada para aluno com matricula vigente.

## Acesso e frequência

| Termo | Significado |
|---|---|
| Credencial | Senha (sempre codificada) usada para entrar no sistema. O aluno entra com o número da matrícula; a equipe entra com o seu número de acesso (ex.: EQ001). |
| Senha de primeiro acesso | Enquanto o aluno não troca a senha, ela é formada pelos 6 primeiros dígitos do CPF. |
| Perfil | Quem entrou: ALUNO (vê a área do aluno) ou EQUIPE (vê recepção e sala de treino). |
| Presença (check-in) | Registro de que o aluno compareceu em um dia. Um por dia e só com matrícula ativa. |
| Sequência (foguinho) | Quantidade de dias seguidos com presença. Continua acesa até o fim do dia seguinte ao último check-in. |
| Treino da semana | Ficha de treino mais recente do aluno, com exercícios, séries e repetições. |
