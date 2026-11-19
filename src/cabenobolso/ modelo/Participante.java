package cabenobolso.modelo;

import cabenobolso.excecao.DadosInvalidosException;
import java.io.Serializable;

public class Participante implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int codigo;
    private String nome;
    private long orcamento; 

    public Participante(int codigo, String nome, long orcamento)
            throws DadosInvalidosException {
        validarNome(nome);
        validarOrcamento(orcamento);
        this.codigo = codigo;
        this.nome = nome.trim();
        this.orcamento = orcamento;
    }

    public int getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public long getOrcamento() { return orcamento; }

    public void setNome(String nome) throws DadosInvalidosException {
        validarNome(nome);
        this.nome = nome.trim();
    }

    public void setOrcamento(long orcamento) throws DadosInvalidosException {
        validarOrcamento(orcamento);
        this.orcamento = orcamento;
    }

    private static void validarNome(String nome) throws DadosInvalidosException {
        if (nome == null || nome.isBlank())
            throw new DadosInvalidosException("O nome não pode ser vazio.");
    }

    private static void validarOrcamento(long orcamento) throws DadosInvalidosException {
        if (orcamento < 0)
            throw new DadosInvalidosException("O orçamento não pode ser negativo.");
    }
}
