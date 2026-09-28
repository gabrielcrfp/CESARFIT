# language: pt
Funcionalidade: Ficha de treino

  Cenario: Gerar ficha para aluno matriculado e concluir um exercicio
    Dado um aluno matriculado chamado "Rafa Melo" com CPF "56567678789" e objetivo "HIPERTROFIA"
    E que a ficha de treino do aluno foi gerada
    Quando eu concluir o item na posicao 0
    Entao a ficha deve ter 4 itens pendentes
    E o proximo exercicio pendente deve ser "Remada curvada"

  Cenario: Aluno sem matricula nao recebe ficha
    Dado um aluno sem matricula chamado "Sara Vaz" com CPF "67678789890"
    Quando eu tentar gerar a ficha de treino do aluno
    Entao devo receber o erro de treino "Aluno sem matricula vigente nao pode receber ficha"
