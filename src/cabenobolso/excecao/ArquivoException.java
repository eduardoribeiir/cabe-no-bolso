package cabenobolso.excecao;

public class ArquivoException extends CabeNoBolsoException {

    private static final long serialVersionUID = 1L;

    public ArquivoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
