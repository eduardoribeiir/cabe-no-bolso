package cabenobolso.controle;

/**
 * Resultado de uma ação executada pelo controle: sucesso ou erro, sempre com
 * uma mensagem clara para ser exibida ao usuário pela visão.
 */
public final class Resposta {

    private final boolean sucesso;
    private final String mensagem;

    private Resposta(boolean sucesso, String mensagem) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }

    public static Resposta ok(String mensagem) {
        return new Resposta(true, mensagem);
    }

    public static Resposta erro(String mensagem) {
        return new Resposta(false, mensagem);
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }
}
