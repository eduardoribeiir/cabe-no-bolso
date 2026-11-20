
package cabenobolso.controle;

import cabenobolso.modelo.*;
import cabenobolso.persistencia.Persistencia;
import cabenobolso.excecao.DadosInvalidosException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlanejamentoController {

    private Planejamento planejamento;
    private final Persistencia persistencia;

    public PlanejamentoController(Persistencia persistencia) {
        this.planejamento = new Planejamento();
        this.persistencia = persistencia;
    }

    public Planejamento getPlanejamento() {
        return planejamento;
    }

    public void cadastrarParticipante(
            int codigo, String nome, long orcamento)
            throws DadosInvalidosException {

        Participante participante =
            new Participante(codigo, nome, orcamento);

        planejamento.adicionarParticipante(participante);
    }

  
    public void cadastrarDespesaFixa(
            String descricao, long valor,
            List<Participante> participantes)
            throws DadosInvalidosException {

        List<Participante> selecionados =
            validarParticipantes(participantes);

        Despesa despesa = new DespesaFixa(
            descricao, valor, selecionados
        );

        planejamento.adicionarDespesa(despesa);
    }

   
    public void cadastrarDespesaPorPessoa(
            String descricao, long valor,
            List<Participante> participantes)
            throws DadosInvalidosException {

        List<Participante> selecionados =
            validarParticipantes(participantes);

        Despesa despesa = new DespesaPorPessoa(
            descricao, valor, selecionados
        );

        planejamento.adicionarDespesa(despesa);
    }

  
    private List<Participante> validarParticipantes(
            List<Participante> selecionados)
            throws DadosInvalidosException {

        if (selecionados == null || selecionados.isEmpty()) {
            throw new DadosInvalidosException(
                "Selecione pelo menos um participante.");
        }

        List<Participante> resultado = new ArrayList<>();
        Set<Integer> codigos = new HashSet<>();

        for (Participante selecionado : selecionados) {

            if (selecionado == null ||
                !codigos.add(selecionado.getCodigo())) {

                throw new DadosInvalidosException(
                    "Participante inválido ou repetido.");
            }

            Participante cadastrado = localizarParticipante(
                selecionado.getCodigo()
            );

            if (cadastrado == null) {
                throw new DadosInvalidosException(
                    "Participante não cadastrado.");
            }

            resultado.add(cadastrado);
        }

        return resultado;
    }

    
    private Participante localizarParticipante(int codigo) {

        for (Participante p : planejamento.getParticipantes()) {

            if (p.getCodigo() == codigo) {
                return p;
            }
        }

        return null;
    }

   
    public void alterarNome(int codigo, String nome)
            throws DadosInvalidosException {

        Participante p = localizarParticipante(codigo);

        if (p == null) {
            throw new DadosInvalidosException(
                "Participante não encontrado.");
        }

        p.setNome(nome);
    }

    
    public void alterarOrcamento(int codigo, long orcamento)
            throws DadosInvalidosException {

        Participante p = localizarParticipante(codigo);

        if (p == null) {
            throw new DadosInvalidosException(
                "Participante não encontrado.");
        }

        p.setOrcamento(orcamento);
    }

    
    public void excluirDespesa(Despesa despesa) {
        planejamento.excluirDespesa(despesa);
    }

    public void salvar() throws IOException {
        persistencia.salvar(planejamento);
    }

    
    public void carregar()
            throws IOException, ClassNotFoundException {

        Planejamento carregado = persistencia.carregar();

        if (carregado == null) {
            throw new IOException(
                "Arquivo não contém um planejamento válido.");
        }

        planejamento = carregado;
    }
}

