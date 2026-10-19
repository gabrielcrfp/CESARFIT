package school.cesar.gymflow.presentation.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.application.usecase.AlterarSenhaUseCase;
import school.cesar.gymflow.application.usecase.ConcluirItemDeTreinoUseCase;
import school.cesar.gymflow.application.usecase.ConsultarAreaDoAlunoUseCase;
import school.cesar.gymflow.application.usecase.GerenciadorDeSessoes.SessaoAtiva;
import school.cesar.gymflow.application.usecase.RegistrarPresencaUseCase;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Area do aluno logado (adaptador de entrada). O aluno so acessa os proprios dados. */
@RestController
@RequestMapping("/api/area-aluno")
public class AreaDoAlunoController {

    private final ConsultarAreaDoAlunoUseCase consultar;
    private final RegistrarPresencaUseCase registrarPresenca;
    private final ConcluirItemDeTreinoUseCase concluirItem;
    private final AlterarSenhaUseCase alterarSenha;

    public AreaDoAlunoController(ConsultarAreaDoAlunoUseCase consultar, RegistrarPresencaUseCase registrarPresenca,
                                 ConcluirItemDeTreinoUseCase concluirItem, AlterarSenhaUseCase alterarSenha) {
        this.consultar = consultar;
        this.registrarPresenca = registrarPresenca;
        this.concluirItem = concluirItem;
        this.alterarSenha = alterarSenha;
    }

    @GetMapping
    public Respostas.AreaDoAluno minhaArea(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva sessao) {
        return consultar.executar(sessao.alunoId());
    }

    @PostMapping("/presencas")
    public ResponseEntity<Respostas.AreaDoAluno> checkIn(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva sessao) {
        registrarPresenca.executar(sessao.alunoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(consultar.executar(sessao.alunoId()));
    }

    @PostMapping("/fichas/{fichaId}/itens/{posicao}/conclusao")
    public Respostas.AreaDoAluno concluir(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva sessao,
                                          @PathVariable Long fichaId, @PathVariable int posicao) {
        if (!consultar.fichaPertenceAoAluno(fichaId, sessao.alunoId())) {
            throw new RegraDeNegocioException("Ficha nao pertence ao aluno");
        }
        concluirItem.executar(new Comandos.ConcluirItem(fichaId, posicao));
        return consultar.executar(sessao.alunoId());
    }

    @PutMapping("/senha")
    public ResponseEntity<Void> trocarSenha(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva sessao,
                                            @RequestBody Comandos.AlterarSenha comando) {
        alterarSenha.executar(sessao.alunoId(), comando);
        return ResponseEntity.noContent().build();
    }
}
