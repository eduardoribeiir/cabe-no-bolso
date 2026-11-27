# Modelagem

## Visão geral

O sistema organiza um planejamento com participantes e despesas. O modelo mantém
as regras de negócio e os cálculos; a interface Swing coleta as entradas e
apresenta os resultados; o controlador conecta as telas ao modelo e à
persistência. A aplicação também inclui um simulador opcional que avalia
alternativas sem modificar o planejamento original.

## Classes e responsabilidades

| Classe | Responsabilidade |
|---|---|
| `Planejamento` | Mantém os participantes e as despesas; cadastra, altera e remove esses dados; calcula o custo total e os resultados individuais. |
| `Participante` | Representa código, nome e orçamento. Valida os dados e permite alterar nome e orçamento por meio das operações do modelo. |
| `Despesa` | Classe abstrata com descrição, valor e participantes envolvidos. Define o contrato de rateio e oferece cálculos de custo por participante e custo total. |
| `DespesaFixa` | Divide um valor entre os participantes selecionados. |
| `DespesaPorPessoa` | Cobra o valor informado de cada participante selecionado. |
| `ResultadoParticipante` | Apresenta uma visão calculada do participante: orçamento, custo, excesso e situação em relação ao limite. |
| `PlanejamentoControle` | Recebe operações da interface, interpreta os dados de entrada, chama o modelo e devolve uma `Resposta`. Também notifica as telas quando o planejamento muda. |
| `Persistencia` / `PersistenciaArquivo` | Define as operações de salvar e carregar e implementa a gravação local dos planejamentos, inclusive por nome. |
| `SimuladorAlternativas` | Busca combinações de despesas opcionais que respeitem os orçamentos, maximizando a soma das prioridades mantidas. |
| `ResultadoSimulacao` | Reúne as despesas mantidas e retiradas, os custos originais e simulados e a prioridade total da sugestão. |

## Relações entre as classes

Um `Planejamento` mantém vários `Participante` identificados por código e uma
lista ordenada de `Despesa`. Cada despesa referencia um ou mais participantes;
um participante pode estar associado a várias despesas. Por isso, o modelo
impede remover um participante enquanto ele estiver vinculado a alguma
despesa.

`DespesaFixa` e `DespesaPorPessoa` especializam `Despesa`. O planejamento
trabalha com a classe abstrata e delega o cálculo de rateio à implementação
concreta. Para exibir o resumo, `Planejamento` calcula objetos
`ResultadoParticipante`; estes guardam os valores do resultado, sem substituir
os dados originais do participante.

`PlanejamentoControle` utiliza `Planejamento` e `Persistencia`. As telas Swing,
incluindo `PainelSimulador`, interagem com o controlador. Para a simulação, o
controlador chama `SimuladorAlternativas`, que consulta o planejamento e
produz um `ResultadoSimulacao`. A classificação e as prioridades são entradas
da simulação, não propriedades permanentes das despesas.

## Conceitos de orientação a objetos

- **Encapsulamento:** os atributos do modelo são privados. Alterações passam
  por operações que validam os dados; coleções expostas pelo planejamento e
  pelas despesas não permitem modificar diretamente seu estado interno.
- **Abstração e herança:** `Despesa` define os dados e operações comuns. As
  subclasses representam as duas regras de cobrança.
- **Polimorfismo:** as despesas são armazenadas e percorridas como
  `Despesa`; cada subtipo calcula seu próprio rateio por
  `calcularRateio()`.
- **Interfaces:** `Persistencia` desacopla o controlador do armazenamento em
  arquivo. `PlanejamentoControle.Ouvinte` permite que as telas sejam
  notificadas quando o estado muda.

## Organização MVC

- **Modelo:** `Planejamento`, `Participante`, `Despesa` e suas subclasses,
  `ResultadoParticipante` e as classes do simulador concentram dados, regras e
  cálculos.
- **Visão:** os painéis Swing recebem entradas e exibem cadastros, resumos,
  mensagens e sugestões.
- **Controle:** `PlanejamentoControle` valida e interpreta os valores
  recebidos da interface, coordena as operações do modelo e informa sucesso ou
  erro por meio de `Resposta`.

O fluxo típico é: **visão → controlador → modelo → controlador → visão**.
Assim, os cálculos não dependem dos componentes Swing e as telas não alteram
diretamente as coleções do modelo.

## Coleções e consistência

`Planejamento` usa `Map<Integer, Participante>` para localizar participantes
por código e rejeitar códigos duplicados, e `List<Despesa>` para preservar a
ordem de inclusão exibida na interface. As despesas mantêm uma lista dos
participantes envolvidos. Essa lista é ordenada pelo código e exposta como
imutável; a ordenação também define quem recebe os centavos restantes no
rateio fixo.

O simulador recebe uma lista de prioridades na mesma ordem das despesas.
`null` identifica uma despesa essencial; um inteiro positivo identifica uma
opcional. Ele examina todas as combinações das opcionais, até o limite de dez,
e seleciona uma combinação viável de maior prioridade total. A simulação
devolve um resultado separado e não altera as despesas nem os totais do
planejamento.

## Valores, validação e cálculos

Os valores monetários são representados por `BigDecimal`, limitados a duas
casas decimais. O orçamento pode ser zero; cada despesa deve ter valor maior
que zero, descrição não vazia e ao menos um participante cadastrado, sem
repetições. O rateio fixo converte o valor em centavos inteiros para dividir
com precisão e distribui os centavos restantes em ordem crescente de código.
Na despesa por pessoa, cada participante selecionado paga o valor unitário.

O custo igual ao orçamento é considerado dentro do limite. Quando o custo é
maior, `ResultadoParticipante` informa o excesso. Edições e remoções são
validadas antes de atualizar o modelo, preservando o estado anterior quando a
entrada não é válida. As regras e falhas são comunicadas por exceções de
domínio e respostas do controlador.

## Persistência

`PersistenciaArquivo` implementa `Persistencia` usando serialização de
objetos. As classes de dados serializáveis declaram `serialVersionUID`; os
fluxos são fechados com `try-with-resources`. A implementação oferece operações
para salvar e carregar o arquivo legado e para salvar, listar e recuperar
planejamentos nomeados. Falhas de leitura ou gravação são convertidas em
mensagens de erro, e uma falha ao carregar não substitui o planejamento que
está em uso.

## Correspondência com o diagrama UML

O diagrama deve representar as classes e relações descritas acima, incluindo
`ResultadoParticipante` e, se o objetivo extra fizer parte da entrega,
`SimuladorAlternativas` e `ResultadoSimulacao`. A versão do diagrama enviada
junto com a solicitação usa operações e tipos que não correspondem à
implementação atual: por exemplo, apresenta valores monetários como `long`,
enquanto o código usa `BigDecimal`, e mostra operações como
`calcularTotalParticipante()` que não existem na classe `Planejamento` atual.
Atualize o diagrama antes da entrega para que ele e o código-fonte descrevam a
mesma versão do sistema.
