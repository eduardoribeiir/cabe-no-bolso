package cabenobolso.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cabenobolso.excecao.DadosInvalidosException;

public class DespesaFixa extends Despesa {

    private static final long serialVersionUID = 1L;

    public DespesaFixa(String descricao, long valor,
                       List<Participante> participantes)
                       throws DadosInvalidosException {
        super(descricao, valor, participantes);
    }

    public DespesaFixa(String descricao, BigDecimal valorReais,
                       List<Participante> participantes)
                       throws DadosInvalidosException {
        super(descricao, valorReais, participantes);
    }

    @Override
    public String getTipo() {
        return "Fixa";
    }

    @Override
    public Map<Integer, Long> calcularRateio() {

        Map<Integer, Long> resultado = new TreeMap<>();

        List<Participante> selecionados =
            new ArrayList<>(getParticipantes());

        selecionados.sort(
            Comparator.comparingInt(Participante::getCodigo)
        );

        int quantidade = selecionados.size();
        long valorBase = getValor() / quantidade;
        long centavosRestantes = getValor() % quantidade;

        for (int i = 0; i < quantidade; i++) {

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

    @Override
    public long calcularCustoTotal() {
        return getValor();
    }
}
