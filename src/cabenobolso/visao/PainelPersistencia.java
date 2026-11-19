package cabenobolso.visao;

import cabenobolso.controle.PlanejamentoControle;
import cabenobolso.controle.Resposta;

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

/** Aba com as ações Salvar, Carregar e Novo planejamento. */
class PainelPersistencia extends JPanel {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final PlanejamentoControle controle;
    private final JTextArea registro = new JTextArea(8, 40);

    PainelPersistencia(PlanejamentoControle controle) {
        super(new BorderLayout(8, 8));
        this.controle = controle;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel ajuda = new JLabel("<html>Salve o planejamento completo em arquivo e carregue-o depois. "
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
        Resposta r = controle.salvar();
        registrar(r.getMensagem());
        if (r.isSucesso()) {
            Apoio.info(this, r.getMensagem());
        } else {
            Apoio.erro(this, r.getMensagem());
        }
    }

    private void carregar() {
        if (!Apoio.confirmar(this, "Carregar substitui o planejamento atual. Continuar?")) {
            return;
        }
        Resposta r = controle.carregar();
        registrar(r.getMensagem());
        if (r.isSucesso()) {
            Apoio.info(this, r.getMensagem());
            return;
        }
        // Falhou: o planejamento atual foi preservado; o usuário pode optar por começar vazio.
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
