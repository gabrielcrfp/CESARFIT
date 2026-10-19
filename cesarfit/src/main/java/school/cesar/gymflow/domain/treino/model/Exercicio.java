package school.cesar.gymflow.domain.treino.model;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.shared.Texto;

/**
 * Raiz de agregado: Exercicio do catalogo da academia.
 * Somente exercicios ativos podem entrar em fichas novas ou substituicoes.
 */
public class Exercicio {

    private Long id;
    private String nome;
    private GrupoMuscular grupoMuscular;
    private String equipamento;
    private boolean ativo;

    public Exercicio(Long id, String nome, GrupoMuscular grupoMuscular, String equipamento, boolean ativo) {
        this.id = id;
        this.nome = Texto.obrigatorio(nome, 3, 60, "Nome do exercicio");
        if (grupoMuscular == null) {
            throw new RegraDeNegocioException("Grupo muscular e obrigatorio");
        }
        this.grupoMuscular = grupoMuscular;
        this.equipamento = Texto.opcional(equipamento, 60, "Equipamento");
        this.ativo = ativo;
    }

    public static Exercicio novo(String nome, GrupoMuscular grupo, String equipamento) {
        return new Exercicio(null, nome, grupo, equipamento, true);
    }

    /** Exercicio ja usado em fichas mantem nome e grupo para nao quebrar o historico. */
    public void alterar(String novoNome, GrupoMuscular novoGrupo, String novoEquipamento, boolean usadoEmFichas) {
        Exercicio validado = new Exercicio(id, novoNome, novoGrupo, novoEquipamento, ativo);
        if (usadoEmFichas && (!validado.nome.equalsIgnoreCase(nome) || validado.grupoMuscular != grupoMuscular)) {
            throw new RegraDeNegocioException(
                    "Exercicio usado em fichas nao pode mudar de nome ou grupo; desative-o e cadastre outro");
        }
        this.nome = validado.nome;
        this.grupoMuscular = validado.grupoMuscular;
        this.equipamento = validado.equipamento;
    }

    public void desativar() {
        if (!ativo) {
            throw new RegraDeNegocioException("Exercicio ja esta desativado");
        }
        ativo = false;
    }

    public void reativar() {
        if (ativo) {
            throw new RegraDeNegocioException("Exercicio ja esta ativo");
        }
        ativo = true;
    }

    public boolean mesmoNome(String outro) {
        return outro != null && nome.equalsIgnoreCase(outro.trim());
    }

    public void atribuirId(Long id) {
        this.id = id;
    }

    public Long id() {
        return id;
    }

    public String nome() {
        return nome;
    }

    public GrupoMuscular grupoMuscular() {
        return grupoMuscular;
    }

    public String equipamento() {
        return equipamento;
    }

    public boolean exigeEquipamento() {
        return equipamento != null;
    }

    public boolean ativo() {
        return ativo;
    }
}
