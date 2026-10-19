# language: pt
Funcionalidade: Divisao A/B/C e treino montado pelo aluno

  Cenario: Aluno matriculado ja recebe a ficha padrao com divisao A, B e C
    Dado um aluno matriculado chamado "Ana Brito" com CPF "81811111128" e objetivo "HIPERTROFIA"
    Entao a ficha atual do aluno deve ter os treinos A, B e C

  Cenario: Aluno inclui um exercicio escolhendo series e repeticoes
    Dado um aluno matriculado chamado "Beto Lago" com CPF "81811111129" e objetivo "HIPERTROFIA"
    Quando o aluno incluir o exercicio "Rosca martelo" no treino "B" com 4 series de 10 repeticoes
    Entao o treino "B" deve conter "Rosca martelo" com 4 series de 10 repeticoes

  Cenario: Nao repetir exercicio no mesmo treino
    Dado um aluno matriculado chamado "Caio Mota" com CPF "81811111130" e objetivo "HIPERTROFIA"
    Quando o aluno tentar incluir o exercicio "Supino reto" no treino "A" com 3 series de 10 repeticoes
    Entao devo receber o erro "Este exercicio ja esta no treino A"

  Cenario: Series acima do limite sao recusadas
    Dado um aluno matriculado chamado "Duda Reis" com CPF "81811111131" e objetivo "HIPERTROFIA"
    Quando o aluno tentar incluir o exercicio "Rosca martelo" no treino "B" com 12 series de 10 repeticoes
    Entao devo receber o erro "Series devem ficar entre 1 e 10"

  Cenario: Aluno ajusta series e repeticoes de um exercicio
    Dado um aluno matriculado chamado "Enzo Paiva" com CPF "81811111132" e objetivo "HIPERTROFIA"
    Quando o aluno ajustar o exercicio "Leg press" para 5 series de 15 repeticoes
    Entao o treino "C" deve conter "Leg press" com 5 series de 15 repeticoes

  Cenario: Cada treino precisa de pelo menos um exercicio
    Dado um aluno matriculado chamado "Fran Lopes" com CPF "81811111133" e objetivo "HIPERTROFIA"
    E o aluno remover o exercicio "Supino reto"
    E o aluno remover o exercicio "Supino inclinado com halteres"
    Quando o aluno tentar remover o exercicio "Triceps na polia"
    Entao devo receber o erro "O treino A precisa de pelo menos um exercicio"
