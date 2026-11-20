
package cabenobolso.modelo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Comparator;

import cabenobolso.excecao.DadosInvalidosException;

public class DespesaFixa extends Despesa {

    private static final long serialVersionUID = 1L;

    public DespesaFixa(String descricao, long valor,
                       List<Participante> participantes)
                       throws DadosInvalidosException {

        super(descricao, valor, participantes);
    }

    @Override
    public Map<Integer, Long> calcularRateio() {

        Map<Integer, Long> resultado = new TreeMap<>();

        List<Participante> selecionados =
            new ArrayList<>(getParticipantes());

        selecionados.sort(
            Comparator.comparingInt(Participante::getCodigo)
        );

        long valorBase = getValor() / selecionados.size();
        long centavosRestantes = getValor() % selecionados.size();

        for (int i = 0; i < selecionados.size(); i++) {

            Participante participante = selecionados.get(i);

            long valorIndividual = valorBase;

            if (i < centavosRestantes) {
                valorIndividual++;
            }

            resultado.put(
                participante.getCodigo(),
                valorIndividual
            );
        }

        return resultado;
    }
}

