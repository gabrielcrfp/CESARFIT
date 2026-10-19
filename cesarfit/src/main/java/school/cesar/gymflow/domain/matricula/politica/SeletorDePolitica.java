package school.cesar.gymflow.domain.matricula.politica;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import school.cesar.gymflow.domain.matricula.model.Modalidade;
import school.cesar.gymflow.domain.shared.Dinheiro;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/**
 * Servico de dominio que escolhe a estrategia de preco do plano.
 * E o UNICO ponto do sistema que define quanto custa cada plano,
 * garantindo que cadastro e edicao de matricula usem sempre o mesmo calculo.
 */
public class SeletorDePolitica {

    private final Map<Modalidade, PoliticaDePreco> politicas = new EnumMap<>(Modalidade.class);

    public SeletorDePolitica(List<PoliticaDePreco> lista) {
        for (PoliticaDePreco politica : lista) {
            politicas.put(politica.modalidade(), politica);
        }
    }

    public PoliticaDePreco escolher(Modalidade modalidade) {
        if (modalidade == null) {
            throw new RegraDeNegocioException("Selecione um plano (Mensal ou Anual)");
        }
        PoliticaDePreco politica = politicas.get(modalidade);
        if (politica == null) {
            throw new RegraDeNegocioException("Nao ha preco definido para o plano " + modalidade);
        }
        return politica;
    }

    public Dinheiro valorDe(Modalidade modalidade) {
        return escolher(modalidade).calcular();
    }
}
