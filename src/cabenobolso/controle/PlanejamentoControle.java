package cabenobolso.controle;

import cabenobolso.excecao.ArquivoException;
import cabenobolso.excecao.CabeNoBolsoException;
import cabenobolso.excecao.EntradaInvalidaException;
import cabenobolso.modelo.Despesa;
import cabenobolso.modelo.Participante;
import cabenobolso.modelo.Planejamento;
import cabenobolso.modelo.ResultadoParticipante;
import cabenobolso.persistencia.Persistencia;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * C (Controle): recebe as ações da visão (textos digitados e seleções), converte
 * os dados, chama o modelo e devolve uma {@link Resposta} com sucesso ou mensagem
 * de erro. Depois de qualquer alteração bem-sucedida (cadastro, alteração,
 * exclusão, carregar, novo) avisa os ouvintes para que a visão recalcule e
 * redesenhe totais e resultados.
 *
 * Não conhece Swing. Toda validação de regra de negócio fica no modelo.
 */
public class PlanejamentoControle {

    /** Notificado sempre que o planejamento muda. */
    public interface Ouvinte {
        void planejamentoAlterado();
    }

    @FunctionalInterface
    private interface Acao {
        void executar() throws CabeNoBolsoException;
    }

    private Planejamento planejamento;
    private final Persistencia persistencia;
    private final List<Ouvinte> ouvintes = new ArrayList<>();

    public PlanejamentoControle(Planejamento planejamento, Persistencia persistencia) {
        this.planejamento = Objects.requireNonNull(planejamento, "planejamento");
        this.persistencia = Objects.requireNonNull(persistencia, "persistencia");
    }

    // ------------------------------------------------------------------ ouvintes

    public void adicionarOuvinte(Ouvinte ouvinte) {
        if (ouvinte != null) {
            ouvintes.add(ouvinte);
        }
    }

    private void notificar() {
        for (Ouvinte ouvinte : new ArrayList<>(ouvintes)) {
            ouvinte.planejamentoAlterado();
        }
    }

    // ------------------------------------------------------------- participantes

    public Resposta cadastrarParticipante(String codigo, String nome, String orcamento) {
        return executar(() -> {
            int cod = lerCodigo(codigo);
            BigDecimal orc = lerValor(orcamento, "Orçamento");
            planejamento.adicionarParticipante(cod, nome == null ? "" : nome.trim(), orc);
        }, "Participante cadastrado com sucesso.");
    }

    public Resposta alterarOrcamento(String codigo, String novoOrcamento) {
        return executar(() -> {
            int cod = lerCodigo(codigo);
            BigDecimal orc = lerValor(novoOrcamento, "Orçamento");
            planejamento.alterarOrcamento(cod, orc);
        }, "Orçamento alterado com sucesso.");
    }

    /** O modelo impede a exclusão de participante vinculado a despesas. */
    public Resposta excluirParticipante(int codigo) {
        return executar(() -> planejamento.removerParticipante(codigo),
                "Participante excluído com sucesso.");
    }

    public List<Participante> listarParticipantes() {
        return planejamento.listarParticipantes();
    }

    // ------------------------------------------------------------------ despesas

    /**
     * @param fixa    true = despesa fixa (dividida); false = despesa por pessoa
     * @param codigos códigos dos participantes selecionados (pelo menos 1, sem repetição)
     */
    public Resposta cadastrarDespesa(String descricao, String valor, boolean fixa,
                                     Collection<Integer> codigos) {
        return executar(() -> {
            List<Integer> lista = codigos == null ? new ArrayList<>() : new ArrayList<>(codigos);
            if (lista.isEmpty()) {
                throw new EntradaInvalidaException("Selecione ao menos um participante para a despesa.");
            }
            if (new HashSet<>(lista).size() != lista.size()) {
                throw new EntradaInvalidaException("Há participantes repetidos na seleção.");
            }
            BigDecimal v = lerValor(valor, "Valor");
            String desc = descricao == null ? "" : descricao.trim();
            if (fixa) {
                planejamento.adicionarDespesaFixa(desc, v, lista);
            } else {
                planejamento.adicionarDespesaPorPessoa(desc, v, lista);
            }
        }, "Despesa cadastrada com sucesso.");
    }

    /** @param indice posição da despesa na ordem de inclusão (começa em 0) */
    public Resposta excluirDespesa(int indice) {
        return executar(() -> planejamento.removerDespesa(indice), "Despesa excluída com sucesso.");
    }

    public List<Despesa> listarDespesas() {
        return planejamento.listarDespesas();
    }

    // ------------------------------------------------------------------- resumo

    public BigDecimal custoTotal() {
        return planejamento.custoTotal();
    }

    public List<ResultadoParticipante> resultados() {
        return planejamento.calcularResultados();
    }

    // -------------------------------------------------------------- persistência

    public Resposta salvar() {
        try {
            persistencia.salvar(planejamento);
            return Resposta.ok("Planejamento salvo com sucesso.");
        } catch (ArquivoException e) {
            return Resposta.erro(e.getMessage());
        } catch (RuntimeException e) {
            return Resposta.erro("Erro inesperado ao salvar: " + e.getMessage());
        }
    }

    /**
     * Só troca o planejamento em uso se o carregamento der certo; em caso de
     * falha o planejamento atual é preservado.
     */
    public Resposta carregar() {
        try {
            Planejamento carregado = persistencia.carregar();
            if (carregado == null) {
                return Resposta.erro("O arquivo não contém um planejamento válido.");
            }
            this.planejamento = carregado;
        } catch (ArquivoException e) {
            return Resposta.erro(e.getMessage());
        } catch (RuntimeException e) {
            return Resposta.erro("Erro inesperado ao carregar: " + e.getMessage());
        }
        notificar();
        return Resposta.ok("Planejamento carregado com sucesso.");
    }

    /** Descarta o planejamento atual e inicia um vazio. */
    public Resposta novo() {
        this.planejamento = new Planejamento();
        notificar();
        return Resposta.ok("Novo planejamento iniciado.");
    }

    // ------------------------------------------------------------------ apoio

    private Resposta executar(Acao acao, String mensagemSucesso) {
        try {
            acao.executar();
        } catch (CabeNoBolsoException e) {
            return Resposta.erro(e.getMessage());
        } catch (NumberFormatException e) {
            return Resposta.erro("Valor numérico inválido.");
        } catch (RuntimeException e) {
            return Resposta.erro("Erro inesperado: " + e.getMessage());
        }
        notificar();
        return Resposta.ok(mensagemSucesso);
    }

    private static int lerCodigo(String texto) throws EntradaInvalidaException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new EntradaInvalidaException("Informe o código do participante.");
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new EntradaInvalidaException("O código deve ser um número inteiro.");
        }
    }

    /** Aceita "70", "70,00", "70.00" e "R$ 1.250,50". Máximo de 2 casas decimais. */
    private static BigDecimal lerValor(String texto, String campo) throws EntradaInvalidaException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new EntradaInvalidaException("Informe o campo " + campo + ".");
        }
        String s = texto.trim().replace("R$", "").replace(" ", "").replace("\u00a0", "");
        if (s.contains(",")) {
            s = s.replace(".", "").replace(",", ".");
        }
        BigDecimal valor;
        try {
            valor = new BigDecimal(s);
        } catch (NumberFormatException e) {
            throw new EntradaInvalidaException(
                    "Formato inválido no campo " + campo + ". Use números como 70,00.");
        }
        if (valor.stripTrailingZeros().scale() > 2) {
            throw new EntradaInvalidaException(
                    "O campo " + campo + " aceita no máximo duas casas decimais.");
        }
        return valor.setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
