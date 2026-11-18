# Modelagem

> Preencher junto com quem fizer a UML e o modelo. O diagrama atualizado fica em `docs/uml/`.

## Classes e relações

| Relação | Tipo | Multiplicidade | Justificativa |
|---------|------|----------------|---------------|
| Planejamento — Participante | Composição | 1 → 0..* | O planejamento cria e controla o ciclo de vida dos participantes |
| Planejamento — Despesa | Composição | 1 → 0..* | Despesas só existem dentro de um planejamento |
| Despesa — Participante | Associação | 1..* → 0..* | Cada despesa tem ao menos um participante; um participante pode estar em várias despesas |
| Despesa ◁ DespesaFixa / DespesaPorPessoa | Herança | — | Abstração do cálculo do rateio; subclasses sobrescrevem o método de cálculo |
| PersistenciaArquivo ▷ Persistencia | Realização | — | Contrato de salvar/carregar independente do meio |

_(ajustar conforme o diagrama final da equipe)_

## Conceitos de POO (onde aparecem)

- **Encapsulamento:** atributos privados com getters; validações nos construtores e setters.
- **Abstração e herança:** `Despesa` abstrata com `DespesaFixa` e `DespesaPorPessoa`.
- **Polimorfismo:** o `Planejamento` trabalha com referências `Despesa` e chama o cálculo sobrescrito.
- **Interface de persistência:** `Persistencia` usada pelo controle; `PersistenciaArquivo` implementa.

## Coleções escolhidas

_Justificar (ex.: `Map<Integer, Participante>` ordenado por código para o rateio de centavos; `List<Despesa>` para manter ordem de inclusão)._
