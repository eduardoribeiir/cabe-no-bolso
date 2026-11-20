package cabenobolso.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cabenobolso.excecao.DadosInvalidosException;

public abstract class Despesa implements Serializable {

    private static final long serialVersionUID = 1L;

    private String descricao;
    private long valor;
    private List<Participante> participantes;

    public Despesa(String descricao, long valor,
                   List<Participante> participantes)
                   throws DadosInvalidosException {

        if (descricao == null || descricao.isBlank()) {
            throw new DadosInvalidosException(
                "A descrição não pode ser vazia.");
        }

        if (valor <= 0) {
            throw new DadosInvalidosException(
                "O valor deve ser maior que zero.");
        }

        if (participantes == null || participantes.isEmpty()) {
            throw new DadosInvalidosException(
                "Selecione pelo menos um participante.");
        }

        List<Participante> copia = new ArrayList<>();
        Set<Integer> codigos = new HashSet<>();
        for (Participante p : participantes) {
            if (p == null) {
                throw new DadosInvalidosException(
                    "Participante inválido na seleção.");
            }
            if (!codigos.add(p.getCodigo())) {
                throw new DadosInvalidosException(
                    "Não é permitido repetir participantes na mesma despesa.");
            }
            copia.add(p);
        }
        copia.sort(Comparator.comparingInt(Participante::getCodigo));

        this.descricao = descricao.trim();
        this.valor = valor;
        this.participantes = copia;
    }

    public Despesa(String descricao, BigDecimal valorReais,
                   List<Participante> participantes)
                   throws DadosInvalidosException {
        this(descricao, converterParaCentavos(valorReais), participantes);
    }

    protected static long converterParaCentavos(BigDecimal valorReais)
            throws DadosInvalidosException {
        if (valorReais == null) {
            throw new DadosInvalidosException(
                "O valor deve ser informado.");
        }
        return valorReais.setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2)
                .longValueExact();
    }

    protected static BigDecimal converterParaReais(long centavos) {
        return BigDecimal.valueOf(centavos, 2);
    }

    public String getDescricao() {
        return descricao;
    }

    public long getValor() {
        return valor;
    }

    public BigDecimal getValorDecimal() {
        return converterParaReais(valor);
    }

    public List<Participante> getParticipantes() {
        return Collections.unmodifiableList(participantes);
    }

    public boolean envolveParticipante(int codigo) {
        for (Participante p : participantes) {
            if (p.getCodigo() == codigo) {
                return true;
            }
        }
        return false;
    }

    public abstract String getTipo();

    public abstract Map<Integer, Long> calcularRateio();

    public Map<Integer, BigDecimal> calcularRateioDecimal() {
        Map<Integer, Long> rateioCentavos = calcularRateio();
        Map<Integer, BigDecimal> rateioReais = new TreeMap<>();
        for (Map.Entry<Integer, Long> entry : rateioCentavos.entrySet()) {
            rateioReais.put(entry.getKey(), converterParaReais(entry.getValue()));
        }
        return rateioReais;
    }

    public long calcularCustoParticipante(int codigo) {
        return calcularRateio().getOrDefault(codigo, 0L);
    }

    public BigDecimal custoDe(int codigo) {
        return converterParaReais(calcularCustoParticipante(codigo));
    }

    public long calcularCustoTotal() {
        long soma = 0L;
        for (Long parcela : calcularRateio().values()) {
            soma += parcela;
        }
        return soma;
    }

    public BigDecimal custoTotal() {
        return converterParaReais(calcularCustoTotal());
    }
}
