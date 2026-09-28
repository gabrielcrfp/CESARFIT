package school.cesar.gymflow.bdd;

import org.springframework.stereotype.Component;

import school.cesar.gymflow.application.dto.Respostas;

/** Estado compartilhado entre os steps de um mesmo cenario. */
@Component
public class ContextoDeTeste {

    private Long alunoId;
    private Respostas.MatriculaResumo matricula;
    private Respostas.FichaResumo ficha;
    private RuntimeException erro;

    public void limpar() {
        alunoId = null;
        matricula = null;
        ficha = null;
        erro = null;
    }

    public Long alunoId() {
        return alunoId;
    }

    public void alunoId(Long id) {
        this.alunoId = id;
    }

    public Respostas.MatriculaResumo matricula() {
        return matricula;
    }

    public void matricula(Respostas.MatriculaResumo m) {
        this.matricula = m;
    }

    public Respostas.FichaResumo ficha() {
        return ficha;
    }

    public void ficha(Respostas.FichaResumo f) {
        this.ficha = f;
    }

    public RuntimeException erro() {
        return erro;
    }

    public void erro(RuntimeException e) {
        this.erro = e;
    }
}
