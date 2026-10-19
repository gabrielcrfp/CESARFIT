# language: pt
Funcionalidade: Avisos de vencimento e problemas de equipamentos

  Cenario: Aviso de vencimento e enviado uma unica vez
    Dado um aluno chamado "Iara Cruz" com CPF "81811111111"
    E que o aluno tem uma matricula "MENSAL" iniciada ha 29 dias
    Quando o sistema verificar os vencimentos
    E o sistema verificar os vencimentos
    Entao o aluno deve ter 1 aviso de vencimento

  Cenario: Matricula longe do vencimento nao recebe aviso
    Dado um aluno chamado "Joel Reis" com CPF "81811111112"
    E que o aluno ja foi matriculado no plano "MENSAL"
    Quando o sistema verificar os vencimentos
    Entao o aluno deve ter 0 aviso de vencimento

  Cenario: Ocorrencia percorre aberta, em andamento e resolvida
    Quando eu registrar uma ocorrencia para o equipamento "Bicicleta 7" com a descricao "Pedal solto do lado esquerdo"
    E eu colocar a ocorrencia em andamento
    E eu resolver a ocorrencia com a solucao "Pedal reapertado e testado"
    Entao a ocorrencia deve estar "RESOLVIDA"

  Cenario: Ocorrencia nao pode ser resolvida sem estar em andamento
    Quando eu registrar uma ocorrencia para o equipamento "Esteira 9" com a descricao "Painel nao liga ao apertar start"
    E eu tentar resolver a ocorrencia com a solucao "Troca do painel"
    Entao devo receber o erro "A ocorrencia precisa estar em andamento para ser resolvida"

  Cenario: Nao duplicar ocorrencia pendente do mesmo equipamento
    Quando eu registrar uma ocorrencia para o equipamento "Remo 2" com a descricao "Banco travando no trilho"
    E eu tentar registrar uma ocorrencia para o equipamento "remo 2" com a descricao "Mesmo problema relatado de novo"
    Entao devo receber o erro "Ja existe uma ocorrencia pendente para o equipamento remo 2"
