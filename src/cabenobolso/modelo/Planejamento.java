package cabenobolso.modelo;

import cabenobolso.excecao.EntradaInvalidaException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Planejamento implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Integer, Participante> participantes = new LinkedHashMap<>();
    private final List<Despesa> despesas = new ArrayList<>();

    public void adicionarParticipante(int codigo, String nome, BigDecimal orcamento)
            throws EntradaInvalidaException {
        if (participantes.containsKey(codigo)) {
            throw new EntradaInvalidaException("Código de participante já cadastrado.");
        }
        participantes.put(codigo, new Participante(codigo, nome, orcamento));
    }

    public void alterarOrcamento(int codigo, BigDecimal orcamento)
            throws EntradaInvalidaException {
        localizarParticipante(codigo).alterarOrcamento(orcamento);
    }

    public void alterarNome(int codigo, String nome) throws EntradaInvalidaException {
        localizarParticipante(codigo).alterarNome(nome);
    }

    public void removerParticipante(int codigo) throws EntradaInvalidaException {
        Participante participante = localizarParticipante(codigo);
        for (Despesa despesa : despesas) {
            if (despesa.getParticipantes().contains(participante)) {
                throw new EntradaInvalidaException(
                        "Não é possível excluir: o participante está vinculado à despesa \""
                                + despesa.getDescricao() + "\".");
            }
        }
        participantes.remove(codigo);
    }

    public List<Participante> listarParticipantes() {
        return Collections.unmodifiableList(new ArrayList<>(participantes.values()));
    }

    public void adicionarDespesaFixa(String descricao, BigDecimal valor,
                                     List<Integer> codigos)
            throws EntradaInvalidaException {
        despesas.add(new DespesaFixa(descricao, valor, resolverParticipantes(codigos)));
    }

    public void adicionarDespesaPorPessoa(String descricao, BigDecimal valor,
                                          List<Integer> codigos)
            throws EntradaInvalidaException {
        despesas.add(new DespesaPorPessoa(descricao, valor, resolverParticipantes(codigos)));
    }

    public void editarDespesa(int indice, String descricao, BigDecimal valor,
                              boolean fixa, List<Integer> codigos)
            throws EntradaInvalidaException {
        if (indice < 0 || indice >= despesas.size()) {
            throw new EntradaInvalidaException("Despesa não encontrada.");
        }
        List<Participante> selecionados = resolverParticipantes(codigos);
        Despesa atualizada = fixa
                ? new DespesaFixa(descricao, valor, selecionados)
                : new DespesaPorPessoa(descricao, valor, selecionados);
        despesas.set(indice, atualizada);
    }

    public void removerDespesa(int indice) throws EntradaInvalidaException {
        if (indice < 0 || indice >= despesas.size()) {
            throw new EntradaInvalidaException("Despesa não encontrada.");
        }
        despesas.remove(indice);
    }

    public List<Despesa> listarDespesas() {
        return Collections.unmodifiableList(new ArrayList<>(despesas));
    }

    public BigDecimal custoTotal() {
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (Despesa despesa : despesas) {
            total = total.add(despesa.custoTotal());
        }
        return total;
    }

    public List<ResultadoParticipante> calcularResultados() {
        List<ResultadoParticipante> resultados = new ArrayList<>();
        for (Participante participante : participantes.values()) {
            BigDecimal custo = BigDecimal.ZERO.setScale(2);
            for (Despesa despesa : despesas) {
                custo = custo.add(despesa.custoDe(participante.getCodigo()));
            }
            resultados.add(new ResultadoParticipante(participante, custo));
        }
        return Collections.unmodifiableList(resultados);
    }

    private Participante localizarParticipante(int codigo) throws EntradaInvalidaException {
        Participante participante = participantes.get(codigo);
        if (participante == null) {
            throw new EntradaInvalidaException("Participante não encontrado.");
        }
        return participante;
    }

    private List<Participante> resolverParticipantes(List<Integer> codigos)
            throws EntradaInvalidaException {
        if (codigos == null || codigos.isEmpty()) {
            throw new EntradaInvalidaException(
                    "Selecione ao menos um participante para a despesa.");
        }
        List<Participante> selecionados = new ArrayList<>();
        for (Integer codigo : codigos) {
            if (codigo == null) {
                throw new EntradaInvalidaException("Há um participante inválido na seleção.");
            }
            selecionados.add(localizarParticipante(codigo));
        }
        return selecionados;
    }
}
