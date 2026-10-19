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

## Ampliacao do dominio

| Termo | Significado |
|---|---|
| Catalogo de exercicios | Lista de exercicios da academia. So exercicios **ativos** entram em fichas novas e em substituicoes. |
| Exercicio em uso | Exercicio que ja aparece em alguma ficha: nao muda de nome/grupo e nao pode ser excluido, apenas desativado. |
| Vaga da ficha | Posicao da ficha definida pelo objetivo (grupo, exercicio preferido, series, repeticoes, descanso). |
| Aviso de vencimento | Mensagem enviada quando a matricula ativa vence em ate 3 dias. Um aviso por vencimento. |
| Ocorrencia de equipamento | Problema registrado em um equipamento: Aberta, Em andamento, Resolvida (com solucao). |
| Justificativa de cancelamento | Motivo obrigatorio do cancelamento; a data da solicitacao fica registrada. |
| Estorno | Devolucao de um pagamento com motivo. O valor deixa de contar no total pago; nao ha estorno duplicado. |
| Contestacao de cobranca | Pedido do aluno questionando um pagamento. A equipe registra o parecer; se procedente, o pagamento e estornado. |
| Avaliacao fisica | Pedido de horario do aluno (6h-21h, ate 60 dias). A equipe confirma ou propoe outro horario. |
| Substituicao de exercicio | Pedido para trocar um exercicio da ficha. Aprovada, o item recebe outro exercicio ativo do mesmo grupo. |
| Atendimento do instrutor | Pedido de ajuda vinculado a ficha. Aberto, Em atendimento (instrutor assumiu), Concluido (com orientacao). |
| Feedback pos-treino | Percepcao de esforco (1 a 10) e observacoes do dia. Exige check-in e ao menos um exercicio concluido. |

## Planos, cancelamento e divisao de treino

| Termo | Significado |
|---|---|
| Plano Mensal | R$ 120,00, vigencia de 1 mes. |
| Plano Anual | R$ 1320,00, vigencia de 12 meses (equivale a R$ 110,00 por mes). |
| Taxa de cancelamento | Uma mensalidade (R$ 120,00 no Mensal; R$ 110,00 no Anual), cobrada quando o cancelamento e pedido com menos de 30 dias de antecedencia do fim do contrato. |
| Divisao A/B/C | Toda ficha nasce dividida em tres treinos (A, B e C), gerada automaticamente quando a matricula e efetivada. |
| Montar meu treino | O aluno inclui exercicios ativos do catalogo em A, B ou C, escolhendo series (1-10) e repeticoes (1-60); pode ajustar e remover, mas cada treino mantem ao menos um exercicio. |
| Horario redondo | Avaliacao fisica so em horarios cheios ou meia hora (ex.: 14:00, 14:30). |
