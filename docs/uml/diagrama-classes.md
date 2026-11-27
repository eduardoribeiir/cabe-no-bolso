# Diagrama de classes — Cabe no bolso

```mermaid
classDiagram
direction TB

class Planejamento {
  -List~Participante~ participantes
  -List~Despesa~ despesas
  +Planejamento()
  +adicionarParticipante(Participante participante) void
  +adicionarParticipante(int codigo, String nome, long orcamento) void
  +alterarNome(int codigo, String nome) void
  +alterarOrcamento(int codigo, long orcamento) void
  +excluirParticipante(int codigo) void
  +adicionarDespesa(Despesa despesa) void
  +adicionarDespesa(boolean fixa, String descricao, long valor, List~Integer~ codigos) void
  +editarDespesa(int indice, boolean fixa, String descricao, long valor, List~Integer~ codigos) void
  +removerDespesa(int indice) void
  +excluirDespesa(Despesa despesa) void
  +substituirDespesa(Despesa antiga, Despesa nova) void
  +getParticipantes() List~Participante~
  +getDespesas() List~Despesa~
  +calcularTotalParticipante(int codigo) long
  +calcularCustoTotal() long
  +calcularValorExcedido(int codigo) long
  +estaDentroDoOrcamento(int codigo) boolean
  +calcularResultados() List~ResultadoParticipante~
  -validarVinculos(Despesa despesa) void
  -criarDespesa(boolean fixa, String descricao, long valor, List~Integer~ codigos) Despesa
  -exigirParticipante(int codigo) Participante
  -buscarParticipante(int codigo) Participante
}

class Participante {
  -int codigo
  -String nome
  -long orcamento
  +Participante(int codigo, String nome, long orcamento)
  +getCodigo() int
  +getNome() String
  +getOrcamento() long
  +setNome(String nome) void
  +setOrcamento(long orcamento) void
  -validarCodigo(int codigo)$ void
  -validarNome(String nome)$ void
  -validarOrcamento(long orcamento)$ void
}

class Despesa {
  <<abstract>>
  -String descricao
  -long valor
  -List~Participante~ participantes
  +getDescricao() String
  +getValor() long
  +getValorDecimal() BigDecimal
  +getParticipantes() List~Participante~
  +envolveParticipante(int codigo) boolean
  +getTipo() String*
  +calcularRateio() Map~Integer,Long~*
  +calcularRateioDecimal() Map~Integer,BigDecimal~
  +calcularCustoParticipante(int codigo) long
  +custoDe(int codigo) BigDecimal
  +calcularCustoTotal() long
  +custoTotal() BigDecimal
  #converterParaCentavos(BigDecimal valorReais)$ long
  #converterParaReais(long centavos)$ BigDecimal
}

class DespesaFixa {
  +getTipo() String
  +calcularRateio() Map~Integer,Long~
  +calcularCustoTotal() long
}

class DespesaPorPessoa {
  +getTipo() String
  +calcularRateio() Map~Integer,Long~
  +calcularCustoTotal() long
}

class ResultadoParticipante {
  <<final>>
  -int codigo
  -String nome
  -long orcamento
  -long custo
  +ResultadoParticipante(int codigo, String nome, long orcamento, long custo)
  +getCodigo() int
  +getNome() String
  +getOrcamento() BigDecimal
  +getCusto() BigDecimal
  +isDentroDoOrcamento() boolean
  +getExcesso() BigDecimal
}

class DadosInvalidosException {
  +DadosInvalidosException(String mensagem)
}

class Persistencia {
  <<interface>>
  +salvar(Planejamento planejamento) void
  +carregar() Planejamento
}

class ArquivoPersistencia {
  -String caminho
  +ArquivoPersistencia(String caminho)
  +salvar(Planejamento planejamento) void
  +carregar() Planejamento
}

class Serializable {
  <<interface>>
}

class PlanejamentoControle {
  -Planejamento planejamento
  -Persistencia persistencia
  -List~Ouvinte~ ouvintes
  +PlanejamentoControle(Planejamento planejamento, Persistencia persistencia)
  +adicionarOuvinte(Ouvinte ouvinte) void
  +cadastrarParticipante(String codigo, String nome, String orcamento) Resposta
  +alterarNome(String codigo, String novoNome) Resposta
  +alterarOrcamento(String codigo, String novoOrcamento) Resposta
  +excluirParticipante(int codigo) Resposta
  +listarParticipantes() List~Participante~
  +cadastrarDespesa(String descricao, String valor, boolean fixa, Collection~Integer~ codigos) Resposta
  +editarDespesa(int indice, String descricao, String valor, boolean fixa, Collection~Integer~ codigos) Resposta
  +excluirDespesa(int indice) Resposta
  +listarDespesas() List~Despesa~
  +custoTotal() BigDecimal
  +resultados() List~ResultadoParticipante~
  +salvar() Resposta
  +carregar() Resposta
  +novo() Resposta
  -notificar() void
  -executar(Acao acao, String mensagemSucesso) Resposta
  -lerCodigo(String texto)$ int
  -lerCentavos(String texto, String campo)$ long
}

class Ouvinte {
  <<interface>>
  +planejamentoAlterado() void
}

class Resposta {
  <<final>>
  -boolean sucesso
  -String mensagem
  -Resposta(boolean sucesso, String mensagem)
  +ok(String mensagem)$ Resposta
  +erro(String mensagem)$ Resposta
  +isSucesso() boolean
  +getMensagem() String
}

class PlanejamentoController {
  <<legado - usado apenas em testes>>
  -Planejamento planejamento
  -Persistencia persistencia
  +PlanejamentoController(Persistencia persistencia)
  +getPlanejamento() Planejamento
  +cadastrarParticipante(int codigo, String nome, long orcamento) void
  +cadastrarDespesaFixa(String descricao, long valor, List~Participante~ participantes) void
  +cadastrarDespesaPorPessoa(String descricao, long valor, List~Participante~ participantes) void
  +alterarNome(int codigo, String nome) void
  +alterarOrcamento(int codigo, long orcamento) void
  +editarDespesaFixa(Despesa antiga, String descricao, long valor, List~Participante~ participantes) void
  +editarDespesaPorPessoa(Despesa antiga, String descricao, long valor, List~Participante~ participantes) void
  +excluirDespesa(Despesa despesa) void
  +excluirParticipante(int codigo) void
  +salvar() void
  +carregar() void
}

class JanelaPrincipal {
  +JanelaPrincipal(PlanejamentoControle controle)
}

class PainelParticipantes {
  -PlanejamentoControle controle
  -JTextField txtCodigo
  -JTextField txtNome
  -JTextField txtOrcamento
  -DefaultTableModel modelo
  -JTable tabela
  -List~Participante~ exibidos
  -boolean atualizando
  -criarFormulario() JPanel
  -criarBotoes() JPanel
  -adicionar() void
  -alterarOrcamento() void
  -alterarNome() void
  -excluir() void
  -limparCampos() void
  -preencherCamposComSelecao() void
  -atualizar() void
}

class PainelDespesas {
  -PlanejamentoControle controle
  -JTextField txtDescricao
  -JTextField txtValor
  -JComboBox~String~ cmbTipo
  -JPanel painelChecks
  -Map~Integer,JCheckBox~ checks
  -DefaultTableModel modelo
  -JTable tabela
  -boolean atualizando
  -criarFormulario() JPanel
  -criarBotoesInferiores() JPanel
  -adicionar() void
  -editar() void
  -excluir() void
  -marcarTodos(boolean marcar) void
  -codigosMarcados() List~Integer~
  -preencherCamposComSelecao() void
  -atualizar() void
  -atualizarTabela() void
  -reconstruirChecks(List~Integer~ marcados) void
}

class PainelResumo {
  -PlanejamentoControle controle
  -JLabel lblTotal
  -JLabel lblSituacao
  -DefaultTableModel modeloDespesas
  -DefaultTableModel modeloParticipantes
  -JTable tabelaDespesas
  -JTable tabelaParticipantes
  -atualizar() void
}

class PainelPersistencia {
  -PlanejamentoControle controle
  -JTextArea registro
  -salvar() void
  -carregar() void
  -novo() void
  -registrar(String mensagem) void
}

class Apoio {
  <<utility>>
  ~moeda(BigDecimal valor)$ String
  ~moeda(long centavos)$ String
  ~numero(long centavos)$ String
  ~numero(BigDecimal valor)$ String
  ~erro(Component pai, String mensagem)$ void
  ~info(Component pai, String mensagem)$ void
  ~confirmar(Component pai, String mensagem)$ boolean
  ~exibir(Component pai, Resposta resposta)$ boolean
  ~modeloSomenteLeitura(Object[] colunas)$ DefaultTableModel
  ~adicionar(JPanel painel, Component componente, int x, int y, int largura, double pesoX)$ void
}

class Main {
  -String ARQUIVO_PADRAO$
  +main(String[] args)$ void
  -iniciar()$ void
}

Planejamento "1" *-- "0..*" Participante : participantes
Planejamento "1" *-- "0..*" Despesa : despesas
Despesa "0..*" --> "1..*" Participante : participantes
Despesa <|-- DespesaFixa
Despesa <|-- DespesaPorPessoa
Planejamento ..> ResultadoParticipante : «cria»
Serializable <|.. Planejamento
Serializable <|.. Despesa
Serializable <|.. Participante
Exception <|-- DadosInvalidosException
Planejamento ..> DadosInvalidosException : «throws»
Despesa ..> DadosInvalidosException : «throws»
Participante ..> DadosInvalidosException : «throws»


Persistencia <|.. ArquivoPersistencia
Persistencia ..> Planejamento : «usa»

PlanejamentoControle "1" --> "1" Planejamento : planejamento
PlanejamentoControle "1" --> "1" Persistencia : persistencia
PlanejamentoControle "1" --> "0..*" Ouvinte : ouvintes
PlanejamentoControle ..> Resposta : «cria»
PlanejamentoControle ..> DadosInvalidosException : «captura»
PlanejamentoControle ..> Despesa : «retorna»
PlanejamentoControle ..> Participante : «retorna»
PlanejamentoControle ..> ResultadoParticipante : «retorna»
PlanejamentoController "1" --> "1" Planejamento
PlanejamentoController "1" --> "1" Persistencia

%% ===================== RELACIONAMENTOS: VISÃO =====================
JFrame <|-- JanelaPrincipal
JPanel <|-- PainelParticipantes
JPanel <|-- PainelDespesas
JPanel <|-- PainelResumo
JPanel <|-- PainelPersistencia
JanelaPrincipal "1" *-- "1" PainelParticipantes
JanelaPrincipal "1" *-- "1" PainelDespesas
JanelaPrincipal "1" *-- "1" PainelResumo
JanelaPrincipal "1" *-- "1" PainelPersistencia
JanelaPrincipal ..> PlanejamentoControle : «recebe»
PainelParticipantes "1" --> "1" PlanejamentoControle : controle
PainelDespesas "1" --> "1" PlanejamentoControle : controle
PainelResumo "1" --> "1" PlanejamentoControle : controle
PainelPersistencia "1" --> "1" PlanejamentoControle : controle
PainelParticipantes ..> Ouvinte : «registra (this::atualizar)»
PainelDespesas ..> Ouvinte : «registra (this::atualizar)»
PainelResumo ..> Ouvinte : «registra (this::atualizar)»
PainelParticipantes ..> Apoio
PainelDespesas ..> Apoio
PainelResumo ..> Apoio
PainelPersistencia ..> Apoio
Apoio ..> Resposta
PainelDespesas ..> Despesa : «exibe»
PainelDespesas ..> Participante : «exibe»
PainelParticipantes ..> Participante : «exibe»
PainelResumo ..> Despesa : «exibe»
PainelResumo ..> Participante : «exibe»
PainelResumo ..> ResultadoParticipante : «exibe»

Main ..> Planejamento : «cria»
Main ..> ArquivoPersistencia : «cria»
Main ..> PlanejamentoControle : «cria»
Main ..> JanelaPrincipal : «cria»
```

**Legenda:** `*` abstrato · `$` estático · `~` visibilidade de pacote. Atributos `serialVersionUID`, classe interna privada `Renderizador` e interface privada `Acao` foram omitidos por serem detalhes internos.
