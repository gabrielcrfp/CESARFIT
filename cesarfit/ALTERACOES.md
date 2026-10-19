# Alteracoes desta versao

## Removido
- **Catraca por completo**: aba e secao da tela, JS (`desenharCatraca`, botoes de entrada/saida),
  CSS do painel, `AcessoController` (`/api/acessos/*`), casos de uso `RegistrarCheckInUseCase`,
  `EncerrarCheckInUseCase`, `ConsultaDeVigenciaPorMatricula`, pacote `domain/acesso`
  (`Catraca`, `CatracaProxy`, `CatracaReal`, `CheckIn`, `ResultadoDeAcesso`...), entidade/repositorios
  de CheckIn, eventos `CHECKIN_REGISTRADO`/`ACESSO_NEGADO` e o step BDD `PassosDeAcesso`.
- **Planos antigos**: Smart/Plus/Black, modalidades TRIMESTRAL e **ANUAL**, tabela `plano`
  (`PlanoEntity`, `PlanoJpaRepository`, `PlanoRepositorioJpa`, `RepositorioDePlanos`).
- **Calculos antigos**: `DescontoFidelidade` (anual 20%), `DescontoEstudante` (15%), `PrecoPadrao`,
  adicionais (Personal, Avaliacao fisica, Piscina) e os decoradores `PlanoDecorator`, `ComPersonal`,
  `ComAvaliacaoFisica`, `ComPiscina`, `FabricaDePlano`, `Adicional`. Campo "estudante" do aluno.

## Corrigido
- **Recalculo do valor ao trocar o plano**: a edicao de matricula agora recalcula pelo plano
  (Semanal R$ 50,00 / Mensal R$ 99,00) e **grava** o novo valor no banco.
- A matricula guarda o plano (`modalidade`) em vez de um texto solto, entao o valor e a vigencia
  ficam sempre coerentes com o plano.
- `DELETE /api/matriculas/{id}` antes so cancelava; agora exclui. Cancelar virou
  `POST /api/matriculas/{id}/cancelamento`.
- Valores exibidos no padrao brasileiro (`R$ 99,00`) e aceitos com virgula.
- Erros de dados invalidos (data, numero, opcao) retornam mensagem clara em vez de erro generico.
- Tela nao precisa mais de F5: toda acao recarrega alunos, matriculas, painel e seletores.
- Nomes escapados no HTML (evita quebra de layout com caracteres especiais).

## Adicionado
- Alunos: campo **telefone**, **editar**, **excluir com confirmacao**, **pesquisa**, **ver detalhes**.
- Matriculas: **listagem geral** com nome do aluno, **filtros** (aluno, plano, situacao),
  **editar** (aluno, plano, data de inicio, situacao), **excluir com confirmacao**,
  valor calculado na tela ao escolher o plano, etiquetas coloridas de plano e situacao.
- **Dashboard** (aba Painel): alunos cadastrados, matriculas ativas, ativas por plano,
  receita das ativas, vencidas, canceladas, alunos sem matricula ativa e ultimas matriculas.
- Validacoes: telefone, e-mail, CPF duplicado tambem na edicao, uma matricula ativa por aluno,
  data de inicio obrigatoria, plano inexistente rejeitado.
- Cenarios BDD novos: `alunos.feature`, `matriculas.feature`, `treinos.feature`.

## Arquivos modificados / criados
- Dominio: `Aluno`, `Matricula`, `Modalidade`, `StatusMatricula`, `Dinheiro`, `EventoDeDominio`,
  `RepositorioDeAlunos`, `RepositorioDeMatriculas`, `RepositorioDeFichas`,
  `politica/PoliticaDePreco`, `SeletorDePolitica`, **novos** `PrecoSemanal`, `PrecoMensal`.
- Aplicacao: `Comandos`, `Respostas`, `CadastrarAlunoUseCase`, `MatricularAlunoUseCase`,
  `RenovarMatriculaUseCase`, `CancelarMatriculaUseCase`, `RegistrarPagamentoUseCase`,
  `ServicoDeConsultas`; **novos** `AtualizarAlunoUseCase`, `ExcluirAlunoUseCase`,
  `AtualizarMatriculaUseCase`, `ExcluirMatriculaUseCase`, `Resumos`.
- Infraestrutura: `AlunoEntity`, `MatriculaEntity`, `AlunoRepositorioJpa`, `MatriculaRepositorioJpa`,
  `FichaRepositorioJpa`, `AlunoJpaRepository`, `MatriculaJpaRepository`, `BeansDeDominio`, `CargaInicial`.
- Apresentacao: `AlunoController`, `MatriculaController`, `PainelController`, `TratadorDeErros`.
- Interface: `index.html`, `app.js`, `estilo.css`.
- Testes: `ContextoDeTeste`, `PassosComuns`, `PassosDeMatricula`, `PassosDeTreino`, **novo** `PassosDeAluno`, features.
- Docs: `README.md`, `docs/linguagem-onipresente.md`, `docs/mapa-de-historias.md`, `docs/prototipos.md`.

## Roteiro de teste manual (http://localhost:8080)
1. **Painel**: confira 2 alunos e 0 matriculas ativas.
2. **Alunos > cadastrar**: deixe o telefone vazio → mensagem de erro. Preencha certo → aparece na tabela.
3. **Alunos > pesquisar**: digite parte do nome ou do CPF → a tabela filtra.
4. **Alunos > Ver**: abre a janela com os dados e as matriculas do aluno.
5. **Alunos > Editar**: mude nome/telefone → "Salvar alteracoes" → tabela atualiza na hora.
6. **Matriculas > nova**: escolha aluno e **Plano Mensal** → valor mostra R$ 99,00 → Matricular.
   Painel passa a mostrar 1 ativa.
7. **Matriculas > Editar**: troque para **Plano Semanal** → salvar → tabela mostra **R$ 50,00**
   (recarregue a pagina: continua R$ 50,00, pois foi salvo). Troque de volta → R$ 99,00.
8. **Matriculas > Editar > Situacao = Cancelada** → etiqueta vermelha; Painel atualiza.
9. **Matriculas > filtros**: filtre por plano e por situacao; pesquise pelo nome do aluno.
10. **Alunos > Excluir** um aluno com matricula ativa → bloqueado com mensagem.
    Exclua/cancele a matricula e tente de novo → confirmacao → aluno removido.
11. **Matriculas > Excluir** → confirmacao "Tem certeza que deseja excluir esta matricula?" → some da lista
    e o Painel atualiza.
12. **Treinos**: com matricula ativa, gere a ficha e conclua um exercicio.
13. **Eventos**: veja a trilha de todas as acoes acima.

## Ampliacao para 16 funcionalidades
- Novas: aviso de vencimento, ocorrencias de equipamentos, estorno, contestacao de cobranca,
  catalogo de exercicios (integrado ao gerador), avaliacao fisica, substituicao de exercicio,
  feedback pos-treino e atendimento do instrutor.
- Cancelamento agora exige justificativa e grava a data da solicitacao; a edicao nao cancela mais.
- Novos cenarios: `catalogo.feature`, `financeiro.feature`, `operacao.feature`, `solicitacoes.feature`.
- Modelo Context Mapper em `docs/gymflow.cml`.

## Ajustes pedidos (planos, divisao A/B/C e treino do aluno)
- Planos agora sao Mensal (R$ 120,00) e Anual (R$ 1320,00); cancelamento com menos de 30 dias do fim cobra uma mensalidade.
- Ficha padrao com divisao A/B/C gerada automaticamente na matricula; aluno monta o proprio treino.
- Equipe nao conclui mais exercicios; abas "Alunos" e "Matriculas" juntas (funcionalidades mantidas).
- Busca de matriculas tambem por CPF; catalogo com mais exercicios; avaliacao fisica so em horarios redondos.
- Novo arquivo de cenarios: `personalizacao.feature`.
