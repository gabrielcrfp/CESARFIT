# Mapa da historia do usuario (User Story Map)

## Espinha dorsal (atividades, da esquerda para a direita)

`Conhecer a academia` -> `Contratar plano` -> `Entrar na academia` -> `Treinar` -> `Manter o vinculo`

## Estrutura

| Atividade | Contratar plano | Entrar na academia | Treinar | Manter o vinculo |
|---|---|---|---|---|
| **Tarefas** | Cadastrar aluno; escolher plano; editar/excluir aluno; pagar | Consultar e filtrar matriculas; editar plano e situacao; dashboard | Receber ficha; executar exercicio; marcar conclusao | Renovar; cancelar; pagar mensalidade |
| **Release 1 (MVP)** | Cadastrar aluno; Matricular aluno; Registrar pagamento | Editar/excluir aluno; Editar/excluir matricula; Dashboard | Gerar ficha; Consultar proximo exercicio; Concluir item | Renovar matricula; Cancelar matricula |
| **Release 2** | Contratar adicionais pelo app; pagamento recorrente | Reconhecimento facial | Historico de evolucao de carga | Cobranca automatica de inadimplentes |

## Historias do Release 1

| # | Historia | Complexidade |
|---|---|---|
| H1 | Como recepcionista, quero cadastrar um aluno para iniciar o relacionamento | Baixa |
| H2 | Como recepcionista, quero matricular o aluno no Plano Semanal ou Mensal com o valor calculado automaticamente | Alta |
| H3 | Como financeiro, quero registrar o pagamento da mensalidade para manter a matricula ativa | Media |
| H4 | Como aluno, quero renovar minha matricula para continuar treinando | Baixa |
| H5 | Como aluno, quero cancelar minha matricula quando nao quiser mais treinar | Baixa |
| H6 | Como professor, quero gerar a ficha conforme o objetivo do aluno para prescrever o treino certo | Alta |
| H7 | Como aluno, quero ver o proximo exercicio pendente para seguir a ficha | Baixa |
| H8 | Como aluno, quero marcar um item como concluido para acompanhar meu progresso | Baixa |
| H9 | Como gestor, quero um dashboard com alunos e matriculas ativas para acompanhar a academia | Media |
| H10 | Como gestor, quero registrar a saida do aluno para medir o tempo de permanencia | Baixa |

## Historias da ampliacao (divisao em 16 funcionalidades)

| # | Historia | Complexidade | Integrante |
|---|---|---|---|
| H11 | Como aluno, quero ser avisado quando minha matricula estiver perto de vencer para renovar a tempo | Baixa | 1 |
| H12 | Como funcionario, quero registrar e acompanhar problemas de equipamentos ate a resolucao | Baixa | 1 |
| H13 | Como recepcionista, quero cancelar uma matricula registrando a justificativa e a data do pedido | Adicional | 2 |
| H14 | Como financeiro, quero estornar um pagamento informando o motivo | Baixa | 3 |
| H15 | Como aluno, quero contestar uma cobranca e acompanhar a analise da equipe | Baixa | 3 |
| H16 | Como professor, quero manter o catalogo de exercicios usado na geracao das fichas | Alta | 4 |
| H17 | Como aluno, quero solicitar uma avaliacao fisica e receber a confirmacao ou outro horario | Baixa | 5 |
| H18 | Como aluno, quero pedir a troca de um exercicio da ficha informando o motivo | Baixa | 5 |
| H19 | Como aluno, quero registrar como foi meu treino (esforco e observacoes) | Baixa | 6 |
| H20 | Como aluno, quero chamar o instrutor para me ajudar com o treino | Baixa | 6 |

Atividades da espinha dorsal atendidas: *Manter o vinculo* (H11, H13, H14, H15), *Treinar* (H16-H20)
e *Operar a academia* (H12, nova atividade da equipe).
