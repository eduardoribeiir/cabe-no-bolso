package cabenobolso.extra;

import cabenobolso.modelo.Despesa;
import cabenobolso.modelo.Participante;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class ResultadoSimulacao {

    private final boolean solucao;
    private final List<Despesa> mantidas;
    private final List<Despesa> retiradas;
    private final List<Participante> participantes;
    private final Map<Integer, BigDecimal> custosOriginais;
    private final Map<Integer, BigDecimal> custosSimulados;
    private final long prioridade;

    ResultadoSimulacao(boolean solucao, List<Despesa> mantidas, List<Despesa> retiradas,
                       List<Participante> participantes, Map<Integer, BigDecimal> custosOriginais,
                       Map<Integer, BigDecimal> custosSimulados, long prioridade) {
        this.solucao = solucao;
        this.mantidas = List.copyOf(mantidas);
        this.retiradas = List.copyOf(retiradas);
        this.participantes = List.copyOf(participantes);
        this.custosOriginais = Map.copyOf(custosOriginais);
        this.custosSimulados = Map.copyOf(custosSimulados);
        this.prioridade = prioridade;
    }

    public boolean temSolucao() {
        return solucao;
    }

    public List<Despesa> getMantidas() {
        return mantidas;
    }

    public List<Despesa> getRetiradas() {
        return retiradas;
    }

    public List<Participante> getParticipantes() {
        return participantes;
    }

    public BigDecimal getCustoOriginal(int codigo) {
        return custosOriginais.get(codigo);
    }

    public BigDecimal getCustoSimulado(int codigo) {
        return custosSimulados.get(codigo);
    }

    public long getPrioridade() {
        return prioridade;
    }

    public BigDecimal getTotalOriginal() {
        return somar(custosOriginais);
    }

    public BigDecimal getTotalSimulado() {
        return somar(custosSimulados);
    }

    private static BigDecimal somar(Map<Integer, BigDecimal> custos) {
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (BigDecimal custo : custos.values()) {
            total = total.add(custo);
        }
        return total;
    }
}
