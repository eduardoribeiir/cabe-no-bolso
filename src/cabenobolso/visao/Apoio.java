package cabenobolso.visao;

import cabenobolso.controle.Resposta;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

final class Apoio {

    private static final NumberFormat MOEDA =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private Apoio() {
    }

    /** Ex.: "R$ 70,00". */
    static String moeda(BigDecimal valor) {
        return MOEDA.format(valor);
    }

    /** Ex.: "70,00" (para preencher campos de texto). */
    static String numero(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ',');
    }

    static void erro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    static void info(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Cabe no bolso", JOptionPane.INFORMATION_MESSAGE);
    }

    static boolean confirmar(Component pai, String mensagem) {
        return JOptionPane.showConfirmDialog(pai, mensagem, "Confirmação",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    static boolean exibir(Component pai, Resposta resposta) {
        if (!resposta.isSucesso()) {
            erro(pai, resposta.getMensagem());
        }
        return resposta.isSucesso();
    }

    static DefaultTableModel modeloSomenteLeitura(Object[] colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    static void adicionar(JPanel painel, Component componente, int x, int y, int largura, double pesoX) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x;
        g.gridy = y;
        g.gridwidth = largura;
        g.weightx = pesoX;
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        painel.add(componente, g);
    }
}
