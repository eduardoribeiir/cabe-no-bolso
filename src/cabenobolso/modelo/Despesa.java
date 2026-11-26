package cabenobolso.modelo;

import cabenobolso.excecao.EntradaInvalidaException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Despesa implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String descricao;
    private final BigDecimal valor;
    private final List<Participante> participantes;

    protected Despesa(String descricao, BigDecimal valor,
                      List<Participante> participantes)
            throws EntradaInvalidaException {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new EntradaInvalidaException("A descrição não pode ser vazia.");
        }
        BigDecimal normalizado = Participante.normalizarValor(valor, "Valor");
        if (normalizado.signum() <= 0) {
            throw new EntradaInvalidaException("O valor deve ser maior que zero.");
        }
        if (participantes == null || participantes.isEmpty()) {
            throw new EntradaInvalidaException("Selecione ao menos um participante para a despesa.");
        }

        Set<Integer> codigos = new HashSet<>();
        List<Participante> copia = new ArrayList<>();
        for (Participante participante : participantes) {
            if (participante == null) {
                throw new EntradaInvalidaException("Há um participante inválido na seleção.");
            }
            if (!codigos.add(participante.getCodigo())) {
                throw new EntradaInvalidaException("Há participantes repetidos na seleção.");
            }
            copia.add(participante);
        }
        copia.sort(Comparator.comparingInt(Participante::getCodigo));

        this.descricao = descricao.trim();
        this.valor = normalizado;
        this.participantes = Collections.unmodifiableList(copia);
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public List<Participante> getParticipantes() {
        return participantes;
    }

    public abstract String getTipo();

    public abstract Map<Integer, BigDecimal> calcularRateio();

    public BigDecimal custoDe(int codigo) {
        return calcularRateio().getOrDefault(codigo, BigDecimal.ZERO.setScale(2));
    }

    public BigDecimal custoTotal() {
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (BigDecimal parcela : calcularRateio().values()) {
            total = total.add(parcela);
        }
        return total;
    }
}
