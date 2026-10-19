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
@org.springframework.core.annotation.Order(10)
public class CargaInicial implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CargaInicial.class);

    private final RepositorioDeAlunos repositorioDeAlunos;
    private final RepositorioDeCredenciais repositorioDeCredenciais;
    private final CodificadorDeSenha codificador;
    private final MatricularAlunoUseCase matricular;
    private final GerarFichaDeTreinoUseCase gerarFicha;
    private final RegistrarPresencaUseCase registrarPresenca;
    private final school.cesar.gymflow.application.usecase.RegistrarPagamentoUseCase registrarPagamento;
    private final school.cesar.gymflow.application.usecase.NotificacaoDeVencimentoUseCase notificacoes;
    private final school.cesar.gymflow.application.usecase.OcorrenciaDeEquipamentoUseCase ocorrencias;

    public CargaInicial(RepositorioDeAlunos repositorioDeAlunos, RepositorioDeCredenciais repositorioDeCredenciais,
                        CodificadorDeSenha codificador, MatricularAlunoUseCase matricular,
                        GerarFichaDeTreinoUseCase gerarFicha, RegistrarPresencaUseCase registrarPresenca,
                        school.cesar.gymflow.application.usecase.RegistrarPagamentoUseCase registrarPagamento,
                        school.cesar.gymflow.application.usecase.NotificacaoDeVencimentoUseCase notificacoes,
                        school.cesar.gymflow.application.usecase.OcorrenciaDeEquipamentoUseCase ocorrencias) {
        this.registrarPagamento = registrarPagamento;
        this.notificacoes = notificacoes;
        this.ocorrencias = ocorrencias;
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
            Aluno ana = repositorioDeAlunos.salvar(new Aluno(null, "Ana Lima", Cpf.de("11122233344"), "81988887777",
                    "ana@email.com", LocalDate.of(1998, 4, 12), Objetivo.HIPERTROFIA));
            Aluno bruno = repositorioDeAlunos.salvar(new Aluno(null, "Bruno Souza", Cpf.de("55566677788"), "81999990000",
                    "bruno@email.com", LocalDate.of(1990, 9, 3), Objetivo.EMAGRECIMENTO));
            var matricula = matricular.executar(new Comandos.SalvarMatricula(
                    ana.id(), "MENSAL", LocalDate.now().minusDays(10), null));
            registrarPagamento.executar(new Comandos.RegistrarPagamento(matricula.id(), "120,00", "PIX"));
            matricular.executar(new Comandos.SalvarMatricula(bruno.id(), "MENSAL", LocalDate.now().minusMonths(1).plusDays(2), null));
            notificacoes.verificar(LocalDate.now());
            ocorrencias.registrar(new Comandos.RegistrarOcorrencia("Esteira 3",
                    "Correia fazendo barulho e travando em velocidade alta"));
            for (int dias = 3; dias >= 1; dias--) {
                registrarPresenca.registrarEm(ana.id(), LocalDate.now().minusDays(dias));
            }
            LOG.info("Demo: aluna Ana Lima -> matricula {} / senha 111222 | equipe -> EQ001 / cesarfit",
                    matricula.id());
        }
    }
}
