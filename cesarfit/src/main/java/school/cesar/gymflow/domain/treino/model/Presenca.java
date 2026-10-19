package school.cesar.gymflow.domain.treino.model;

import java.time.LocalDate;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Registro de que o aluno compareceu a academia em um dia (check-in). */
public record Presenca(Long id, Long alunoId, LocalDate data) {

    public Presenca {
        if (alunoId == null) {
            throw new RegraDeNegocioException("Presenca exige um aluno");
        }
        if (data == null || data.isAfter(LocalDate.now())) {
            throw new RegraDeNegocioException("Data da presenca invalida");
        }
    }
}
