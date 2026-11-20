
package cabenobolso.persistencia;

import cabenobolso.modelo.Planejamento;
import java.io.IOException;

public interface Persistencia {

    void salvar(Planejamento planejamento)
            throws IOException;

    Planejamento carregar()
            throws IOException, ClassNotFoundException;
}

