package cabenobolso.excecao;

public class EntradaInvalidaException extends CabeNoBolsoException {

    private static final long serialVersionUID = 1L;

    public EntradaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
