# language: pt
Funcionalidade: Solicitacoes dos alunos e feedback pos-treino

  Cenario: Equipe propoe outro horario e o aluno aceita a avaliacao fisica
    Dado um aluno matriculado chamado "Kau Lima" com CPF "81811111113" e objetivo "CONDICIONAMENTO"
    Quando o aluno solicitar avaliacao fisica para daqui a 2 dias as 10h
    E a equipe propor um novo horario para daqui a 3 dias as 15h
    E o aluno aceitar a proposta
    Entao a avaliacao deve estar "CONFIRMADA"

  Cenario: Avaliacao fora do horario de funcionamento
    Dado um aluno matriculado chamado "Lia Prado" com CPF "81811111114" e objetivo "CONDICIONAMENTO"
    Quando o aluno tentar solicitar avaliacao fisica para daqui a 2 dias as 23h
    Entao devo receber o erro "Horario fora do funcionamento da academia (6h as 21h)"

  Cenario: Avaliacao so aceita horarios redondos
    Dado um aluno matriculado chamado "Nico Sales" com CPF "81811111127" e objetivo "CONDICIONAMENTO"
    Quando o aluno tentar solicitar avaliacao fisica para daqui a 2 dias as 10:15
    Entao devo receber o erro "Escolha um horario redondo (ex.: 14:00 ou 14:30)"

  Cenario: Apenas uma avaliacao em aberto por aluno
    Dado um aluno matriculado chamado "Mel Costa" com CPF "81811111115" e objetivo "CONDICIONAMENTO"
    E o aluno solicitar avaliacao fisica para daqui a 2 dias as 10h
    Quando o aluno tentar solicitar avaliacao fisica para daqui a 4 dias as 9h
    Entao devo receber o erro "Voce ja tem uma avaliacao fisica em aberto"

  Cenario: Substituicao aprovada troca o exercicio na ficha
    Dado um aluno matriculado chamado "Noa Leal" com CPF "81811111116" e objetivo "HIPERTROFIA"
    E que a ficha de treino do aluno foi gerada
    Quando o aluno pedir a substituicao do exercicio na posicao 0 com o motivo "Sinto dor no ombro ao fazer supino"
    E a equipe aprovar a substituicao pelo exercicio "Flexao de bracos"
    Entao o exercicio na posicao 0 da ficha deve ser "Flexao de bracos"

  Cenario: Substituto precisa ser do mesmo grupo muscular
    Dado um aluno matriculado chamado "Olga Vaz" com CPF "81811111117" e objetivo "HIPERTROFIA"
    E que a ficha de treino do aluno foi gerada
    E o aluno pedir a substituicao do exercicio na posicao 0 com o motivo "Sinto dor no ombro ao fazer supino"
    Quando a equipe tentar aprovar a substituicao pelo exercicio "Leg press"
    Entao devo receber o erro "O substituto deve trabalhar o mesmo grupo muscular (PEITO)"

  Cenario: Substituicao rejeitada com justificativa
    Dado um aluno matriculado chamado "Pia Melo" com CPF "81811111118" e objetivo "HIPERTROFIA"
    E que a ficha de treino do aluno foi gerada
    E o aluno pedir a substituicao do exercicio na posicao 1 com o motivo "Nao gosto deste exercicio"
    Quando a equipe rejeitar a substituicao com a justificativa "Exercicio essencial para o objetivo"
    Entao a substituicao deve estar "REJEITADA"

  Cenario: Instrutor assume e conclui o atendimento
    Dado um aluno matriculado chamado "Rui Sena" com CPF "81811111119" e objetivo "HIPERTROFIA"
    E que a ficha de treino do aluno foi gerada
    Quando o aluno pedir atendimento do instrutor com o motivo "Duvida na execucao do agachamento"
    E o instrutor "Carlos" assumir o atendimento
    E o instrutor concluir o atendimento com a orientacao "Manter a coluna neutra e descer ate 90 graus"
    Entao o atendimento deve estar "CONCLUIDO"

  Cenario: Atendimento precisa ser assumido antes de concluir
    Dado um aluno matriculado chamado "Sol Brito" com CPF "81811111120" e objetivo "HIPERTROFIA"
    E que a ficha de treino do aluno foi gerada
    E o aluno pedir atendimento do instrutor com o motivo "Duvida na execucao do agachamento"
    Quando o instrutor tentar concluir o atendimento com a orientacao "Orientacao sem assumir"
    Entao devo receber o erro "O atendimento precisa ser assumido antes de ser concluido"

  Cenario: Registrar feedback depois do treino
    Dado um aluno matriculado chamado "Tais Moura" com CPF "81811111121" e objetivo "HIPERTROFIA"
    E que a ficha de treino do aluno foi gerada
    E eu concluir o item na posicao 0
    E que o aluno ja fez o check-in de hoje
    Quando o aluno registrar o feedback com esforco 7 e a observacao "Treino puxado mas consegui terminar"
    Entao o aluno deve ter 1 feedback registrado

  Cenario: Feedback exige check-in no dia
    Dado um aluno matriculado chamado "Udo Farias" com CPF "81811111122" e objetivo "HIPERTROFIA"
    E que a ficha de treino do aluno foi gerada
    E eu concluir o item na posicao 0
    Quando o aluno tentar registrar o feedback com esforco 5 e a observacao "Sem check-in"
    Entao devo receber o erro "Faca o check-in de hoje antes de registrar o feedback"

  Cenario: Apenas um feedback por dia
    Dado um aluno matriculado chamado "Vera Gil" com CPF "81811111123" e objetivo "HIPERTROFIA"
    E que a ficha de treino do aluno foi gerada
    E eu concluir o item na posicao 0
    E que o aluno ja fez o check-in de hoje
    E o aluno registrar o feedback com esforco 7 e a observacao "Primeiro registro"
    Quando o aluno tentar registrar o feedback com esforco 8 e a observacao "Segundo registro"
    Entao devo receber o erro "O feedback do treino de hoje ja foi registrado"
