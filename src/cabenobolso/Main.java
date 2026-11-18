package cabenobolso;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Ponto de entrada da aplicação "Cabe no bolso".
 *
 * Esta classe é um esqueleto: quando a visão e o controle estiverem prontos,
 * ela deve apenas instanciar o modelo, o controle e a janela principal.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // TODO: Planejamento modelo = new Planejamento();
            // TODO: Persistencia persistencia = new PersistenciaArquivo("planejamento.dat");
            // TODO: PlanejamentoControle controle = new PlanejamentoControle(modelo, persistencia);
            // TODO: new JanelaPrincipal(controle).setVisible(true);

            JFrame janela = new JFrame("Cabe no bolso");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.add(new JLabel("Estrutura MVC pronta. Implemente as telas.", SwingConstants.CENTER));
            janela.setSize(480, 200);
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }
}
