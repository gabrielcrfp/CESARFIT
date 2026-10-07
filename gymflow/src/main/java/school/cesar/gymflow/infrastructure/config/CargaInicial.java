package school.cesar.gymflow.infrastructure.config;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.usecase.GerarFichaDeTreinoUseCase;
import school.cesar.gymflow.application.usecase.MatricularAlunoUseCase;
import school.cesar.gymflow.application.usecase.RegistrarPresencaUseCase;
import school.cesar.gymflow.domain.acesso.model.Credencial;
import school.cesar.gymflow.domain.acesso.repositorio.RepositorioDeCredenciais;
import school.cesar.gymflow.domain.acesso.servico.CodificadorDeSenha;
import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.model.Objetivo;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.shared.Cpf;

/**
 * Dados de demonstracao na subida da aplicacao (os planos sao fixos no dominio).
 * Equipe: EQ001 / cesarfit. Ana Lima ja sai matriculada, com ficha e alguns check-ins.
 */
@Component
@Profile("!test")
public class CargaInicial implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CargaInicial.class);

    private final RepositorioDeAlunos repositorioDeAlunos;
    private final RepositorioDeCredenciais repositorioDeCredenciais;
    private final CodificadorDeSenha codificador;
    private final MatricularAlunoUseCase matricular;
    private final GerarFichaDeTreinoUseCase gerarFicha;
    private final RegistrarPresencaUseCase registrarPresenca;

    public CargaInicial(RepositorioDeAlunos repositorioDeAlunos, RepositorioDeCredenciais repositorioDeCredenciais,
                        CodificadorDeSenha codificador, MatricularAlunoUseCase matricular,
                        GerarFichaDeTreinoUseCase gerarFicha, RegistrarPresencaUseCase registrarPresenca) {
        this.repositorioDeAlunos = repositorioDeAlunos;
        this.repositorioDeCredenciais = repositorioDeCredenciais;
        this.codificador = codificador;
        this.matricular = matricular;
        this.gerarFicha = gerarFicha;
        this.registrarPresenca = registrarPresenca;
    }

    @Override
    public void run(String... args) {
        if (repositorioDeCredenciais.daEquipe("EQ001").isEmpty()) {
            repositorioDeCredenciais.salvar(Credencial.daEquipe("EQ001", "Recepcao CesarFit",
                    codificador.codificar("cesarfit")));
        }
        if (repositorioDeAlunos.quantidade() == 0) {
            Aluno ana = repositorioDeAlunos.salvar(new Aluno(null, "Ana Lima", Cpf.de("52998224725"), "81988887777",
                    "ana@email.com", LocalDate.of(1998, 4, 12), Objetivo.HIPERTROFIA));
            repositorioDeAlunos.salvar(new Aluno(null, "Bruno Souza", Cpf.de("11144477735"), "81999990000",
                    "bruno@email.com", LocalDate.of(1990, 9, 3), Objetivo.EMAGRECIMENTO));
            var matricula = matricular.executar(new Comandos.SalvarMatricula(
                    ana.id(), "MENSAL", LocalDate.now().minusDays(10), null));
            gerarFicha.executar(ana.id());
            for (int dias = 3; dias >= 1; dias--) {
                registrarPresenca.registrarEm(ana.id(), LocalDate.now().minusDays(dias));
            }
            LOG.info("Demo: aluna Ana Lima -> e-mail ana@email.com / senha 529982 | equipe -> EQ001 / cesarfit",
                    matricula.id());
        }
    }
}
