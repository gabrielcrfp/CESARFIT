package school.cesar.gymflow.application.usecase;

import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.model.Matricula;
import school.cesar.gymflow.domain.matricula.model.Modalidade;
import school.cesar.gymflow.domain.matricula.model.StatusMatricula;
import school.cesar.gymflow.domain.matricula.politica.SeletorDePolitica;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.Dinheiro;
import school.cesar.gymflow.domain.shared.RegistroDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeFichas;

/**
 * Consultas de leitura usadas pela interface grafica: listagens, pesquisas,
 * filtros, detalhe do aluno e o dashboard. Sempre leem os dados reais do banco.
 */
@Service
public class ServicoDeConsultas {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");

    private final RepositorioDeAlunos repositorioDeAlunos;
    private final RepositorioDeMatriculas repositorioDeMatriculas;
    private final RepositorioDeFichas repositorioDeFichas;
    private final RegistroDeEventos registroDeEventos;
    private final SeletorDePolitica seletorDePolitica;

    public ServicoDeConsultas(RepositorioDeAlunos repositorioDeAlunos,
                              RepositorioDeMatriculas repositorioDeMatriculas,
                              RepositorioDeFichas repositorioDeFichas,
                              RegistroDeEventos registroDeEventos,
                              SeletorDePolitica seletorDePolitica) {
        this.repositorioDeAlunos = repositorioDeAlunos;
        this.repositorioDeMatriculas = repositorioDeMatriculas;
        this.repositorioDeFichas = repositorioDeFichas;
        this.registroDeEventos = registroDeEventos;
        this.seletorDePolitica = seletorDePolitica;
    }

    /** Listagem e pesquisa de alunos por nome, CPF, e-mail ou telefone (Integrante 1). */
    @Transactional(readOnly = true)
    public List<Respostas.AlunoResumo> alunos(String busca) {
        Map<Long, List<Matricula>> porAluno = repositorioDeMatriculas.todas().stream()
                .collect(Collectors.groupingBy(Matricula::alunoId));
        String termo = normalizar(busca);
        String digitos = busca == null ? "" : busca.replaceAll("\\D", "");
        return repositorioDeAlunos.todos().stream()
                .filter(a -> termo.isEmpty()
                        || normalizar(a.nome()).contains(termo)
                        || a.email().contains(termo)
                        || (!digitos.isEmpty() && (a.cpf().numero().contains(digitos)
                                || a.telefone().contains(digitos))))
                .map(a -> Resumos.aluno(a, porAluno.getOrDefault(a.id(), List.of())))
                .toList();
    }

    /** Visualizacao das informacoes individuais de um aluno (Integrante 1). */
    @Transactional(readOnly = true)
    public Respostas.AlunoDetalhe aluno(Long id) {
        Aluno aluno = repositorioDeAlunos.porId(id)
                .orElseThrow(() -> new RegraDeNegocioException("Aluno nao encontrado"));
        List<Matricula> matriculas = repositorioDeMatriculas.porAluno(id);
        return new Respostas.AlunoDetalhe(Resumos.aluno(aluno, matriculas),
                matriculas.stream().map(m -> Resumos.matricula(m, aluno.nome())).toList());
    }

    /** Os dois unicos planos, com o valor vindo da politica de preco. */
    public List<Respostas.PlanoResumo> planos() {
        return Arrays.stream(Modalidade.values())
                .map(m -> {
                    Dinheiro valor = seletorDePolitica.valorDe(m);
                    return new Respostas.PlanoResumo(m.name(), m.nome(), valor.toString(),
                            valor.valor(), m.duracao());
                })
                .toList();
    }

    /** Consulta de matriculas com pesquisa por aluno e filtros de plano e situacao (Integrante 5). */
    @Transactional(readOnly = true)
    public List<Respostas.MatriculaResumo> matriculas(Long alunoId, String busca, String plano, String status) {
        Map<Long, String> nomes = nomesDosAlunos();
        Map<Long, String> cpfs = repositorioDeAlunos.todos().stream()
                .collect(Collectors.toMap(a -> a.id(), a -> a.cpf().numero()));
        String termo = normalizar(busca);
        String digitos = busca == null ? "" : busca.replaceAll("\\D", "");
        List<Matricula> base = alunoId == null
                ? repositorioDeMatriculas.todas()
                : repositorioDeMatriculas.porAluno(alunoId);
        return base.stream()
                .map(m -> Resumos.matricula(m, nomes.getOrDefault(m.alunoId(), "(aluno removido)")))
                .filter(r -> termo.isEmpty() || normalizar(r.alunoNome()).contains(termo)
                        || String.valueOf(r.id()).equals(termo)
                        || (digitos.length() >= 3 && cpfs.getOrDefault(r.alunoId(), "").contains(digitos)))
                .filter(r -> vazio(plano) || r.plano().equalsIgnoreCase(plano))
                .filter(r -> vazio(status) || r.status().equalsIgnoreCase(status))
                .toList();
    }

    @Transactional(readOnly = true)
    public Respostas.MatriculaResumo matricula(Long id) {
        Matricula m = repositorioDeMatriculas.porId(id)
                .orElseThrow(() -> new RegraDeNegocioException("Matricula nao encontrada"));
        String nome = repositorioDeAlunos.porId(m.alunoId()).map(Aluno::nome).orElse("(aluno removido)");
        return Resumos.matricula(m, nome);
    }

    /** Dashboard com dados reais (Integrante 6). */
    @Transactional(readOnly = true)
    public Respostas.Painel painel() {
        Map<Long, String> nomes = nomesDosAlunos();
        List<Respostas.MatriculaResumo> todas = repositorioDeMatriculas.todas().stream()
                .map(m -> Resumos.matricula(m, nomes.getOrDefault(m.alunoId(), "(aluno removido)")))
                .toList();
        List<Respostas.MatriculaResumo> ativas = todas.stream()
                .filter(r -> r.status().equals(StatusMatricula.ATIVA.name())).toList();
        Dinheiro receita = ativas.stream().map(r -> Dinheiro.de(r.valorNumerico()))
                .reduce(Dinheiro.ZERO, Dinheiro::somar);
        long alunosComAtiva = ativas.stream().map(Respostas.MatriculaResumo::alunoId).distinct().count();
        return new Respostas.Painel(
                nomes.size(),
                ativas.size(),
                contar(todas, StatusMatricula.VENCIDA),
                contar(todas, StatusMatricula.CANCELADA),
                ativas.stream().filter(r -> r.plano().equals(Modalidade.MENSAL.name())).count(),
                ativas.stream().filter(r -> r.plano().equals(Modalidade.ANUAL.name())).count(),
                receita.toString(),
                nomes.size() - alunosComAtiva,
                todas.stream().limit(5).toList());
    }

    @Transactional(readOnly = true)
    public List<Respostas.FichaResumo> fichasDoAluno(Long alunoId) {
        return repositorioDeFichas.porAluno(alunoId).stream()
                .map(GerarFichaDeTreinoUseCase::montarResumo)
                .toList();
    }

    public List<Respostas.EventoResumo> eventos() {
        return registroDeEventos.ultimos(30).stream()
                .map(e -> new Respostas.EventoResumo(e.tipo(), e.referencia(), e.detalhe(),
                        e.ocorridoEm().format(HORA)))
                .toList();
    }

    private Map<Long, String> nomesDosAlunos() {
        return repositorioDeAlunos.todos().stream()
                .collect(Collectors.toMap(Aluno::id, Aluno::nome, (a, b) -> a));
    }

    private static long contar(List<Respostas.MatriculaResumo> lista, StatusMatricula status) {
        return lista.stream().filter(r -> r.status().equals(status.name())).count();
    }

    private static boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
