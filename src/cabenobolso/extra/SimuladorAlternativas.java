package cabenobolso.extra;

import cabenobolso.excecao.EntradaInvalidaException;
import cabenobolso.modelo.Despesa;
import cabenobolso.modelo.Participante;
import cabenobolso.modelo.Planejamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SimuladorAlternativas {

    private SimuladorAlternativas() {
    }

    /** Uma prioridade nula representa uma despesa essencial. */
    public static ResultadoSimulacao simular(Planejamento planejamento, List<Integer> prioridades)
            throws EntradaInvalidaException {
        if (planejamento == null || prioridades == null) {
            throw new EntradaInvalidaException("Informe o planejamento e a classificação das despesas.");
        }
        List<Despesa> despesas = planejamento.listarDespesas();
        List<Participante> participantes = planejamento.listarParticipantes();
        if (prioridades.size() != despesas.size()) {
            throw new EntradaInvalidaException("Classifique todas as despesas antes de simular.");
        }
        List<Integer> opcionais = new ArrayList<>();
        List<Despesa> essenciais = new ArrayList<>();
        for (int i = 0; i < despesas.size(); i++) {
            Integer prioridade = prioridades.get(i);
            if (prioridade == null) {
                essenciais.add(despesas.get(i));
            } else {
                if (prioridade <= 0) {
                    throw new EntradaInvalidaException(
                            "A prioridade de cada despesa opcional deve ser um inteiro positivo.");
                }
                opcionais.add(i);
            }
        }
        if (opcionais.size() > 10) {
            throw new EntradaInvalidaException("A simulação aceita no máximo 10 despesas opcionais.");
        }

        Map<Integer, BigDecimal> original = calcularCustos(participantes, despesas);
        Map<Integer, BigDecimal> custoEssenciais = calcularCustos(participantes, essenciais);
        if (!dentroDosOrcamentos(participantes, custoEssenciais)) {
            List<Despesa> retiradas = new ArrayList<>();
            for (int indice : opcionais) {
                retiradas.add(despesas.get(indice));
            }
            return new ResultadoSimulacao(false, essenciais, retiradas, participantes,
                    original, custoEssenciais, 0);
        }

        long melhorPrioridade = -1;
        int melhorMascara = 0;
        Map<Integer, BigDecimal> melhorCusto = custoEssenciais;
        for (int mascara = 0; mascara < (1 << opcionais.size()); mascara++) {
            List<Despesa> selecionadas = new ArrayList<>(essenciais);
            long somaPrioridades = 0;
            for (int i = 0; i < opcionais.size(); i++) {
                if ((mascara & (1 << i)) != 0) {
                    int indice = opcionais.get(i);
                    selecionadas.add(despesas.get(indice));
                    somaPrioridades += prioridades.get(indice);
                }
            }
            Map<Integer, BigDecimal> custos = calcularCustos(participantes, selecionadas);
            if (somaPrioridades > melhorPrioridade && dentroDosOrcamentos(participantes, custos)) {
                melhorPrioridade = somaPrioridades;
                melhorMascara = mascara;
                melhorCusto = custos;
            }
        }

        List<Despesa> mantidas = new ArrayList<>();
        List<Despesa> retiradas = new ArrayList<>();
        int opcional = 0;
        for (int i = 0; i < despesas.size(); i++) {
            if (prioridades.get(i) == null) {
                mantidas.add(despesas.get(i));
            } else {
                if ((melhorMascara & (1 << opcional)) != 0) {
                    mantidas.add(despesas.get(i));
                } else {
                    retiradas.add(despesas.get(i));
                }
                opcional++;
            }
        }
        return new ResultadoSimulacao(true, mantidas, retiradas, participantes,
                original, melhorCusto, melhorPrioridade);
    }

    private static Map<Integer, BigDecimal> calcularCustos(List<Participante> participantes,
                                                            List<Despesa> despesas) {
        Map<Integer, BigDecimal> custos = new LinkedHashMap<>();
        for (Participante participante : participantes) {
            BigDecimal total = BigDecimal.ZERO.setScale(2);
            for (Despesa despesa : despesas) {
                total = total.add(despesa.custoDe(participante.getCodigo()));
            }
            custos.put(participante.getCodigo(), total);
        }
        return custos;
    }

    private static boolean dentroDosOrcamentos(List<Participante> participantes,
                                                Map<Integer, BigDecimal> custos) {
        for (Participante participante : participantes) {
            if (custos.get(participante.getCodigo()).compareTo(participante.getOrcamento()) > 0) {
                return false;
            }
        }
        return true;
    }
}
