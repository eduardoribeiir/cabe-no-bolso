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

    private List<Despesa> exibidas = new ArrayList<>();
    private boolean atualizando = false;

    PainelDespesas(PlanejamentoControle controle) {
        super(new BorderLayout(8, 8));
        this.controle = controle;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !atualizando) {
                preencherFormularioComSelecao();
            }
        });

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
        JButton btnEditar = new JButton("Salvar alterações da selecionada");
        JButton btnTodos = new JButton("Selecionar todos");
        JButton btnNenhum = new JButton("Limpar seleção");
        btnAdicionar.addActionListener(e -> adicionar());
        btnEditar.addActionListener(e -> editar());
        btnTodos.addActionListener(e -> marcarTodos(true));
        btnNenhum.addActionListener(e -> marcarTodos(false));
        acoes.add(btnAdicionar);
        acoes.add(btnEditar);
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
            tabela.clearSelection();
            txtDescricao.requestFocusInWindow();
        }
    }

    private void editar() {
        int linha = linhaSelecionada();
        if (linha < 0 || linha >= exibidas.size()) {
            Apoio.erro(this, "Selecione uma despesa na tabela para editar.");
            return;
        }
        boolean fixa = cmbTipo.getSelectedIndex() == 0;
        Apoio.exibir(this, controle.editarDespesa(linha, txtDescricao.getText(),
                txtValor.getText(), fixa, codigosMarcados()));
    }

    private void excluir() {
        int linha = linhaSelecionada();
        if (linha < 0 || linha >= exibidas.size()) {
            Apoio.erro(this, "Selecione uma despesa na tabela.");
            return;
        }
        String descricao = exibidas.get(linha).getDescricao();
        if (!Apoio.confirmar(this, "Excluir a despesa \"" + descricao + "\"?")) {
            return;
        }
        // A tabela mostra as despesas na mesma ordem de inclusão do modelo.
        Apoio.exibir(this, controle.excluirDespesa(linha));
    }

    private int linhaSelecionada() {
        int linha = tabela.getSelectedRow();
        return linha < 0 ? -1 : tabela.convertRowIndexToModel(linha);
    }

    private void preencherFormularioComSelecao() {
        int linha = linhaSelecionada();
        if (linha < 0 || linha >= exibidas.size()) {
            return;
        }
        Despesa despesa = exibidas.get(linha);
        txtDescricao.setText(despesa.getDescricao());
        txtValor.setText(Apoio.numero(despesa.getValor()));
        cmbTipo.setSelectedIndex("Fixa".equals(despesa.getTipo()) ? 0 : 1);
        marcarTodos(false);
        for (Participante participante : despesa.getParticipantes()) {
            JCheckBox check = checks.get(participante.getCodigo());
            if (check != null) {
                check.setSelected(true);
            }
        }
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
        atualizando = true;
        try {
            int linhaSelecionada = linhaSelecionada();
            reconstruirChecks(codigosMarcados());

            exibidas = new ArrayList<>(controle.listarDespesas());
            modelo.setRowCount(0);
            int numero = 1;
            for (Despesa d : exibidas) {
                String nomes = d.getParticipantes().stream()
                        .map(Participante::getNome)
                        .collect(Collectors.joining(", "));
                modelo.addRow(new Object[]{
                        numero++, d.getDescricao(), d.getTipo(), Apoio.moeda(d.getValor()),
                        nomes, Apoio.moeda(d.custoTotal())});
            }
            if (linhaSelecionada >= 0 && linhaSelecionada < exibidas.size()) {
                tabela.setRowSelectionInterval(linhaSelecionada, linhaSelecionada);
            }
        } finally {
            atualizando = false;
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
