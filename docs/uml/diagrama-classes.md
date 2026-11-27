# Diagrama de classes

O diagrama representa as classes principais do modelo, da persistência e do
objetivo extra, incluindo a ligação do simulador com o controle e a interface.
Para manter a leitura, são mostrados os atributos e as operações principais,
sem detalhar todos os métodos auxiliares e componentes Swing.

```mermaid
classDiagram
    direction TB

    class Planejamento {
        -Map participantes
        -List~Despesa~ despesas
        +adicionarParticipante(int codigo, String nome, BigDecimal orcamento) void
        +alterarNome(int codigo, String nome) void
        +alterarOrcamento(int codigo, BigDecimal orcamento) void
        +removerParticipante(int codigo) void
        +listarParticipantes() List~Participante~
        +adicionarDespesaFixa(String descricao, BigDecimal valor, List~Integer~ codigos) void
        +adicionarDespesaPorPessoa(String descricao, BigDecimal valor, List~Integer~ codigos) void
        +editarDespesa(int indice, String descricao, BigDecimal valor, boolean fixa, List~Integer~ codigos) void
        +removerDespesa(int indice) void
        +listarDespesas() List~Despesa~
        +custoTotal() BigDecimal
        +calcularResultados() List~ResultadoParticipante~
    }

    class Participante {
        -int codigo
        -String nome
        -BigDecimal orcamento
        +getCodigo() int
        +getNome() String
        +getOrcamento() BigDecimal
        ~alterarNome(String novoNome) void
        ~alterarOrcamento(BigDecimal novoOrcamento) void
    }

    class Despesa {
        <<abstract>>
        -String descricao
        -BigDecimal valor
        -List~Participante~ participantes
        +getDescricao() String
        +getValor() BigDecimal
        +getParticipantes() List~Participante~
        +getTipo() String*
        +calcularRateio() Map*
        +custoDe(int codigo) BigDecimal
        +custoTotal() BigDecimal
    }

    class DespesaFixa {
        +getTipo() String
        +calcularRateio() Map
    }

    class DespesaPorPessoa {
        +getTipo() String
        +calcularRateio() Map
    }

    class ResultadoParticipante {
        -int codigo
        -String nome
        -BigDecimal orcamento
        -BigDecimal custo
        -BigDecimal excesso
        +getCusto() BigDecimal
        +getExcesso() BigDecimal
        +isDentroDoOrcamento() boolean
    }

    class Persistencia {
        <<interface>>
        +salvar(Planejamento planejamento) void
        +carregar() Planejamento
        +listarNomes() List~String~
        +salvar(String nome, Planejamento planejamento) void
        +carregar(String nome) Planejamento
    }

    class PersistenciaArquivo {
        -Path caminho
        -Path pastaPlanejamentos
        +salvar(Planejamento planejamento) void
        +carregar() Planejamento
        +listarNomes() List~String~
        +salvar(String nome, Planejamento planejamento) void
        +carregar(String nome) Planejamento
    }

    class PlanejamentoControle {
        -Planejamento planejamento
        -Persistencia persistencia
        +cadastrarParticipante(String codigo, String nome, String orcamento) Resposta
        +cadastrarDespesa(String descricao, String valor, boolean fixa, Collection~Integer~ codigos) Resposta
        +listarParticipantes() List~Participante~
        +listarDespesas() List~Despesa~
        +resultados() List~ResultadoParticipante~
        +simularAlternativas(List~Integer~ prioridades) ResultadoSimulacao
        +salvar(String nome) Resposta
        +carregar(String nome) Resposta
    }

    class PainelSimulador {
        -PlanejamentoControle controle
        -simular() void
        -exibirResultado(ResultadoSimulacao simulacao) void
    }

    class SimuladorAlternativas {
        +simular(Planejamento planejamento, List~Integer~ prioridades) ResultadoSimulacao$
    }

    class ResultadoSimulacao {
        -boolean solucao
        -List~Despesa~ mantidas
        -List~Despesa~ retiradas
        -List~Participante~ participantes
        -Map custosOriginais
        -Map custosSimulados
        -long prioridade
        +temSolucao() boolean
        +getMantidas() List~Despesa~
        +getRetiradas() List~Despesa~
        +getParticipantes() List~Participante~
        +getCustoOriginal(int codigo) BigDecimal
        +getCustoSimulado(int codigo) BigDecimal
        +getPrioridade() long
        +getTotalOriginal() BigDecimal
        +getTotalSimulado() BigDecimal
    }

    Planejamento "1" --> "0..*" Participante : cadastra
    Planejamento "1" --> "0..*" Despesa : inclui
    Despesa "0..*" --> "1..*" Participante : envolve
    Despesa <|-- DespesaFixa
    Despesa <|-- DespesaPorPessoa
    Planejamento ..> ResultadoParticipante : calcula
    Persistencia <|.. PersistenciaArquivo
    PlanejamentoControle --> Planejamento
    PlanejamentoControle --> Persistencia
    PainelSimulador --> PlanejamentoControle
    PlanejamentoControle ..> SimuladorAlternativas : usa
    SimuladorAlternativas ..> Planejamento : consulta
    SimuladorAlternativas ..> ResultadoSimulacao : produz
    PainelSimulador ..> ResultadoSimulacao : exibe
    ResultadoSimulacao "0..*" --> "0..*" Despesa : mantidas e retiradas
    ResultadoSimulacao "0..*" --> "0..*" Participante : compara custos
```
