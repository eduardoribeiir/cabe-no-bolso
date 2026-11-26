package cabenobolso.visao;

import cabenobolso.controle.PlanejamentoControle;
import cabenobolso.modelo.Planejamento;
import cabenobolso.persistencia.PersistenciaArquivo;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;

public final class TestesSimuladorVisao {

    private TestesSimuladorVisao() {
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(TestesSimuladorVisao::testar);
        System.out.println("Teste da aba Simulador passou.");
    }

    private static void testar() {
        PlanejamentoControle controle = new PlanejamentoControle(
                new Planejamento(), new PersistenciaArquivo("planejamento.dat"));
        conferir(controle.cadastrarParticipante("1", "Ana", "70").isSucesso());
        conferir(controle.cadastrarParticipante("2", "Bruno", "60").isSucesso());
        conferir(controle.cadastrarParticipante("3", "Carla", "70").isSucesso());
        conferir(controle.cadastrarDespesa("Transporte", "90", true,
                java.util.List.of(1, 2, 3)).isSucesso());
        conferir(controle.cadastrarDespesa("Almoço", "20", false,
                java.util.List.of(1, 2, 3)).isSucesso());
        conferir(controle.cadastrarDespesa("Atividade", "30", true,
                java.util.List.of(1, 3)).isSucesso());
        conferir(controle.cadastrarDespesa("Sobremesa", "10", false,
                java.util.List.of(1, 2, 3)).isSucesso());

        PainelSimulador painel = new PainelSimulador(controle);
        JTable tabela = encontrar(painel, JTable.class);
        JTextArea resultado = encontrar(painel, JTextArea.class);
        JButton botao = encontrarBotao(painel, "Gerar sugestão automática");
        JButton redefinir = encontrarBotao(painel, "Marcar todas como essenciais");
        conferir(tabela != null && resultado != null && botao != null && redefinir != null);
        conferir("Essencial".equals(tabela.getModel().getValueAt(0, 3)));
        conferir(textoClassificacao(tabela, 0).equals("Essencial"));
        botao.doClick();
        conferir(resultado.getText().contains("Não há solução viável")
                && resultado.getText().contains("mude-a para Opcional"));

        for (int linha = 0; linha < tabela.getRowCount(); linha++) {
            tabela.getModel().setValueAt("Opcional", linha, 3);
            conferir("1".equals(tabela.getModel().getValueAt(linha, 4)));
            conferir(textoClassificacao(tabela, linha).equals("Opcional"));
        }
        botao.doClick();
        String texto = resultado.getText();
        conferir(texto.contains("Melhor sugestão: manter 3 despesa(s) e retirar 1.")
                && texto.contains("Prioridade total: 3.")
                && texto.contains("Despesas mantidas:") && texto.contains("Despesas retiradas:")
                && texto.contains("Custo total:"));

        redefinir.doClick();
        conferir("Essencial".equals(tabela.getModel().getValueAt(2, 3))
                && "".equals(tabela.getModel().getValueAt(2, 4)));
        tabela.getModel().setValueAt("Opcional", 2, 3);
        tabela.getModel().setValueAt("3", 2, 4);
        tabela.getModel().setValueAt("Opcional", 3, 3);
        tabela.getModel().setValueAt("1", 3, 4);
        botao.doClick();
        texto = resultado.getText();
        conferir(texto.contains("Melhor sugestão: manter 3 despesa(s) e retirar 1.")
                && texto.contains("Prioridade total: 3")
                && texto.contains("Despesas mantidas:") && texto.contains("Atividade")
                && texto.contains("Despesas retiradas:") && texto.contains("Sobremesa")
                && texto.contains("65,00") && texto.contains("50,00")
                && texto.contains("180,00") && texto.contains("210,00"));

        redefinir.doClick();
        conferir("Essencial".equals(tabela.getModel().getValueAt(2, 3))
                && "".equals(tabela.getModel().getValueAt(2, 4))
                && resultado.getText().contains("Classificação atualizada"));

        conferir(controle.alterarOrcamento("1", "40").isSucesso());
        conferir(!resultado.getText().contains("Alternativa encontrada"));
        tabela.getModel().setValueAt("Opcional", 2, 3);
        tabela.getModel().setValueAt("3", 2, 4);
        tabela.getModel().setValueAt("Opcional", 3, 3);
        tabela.getModel().setValueAt("1", 3, 4);
        botao.doClick();
        texto = resultado.getText();
        conferir(texto.contains("Não há solução viável") && texto.contains("Ana")
                && texto.contains("50,00") && texto.contains("excesso:")
                && texto.contains("10,00"));
    }

    private static <T extends Component> T encontrar(Container raiz, Class<T> tipo) {
        for (Component componente : raiz.getComponents()) {
            if (tipo.isInstance(componente)) {
                return tipo.cast(componente);
            }
            if (componente instanceof Container) {
                T encontrado = encontrar((Container) componente, tipo);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }

    private static JButton encontrarBotao(Container raiz, String texto) {
        for (Component componente : raiz.getComponents()) {
            if (componente instanceof JButton
                    && texto.equals(((JButton) componente).getText())) {
                return (JButton) componente;
            }
            if (componente instanceof Container) {
                JButton encontrado = encontrarBotao((Container) componente, texto);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }

    private static String textoClassificacao(JTable tabela, int linha) {
        Component renderizado = tabela.prepareRenderer(tabela.getCellRenderer(linha, 3), linha, 3);
        return ((JLabel) renderizado).getText();
    }

    private static void conferir(boolean condicao) {
        if (!condicao) {
            throw new AssertionError("Falha na interação com a aba Simulador.");
        }
    }
}
