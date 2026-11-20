
package cabenobolso.persistencia;

import cabenobolso.modelo.Planejamento;

import java.io.*;

public class ArquivoPersistencia implements Persistencia {

    private final String caminho;

    public ArquivoPersistencia(String caminho) {
        this.caminho = caminho;
    }

    @Override
    public void salvar(Planejamento planejamento)
            throws IOException {

        try (ObjectOutputStream saida =
                new ObjectOutputStream(
                    new FileOutputStream(caminho))) {

            saida.writeObject(planejamento);
        }
    }

    @Override
    public Planejamento carregar()
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream entrada =
                new ObjectInputStream(
                    new FileInputStream(caminho))) {

            return (Planejamento) entrada.readObject();
        }
    }
}

