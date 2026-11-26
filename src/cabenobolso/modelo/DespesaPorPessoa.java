package cabenobolso.modelo;

import cabenobolso.excecao.EntradaInvalidaException;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DespesaPorPessoa extends Despesa {

    private static final long serialVersionUID = 1L;

    public DespesaPorPessoa(String descricao, BigDecimal valor,
                            List<Participante> participantes)
            throws EntradaInvalidaException {
        super(descricao, valor, participantes);
    }

    @Override
    public String getTipo() {
        return "Por pessoa";
    }

    @Override
    public Map<Integer, BigDecimal> calcularRateio() {
        Map<Integer, BigDecimal> rateio = new LinkedHashMap<>();
        for (Participante participante : getParticipantes()) {
            rateio.put(participante.getCodigo(), getValor());
        }
        return rateio;
    }
}
