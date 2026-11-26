package cabenobolso.modelo;

import java.math.BigDecimal;

public final class ResultadoParticipante {

    private final int codigo;
    private final String nome;
    private final BigDecimal orcamento;
    private final BigDecimal custo;
    private final BigDecimal excesso;

    ResultadoParticipante(Participante participante, BigDecimal custo) {
        this.codigo = participante.getCodigo();
        this.nome = participante.getNome();
        this.orcamento = participante.getOrcamento();
        this.custo = custo;
        this.excesso = custo.subtract(orcamento).max(BigDecimal.ZERO.setScale(2));
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getOrcamento() {
        return orcamento;
    }

    public BigDecimal getCusto() {
        return custo;
    }

    public BigDecimal getExcesso() {
        return excesso;
    }

    public boolean isDentroDoOrcamento() {
        return excesso.signum() == 0;
    }
}
