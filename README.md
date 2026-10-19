# CesarFit - Sistema de Gestao de Academia

Projeto da disciplina (trabalho em grupo, 6 integrantes) - CESAR School.
Java 17 + Spring Boot 3 + JPA/Hibernate + H2/PostgreSQL + Cucumber.
Modelado com DDD e arquitetura limpa. Interface web em HTML/CSS/JS puro servida pelo Spring.

> O que mudou nesta versao (Catraca removida, planos Semanal/Mensal, CRUD completo de
> alunos e matriculas, dashboard): veja **`ALTERACOES.md`**.

---

## 1. Como executar

### Pre-requisitos
- **JDK 17 ou superior** (`java -version`)
- **Maven 3.9+** (`mvn -v`) — ou use o Maven embutido do VS Code
- VS Code com a extensao **Extension Pack for Java** (Microsoft)

### Rodando no VS Code
1. `File > Open Folder...` e abra a pasta **`cesafit`** (a que contem o `pom.xml`).
2. Aguarde a extensao Java importar o projeto (barra inferior: "Java: Ready").
3. Abra `src/main/java/school/cesar/cesarfit/GymflowApplication.java` e clique em **Run**
   acima do metodo `main` — **ou** no terminal do VS Code:
   ```bash
   mvn spring-boot:run
   ```
4. Acesse **http://localhost:8080** (console do banco H2 em http://localhost:8080/h2,
   JDBC URL `jdbc:h2:mem:gymflow`, usuario `sa`, sem senha).

O banco H2 e em memoria: ao reiniciar, os dados voltam ao estado inicial
(2 alunos de demonstracao: Ana Lima e Bruno Souza).

### Testes automatizados (BDD)
```bash
mvn test        # relatorio em target/cucumber-report.html
```
Cenarios em `src/test/resources/features` (alunos, matriculas, treinos).

### PostgreSQL (opcional)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gymflow
spring.datasource.username=postgres
spring.datasource.password=postgres
```

---

## 2. Funcionalidades e divisao pelos 6 integrantes

Regra do enunciado (por integrante): 2 altas **ou** 1 media + 2 baixas **ou** 4 baixas.
Toda funcionalidade contada grava dados (nenhuma e so leitura). Listagens, filtros, indicadores do
dashboard e a consulta do proximo exercicio **nao** sao contados separadamente.
As classificacoes de complexidade sao propostas e precisam ser validadas pelo professor.

| Integrante | Criterio | Funcionalidade | Complexidade | Onde esta no codigo | Cenarios BDD |
|---|---|---|---|---|---|
| **1 - Alunos e operacao** | 1 media + 2 baixas | Gerenciamento completo de alunos (cadastrar, pesquisar, ver, editar, excluir) | Media | `CadastrarAlunoUseCase`, `AtualizarAlunoUseCase`, `ExcluirAlunoUseCase`, `AlunoController` | `alunos.feature` |
| | | Notificacao automatica de vencimento de matricula (agendada as 8h + botao "Verificar agora", sem envio duplicado) | Baixa | `NotificacaoDeVencimento`, `NotificacaoDeVencimentoUseCase`, `AgendadorDeVencimentos`, `POST /api/notificacoes/verificacao` | `operacao.feature` |
| | | Registro de problemas de equipamentos (aberta -> em andamento -> resolvida) | Baixa | `OcorrenciaDeEquipamento`, `OcorrenciaDeEquipamentoUseCase`, `/api/ocorrencias` | `operacao.feature` |
| **2 - Matriculas** | 2 altas | Gerenciamento completo de matriculas (efetivar, precificar, consultar, editar, excluir) | Alta | `MatricularAlunoUseCase`, `AtualizarMatriculaUseCase`, `ExcluirMatriculaUseCase`, `MatriculaController` | `matriculas.feature` |
| | | Renovacao de matricula e alteracao de plano | Alta | `RenovarMatriculaUseCase`, `AtualizarMatriculaUseCase` | `matriculas.feature` |
| | | *Adicional:* cancelamento com justificativa, data da solicitacao e taxa de 1 mensalidade se faltarem menos de 30 dias para o fim do contrato | - | `CancelarMatriculaUseCase`, `Matricula.cancelar`, `POST /api/matriculas/{id}/cancelamento` | `financeiro.feature` |
| **3 - Financeiro** | 1 media + 2 baixas | Registro e regularizacao de pagamentos | Media | `RegistrarPagamentoUseCase`, `POST /api/matriculas/{id}/pagamentos` | `matriculas.feature` |
| | | Estorno de pagamento (motivo obrigatorio, sem estorno duplicado) | Baixa | `Pagamento.estornar`, `EstornarPagamentoUseCase`, `POST /api/matriculas/{id}/pagamentos/{indice}/estorno` | `financeiro.feature` |
| | | Contestacao de cobranca (aluno abre, equipe analisa; procedente estorna) | Baixa | `ContestacaoDeCobranca`, `ContestacaoDeCobrancaUseCase`, `/api/contestacoes`, `/api/area-aluno/contestacoes` | `financeiro.feature` |
| **4 - Fichas e catalogo** | 2 altas | Geracao automatica de ficha de treino com divisao padrao A/B/C (gerada ja na matricula; usa so exercicios ativos do catalogo) | Alta | `GerarFichaDeTreinoUseCase`, `GeradorDeFicha` e subclasses | `treinos.feature`, `catalogo.feature` |
| | | Gerenciamento do catalogo de exercicios (incluir, editar, desativar, reativar, excluir; regras de duplicidade, uso em fichas e ultimo ativo do grupo) | Alta | `Exercicio`, `CatalogoDeExerciciosUseCase`, `/api/exercicios` | `catalogo.feature` |
| | | *Adicional:* execucao e acompanhamento do treino (feito pelo aluno) | - | `ConcluirItemDeTreinoUseCase`, `ConsultarProximoExercicioUseCase` | `treinos.feature` |
| | | *Adicional:* o aluno monta o proprio treino (escolhe exercicios do catalogo, series e repeticoes; ajusta e remove) | - | `PersonalizarFichaUseCase`, `FichaDeTreino.adicionarExercicio/ajustarItem/removerItem`, `/api/area-aluno/ficha/itens` | `personalizacao.feature` |
| **5 - Acesso e solicitacoes** | 1 media + 2 baixas | Autenticacao e controle de sessao | Media | `EntrarNoSistemaUseCase`, `GerenciadorDeSessoes`, `FiltroDeSessao`, `AcessoController` | `acesso.feature` |
| | | Solicitacao de avaliacao fisica (equipe confirma ou propoe outro horario) | Baixa | `SolicitacaoDeAvaliacao`, `AvaliacaoFisicaUseCase`, `/api/avaliacoes`, `/api/area-aluno/avaliacoes` | `solicitacoes.feature` |
| | | Solicitacao de substituicao de exercicio (aprovar troca o exercicio na ficha) | Baixa | `SolicitacaoDeSubstituicao`, `SubstituicaoDeExercicioUseCase`, `FichaDeTreino.substituirExercicio` | `solicitacoes.feature` |
| | | *Adicional:* alteracao de senha | - | `AlterarSenhaUseCase`, `PUT /api/area-aluno/senha` | `acesso.feature` |
| **6 - Frequencia e treino** | 1 media + 2 baixas | Registro de presenca (check-in) e frequencia | Media | `RegistrarPresencaUseCase`, `Frequencia`, `POST /api/area-aluno/presencas` | `acesso.feature` |
| | | Registro de feedback pos-treino (esforco 1-10 e observacoes) | Baixa | `FeedbackDeTreino`, `FeedbackPosTreinoUseCase`, `POST /api/area-aluno/feedbacks` | `solicitacoes.feature` |
| | | Solicitacao de atendimento do instrutor (aberto -> em atendimento -> concluido) | Baixa | `SolicitacaoDeAtendimento`, `AtendimentoDoInstrutorUseCase`, `/api/atendimentos` | `solicitacoes.feature` |

**Total:** 2 integrantes x 2 altas + 4 integrantes x (1 media + 2 baixas) = **16 funcionalidades contabilizadas**.
Cancelamento, alteracao de senha e execucao do treino ficam como entregas adicionais.

---

## 3. Regras de negocio principais

- Existem **somente dois planos**: **Plano Semanal = R$ 50,00** (vigencia 7 dias) e
  **Plano Mensal = R$ 99,00** (vigencia 1 mes). Nao ha plano anual, trimestral, adicionais nem descontos.
- O valor **nunca vem da tela**: o servidor calcula pelo plano (Strategy) no cadastro **e** na edicao,
  e grava o valor na matricula.
- Um aluno so pode ter **uma matricula ativa** por vez.
- Aluno com matricula ativa **nao pode ser excluido**; com matriculas encerradas (canceladas/vencidas),
  o historico dele e removido junto, sem deixar registros orfaos.
- Validacoes com mensagens claras: CPF (11 digitos, unico), telefone (DDD + numero), e-mail,
  data de nascimento (idade minima 16), aluno e plano obrigatorios na matricula, data de inicio
  obrigatoria, pagamento nao pode ser menor que o valor do plano.

---

## 4. Padroes de projeto

| Padrao | Implementacao | Problema que resolve |
|---|---|---|
| **Strategy** | `politica.PoliticaDePreco` + `PrecoSemanal`, `PrecoMensal`, escolhidos por `SeletorDePolitica` | calcular o valor do plano em um unico lugar, sem `if` espalhado |
| **Observer** | `shared.PublicadorDeEventos` + `NotificadorPorEmail`, `TrilhaDeAuditoria` | reagir a eventos de dominio sem acoplar os casos de uso |
| **Template Method** | `treino.servico.GeradorDeFicha` + `GeradorHipertrofia`, `GeradorEmagrecimento`, `GeradorCondicionamento` | fixar o esqueleto da geracao da ficha variando os passos |
| **Factory Method** (extra) | `FabricaDeGerador.para(objetivo)` | escolher o gerador de ficha pelo objetivo |
| **Iterator** | `FichaDeTreino.iterator()` / `IteradorDePendentes` | percorrer apenas os itens pendentes sem expor a colecao |

---

## 5. Arquitetura

```
presentation  ->  application  ->  domain  <-  infrastructure
  (REST)          (casos de uso)   (regras)     (JPA, observers, beans)
```

```
src/main/java/school/cesar/gymflow
├── domain           # regras de negocio puras (sem framework)
│   ├── shared       # Dinheiro, Cpf, eventos, Observer
│   ├── matricula    # Aluno, Matricula, Modalidade (planos), politica (Strategy), portas
│   └── treino       # ficha (Iterator), geradores (Template Method), portas
├── application      # casos de uso + DTOs
├── infrastructure   # JPA (entities, repos, adapters), observers, beans, carga inicial
└── presentation     # controllers REST + tratador de erros
src/main/resources/static    # interface (index.html, estilo.css, app.js)
src/test/...                 # Cucumber: steps + features
```

### Endpoints

| Metodo | Rota | Descricao |
|---|---|---|
| GET | `/api/painel` | dashboard |
| GET | `/api/planos` | Plano Semanal e Plano Mensal com valores |
| GET/POST | `/api/alunos` (`?busca=`) | listar/pesquisar e cadastrar |
| GET/PUT/DELETE | `/api/alunos/{id}` | detalhar, editar, excluir |
| GET/POST | `/api/matriculas` (`?busca=&plano=&status=&alunoId=`) | listar/filtrar e cadastrar |
| GET/PUT/DELETE | `/api/matriculas/{id}` | detalhar, editar (recalcula valor), excluir |
| POST | `/api/matriculas/{id}/pagamentos` | registrar pagamento |
| POST | `/api/matriculas/{id}/renovacoes` | renovar pelo mesmo plano |
| POST | `/api/matriculas/{id}/cancelamento` | cancelar |
| POST/GET | `/api/treinos/fichas?alunoId=` | gerar/listar ficha |
| POST | `/api/treinos/fichas/{id}/itens/{posicao}/conclusao` | concluir item |
| GET | `/api/treinos/fichas/{id}/proximo` | proximo exercicio |
| GET | `/api/eventos` | trilha de eventos |

---

## 6. Pendencias do grupo antes da entrega

1. Preencher os nomes reais dos integrantes na secao 2.
2. Colar o link do Figma em `docs/prototipos.md`.
3. Revisar o modelo Context Mapper em `docs/gymflow.cml` (abrir no plugin Context Mapper).
4. Anexar as interacoes com IA em `docs/declaracao-de-ia.md`.

## Login e área do aluno

- Tela de login com **número da matrícula** e **senha**.
- **Aluno:** número da matrícula; senha de primeiro acesso = 6 primeiros dígitos do CPF (pode ser trocada na área do aluno). Na demo, a aluna Ana Lima já sai matriculada (o número aparece no log) com senha `111222`.
- **Equipe:** `EQ001` / `cesarfit`.
- Área do aluno: frequência com foguinho (check-in do dia), dados do aluno e da matrícula, treino da semana com conclusão de exercícios e troca de senha.
- A API exige o cabeçalho `X-Sessao` (token devolvido no login): `/api/area-aluno/**` só para ALUNO, o restante de `/api/**` só para EQUIPE.
- Cenários BDD em `src/test/resources/features/acesso.feature`.
