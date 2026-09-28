# GymFlow - Sistema de Gestao de Academia

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
1. `File > Open Folder...` e abra a pasta **`gymflow`** (a que contem o `pom.xml`).
2. Aguarde a extensao Java importar o projeto (barra inferior: "Java: Ready").
3. Abra `src/main/java/school/cesar/gymflow/GymflowApplication.java` e clique em **Run**
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
Todos os 6 integrantes cumprem **1 media + 2 baixas**.

| Integrante | Complexidade | Funcionalidade | Onde esta no codigo |
|---|---|---|---|
| **1 - Gerenciamento de Alunos** | Media | Cadastro completo de aluno com validacoes (nome, CPF unico, telefone, e-mail, idade minima 16) | `CadastrarAlunoUseCase`, `Aluno`, `POST /api/alunos` |
| | Baixa | Listagem e pesquisa de alunos (nome, CPF, telefone, e-mail) | `ServicoDeConsultas.alunos`, `GET /api/alunos?busca=` |
| | Baixa | Visualizacao das informacoes individuais do aluno (botao **Ver**) | `ServicoDeConsultas.aluno`, `GET /api/alunos/{id}` |
| **2 - Alteracao e Exclusao de Alunos** | Media | Edicao completa dos dados do aluno (botao **Editar**) | `AtualizarAlunoUseCase`, `PUT /api/alunos/{id}` |
| | Baixa | Exclusao de aluno com confirmacao (botao **Excluir**) | `ExcluirAlunoUseCase`, `DELETE /api/alunos/{id}` |
| | Baixa | Tratamento da exclusao quando o aluno possui matricula | `ExcluirAlunoUseCase` (bloqueia se ativa; remove historico se encerrada) |
| **3 - Cadastro de Matriculas** | Media | Cadastro de matricula associando aluno a um plano | `MatricularAlunoUseCase`, `POST /api/matriculas` |
| | Baixa | Selecao entre Plano Semanal e Plano Mensal | `Modalidade`, `GET /api/planos` |
| | Baixa | Calculo automatico do valor pelo plano | `SeletorDePolitica`, `PrecoSemanal`, `PrecoMensal` |
| **4 - Gerenciamento de Matriculas** | Media | Edicao da matricula com recalculo e gravacao do valor | `AtualizarMatriculaUseCase`, `PUT /api/matriculas/{id}` |
| | Baixa | Exclusao de matricula com confirmacao | `ExcluirMatriculaUseCase`, `DELETE /api/matriculas/{id}` |
| | Baixa | Alteracao da situacao (Ativa/Vencida/Cancelada) | `Matricula.alterar`, campo "Situacao" na edicao |
| **5 - Consulta e Controle** | Media | Tela de consulta/listagem das matriculas com dados do aluno | `ServicoDeConsultas.matriculas`, `GET /api/matriculas` |
| | Baixa | Pesquisa/filtro de matriculas (aluno, plano, situacao) | `GET /api/matriculas?busca=&plano=&status=` |
| | Baixa | Identificacao visual do plano e da situacao (etiquetas coloridas) | `app.js` + `estilo.css` (`.etiqueta.SEMANAL/MENSAL/ATIVA/...`) |
| **6 - Dashboard e Integracao** | Media | Dashboard com dados reais do banco | `ServicoDeConsultas.painel`, `GET /api/painel`, aba **Painel** |
| | Baixa | Indicador de quantidade de alunos cadastrados | `Painel.totalDeAlunos` |
| | Baixa | Indicador de quantidade de matriculas ativas | `Painel.matriculasAtivas` |

Funcionalidades que ja existiam e foram mantidas (extras, nao contam na divisao):
geracao de ficha de treino, conclusao de item, proximo exercicio, registro de pagamento,
renovacao de matricula e trilha de eventos.

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
3. Adicionar o modelo Context Mapper (`docs/gymflow.cml`), se exigido.
4. Anexar as interacoes com IA em `docs/declaracao-de-ia.md`.

## Login e área do aluno

- Tela de login com **número da matrícula** e **senha**.
- **Aluno:** número da matrícula; senha de primeiro acesso = 6 primeiros dígitos do CPF (pode ser trocada na área do aluno). Na demo, a aluna Ana Lima já sai matriculada (o número aparece no log) com senha `111222`.
- **Equipe:** `EQ001` / `cesarfit`.
- Área do aluno: frequência com foguinho (check-in do dia), dados do aluno e da matrícula, treino da semana com conclusão de exercícios e troca de senha.
- A API exige o cabeçalho `X-Sessao` (token devolvido no login): `/api/area-aluno/**` só para ALUNO, o restante de `/api/**` só para EQUIPE.
- Cenários BDD em `src/test/resources/features/acesso.feature`.
