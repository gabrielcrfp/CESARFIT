# language: pt
Funcionalidade: Matriculas com planos Mensal e Anual
  Somente dois planos existem: Mensal (R$ 120,00) e Anual (R$ 1320,00)

  Cenario: Matricular no plano Mensal
    Dado um aluno chamado "Hugo Paiva" com CPF "60670780899"
    Quando eu matricular o aluno no plano "MENSAL"
    Entao o valor da matricula deve ser "R$ 120,00"
    E o dashboard deve contar a matricula como ativa

  Cenario: Matricular no plano Anual
    Dado um aluno chamado "Iris Campos" com CPF "70780890900"
    Quando eu matricular o aluno no plano "ANUAL"
    Entao o valor da matricula deve ser "R$ 1320,00"
    E o filtro por plano "ANUAL" deve encontrar a matricula

  Cenario: Trocar de Mensal para Anual recalcula e salva o valor
    Dado um aluno chamado "Joao Brito" com CPF "80890901011"
    E que o aluno ja foi matriculado no plano "MENSAL"
    Quando eu alterar o plano da matricula para "ANUAL"
    Entao o valor da matricula deve ser "R$ 1320,00"
    E o valor salvo no banco deve ser "R$ 1320,00"

  Cenario: Trocar de Anual para Mensal recalcula e salva o valor
    Dado um aluno chamado "Karen Dias" com CPF "90901011122"
    E que o aluno ja foi matriculado no plano "ANUAL"
    Quando eu alterar o plano da matricula para "MENSAL"
    Entao o valor salvo no banco deve ser "R$ 120,00"

  Cenario: Plano anual nao existe mais
    Dado um aluno chamado "Lucas Reis" com CPF "01011121233"
    Quando eu tentar matricular o aluno no plano "SEMANAL"
    Entao devo receber o erro "Plano invalido: SEMANAL. Os planos disponiveis sao Mensal e Anual"

  Cenario: Nao permitir duas matriculas ativas
    Dado um aluno chamado "Mara Luz" com CPF "12123234345"
    E que o aluno ja foi matriculado no plano "MENSAL"
    Quando eu tentar matricular o aluno no plano "ANUAL"
    Entao devo receber o erro "Mara Luz ja possui matricula ativa. Edite a matricula existente para trocar de plano."

  Cenario: Alterar a situacao da matricula
    Dado um aluno chamado "Nina Prado" com CPF "23234345456"
    E que o aluno ja foi matriculado no plano "MENSAL"
    Quando eu alterar a situacao da matricula para "VENCIDA"
    Entao o status da matricula deve ser "VENCIDA"

  Cenario: Excluir matricula
    Dado um aluno chamado "Otto Lins" com CPF "34345456567"
    E que o aluno ja foi matriculado no plano "ANUAL"
    Quando eu excluir a matricula
    Entao a matricula nao deve mais aparecer na listagem

  Cenario: Pagamento abaixo do valor do plano e recusado
    Dado um aluno chamado "Paula Sa" com CPF "45456567678"
    E que o aluno ja foi matriculado no plano "MENSAL"
    Quando eu registrar um pagamento de "50.00" via "PIX"
    Entao devo receber o erro "Pagamento inferior ao valor do plano: R$ 120,00"
