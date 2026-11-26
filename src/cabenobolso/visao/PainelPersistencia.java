package cabenobolso.visao;

import cabenobolso.controle.PlanejamentoControle;
import cabenobolso.controle.Resposta;
import cabenobolso.excecao.ArquivoException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

class PainelPersistencia extends JPanel {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final PlanejamentoControle controle;
    private final JTextArea registro = new JTextArea(8, 40);

    PainelPersistencia(PlanejamentoControle controle) {
        super(new BorderLayout(8, 8));
        this.controle = controle;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel ajuda = new JLabel("<html>Salve cada planejamento com um nome para manter várias versões "
                + "e escolha qual recuperar. "
                + "<b>Novo</b> descarta os dados atuais e inicia um planejamento vazio.</html>");

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btnSalvar = new JButton("Salvar planejamento");
        JButton btnCarregar = new JButton("Carregar planejamento");
        JButton btnNovo = new JButton("Novo planejamento");
        btnSalvar.addActionListener(e -> salvar());
        btnCarregar.addActionListener(e -> carregar());
        btnNovo.addActionListener(e -> novo());
        botoes.add(btnSalvar);
        botoes.add(btnCarregar);
        botoes.add(btnNovo);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(ajuda, BorderLayout.NORTH);
        topo.add(botoes, BorderLayout.CENTER);
        add(topo, BorderLayout.NORTH);

        registro.setEditable(false);
        JScrollPane rolagem = new JScrollPane(registro);
        rolagem.setBorder(BorderFactory.createTitledBorder("Registro de operações"));
        add(rolagem, BorderLayout.CENTER);
    }

    private void salvar() {
        String nome = JOptionPane.showInputDialog(this,
                "Nome do planejamento (ex.: Férias de janeiro):",
                "Salvar planejamento", JOptionPane.QUESTION_MESSAGE);
        if (nome == null) {
            return;
        }
        nome = nome.trim();
        if (nome.isEmpty()) {
            Apoio.erro(this, "Informe um nome para o planejamento.");
            return;
        }

        try {
            for (String existente : controle.listarPlanejamentos()) {
                if (existente.equalsIgnoreCase(nome)) {
                    if (!Apoio.confirmar(this, "Já existe um planejamento chamado \""
                            + existente + "\". Deseja substituí-lo?")) {
                        return;
                    }
                    break;
                }
            }
        } catch (ArquivoException e) {
            registrar(e.getMessage());
            Apoio.erro(this, e.getMessage());
            return;
        }

        Resposta r = controle.salvar(nome);
        registrar(r.getMensagem());
        if (r.isSucesso()) {
            Apoio.info(this, r.getMensagem());
        } else {
            Apoio.erro(this, r.getMensagem());
        }
    }

    private void carregar() {
        List<String> nomes;
        try {
            nomes = controle.listarPlanejamentos();
        } catch (ArquivoException e) {
            registrar(e.getMessage());
            Apoio.erro(this, e.getMessage());
            return;
        }
        if (nomes.isEmpty()) {
            Apoio.info(this, "Ainda não há planejamentos salvos.");
            return;
        }
        Object selecionado = JOptionPane.showInputDialog(this,
                "Escolha qual planejamento deseja carregar:",
                "Carregar planejamento", JOptionPane.QUESTION_MESSAGE,
                null, nomes.toArray(), nomes.get(0));
        if (selecionado == null) {
            return;
        }
        if (!Apoio.confirmar(this, "Carregar substitui o planejamento atual. Continuar?")) {
            return;
        }
        Resposta r = controle.carregar(selecionado.toString());
        registrar(r.getMensagem());
        if (r.isSucesso()) {
            Apoio.info(this, r.getMensagem());
            return;
        }
        int opcao = JOptionPane.showConfirmDialog(this,
                r.getMensagem() + "\n\nO planejamento atual foi mantido.\n"
                        + "Deseja iniciar um planejamento vazio?",
                "Falha ao carregar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcao == JOptionPane.YES_OPTION) {
            registrar(controle.novo().getMensagem());
        }
    }

    private void novo() {
        if (!Apoio.confirmar(this, "Descartar os dados atuais e iniciar um planejamento vazio?")) {
            return;
        }
        registrar(controle.novo().getMensagem());
    }

    private void registrar(String mensagem) {
        registro.append(LocalTime.now().format(HORA) + "  " + mensagem + "\n");
    }
}
