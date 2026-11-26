package cabenobolso.controle;

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
