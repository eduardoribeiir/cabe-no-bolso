# Diagrama de Classes UML — Cabe no Bolso

O diagrama representa a estrutura das principais classes do sistema Cabe no Bolso, contemplando o modelo de domínio, as operações de negócio, os relacionamentos entre objetos, os mecanismos de abstração e herança e a interface de persistência.

```mermaid
classDiagram
direction TB

class Planejamento {
    -List~Participante~ participantes
    -List~Despesa~ despesas
    +Planejamento()
    +adicionarParticipante(participante: Participante) void
    +adicionarParticipante(codigo: int, nome: String, orcamento: long) void
    +adicionarDespesa(despesa: Despesa) void
    +adicionarDespesa(fixa: boolean, descricao: String, valor: long, codigos: List~Integer~) void
    +editarDespesa(indice: int, fixa: boolean, descricao: String, valor: long, codigos: List~Integer~) void
    +removerDespesa(indice: int) void
    +alterarNome(codigo: int, nome: String) void
    +alterarOrcamento(codigo: int, orcamento: long) void
    +excluirParticipante(codigo: int) void
    +calcularResultados() List~ResultadoParticipante~
    +calcularTotalParticipante(codigo: int) long
    +calcularCustoTotal() long
    +calcularValorExcedido(codigo: int) long
    +estaDentroDoOrcamento(codigo: int) boolean
    +getParticipantes() List~Participante~
    +getDespesas() List~Despesa~
}

class Participante {
    -int codigo
    -String nome
    -long orcamento
    +Participante(codigo: int, nome: String, orcamento: long)
    +getCodigo() int
    +getNome() String
    +getOrcamento() long
    +setNome(nome: String) void
    +setOrcamento(orcamento: long) void
}

class Despesa {
    <<abstract>>
    -String descricao
    -long valor
    -List~Participante~ participantes
    +Despesa(descricao: String, valor: long, participantes: List~Participante~)
    +getDescricao() String
    +getValor() long
    +getValorDecimal() BigDecimal
    +getParticipantes() List~Participante~
    +getTipo() String*
    +calcularRateio() Map~Integer, Long~*
    +calcularRateioDecimal() Map~Integer, BigDecimal~
    +calcularCustoParticipante(codigo: int) long
    +calcularCustoTotal() long
    +custoTotal() BigDecimal
}

class DespesaFixa {
    +DespesaFixa(descricao: String, valor: long, participantes: List~Participante~)
    +getTipo() String
    +calcularRateio() Map~Integer, Long~
    +calcularCustoTotal() long
}

class DespesaPorPessoa {
    +DespesaPorPessoa(descricao: String, valor: long, participantes: List~Participante~)
    +getTipo() String
    +calcularRateio() Map~Integer, Long~
    +calcularCustoTotal() long
}

class ResultadoParticipante {
    -int codigo
    -String nome
    -long orcamento
    -long custo
    +ResultadoParticipante(codigo: int, nome: String, orcamento: long, custo: long)
    +getCodigo() int
    +getNome() String
    +getOrcamento() BigDecimal
    +getCusto() BigDecimal
    +isDentroDoOrcamento() boolean
    +getExcesso() BigDecimal
}

class PlanejamentoControle {
    -Planejamento planejamento
    -Persistencia persistencia
    -List~Ouvinte~ ouvintes
    +PlanejamentoControle(planejamento: Planejamento, persistencia: Persistencia)
    +cadastrarParticipante(codigo: String, nome: String, orcamento: String) Resposta
    +alterarNome(codigo: String, novoNome: String) Resposta
    +alterarOrcamento(codigo: String, novoOrcamento: String) Resposta
    +excluirParticipante(codigo: int) Resposta
    +cadastrarDespesa(descricao: String, valor: String, fixa: boolean, codigos: Collection~Integer~) Resposta
    +editarDespesa(indice: int, descricao: String, valor: String, fixa: boolean, codigos: Collection~Integer~) Resposta
    +excluirDespesa(indice: int) Resposta
    +salvar() Resposta
    +carregar() Resposta
    +novo() Resposta
    +custoTotal() BigDecimal
    +resultados() List~ResultadoParticipante~
}

class Ouvinte {
    <<interface>>
    +planejamentoAlterado() void
}

class Resposta {
    <<final>>
    -boolean sucesso
    -String mensagem
    -Resposta(sucesso: boolean, mensagem: String)
    +ok(mensagem: String)$ Resposta
    +erro(mensagem: String)$ Resposta
    +isSucesso() boolean
    +getMensagem() String
}

class Persistencia {
    <<interface>>
    +salvar(planejamento: Planejamento) void
    +carregar() Planejamento
}

class ArquivoPersistencia {
    -String caminho
    +ArquivoPersistencia(caminho: String)
    +salvar(planejamento: Planejamento) void
    +carregar() Planejamento
}

Planejamento "1" *-- "0..*" Participante : possui
Planejamento "1" *-- "0..*" Despesa : organiza

Despesa "0..*" -- "1..*" Participante : envolve

Despesa <|-- DespesaFixa : heranca
Despesa <|-- DespesaPorPessoa : heranca

Planejamento ..> ResultadoParticipante : gera resultados

PlanejamentoControle "1" --> "1" Planejamento : gerencia
PlanejamentoControle "1" --> "1" Persistencia : utiliza
PlanejamentoControle "1" --> "0..*" Ouvinte : notifica
PlanejamentoControle ..> Resposta : retorna

Persistencia <|.. ArquivoPersistencia : implementa
```
