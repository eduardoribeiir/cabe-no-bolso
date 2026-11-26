package cabenobolso.modelo;

import cabenobolso.excecao.EntradaInvalidaException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Participante implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int codigo;
    private String nome;
    private BigDecimal orcamento;

    Participante(int codigo, String nome, BigDecimal orcamento)
            throws EntradaInvalidaException {
        if (codigo <= 0) {
            throw new EntradaInvalidaException("O código deve ser maior que zero.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new EntradaInvalidaException("O nome não pode ser vazio.");
        }
        this.codigo = codigo;
        this.nome = nome.trim();
        alterarOrcamento(orcamento);
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    void alterarNome(String novoNome) throws EntradaInvalidaException {
        if (novoNome == null || novoNome.trim().isEmpty()) {
            throw new EntradaInvalidaException("O nome não pode ser vazio.");
        }
        this.nome = novoNome.trim();
    }

    public BigDecimal getOrcamento() {
        return orcamento;
    }

    void alterarOrcamento(BigDecimal novoOrcamento) throws EntradaInvalidaException {
        BigDecimal normalizado = normalizarValor(novoOrcamento, "Orçamento");
        if (normalizado.signum() < 0) {
            throw new EntradaInvalidaException("O orçamento não pode ser negativo.");
        }
        this.orcamento = normalizado;
    }

    static BigDecimal normalizarValor(BigDecimal valor, String campo)
            throws EntradaInvalidaException {
        if (valor == null) {
            throw new EntradaInvalidaException("Informe o campo " + campo + ".");
        }
        try {
            return valor.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new EntradaInvalidaException(
                    "O campo " + campo + " aceita no máximo duas casas decimais.");
        }
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Participante)) {
            return false;
        }
        Participante outro = (Participante) objeto;
        return codigo == outro.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
