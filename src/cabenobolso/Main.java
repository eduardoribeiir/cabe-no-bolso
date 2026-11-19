package cabenobolso;

import cabenobolso.controle.PlanejamentoControle;
import cabenobolso.modelo.Planejamento;
import cabenobolso.persistencia.Persistencia;
import cabenobolso.persistencia.PersistenciaArquivo;
import cabenobolso.visao.JanelaPrincipal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada da aplicação "Cabe no bolso".
 * Monta modelo, persistência, controle e visão na thread de eventos do Swing.
 * A aplicação sempre inicia com um planejamento vazio; use Carregar para recuperar o arquivo.
 */
public class Main {

    private static final String ARQUIVO_PADRAO = "planejamento.dat";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::iniciar);
    }

    private static void iniciar() {
        try {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorada) {
                // Sem o visual do sistema, o Swing usa o visual padrão.
            }
            Planejamento modelo = new Planejamento();
            Persistencia persistencia = new PersistenciaArquivo(ARQUIVO_PADRAO);
            PlanejamentoControle controle = new PlanejamentoControle(modelo, persistencia);
            new JanelaPrincipal(controle).setVisible(true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                    "Não foi possível iniciar a aplicação:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
