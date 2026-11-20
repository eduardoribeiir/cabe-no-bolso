
package cabenobolso.modelo;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

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

        this.descricao = descricao.trim();
        this.valor = valor;
        this.participantes = new ArrayList<>(participantes);
    }

    public String getDescricao() {
        return descricao;
    }

    public long getValor() {
        return valor;
    }

    public List<Participante> getParticipantes() {
        return Collections.unmodifiableList(participantes);
    }

    public abstract Map<Integer, Long> calcularRateio();
}
