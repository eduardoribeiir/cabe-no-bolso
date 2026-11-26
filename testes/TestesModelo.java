import cabenobolso.controle.PlanejamentoController;
import cabenobolso.excecao.DadosInvalidosException;
import cabenobolso.modelo.*;
import cabenobolso.persistencia.ArquivoPersistencia;

import java.util.*;

public class TestesModelo {

    static int falhas = 0;

    static void conferir(String nome, long obtido, long esperado) {
        boolean ok = obtido == esperado;
        if (!ok) falhas++;
        System.out.println((ok ? "[OK]    " : "[FALHA] ") + nome
            + " -> obtido=" + obtido + " esperado=" + esperado);
    }

    static void conferir(String nome, boolean obtido, boolean esperado) {
        boolean ok = obtido == esperado;
        if (!ok) falhas++;
        System.out.println((ok ? "[OK]    " : "[FALHA] ") + nome
            + " -> obtido=" + obtido + " esperado=" + esperado);
    }

    static PlanejamentoController casoReferencia() throws Exception {
        PlanejamentoController c = new PlanejamentoController(new ArquivoPersistencia("tmp.dat"));
        c.cadastrarParticipante(1, "Ana", 7000);
        c.cadastrarParticipante(2, "Bruno", 6000);
        c.cadastrarParticipante(3, "Carla", 7000);
        List<Participante> ps = c.getPlanejamento().getParticipantes();
        List<Participante> ac = List.of(ps.get(0), ps.get(2));
        c.cadastrarDespesaFixa("Transporte", 9000, ps);
        c.cadastrarDespesaPorPessoa("Almoço", 2000, ps);
        c.cadastrarDespesaFixa("Atividade recreativa", 3000, ac);
        c.cadastrarDespesaPorPessoa("Sobremesa", 1000, ps);
        return c;
    }

    static Despesa achar(Planejamento p, String desc) {
        for (Despesa d : p.getDespesas()) if (d.getDescricao().equals(desc)) return d;
        return null;
    }

    public static void main(String[] args) throws Exception {

        System.out.println("== Caso principal (seção 4) ==");
        PlanejamentoController c = casoReferencia();
        Planejamento p = c.getPlanejamento();
        conferir("Ana total", p.calcularTotalParticipante(1), 7500);
        conferir("Bruno total", p.calcularTotalParticipante(2), 6000);
        conferir("Carla total", p.calcularTotalParticipante(3), 7500);
        conferir("Custo total", p.calcularCustoTotal(), 21000);
        conferir("Ana excedido", p.calcularValorExcedido(1), 500);
        conferir("Bruno dentro", p.estaDentroDoOrcamento(2), true);
        conferir("Carla excedido", p.calcularValorExcedido(3), 500);

        System.out.println("== Teste 1: por pessoa 20,00 x 3 ==");
        PlanejamentoController c1 = new PlanejamentoController(new ArquivoPersistencia("tmp.dat"));
        c1.cadastrarParticipante(1, "Ana", 10000);
        c1.cadastrarParticipante(2, "Bruno", 10000);
        c1.cadastrarParticipante(3, "Carla", 10000);
        c1.cadastrarDespesaPorPessoa("Almoço", 2000, c1.getPlanejamento().getParticipantes());
        conferir("Total evento", c1.getPlanejamento().calcularCustoTotal(), 6000);
        for (int i = 1; i <= 3; i++)
            conferir("Participante " + i, c1.getPlanejamento().calcularTotalParticipante(i), 2000);

        System.out.println("== Teste 2: fixa 100,00 / 3 ==");
        PlanejamentoController c2 = new PlanejamentoController(new ArquivoPersistencia("tmp.dat"));
        c2.cadastrarParticipante(3, "C", 10000);
        c2.cadastrarParticipante(1, "A", 10000);
        c2.cadastrarParticipante(2, "B", 10000);
        c2.cadastrarDespesaFixa("Fixa", 10000, c2.getPlanejamento().getParticipantes());
        conferir("Cód 1", c2.getPlanejamento().calcularTotalParticipante(1), 3334);
        conferir("Cód 2", c2.getPlanejamento().calcularTotalParticipante(2), 3333);
        conferir("Cód 3", c2.getPlanejamento().calcularTotalParticipante(3), 3333);
        conferir("Soma = total", c2.getPlanejamento().calcularCustoTotal(), 10000);

        System.out.println("== Teste 3: orçamento da Ana -> 80,00 ==");
        c.alterarOrcamento(1, 8000);
        conferir("Ana dentro", p.estaDentroDoOrcamento(1), true);
        conferir("Ana excedido", p.calcularValorExcedido(1), 0);
        conferir("Ana total continua 75,00", p.calcularTotalParticipante(1), 7500);

        System.out.println("== Teste 4: excluir Atividade recreativa ==");
        PlanejamentoController c4 = casoReferencia();
        c4.excluirDespesa(achar(c4.getPlanejamento(), "Atividade recreativa"));
        conferir("Total evento", c4.getPlanejamento().calcularCustoTotal(), 18000);
        for (int i = 1; i <= 3; i++)
            conferir("Participante " + i, c4.getPlanejamento().calcularTotalParticipante(i), 6000);

        System.out.println("== Teste 5: excluir participante ==");
        PlanejamentoController c5 = casoReferencia();
        c5.cadastrarParticipante(4, "Diego", 5000);
        try { c5.excluirParticipante(1); conferir("Vinculado barrado", false, true); }
        catch (DadosInvalidosException e) { conferir("Vinculado barrado", true, true); System.out.println("        msg: " + e.getMessage()); }
        conferir("Ana continua cadastrada", c5.getPlanejamento().getParticipantes().size(), 4);
        c5.excluirParticipante(4);
        conferir("Sem vínculo excluído", c5.getPlanejamento().getParticipantes().size(), 3);
        try { c5.excluirParticipante(99); conferir("Inexistente barrado", false, true); }
        catch (DadosInvalidosException e) { conferir("Inexistente barrado", true, true); }

        System.out.println("== Teste 6: editar Almoço 20,00 -> 25,00 ==");
        PlanejamentoController c6 = casoReferencia();
        Planejamento p6 = c6.getPlanejamento();
        c6.editarDespesaPorPessoa(achar(p6, "Almoço"), "Almoço", 2500, p6.getParticipantes());
        conferir("Total evento", p6.calcularCustoTotal(), 22500);
        conferir("Ana", p6.calcularTotalParticipante(1), 8000);
        conferir("Bruno", p6.calcularTotalParticipante(2), 6500);
        conferir("Bruno excedido", p6.calcularValorExcedido(2), 500);
        conferir("Ana dentro (orçamento 70)", p6.estaDentroDoOrcamento(1), false);
        conferir("Qtde de despesas continua 4", p6.getDespesas().size(), 4);
        try { c6.editarDespesaFixa(achar(p6, "Almoço"), "X", 0, p6.getParticipantes());
              conferir("Edição inválida barrada", false, true); }
        catch (DadosInvalidosException e) { conferir("Edição inválida barrada", true, true); }
        conferir("Despesa original preservada", achar(p6, "Almoço").getValor(), 2500);

        System.out.println();
        System.out.println(falhas == 0 ? "TODOS OS TESTES PASSARAM" : ("FALHAS: " + falhas));
        System.exit(falhas == 0 ? 0 : 1);
    }
}
