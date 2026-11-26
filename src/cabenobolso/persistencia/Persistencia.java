package cabenobolso.persistencia;

import cabenobolso.excecao.ArquivoException;
import cabenobolso.modelo.Planejamento;

import java.util.List;

public interface Persistencia {

    void salvar(Planejamento planejamento) throws ArquivoException;

    Planejamento carregar() throws ArquivoException;

    List<String> listarNomes() throws ArquivoException;

    void salvar(String nome, Planejamento planejamento) throws ArquivoException;

    Planejamento carregar(String nome) throws ArquivoException;
}
