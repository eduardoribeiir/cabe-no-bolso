import cabenobolso.excecao.EntradaInvalidaException;
import cabenobolso.extra.ResultadoSimulacao;
import cabenobolso.extra.SimuladorAlternativas;
import cabenobolso.modelo.Despesa;
import cabenobolso.modelo.Planejamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TestesSimulador {

    private TestesSimulador() {
    }

    public static void main(String[] args) throws Exception {
        testarCasoReferencia();
        testarBuscaOtima();
        testarLimites();
        testarRateioDeCentavos();
        System.out.println("Todos os testes do simulador passaram.");
    }

    private static void testarCasoReferencia() throws Exception {
        Planejamento p = new Planejamento();
        p.adicionarParticipante(1, "Ana", valor("70"));
        p.adicionarParticipante(2, "Bruno", valor("60"));
        p.adicionarParticipante(3, "Carla", valor("70"));
        p.adicionarDespesaFixa("Transporte", valor("90"), List.of(1, 2, 3));
        p.adicionarDespesaPorPessoa("Almoço", valor("20"), List.of(1, 2, 3));
        p.adicionarDespesaFixa("Atividade recreativa", valor("30"), List.of(1, 3));
        p.adicionarDespesaPorPessoa("Sobremesa", valor("10"), List.of(1, 2, 3));
        List<Integer> prioridades = Arrays.asList(null, null, 3, 1);
        List<Despesa> despesasOriginais = p.listarDespesas();

        ResultadoSimulacao resultado = SimuladorAlternativas.simular(p, prioridades);
        conferir(resultado.temSolucao(), "Existe alternativa no caso de referência");
        conferir(resultado.getPrioridade() == 3, "Prioridade máxima do caso de referência");
        conferir(resultado.getMantidas().size() == 3
                && resultado.getMantidas().get(2).getDescricao().equals("Atividade recreativa")
                && resultado.getRetiradas().size() == 1
                && resultado.getRetiradas().get(0).getDescricao().equals("Sobremesa"),
                "Manter atividade e retirar sobremesa");
        conferir(igual(resultado.getCustoSimulado(1), "65")
                && igual(resultado.getCustoSimulado(2), "50")
                && igual(resultado.getCustoSimulado(3), "65")
                && igual(resultado.getTotalSimulado(), "180"),
                "Totais após simulação");
        conferir(igual(resultado.getCustoOriginal(1), "75")
                && igual(resultado.getCustoOriginal(2), "60")
                && igual(resultado.getCustoOriginal(3), "75")
                && igual(resultado.getTotalOriginal(), "210"),
                "Totais originais na comparação");
        conferir(p.listarDespesas().equals(despesasOriginais)
                && igual(p.custoTotal(), "210")
                && igual(p.calcularResultados().get(0).getCusto(), "75"),
                "Planejamento original preservado");

        p.alterarOrcamento(1, valor("40"));
        ResultadoSimulacao impossivel = SimuladorAlternativas.simular(p, prioridades);
        conferir(!impossivel.temSolucao()
                && igual(impossivel.getCustoSimulado(1), "50")
                && igual(impossivel.getCustoSimulado(1).subtract(
                        p.listarParticipantes().get(0).getOrcamento()), "10")
                && igual(impossivel.getTotalSimulado(), "150"),
                "Essenciais acima do orçamento indicam ausência de solução e excesso");
        conferir(p.listarDespesas().equals(despesasOriginais) && igual(p.custoTotal(), "210"),
                "Ausência de solução não modifica o planejamento");
    }

    private static void testarBuscaOtima() throws Exception {
        Planejamento p = new Planejamento();
        p.adicionarParticipante(1, "Pessoa", valor("100"));
        p.adicionarDespesaFixa("Maior prioridade isolada", valor("70"), List.of(1));
        p.adicionarDespesaFixa("Opção A", valor("50"), List.of(1));
        p.adicionarDespesaFixa("Opção B", valor("50"), List.of(1));
        ResultadoSimulacao resultado = SimuladorAlternativas.simular(p, List.of(6, 4, 4));
        conferir(resultado.temSolucao() && resultado.getPrioridade() == 8
                && igual(resultado.getTotalSimulado(), "100")
                && resultado.getRetiradas().get(0).getDescricao().equals("Maior prioridade isolada"),
                "Busca encontra melhor combinação e aceita custo igual ao orçamento");
    }

    private static void testarLimites() throws Exception {
        Planejamento p = new Planejamento();
        p.adicionarParticipante(1, "Pessoa", valor("20"));
        for (int i = 0; i < 11; i++) {
            p.adicionarDespesaFixa("Despesa " + i, valor("1"), List.of(1));
        }
        List<Integer> prioridades = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            prioridades.add(Integer.MAX_VALUE);
        }
        prioridades.add(null);
        ResultadoSimulacao resultado = SimuladorAlternativas.simular(p, prioridades);
        conferir(resultado.temSolucao() && resultado.getPrioridade() == 10L * Integer.MAX_VALUE
                && resultado.getMantidas().size() == 11,
                "Aceita dez opcionais e soma prioridades sem overflow de int");

        prioridades.set(10, 1);
        conferirInvalido(p, prioridades, "Recusa mais de dez opcionais");
        prioridades.set(10, null);
        prioridades.set(0, 0);
        conferirInvalido(p, prioridades, "Recusa prioridade não positiva");
        conferirInvalido(p, List.of(1), "Recusa classificação incompleta");
        conferir(p.listarDespesas().size() == 11 && igual(p.custoTotal(), "11"),
                "Entradas inválidas não alteram o planejamento");
    }

    private static void testarRateioDeCentavos() throws Exception {
        Planejamento p = new Planejamento();
        p.adicionarParticipante(3, "C", valor("33.33"));
        p.adicionarParticipante(1, "A", valor("33.34"));
        p.adicionarParticipante(2, "B", valor("33.33"));
        p.adicionarDespesaFixa("Rateio", valor("100"), List.of(3, 1, 2));
        ResultadoSimulacao resultado = SimuladorAlternativas.simular(p, List.of(1));
        conferir(resultado.temSolucao() && resultado.getMantidas().size() == 1
                && igual(resultado.getCustoSimulado(1), "33.34")
                && igual(resultado.getCustoSimulado(2), "33.33")
                && igual(resultado.getCustoSimulado(3), "33.33"),
                "Simulação respeita o rateio dos centavos");
    }

    private static void conferirInvalido(Planejamento p, List<Integer> prioridades, String cenario)
            throws Exception {
        try {
            SimuladorAlternativas.simular(p, prioridades);
            throw new AssertionError(cenario);
        } catch (EntradaInvalidaException esperada) {
            System.out.println("[OK] " + cenario);
        }
    }

    private static BigDecimal valor(String valor) {
        return new BigDecimal(valor);
    }

    private static boolean igual(BigDecimal obtido, String esperado) {
        return obtido.compareTo(valor(esperado)) == 0;
    }

    private static void conferir(boolean condicao, String cenario) {
        if (!condicao) {
            throw new AssertionError(cenario);
        }
        System.out.println("[OK] " + cenario);
    }
}
