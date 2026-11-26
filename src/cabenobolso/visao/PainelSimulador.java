package cabenobolso.visao;

import cabenobolso.controle.PlanejamentoControle;
import cabenobolso.excecao.EntradaInvalidaException;
import cabenobolso.extra.ResultadoSimulacao;
import cabenobolso.modelo.Despesa;
import cabenobolso.modelo.Participante;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class PainelSimulador extends JPanel {

    private static final String ESSENCIAL = "Essencial";
    private static final String OPCIONAL = "Opcional";
    private static final int LIMITE_OPCIONAIS = 10;

    private final PlanejamentoControle controle;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Despesa", "Tipo", "Valor", "Classificação", "Prioridade"}, 0) {
        @Override
        public Class<?> getColumnClass(int coluna) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return coluna == 3 || (coluna == 4 && OPCIONAL.equals(getValueAt(linha, 3)));
        }
    };
    private final JTable tabela = new JTable(modelo);
    private final JTextArea resultado = new JTextArea();
    private final JLabel resumoClassificacao = new JLabel();

    PainelSimulador(PlanejamentoControle controle) {
        super(new BorderLayout(8, 8));
        this.controle = controle;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel instrucoes = new JPanel(new BorderLayout(4, 4));
        instrucoes.add(new JLabel("<html><b>Como gerar uma sugestão:</b> em cada linha, abra "
                + "Classificação e escolha Opcional para despesas que podem ser retiradas. "
                + "A prioridade começa em 1; aumente-a para itens mais importantes "
                + "(ex.: atividade 3, sobremesa 1). Depois clique em "
                + "<b>Gerar sugestão automática</b>: o aplicativo encontra a combinação viável "
                + "com a maior prioridade total, respeitando o orçamento de todos. "
                + "As demais despesas ficam essenciais. A simulação não altera o planejamento.</html>"),
                BorderLayout.NORTH);
        instrucoes.add(resumoClassificacao, BorderLayout.SOUTH);
        add(instrucoes, BorderLayout.NORTH);

        resultado.setEditable(false);
        resultado.setLineWrap(true);
        resultado.setWrapStyleWord(true);
        modelo.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 3) {
                int linha = e.getFirstRow();
                if (linha >= 0 && linha < modelo.getRowCount()) {
                    modelo.setValueAt(OPCIONAL.equals(modelo.getValueAt(linha, 3)) ? "1" : "", linha, 4);
                }
            }
            atualizarResumoClassificacao();
            resultado.setText("Classificação atualizada. Clique em Gerar sugestão automática para calcular "
                    + "a melhor alternativa.");
        });
        JComboBox<String> opcoesClassificacao = new JComboBox<>(new String[]{ESSENCIAL, OPCIONAL});
        DefaultCellEditor editorClassificacao = new DefaultCellEditor(opcoesClassificacao);
        editorClassificacao.setClickCountToStart(1);
        tabela.getColumnModel().getColumn(3).setCellEditor(editorClassificacao);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(100);
        JScrollPane despesas = new JScrollPane(tabela);
        despesas.setBorder(BorderFactory.createTitledBorder("Classificação das despesas"));
        JScrollPane detalhes = new JScrollPane(resultado);
        detalhes.setBorder(BorderFactory.createTitledBorder("Resultado da simulação"));
        JSplitPane divisor = new JSplitPane(JSplitPane.VERTICAL_SPLIT, despesas, detalhes);
        divisor.setResizeWeight(0.45);
        add(divisor, BorderLayout.CENTER);

        JButton simular = new JButton("Gerar sugestão automática");
        simular.addActionListener(e -> simular());
        JPanel botoes = new JPanel();
        JButton redefinir = new JButton("Marcar todas como essenciais");
        redefinir.addActionListener(e -> marcarTodasEssenciais());
        botoes.add(redefinir);
        botoes.add(simular);
        add(botoes, BorderLayout.SOUTH);

        controle.adicionarOuvinte(this::atualizar);
        atualizar();
    }

    private void atualizar() {
        if (tabela.isEditing()) {
            tabela.getCellEditor().cancelCellEditing();
        }
        modelo.setRowCount(0);
        for (Despesa despesa : controle.listarDespesas()) {
            modelo.addRow(new Object[]{despesa.getDescricao(), despesa.getTipo(),
                    Apoio.moeda(despesa.getValor()), ESSENCIAL, ""});
        }
        atualizarResumoClassificacao();
        resultado.setText("Todas as despesas começam como essenciais. Se quiser que o simulador possa "
                + "retirar alguma, escolha Opcional na linha correspondente e clique em Gerar sugestão automática.");
    }

    private void marcarTodasEssenciais() {
        if (tabela.isEditing()) {
            tabela.getCellEditor().cancelCellEditing();
        }
        for (int linha = 0; linha < modelo.getRowCount(); linha++) {
            modelo.setValueAt(ESSENCIAL, linha, 3);
        }
    }

    private void atualizarResumoClassificacao() {
        int opcionais = 0;
        for (int linha = 0; linha < modelo.getRowCount(); linha++) {
            if (OPCIONAL.equals(modelo.getValueAt(linha, 3))) {
                opcionais++;
            }
        }
        resumoClassificacao.setText("Despesas opcionais: " + opcionais + " de " + LIMITE_OPCIONAIS
                + " | As demais serão mantidas como essenciais.");
    }

    private void simular() {
        if (tabela.isEditing() && !tabela.getCellEditor().stopCellEditing()) {
            Apoio.erro(this, "Conclua a edição da prioridade antes de simular.");
            return;
        }
        List<Integer> prioridades = new ArrayList<>();
        for (int linha = 0; linha < modelo.getRowCount(); linha++) {
            if (ESSENCIAL.equals(modelo.getValueAt(linha, 3))) {
                prioridades.add(null);
                continue;
            }
            Object valor = modelo.getValueAt(linha, 4);
            try {
                prioridades.add(Integer.valueOf(valor == null ? "" : valor.toString().trim()));
            } catch (NumberFormatException e) {
                Apoio.erro(this, "Informe uma prioridade inteira positiva para \""
                        + modelo.getValueAt(linha, 0) + "\".");
                return;
            }
        }
        try {
            exibirResultado(controle.simularAlternativas(prioridades));
        } catch (EntradaInvalidaException e) {
            Apoio.erro(this, e.getMessage());
        }
    }

    private void exibirResultado(ResultadoSimulacao simulacao) {
        StringBuilder texto = new StringBuilder();
        if (simulacao.temSolucao()) {
            texto.append("Melhor sugestão: manter ")
                    .append(simulacao.getMantidas().size()).append(" despesa(s) e retirar ")
                    .append(simulacao.getRetiradas().size()).append(".\n")
                    .append("Prioridade total: ").append(simulacao.getPrioridade()).append(".\n");
            if (simulacao.getRetiradas().isEmpty()) {
                texto.append("Todas as despesas classificadas cabem nos orçamentos; "
                        + "nenhuma precisou ser retirada.\n");
            }
            texto.append('\n');
            adicionarDespesas(texto, "Despesas mantidas", simulacao.getMantidas());
            adicionarDespesas(texto, "Despesas retiradas", simulacao.getRetiradas());
        } else {
            texto.append("Não há solução viável: só as despesas essenciais já ultrapassam "
                    + "o orçamento de pelo menos uma pessoa.\n")
                    .append("Confira os excessos abaixo. Se alguma despesa essencial puder ser retirada, "
                            + "mude-a para Opcional e gere a sugestão novamente.\n\n");
            adicionarDespesas(texto, "Despesas essenciais", simulacao.getMantidas());
            adicionarDespesas(texto, "Opcionais não consideradas", simulacao.getRetiradas());
        }
        texto.append("\nTotais por participante (original → ")
                .append(simulacao.temSolucao() ? "alternativa" : "essenciais")
                .append("):\n");
        for (Participante participante : simulacao.getParticipantes()) {
            BigDecimal custo = simulacao.getCustoSimulado(participante.getCodigo());
            BigDecimal excesso = custo.subtract(participante.getOrcamento())
                    .max(BigDecimal.ZERO);
            texto.append(participante.getNome()).append(": ")
                    .append(Apoio.moeda(simulacao.getCustoOriginal(participante.getCodigo())))
                    .append(" → ").append(Apoio.moeda(custo))
                    .append(" | orçamento: ").append(Apoio.moeda(participante.getOrcamento()));
            if (excesso.signum() > 0) {
                texto.append(" | excesso: ").append(Apoio.moeda(excesso));
            }
            texto.append('\n');
        }
        texto.append("\nCusto total: ").append(Apoio.moeda(simulacao.getTotalOriginal()))
                .append(" → ").append(Apoio.moeda(simulacao.getTotalSimulado()));
        resultado.setText(texto.toString());
        resultado.setCaretPosition(0);
    }

    private static void adicionarDespesas(StringBuilder texto, String titulo, List<Despesa> despesas) {
        texto.append(titulo).append(":\n");
        if (despesas.isEmpty()) {
            texto.append("  Nenhuma.\n");
        }
        for (Despesa despesa : despesas) {
            texto.append("  - ").append(despesa.getDescricao())
                    .append(" (").append(Apoio.moeda(despesa.custoTotal())).append(")\n");
        }
        texto.append('\n');
    }

}
