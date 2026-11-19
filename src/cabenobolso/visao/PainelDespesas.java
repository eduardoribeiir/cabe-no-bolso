package cabenobolso.visao;

import cabenobolso.controle.PlanejamentoControle;
import cabenobolso.modelo.Despesa;
import cabenobolso.modelo.Participante;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Aba de cadastro e exclusão de despesas, com seleção dos participantes por caixas de marcação. */
class PainelDespesas extends JPanel {

    private final PlanejamentoControle controle;

    private final JTextField txtDescricao = new JTextField(20);
    private final JTextField txtValor = new JTextField(10);
    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"Fixa", "Por pessoa"});
    private final JPanel painelChecks = new JPanel();
    private final Map<Integer, JCheckBox> checks = new LinkedHashMap<>();

    private final DefaultTableModel modelo = Apoio.modeloSomenteLeitura(
            new Object[]{"#", "Descrição", "Tipo", "Valor", "Participantes", "Custo total"});
    private final JTable tabela = new JTable(modelo);

    PainelDespesas(PlanejamentoControle controle) {
        super(new BorderLayout(8, 8));
        this.controle = controle;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(criarFormulario(), BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(criarBotoesInferiores(), BorderLayout.SOUTH);

        controle.adicionarOuvinte(this::atualizar);
        atualizar();
    }

    private JPanel criarFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Nova despesa"));

        Apoio.adicionar(p, new JLabel("Descrição:"), 0, 0, 1, 0);
        Apoio.adicionar(p, txtDescricao, 1, 0, 1, 1);
        Apoio.adicionar(p, new JLabel("Valor (R$):"), 2, 0, 1, 0);
        Apoio.adicionar(p, txtValor, 3, 0, 1, 0.3);
        Apoio.adicionar(p, new JLabel("Tipo:"), 4, 0, 1, 0);
        Apoio.adicionar(p, cmbTipo, 5, 0, 1, 0);

        painelChecks.setLayout(new BoxLayout(painelChecks, BoxLayout.Y_AXIS));
        JScrollPane rolagem = new JScrollPane(painelChecks);
        rolagem.setPreferredSize(new Dimension(300, 110));
        Apoio.adicionar(p, new JLabel("Participantes:"), 0, 1, 1, 0);
        Apoio.adicionar(p, rolagem, 1, 1, 5, 1);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton btnAdicionar = new JButton("Adicionar despesa");
        JButton btnTodos = new JButton("Selecionar todos");
        JButton btnNenhum = new JButton("Limpar seleção");
        btnAdicionar.addActionListener(e -> adicionar());
        btnTodos.addActionListener(e -> marcarTodos(true));
        btnNenhum.addActionListener(e -> marcarTodos(false));
        acoes.add(btnAdicionar);
        acoes.add(btnTodos);
        acoes.add(btnNenhum);
        Apoio.adicionar(p, acoes, 1, 2, 5, 1);

        Apoio.adicionar(p, new JLabel("<html>Fixa: o valor é dividido entre os selecionados. "
                + "Por pessoa: o valor é cobrado de cada selecionado.</html>"), 0, 3, 6, 1);
        return p;
    }

    private JPanel criarBotoesInferiores() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnExcluir = new JButton("Excluir despesa selecionada");
        btnExcluir.addActionListener(e -> excluir());
        p.add(btnExcluir);
        return p;
    }

    private void adicionar() {
        boolean fixa = cmbTipo.getSelectedIndex() == 0;
        if (Apoio.exibir(this, controle.cadastrarDespesa(
                txtDescricao.getText(), txtValor.getText(), fixa, codigosMarcados()))) {
            txtDescricao.setText("");
            txtValor.setText("");
            marcarTodos(false);
            txtDescricao.requestFocusInWindow();
        }
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            Apoio.erro(this, "Selecione uma despesa na tabela.");
            return;
        }
        String descricao = String.valueOf(modelo.getValueAt(linha, 1));
        if (!Apoio.confirmar(this, "Excluir a despesa \"" + descricao + "\"?")) {
            return;
        }
        // A tabela mostra as despesas na mesma ordem de inclusão do modelo.
        Apoio.exibir(this, controle.excluirDespesa(linha));
    }

    private void marcarTodos(boolean marcar) {
        for (JCheckBox cb : checks.values()) {
            cb.setSelected(marcar);
        }
    }

    private List<Integer> codigosMarcados() {
        List<Integer> codigos = new ArrayList<>();
        for (Map.Entry<Integer, JCheckBox> e : checks.entrySet()) {
            if (e.getValue().isSelected()) {
                codigos.add(e.getKey());
            }
        }
        return codigos;
    }

    private void atualizar() {
        reconstruirChecks(codigosMarcados());

        modelo.setRowCount(0);
        int numero = 1;
        for (Despesa d : controle.listarDespesas()) {
            String nomes = d.getParticipantes().stream()
                    .map(Participante::getNome)
                    .collect(Collectors.joining(", "));
            modelo.addRow(new Object[]{
                    numero++, d.getDescricao(), d.getTipo(), Apoio.moeda(d.getValor()),
                    nomes, Apoio.moeda(d.custoTotal())});
        }
    }

    private void reconstruirChecks(List<Integer> marcados) {
        checks.clear();
        painelChecks.removeAll();
        List<Participante> participantes = controle.listarParticipantes();
        if (participantes.isEmpty()) {
            painelChecks.add(new JLabel("Cadastre participantes na aba Participantes."));
        }
        for (Participante p : participantes) {
            JCheckBox cb = new JCheckBox(p.getCodigo() + " - " + p.getNome(),
                    marcados.contains(p.getCodigo()));
            checks.put(p.getCodigo(), cb);
            painelChecks.add(cb);
        }
        painelChecks.revalidate();
        painelChecks.repaint();
    }
}
