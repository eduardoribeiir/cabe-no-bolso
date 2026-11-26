package cabenobolso.persistencia;

import cabenobolso.excecao.ArquivoException;
import cabenobolso.modelo.Planejamento;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PersistenciaArquivo implements Persistencia {

    private static final String NOME_ANTIGO = "Planejamento antigo (planejamento.dat)";

    private final Path caminho;
    private final Path pastaPlanejamentos;

    public PersistenciaArquivo(String caminho) {
        this.caminho = Path.of(Objects.requireNonNull(caminho, "caminho"));
        Path diretorio = this.caminho.toAbsolutePath().getParent();
        this.pastaPlanejamentos = diretorio.resolve("planejamentos");
    }

    @Override
    public void salvar(Planejamento planejamento) throws ArquivoException {
        if (planejamento == null) {
            throw new ArquivoException("Não há planejamento para salvar.", null);
        }
        try (FileOutputStream arquivo = new FileOutputStream(caminho.toFile());
             ObjectOutputStream saida = new ObjectOutputStream(arquivo)) {
            saida.writeObject(planejamento);
        } catch (IOException e) {
            throw new ArquivoException("Não foi possível salvar o planejamento: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public Planejamento carregar() throws ArquivoException {
        try (FileInputStream arquivo = new FileInputStream(caminho.toFile());
             ObjectInputStream entrada = new ObjectInputStream(arquivo)) {
            Object objeto = entrada.readObject();
            if (!(objeto instanceof Planejamento)) {
                throw new ArquivoException(
                        "O arquivo não contém um planejamento válido.", null);
            }
            return (Planejamento) objeto;
        } catch (ArquivoException e) {
            throw e;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            String detalhe = e instanceof StreamCorruptedException
                    ? "formato inválido" : e.getMessage();
            throw new ArquivoException(
                    "Não foi possível carregar o planejamento: " + detalhe, e);
        }
    }

    @Override
    public List<String> listarNomes() throws ArquivoException {
        List<String> nomes = new ArrayList<>();
        try {
            if (Files.isDirectory(pastaPlanejamentos)) {
                try (java.util.stream.Stream<Path> arquivos = Files.list(pastaPlanejamentos)) {
                    List<Path> ordenados = new ArrayList<>();
                    arquivos.filter(arquivo -> arquivo.getFileName().toString().endsWith(".dat"))
                            .forEach(ordenados::add);
                    ordenados.sort(Comparator.comparing(
                            arquivo -> arquivo.getFileName().toString()));
                    for (Path arquivo : ordenados) {
                        nomes.add(lerPlanejamentoSalvo(arquivo).nome);
                    }
                }
            }
            if (Files.isRegularFile(caminho)) {
                nomes.add(NOME_ANTIGO);
            }
            nomes.sort(String.CASE_INSENSITIVE_ORDER);
            return nomes;
        } catch (IOException e) {
            throw new ArquivoException("Não foi possível listar os planejamentos: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public void salvar(String nome, Planejamento planejamento) throws ArquivoException {
        String nomeValido = validarNome(nome);
        if (planejamento == null) {
            throw new ArquivoException("Não há planejamento para salvar.", null);
        }

        Path arquivo = caminhoPlanejamento(nomeValido);
        Path temporario = null;
        try {
            Files.createDirectories(pastaPlanejamentos);
            temporario = Files.createTempFile(pastaPlanejamentos, "planejamento-", ".tmp");
            try (FileOutputStream stream = new FileOutputStream(temporario.toFile());
                 ObjectOutputStream saida = new ObjectOutputStream(stream)) {
                saida.writeObject(new PlanejamentoSalvo(nomeValido, planejamento));
            }
            try {
                Files.move(temporario, arquivo, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            if (temporario != null) {
                try {
                    Files.deleteIfExists(temporario);
                } catch (IOException erroLimpeza) {
                    e.addSuppressed(erroLimpeza);
                }
            }
            throw new ArquivoException("Não foi possível salvar o planejamento \""
                    + nomeValido + "\": " + e.getMessage(), e);
        }
    }

    @Override
    public Planejamento carregar(String nome) throws ArquivoException {
        if (NOME_ANTIGO.equals(nome)) {
            return carregar();
        }
        Path arquivo = caminhoPlanejamento(validarNome(nome));
        try {
            return lerPlanejamentoSalvo(arquivo).planejamento;
        } catch (IOException e) {
            String detalhe = e instanceof StreamCorruptedException
                    ? "formato inválido" : e.getMessage();
            throw new ArquivoException("Não foi possível carregar o planejamento \""
                    + nome + "\": " + detalhe, e);
        }
    }

    private PlanejamentoSalvo lerPlanejamentoSalvo(Path arquivo)
            throws IOException {
        try (FileInputStream stream = new FileInputStream(arquivo.toFile());
             ObjectInputStream entrada = new ObjectInputStream(stream)) {
            Object objeto = entrada.readObject();
            if (!(objeto instanceof PlanejamentoSalvo)) {
                throw new IOException("O arquivo não contém um planejamento nomeado válido.");
            }
            return (PlanejamentoSalvo) objeto;
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new IOException("O arquivo contém dados incompatíveis.", e);
        }
    }

    private Path caminhoPlanejamento(String nome) {
        String chave = nome.trim().toLowerCase(Locale.ROOT);
        String codificado = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(chave.getBytes(StandardCharsets.UTF_8));
        return pastaPlanejamentos.resolve(codificado + ".dat");
    }

    private static String validarNome(String nome) throws ArquivoException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ArquivoException("Informe um nome para o planejamento.", null);
        }
        String nomeValido = nome.trim();
        if (nomeValido.length() > 80) {
            throw new ArquivoException("O nome do planejamento deve ter até 80 caracteres.", null);
        }
        if (NOME_ANTIGO.equalsIgnoreCase(nomeValido)) {
            throw new ArquivoException("Esse nome é reservado para o arquivo antigo.", null);
        }
        return nomeValido;
    }

    private static final class PlanejamentoSalvo implements java.io.Serializable {

        private static final long serialVersionUID = 1L;

        private final String nome;
        private final Planejamento planejamento;

        private PlanejamentoSalvo(String nome, Planejamento planejamento) {
            this.nome = nome;
            this.planejamento = planejamento;
        }
    }
}
