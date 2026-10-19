# language: pt
Funcionalidade: Gerenciamento de alunos
  Como recepcionista da academia
  Quero cadastrar, pesquisar, editar e excluir alunos

  Cenario: Pesquisar aluno cadastrado pelo nome
    Dado um aluno chamado "Carla Mendes" com CPF "10120230344"
    Entao a pesquisa por "carla" deve encontrar o aluno

  Cenario: Nao permitir CPF duplicado
    Dado um aluno chamado "Diego Alves" com CPF "20230340455"
    Quando eu tentar cadastrar um aluno com CPF "202.303.404-55"
    Entao devo receber o erro "Ja existe aluno cadastrado com o CPF 202.303.404-55"

  Cenario: Nao permitir cadastro sem telefone
    Quando eu tentar cadastrar um aluno sem telefone
    Entao devo receber o erro "Telefone invalido: informe DDD + numero (10 ou 11 digitos)"

  Cenario: Editar os dados de um aluno
    Dado um aluno chamado "Elisa Rocha" com CPF "30340450566"
    Quando eu alterar o nome do aluno para "Elisa Rocha Lima" e o telefone para "81 3333-4444"
    Entao o aluno deve se chamar "Elisa Rocha Lima" com telefone "(81) 3333-4444"

  Cenario: Excluir aluno sem matricula
    Dado um aluno chamado "Fabio Nunes" com CPF "40450560677"
    Quando eu excluir o aluno
    Entao o aluno nao deve mais aparecer na listagem

  Cenario: Bloquear exclusao de aluno com matricula ativa
    Dado um aluno chamado "Gabi Torres" com CPF "50560670788"
    E que o aluno ja foi matriculado no plano "MENSAL"
    Quando eu tentar excluir o aluno
    Entao devo receber o erro "Nao e possivel excluir Gabi Torres: ele possui matricula ativa (Plano Mensal). Cancele ou exclua a matricula antes."
