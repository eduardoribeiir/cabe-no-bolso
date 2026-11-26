package cabenobolso.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        if (participante == null) {
            throw new DadosInvalidosException(
                "Participante inválido.");
        }

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

    public void substituirDespesa(Despesa antiga, Despesa nova)
            throws DadosInvalidosException {

        if (nova == null) {
            throw new DadosInvalidosException(
                "Despesa inválida.");
        }

        int indice = despesas.indexOf(antiga);

        if (indice < 0) {
            throw new DadosInvalidosException(
                "Despesa não encontrada.");
        }

        despesas.set(indice, nova);
    }

    public void excluirParticipante(int codigo)
            throws DadosInvalidosException {

        Participante participante = buscarParticipante(codigo);

        if (participante == null) {
            throw new DadosInvalidosException(
                "Participante não encontrado.");
        }

        for (Despesa despesa : despesas) {
            if (despesa.envolveParticipante(codigo)) {
                throw new DadosInvalidosException(
                    "Não é possível excluir: o participante está "
                    + "vinculado à despesa \"" + despesa.getDescricao()
                    + "\".");
            }
        }

        participantes.remove(participante);
    }

    private Participante buscarParticipante(int codigo) {

        for (Participante participante : participantes) {
            if (participante.getCodigo() == codigo) {
                return participante;
            }
        }

        return null;
    }

    public long calcularTotalParticipante(int codigo) {

        long total = 0;

        for (Despesa despesa : despesas) {

            Map<Integer, Long> rateio =
                despesa.calcularRateio();

            total += rateio.getOrDefault(codigo, 0L);
        }

        return total;
    }

    public long calcularCustoTotal() {

        long total = 0;

        for (Despesa despesa : despesas) {
            total += despesa.calcularCustoTotal();
        }

        return total;
    }

    public long calcularValorExcedido(int codigo) {

        Participante participanteEncontrado = buscarParticipante(codigo);

        if (participanteEncontrado == null) {
            return 0;
        }

        long total = calcularTotalParticipante(codigo);

        return Math.max(
            0,
            total - participanteEncontrado.getOrcamento()
        );
    }

    public boolean estaDentroDoOrcamento(int codigo) {

        return calcularValorExcedido(codigo) == 0;
    }
}
