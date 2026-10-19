# language: pt
Funcionalidade: Cancelamento, estorno e contestacao de cobranca

  Cenario: Cancelar matricula com justificativa registra a data da solicitacao
    Dado um aluno chamado "Ari Matos" com CPF "81811111103"
    E que o aluno ja foi matriculado no plano "MENSAL"
    Quando eu cancelar a matricula com a justificativa "Mudanca de cidade por trabalho"
    Entao o status da matricula deve ser "CANCELADA"
    E a justificativa e a data da solicitacao devem estar registradas

  Cenario: Cancelamento com 30 dias ou mais de antecedencia nao tem taxa
    Dado um aluno chamado "Iuri Bento" com CPF "81811111124"
    E que o aluno ja foi matriculado no plano "ANUAL"
    Quando eu cancelar a matricula com a justificativa "Vou treinar em casa a partir de agora"
    Entao nao deve haver taxa de cancelamento

  Cenario: Cancelamento com menos de 30 dias do fim do contrato cobra uma mensalidade
    Dado um aluno chamado "Jade Rocha" com CPF "81811111125"
    E que o aluno tem uma matricula "MENSAL" iniciada ha 10 dias
    Quando eu cancelar a matricula com a justificativa "Mudanca de cidade por trabalho"
    Entao a taxa de cancelamento deve ser "R$ 120,00"

  Cenario: Taxa do plano anual equivale a uma mensalidade de R$ 110,00
    Dado um aluno chamado "Kiko Alves" com CPF "81811111126"
    E que o aluno tem uma matricula "ANUAL" iniciada ha 350 dias
    Quando eu cancelar a matricula com a justificativa "Contrato terminando e nao vou renovar"
    Entao a taxa de cancelamento deve ser "R$ 110,00"

  Cenario: Cancelamento sem justificativa e recusado
    Dado um aluno chamado "Bia Torres" com CPF "81811111104"
    E que o aluno ja foi matriculado no plano "ANUAL"
    Quando eu tentar cancelar a matricula com a justificativa ""
    Entao devo receber o erro "Justificativa do cancelamento deve ter pelo menos 10 caracteres"

  Cenario: Edicao nao cancela matricula sem justificativa
    Dado um aluno chamado "Ciro Paz" com CPF "81811111105"
    E que o aluno ja foi matriculado no plano "MENSAL"
    Quando eu tentar alterar a situacao da matricula para "CANCELADA"
    Entao devo receber o erro "Para cancelar, use o cancelamento com justificativa"

  Cenario: Estornar um pagamento
    Dado um aluno chamado "Dora Lins" com CPF "81811111106"
    E que o aluno ja foi matriculado no plano "MENSAL"
    E eu registrar um pagamento de "120,00" via "PIX"
    Quando eu estornar o pagamento 0 com o motivo "Cobranca em duplicidade no cartao"
    Entao o pagamento 0 deve estar estornado
    E o total pago da matricula deve ser zero

  Cenario: Estorno duplicado e impedido
    Dado um aluno chamado "Ed Nunes" com CPF "81811111107"
    E que o aluno ja foi matriculado no plano "MENSAL"
    E eu registrar um pagamento de "120,00" via "PIX"
    E eu estornar o pagamento 0 com o motivo "Cobranca em duplicidade no cartao"
    Quando eu tentar estornar o pagamento 0 com o motivo "Segunda tentativa de estorno"
    Entao devo receber o erro "Pagamento ja foi estornado"

  Cenario: Contestacao procedente estorna o pagamento
    Dado um aluno chamado "Fabi Rios" com CPF "81811111108"
    E que o aluno ja foi matriculado no plano "MENSAL"
    E eu registrar um pagamento de "120,00" via "CARTAO"
    Quando o aluno contestar o pagamento 0 com o motivo "Fui cobrado duas vezes neste mes"
    E a equipe analisar a contestacao como "procedente" com o parecer "Cobranca duplicada confirmada no extrato"
    Entao a contestacao deve estar "PROCEDENTE"
    E o pagamento 0 deve estar estornado

  Cenario: Contestacao improcedente mantem o pagamento
    Dado um aluno chamado "Guto Sa" com CPF "81811111109"
    E que o aluno ja foi matriculado no plano "MENSAL"
    E eu registrar um pagamento de "120,00" via "PIX"
    Quando o aluno contestar o pagamento 0 com o motivo "Nao reconheco esta cobranca"
    E a equipe analisar a contestacao como "improcedente" com o parecer "Pagamento confere com o plano contratado"
    Entao a contestacao deve estar "IMPROCEDENTE"
    E o pagamento 0 nao deve estar estornado

  Cenario: Apenas uma contestacao em analise por pagamento
    Dado um aluno chamado "Hel Dias" com CPF "81811111110"
    E que o aluno ja foi matriculado no plano "MENSAL"
    E eu registrar um pagamento de "120,00" via "PIX"
    E o aluno contestar o pagamento 0 com o motivo "Fui cobrado duas vezes neste mes"
    Quando o aluno tentar contestar o pagamento 0 com o motivo "Insistindo na mesma cobranca"
    Entao devo receber o erro "Ja existe uma contestacao em analise para este pagamento"
