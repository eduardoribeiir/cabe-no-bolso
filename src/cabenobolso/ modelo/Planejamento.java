package cabenobolso.modelo;    

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cabenobolso.excecao.DadosInvalidosException;

public class Planejamento implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Participante> participantes;
    private List<Despesa> despesas;

    public Planejamento() {
        participantes = new ArrayList<>();
        despesas = new ArrayList<>();
    }

    public void adicionarParticipante(Participante participante)
            throws DadosInvalidosException {

        for (Participante p : participantes) {
            if (p.getCodigo() == participante.getCodigo()) {
                throw new DadosInvalidosException(
                    "Código de participante já cadastrado.");
            }
        }

        participantes.add(participante);
    }

    public void adicionarDespesa(Despesa despesa) {
        despesas.add(despesa);
    }

    public List<Participante> getParticipantes() {
        return new ArrayList<>(participantes);
    }

    public List<Despesa> getDespesas() {
        return new ArrayList<>(despesas);
    }

    public void excluirDespesa(Despesa despesa) {
        despesas.remove(despesa);
    }
}

