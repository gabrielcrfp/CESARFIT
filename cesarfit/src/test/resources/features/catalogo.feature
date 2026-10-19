# language: pt
Funcionalidade: Gerenciamento do catalogo de exercicios

  Cenario: Incluir um exercicio no catalogo
    Quando eu cadastrar o exercicio "Crucifixo com halteres" do grupo "PEITO" com equipamento "Halteres"
    Entao o exercicio "Crucifixo com halteres" deve estar ativo no catalogo

  Cenario: Nome de exercicio duplicado
    Quando eu tentar cadastrar o exercicio "supino reto" do grupo "PEITO" com equipamento "Barra"
    Entao devo receber o erro "Ja existe um exercicio com o nome Supino reto"

  Cenario: Exercicio desativado nao entra em fichas novas
    Dado um aluno matriculado chamado "Gil Ramos" com CPF "81811111101" e objetivo "HIPERTROFIA"
    E que o exercicio "Supino reto" foi desativado
    Quando eu gerar a ficha de treino do aluno
    Entao a ficha nao deve conter o exercicio "Supino reto"
    E a ficha deve conter o exercicio "Supino inclinado com halteres"

  Cenario: Exercicio usado em ficha nao pode ser excluido
    Dado um aluno matriculado chamado "Ivo Neri" com CPF "81811111102" e objetivo "HIPERTROFIA"
    E que a ficha de treino do aluno foi gerada
    Quando eu tentar excluir o exercicio "Agachamento livre"
    Entao devo receber o erro "Exercicio usado em fichas nao pode ser excluido; desative-o"

  Cenario: Nao desativar o ultimo exercicio ativo de um grupo
    Dado que o exercicio "Elevacao lateral" foi desativado
    E que o exercicio "Elevacao frontal" foi desativado
    E que o exercicio "Desenvolvimento com halteres" foi desativado
    Quando eu tentar desativar o exercicio "Desenvolvimento militar"
    Entao devo receber o erro "Nao e possivel retirar o ultimo exercicio ativo do grupo OMBROS: a geracao de fichas depende dele"
