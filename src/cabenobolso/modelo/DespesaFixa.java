package cabenobolso.modelo;

import cabenobolso.excecao.EntradaInvalidaException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DespesaFixa extends Despesa {

    private static final long serialVersionUID = 1L;

    public DespesaFixa(String descricao, BigDecimal valor,
                       List<Participante> participantes)
            throws EntradaInvalidaException {
        super(descricao, valor, participantes);
    }

    @Override
    public String getTipo() {
        return "Fixa";
    }

    @Override
    public Map<Integer, BigDecimal> calcularRateio() {
        BigInteger centavos = getValor().movePointRight(2).toBigIntegerExact();
        BigInteger[] divisao = centavos.divideAndRemainder(
                BigInteger.valueOf(getParticipantes().size()));
        int centavosRestantes = divisao[1].intValueExact();
        Map<Integer, BigDecimal> rateio = new LinkedHashMap<>();

        for (int indice = 0; indice < getParticipantes().size(); indice++) {
            BigInteger parcela = divisao[0];
            if (indice < centavosRestantes) {
                parcela = parcela.add(BigInteger.ONE);
            }
            int codigo = getParticipantes().get(indice).getCodigo();
            rateio.put(codigo, new BigDecimal(parcela, 2));
        }
        return rateio;
    }
}
