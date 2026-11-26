import cabenobolso.controle.PlanejamentoControle;
import cabenobolso.controle.Resposta;
import cabenobolso.modelo.ResultadoParticipante;
import cabenobolso.persistencia.PersistenciaArquivo;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TestesBackend {

    private static int falhas;

    private TestesBackend() {
    }

    public static void main(String[] args) throws Exception {
        Path diretorioTemporario = Files.createTempDirectory("cabe-no-bolso-");
        Path arquivo = diretorioTemporario.resolve("planejamento.dat");
        try {
            PlanejamentoControle controle = new PlanejamentoControle(
                    new cabenobolso.modelo.Planejamento(),
                    new PersistenciaArquivo(arquivo.toString()));

            conferir(controle.cadastrarParticipante("1", "Ana", "70,00").isSucesso(),
                    "Cadastrar Ana");
            conferir(controle.cadastrarParticipante("2", "Bruno", "60,00").isSucesso(),
                    "Cadastrar Bruno");
            conferir(controle.cadastrarParticipante("3", "Carla", "70,00").isSucesso(),
                    "Cadastrar Carla");
            conferir(controle.cadastrarDespesa("Transporte", "90,00", true,
                    java.util.List.of(1, 2, 3)).isSucesso(), "Cadastrar despesa fixa");
            conferir(controle.cadastrarDespesa("Almoço", "20,00", false,
                    java.util.List.of(1, 2, 3)).isSucesso(), "Cadastrar despesa por pessoa");

            BigDecimal custoTotal = controle.custoTotal();
            conferir(custoTotal.compareTo(new BigDecimal("150.00")) == 0,
                    "Calcular custo total");
            ResultadoParticipante ana = controle.resultados().get(0);
            conferir(ana.getCusto().compareTo(new BigDecimal("50.00")) == 0,
                    "Ratear despesas por participante");
            conferir(ana.isDentroDoOrcamento(), "Verificar limite de orçamento");

            conferir(controle.alterarNome("1", "Ana Ribeiro").isSucesso(),
                    "Alterar nome");
            conferir("Ana Ribeiro".equals(controle.listarParticipantes().get(0).getNome()),
                    "Atualizar nome no modelo");
            conferir(!controle.alterarNome("1", " ").isSucesso(),
                    "Recusar nome vazio");
            conferir("Ana Ribeiro".equals(controle.listarParticipantes().get(0).getNome()),
                    "Preservar nome após alteração inválida");

            conferir(controle.editarDespesa(0, "Transporte atualizado", "120,00", true,
                    java.util.List.of(1, 2)).isSucesso(), "Editar despesa");
            conferir("Transporte atualizado".equals(controle.listarDespesas().get(0).getDescricao()),
                    "Atualizar dados da despesa");
            conferir(controle.custoTotal().compareTo(new BigDecimal("180.00")) == 0,
                    "Recalcular total após edição");
            conferir(!controle.editarDespesa(0, "Despesa inválida", "-1", true,
                    java.util.List.of(1)).isSucesso(), "Recusar edição inválida");
            conferir("Transporte atualizado".equals(controle.listarDespesas().get(0).getDescricao()),
                    "Preservar despesa após edição inválida");

            conferir(controle.salvar("Férias de janeiro").isSucesso(),
                    "Salvar planejamento com nome");
            conferir(controle.novo().isSucesso(), "Iniciar planejamento para segundo cenário");
            conferir(controle.cadastrarParticipante("9", "Praia", "300,00").isSucesso(),
                    "Cadastrar segundo planejamento");
            conferir(controle.salvar("Passeio para praia").isSucesso(),
                    "Salvar segundo planejamento nomeado");
            conferir(controle.listarPlanejamentos().contains("Férias de janeiro")
                            && controle.listarPlanejamentos().contains("Passeio para praia"),
                    "Listar planejamentos nomeados");
            conferir(controle.carregar("Passeio para praia").isSucesso(),
                    "Carregar segundo planejamento pelo nome");
            conferir(controle.listarParticipantes().size() == 1
                            && controle.listarParticipantes().get(0).getCodigo() == 9,
                    "Restaurar conteúdo do segundo planejamento");
            conferir(controle.carregar("Férias de janeiro").isSucesso(),
                    "Selecionar planejamento salvo");
            conferir("Ana Ribeiro".equals(controle.listarParticipantes().get(0).getNome()),
                    "Restaurar conteúdo do planejamento selecionado");
            conferir(controle.alterarOrcamento("1", "95,00").isSucesso(),
                    "Alterar planejamento antes de substituir");
            conferir(controle.salvar("Férias de janeiro").isSucesso(),
                    "Atualizar planejamento com o mesmo nome");
            conferir(controle.novo().isSucesso(), "Iniciar planejamento antes da recarga");
            conferir(controle.carregar("Férias de janeiro").isSucesso()
                            && controle.listarParticipantes().get(0).getOrcamento()
                            .compareTo(new BigDecimal("95.00")) == 0,
                    "Restaurar versão atualizada do nome existente");

            conferir(!controle.excluirParticipante(1).isSucesso(),
                    "Impedir exclusão de participante vinculado");
            conferir(controle.salvar().isSucesso(), "Salvar arquivo legado");
            conferir(controle.novo().isSucesso(), "Iniciar planejamento vazio");
            conferir(controle.carregar().isSucesso(), "Restaurar planejamento");
            conferir(controle.listarParticipantes().size() == 3,
                    "Restaurar participantes");
            conferir(controle.custoTotal().compareTo(new BigDecimal("180.00")) == 0,
                    "Restaurar valores calculados");

            Files.writeString(arquivo, "arquivo inválido");
            conferir(!controle.carregar().isSucesso(), "Recusar arquivo inválido");
            conferir(controle.listarParticipantes().size() == 3,
                    "Preservar dados após falha ao carregar");
        } finally {
            try (java.util.stream.Stream<Path> caminhos = Files.walk(diretorioTemporario)) {
                for (Path caminho : caminhos.sorted(java.util.Comparator.reverseOrder())
                        .toArray(Path[]::new)) {
                    Files.deleteIfExists(caminho);
                }
            }
        }

        if (falhas > 0) {
            throw new AssertionError(falhas + " teste(s) falharam.");
        }
        System.out.println("Todos os testes do backend passaram.");
    }

    private static void conferir(boolean resultado, String cenario) {
        if (!resultado) {
            falhas++;
            System.err.println("[FALHA] " + cenario);
        } else {
            System.out.println("[OK] " + cenario);
        }
    }
}
