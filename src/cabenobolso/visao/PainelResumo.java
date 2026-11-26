package cabenobolso.visao;

import cabenobolso.controle.PlanejamentoControle;
import cabenobolso.modelo.Despesa;
import cabenobolso.modelo.Participante;
import cabenobolso.modelo.ResultadoParticipante;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class PainelResumo extends JPanel {

    private static final String DENTRO = "Dentro do orçamento";
    private static final String ACIMA = "Acima do orçamento";

    private final PlanejamentoControle controle;

    private final JLabel lblTotal = new JLabel();
    private final JLabel lblSituacao = new JLabel();

    private final DefaultTableModel modeloDespesas = Apoio.modeloSomenteLeitura(
            new Object[]{"Descrição", "Tipo", "Valor informado", "Participantes", "Custo total"});
    private final DefaultTableModel modeloParticipantes = Apoio.modeloSomenteLeitura(
            new Object[]{"Código", "Nome", "Orçamento", "Total", "Situação", "Excedido"});

    private final JTable tabelaDespesas = new JTable(modeloDespesas);
    private final JTable tabelaParticipantes = new JTable(modeloParticipantes);

    PainelResumo(PlanejamentoControle controle) {
        super(new BorderLayout(8, 8));
        this.controle = controle;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 16f));
        JPanel topo = new JPanel(new BorderLayout(4, 4));
        topo.add(lblTotal, BorderLayout.NORTH);
        topo.add(lblSituacao, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);

        tabelaParticipantes.setDefaultRenderer(Object.class, new Renderizador());

        JScrollPane rolagemDespesas = new JScrollPane(tabelaDespesas);
        rolagemDespesas.setBorder(BorderFactory.createTitledBorder("Despesas"));
        JScrollPane rolagemParticipantes = new JScrollPane(tabelaParticipantes);
        rolagemParticipantes.setBorder(BorderFactory.createTitledBorder("Resultado por participante"));

        JSplitPane divisor = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rolagemDespesas, rolagemParticipantes);
        divisor.setResizeWeight(0.4);
        add(divisor, BorderLayout.CENTER);

        controle.adicionarOuvinte(this::atualizar);
        atualizar();
    }

    private void atualizar() {
        List<Despesa> despesas = controle.listarDespesas();

        modeloDespesas.setRowCount(0);
        for (Despesa d : despesas) {
            String nomes = d.getParticipantes().stream()
                    .map(Participante::getNome)
                    .collect(Collectors.joining(", "));
            modeloDespesas.addRow(new Object[]{
                    d.getDescricao(), d.getTipo(), Apoio.moeda(d.getValor()),
                    nomes, Apoio.moeda(d.custoTotal())});
        }
        lblTotal.setText("Custo total: " + Apoio.moeda(controle.custoTotal()));

        List<Object> colunas = new ArrayList<>();
        colunas.add("Código");
        colunas.add("Nome");
        colunas.add("Orçamento");
        for (Despesa d : despesas) {
            colunas.add(d.getDescricao());
        }
        colunas.add("Total");
        colunas.add("Situação");
        colunas.add("Excedido");
        modeloParticipantes.setColumnIdentifiers(colunas.toArray());
        modeloParticipantes.setRowCount(0);

        int dentro = 0;
        int acima = 0;
        for (ResultadoParticipante r : controle.resultados()) {
            List<Object> linha = new ArrayList<>();
            linha.add(r.getCodigo());
            linha.add(r.getNome());
            linha.add(Apoio.moeda(r.getOrcamento()));
            for (Despesa d : despesas) {
                linha.add(Apoio.moeda(d.custoDe(r.getCodigo())));
            }
            linha.add(Apoio.moeda(r.getCusto()));
            if (r.isDentroDoOrcamento()) {
                dentro++;
                linha.add(DENTRO);
                linha.add("—");
            } else {
                acima++;
                linha.add(ACIMA);
                linha.add(Apoio.moeda(r.getExcesso()));
            }
            modeloParticipantes.addRow(linha.toArray());
        }
        lblSituacao.setText("Dentro do orçamento: " + dentro + "   |   Acima do orçamento: " + acima);
    }

    /** Pinta Situação e Excedido de verde (dentro) ou vermelho (acima). */
    private static class Renderizador extends DefaultTableCellRenderer {
        private static final Color VERDE = new Color(0x1B7F3B);
        private static final Color VERMELHO = new Color(0xB00020);

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionado,
                                                       boolean foco, int linha, int coluna) {
            Component c = super.getTableCellRendererComponent(tabela, valor, selecionado, foco, linha, coluna);
            int colunaModelo = tabela.convertColumnIndexToModel(coluna);
            int ultima = tabela.getModel().getColumnCount() - 1;
            if (!selecionado && ultima >= 1 && (colunaModelo == ultima || colunaModelo == ultima - 1)) {
                Object situacao = tabela.getModel()
                        .getValueAt(tabela.convertRowIndexToModel(linha), ultima - 1);
                c.setForeground(String.valueOf(situacao).startsWith("Acima") ? VERMELHO : VERDE);
            }
            return c;
        }
    }
}
