package cabenobolso.excecao;

public class CabeNoBolsoException extends Exception {

    private static final long serialVersionUID = 1L;

    public CabeNoBolsoException(String mensagem) {
        super(mensagem);
    }

    public CabeNoBolsoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
