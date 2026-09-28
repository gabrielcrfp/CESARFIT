# language: pt
Funcionalidade: Acesso ao sistema e area do aluno

  Cenario: Aluno entra com o numero da matricula e a senha de primeiro acesso
    Dado um aluno matriculado chamado "Lia Rocha" com CPF "31313131311" e objetivo "HIPERTROFIA"
    Quando o aluno entrar com o numero da matricula e a senha "313131"
    Entao o acesso deve ser liberado com o perfil "ALUNO"

  Cenario: Senha incorreta nao libera o acesso
    Dado um aluno matriculado chamado "Caio Reis" com CPF "42424242422" e objetivo "EMAGRECIMENTO"
    Quando o aluno entrar com o numero da matricula e a senha "000000"
    Entao devo receber o erro "Numero da matricula ou senha invalidos"

  Cenario: Aluno troca a senha e entra com a nova senha
    Dado um aluno matriculado chamado "Nina Paz" com CPF "53535353533" e objetivo "CONDICIONAMENTO"
    Quando o aluno trocar a senha "535353" pela nova senha "treino2026"
    E o aluno entrar com o numero da matricula e a senha "treino2026"
    Entao o acesso deve ser liberado com o perfil "ALUNO"

  Cenario: Membro da equipe entra com o seu numero de acesso
    Dado um membro da equipe com numero "EQ900" e senha "recepcao1"
    Quando alguem entrar com o numero "EQ900" e a senha "recepcao1"
    Entao o acesso deve ser liberado com o perfil "EQUIPE"

  Cenario: Check-in do dia acende o foguinho de frequencia
    Dado um aluno matriculado chamado "Leo Dias" com CPF "64646464644" e objetivo "HIPERTROFIA"
    Quando o aluno fizer o check-in de hoje
    Entao a sequencia de dias seguidos deve ser 1
    E a presenca de hoje deve aparecer na semana

  Cenario: Apenas um check-in por dia
    Dado um aluno matriculado chamado "Bia Luz" com CPF "75757575755" e objetivo "EMAGRECIMENTO"
    E que o aluno ja fez o check-in de hoje
    Quando o aluno tentar fazer o check-in de hoje novamente
    Entao devo receber o erro "Check-in de hoje ja foi registrado"
