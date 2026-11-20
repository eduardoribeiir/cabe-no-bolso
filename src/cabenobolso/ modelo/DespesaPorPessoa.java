
package cabenobolso.modelo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cabenobolso.excecao.DadosInvalidosException;

public class DespesaPorPessoa extends Despesa {

    private static final long serialVersionUID = 1L;

    public DespesaPorPessoa(String descricao, long valor,
                            List<Participante> participantes)
                            throws DadosInvalidosException {

        super(descricao, valor, participantes);
    }

    @Override
    public Map<Integer, Long> calcularRateio() {

        Map<Integer, Long> resultado = new TreeMap<>();

        for (Participante participante : getParticipantes()) {

            resultado.put(
                participante.getCodigo(),
                getValor()
            );
        }

        return resultado;
    }
}
