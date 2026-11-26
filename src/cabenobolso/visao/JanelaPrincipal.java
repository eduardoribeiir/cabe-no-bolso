package cabenobolso.visao;

import cabenobolso.controle.PlanejamentoControle;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JanelaPrincipal extends JFrame {

    public JanelaPrincipal(PlanejamentoControle controle) {
        super("Cabe no bolso – Planejador de despesas compartilhadas");

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Participantes", new PainelParticipantes(controle));
        abas.addTab("Despesas", new PainelDespesas(controle));
        abas.addTab("Resumo", new PainelResumo(controle));
        abas.addTab("Persistência", new PainelPersistencia(controle));
        add(abas);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (Apoio.confirmar(JanelaPrincipal.this,
                        "Sair do programa? Dados não salvos serão perdidos.")) {
                    dispose();
                    System.exit(0);
                }
            }
        });

        setMinimumSize(new Dimension(820, 560));
        setSize(980, 660);
        setLocationRelativeTo(null);
    }
}
