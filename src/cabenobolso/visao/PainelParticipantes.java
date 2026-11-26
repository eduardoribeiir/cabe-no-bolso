package cabenobolso.visao;

import cabenobolso.controle.PlanejamentoControle;
import cabenobolso.modelo.Participante;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

class PainelParticipantes extends JPanel {

    private final PlanejamentoControle controle;

    private final JTextField txtCodigo = new JTextField(6);
    private final JTextField txtNome = new JTextField(20);
    private final JTextField txtOrcamento = new JTextField(10);

    private final DefaultTableModel modelo =
            Apoio.modeloSomenteLeitura(new Object[]{"Código", "Nome", "Orçamento"});
    private final JTable tabela = new JTable(modelo);

    /** Participantes na mesma ordem das linhas da tabela. */
    private List<Participante> exibidos = new ArrayList<>();
    private boolean atualizando = false;

    PainelParticipantes(PlanejamentoControle controle) {
        super(new BorderLayout(8, 8));
        this.controle = controle;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !atualizando) {
                preencherCamposComSelecao();
            }
        });

        add(criarFormulario(), BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(criarBotoes(), BorderLayout.SOUTH);

        controle.adicionarOuvinte(this::atualizar);
        atualizar();
    }

    private JPanel criarFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Dados do participante"));
        Apoio.adicionar(p, new JLabel("Código:"), 0, 0, 1, 0);
        Apoio.adicionar(p, txtCodigo, 1, 0, 1, 0);
        Apoio.adicionar(p, new JLabel("Nome:"), 2, 0, 1, 0);
        Apoio.adicionar(p, txtNome, 3, 0, 1, 1);
        Apoio.adicionar(p, new JLabel("Orçamento (R$):"), 4, 0, 1, 0);
        Apoio.adicionar(p, txtOrcamento, 5, 0, 1, 0);
        return p;
    }

    private JPanel criarBotoes() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnAlterar = new JButton("Alterar orçamento");
        JButton btnNome = new JButton("Alterar nome");
        JButton btnExcluir = new JButton("Excluir selecionado");
        JButton btnLimpar = new JButton("Limpar campos");
        btnAdicionar.addActionListener(e -> adicionar());
        btnAlterar.addActionListener(e -> alterarOrcamento());
        btnNome.addActionListener(e -> alterarNome());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparCampos());
        p.add(btnAdicionar);
        p.add(btnAlterar);
        p.add(btnNome);
        p.add(btnExcluir);
        p.add(btnLimpar);
        return p;
    }

    private void adicionar() {
        if (Apoio.exibir(this, controle.cadastrarParticipante(
                txtCodigo.getText(), txtNome.getText(), txtOrcamento.getText()))) {
            limparCampos();
            txtCodigo.requestFocusInWindow();
        }
    }

    private void alterarOrcamento() {
        if (Apoio.exibir(this, controle.alterarOrcamento(txtCodigo.getText(), txtOrcamento.getText()))) {
            limparCampos();
        }
    }

    private void alterarNome() {
        if (Apoio.exibir(this, controle.alterarNome(txtCodigo.getText(), txtNome.getText()))) {
            limparCampos();
        }
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();
        if (linha < 0 || linha >= exibidos.size()) {
            Apoio.erro(this, "Selecione um participante na tabela.");
            return;
        }
        Participante p = exibidos.get(linha);
        if (!Apoio.confirmar(this, "Excluir o participante " + p.getNome() + "?")) {
            return;
        }
        if (Apoio.exibir(this, controle.excluirParticipante(p.getCodigo()))) {
            limparCampos();
        }
    }

    private void limparCampos() {
        txtCodigo.setText("");
        txtNome.setText("");
        txtOrcamento.setText("");
        tabela.clearSelection();
    }

    private void preencherCamposComSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0 || linha >= exibidos.size()) {
            return;
        }
        Participante p = exibidos.get(linha);
        txtCodigo.setText(String.valueOf(p.getCodigo()));
        txtNome.setText(p.getNome());
        txtOrcamento.setText(Apoio.numero(p.getOrcamento()));
    }

    private void atualizar() {
        atualizando = true;
        try {
            modelo.setRowCount(0);
            exibidos = new ArrayList<>(controle.listarParticipantes());
            for (Participante p : exibidos) {
                modelo.addRow(new Object[]{p.getCodigo(), p.getNome(), Apoio.moeda(p.getOrcamento())});
            }
        } finally {
            atualizando = false;
        }
    }
}
