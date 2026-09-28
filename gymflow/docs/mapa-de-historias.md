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
