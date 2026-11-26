# Cabe no bolso – Planejador de despesas compartilhadas

Atividade final de Programação Orientada a Objetos (EAD Unifor).
Aplicação Java com interface gráfica Swing para planejar despesas de um passeio em grupo, ratear os custos e verificar se cada participante respeita seu orçamento.

> **Equipe:**


> 1. LUCAS CAUÃ FERREIRA MARTINS – 2641642
> 2. LUIZ EDUARDO PAIVA RIBEIRO – 2640860
> 3. MARIA CLARA DA SILVA MARTINS – 2640859
> 4. MARIA EDUARDA BESSA BRAZ – 2640861
> 5. NINA RAFAELA ARAUJO DA SILVA – 2640875
> 6. RISOLETA PEREIRA PINTO – 2323869

> **Repositório GitHub:** (https://github.com/eduardoribeiir/cabe-no-bolso)

## Requisitos

| Item | Versão |
|------|--------|
| JDK | 17 ou superior (ambiente local verificado com JDK 23.0.2; confirmar a versão usada pela equipe) |
| Dependências externas | Nenhuma (somente a biblioteca padrão: `javax.swing`, `java.io`, `java.util`, `java.math`) |
| Sistema operacional | Windows, Linux ou macOS |

## Organização das pastas

```
cabe-no-bolso/
├── src/cabenobolso/
│   ├── Main.java                  # ponto de entrada
│   ├── modelo/                    # M – regras, cálculos e validações
│   ├── visao/                     # V – telas Swing (entrada e exibição)
│   ├── controle/                  # C – encaminha as ações da visão ao modelo
│   ├── persistencia/              # interface de persistência + classe de arquivo (serialização)
│   ├── excecao/                   # exceções personalizadas (validação e arquivo)
│   └── extra/                     # (opcional) simulador de alternativas – seção 7
├── dados/                         # arquivo de exemplo e roteiro do caso de referência
├── testes/                        # roteiro e código dos testes da seção 4
├── docs/
│   ├── MODELAGEM.md               # justificativas das relações e operações
│   ├── CONTRIBUICOES.md           # contribuição de cada integrante
│   ├── uml/                       # diagrama de classes (imagem + fonte)
│   └── evidencias/                # capturas de tela dos testes
├── scripts/                       # compilar e executar (.sh e .bat)
├── .gitignore
└── README.md
```

### Responsabilidades dos pacotes (MVC)

| Pacote | Papel |
|--------|-------|
| `modelo` | Calcula e valida: `Planejamento`, `Participante`, `Despesa` (abstrata), `DespesaFixa`, `DespesaPorPessoa`. Não conhece Swing. |
| `visao` | Recebe entradas e mostra resultados e mensagens. Não faz cálculo. |
| `controle` | Recebe eventos da visão, chama o modelo e devolve resultado ou mensagem de erro. |
| `persistencia` | Interface `Persistencia` (salvar/carregar) e implementação em arquivo por serialização. |
| `excecao` | Exceções claras para entrada inválida, regra violada e falha de arquivo. |

Fluxo de uma ação: **Visão (evento) → Controle → Modelo (valida/calcula) → Controle → Visão (resultado ou mensagem)**.

## Compilação e execução

### Linux / macOS
```bash
./scripts/compilar.sh
./scripts/executar.sh
```

### Windows
```bat
scripts\compilar.bat
scripts\executar.bat
```

### Manualmente
```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out cabenobolso.Main
```

## Salvar e recuperar o planejamento

Os planejamentos são salvos por **serialização de objetos** em arquivos separados na pasta `planejamentos/`, criada na pasta de execução.

Ao usar *Salvar*, informe um nome, como "Férias de janeiro". Se já existir um planejamento com esse nome, o programa pede confirmação antes de substituí-lo. Para recuperar um planejamento, use *Carregar* e escolha o nome na lista.

Se houver um arquivo antigo `planejamento.dat` na pasta de execução, ele também aparece na lista e pode ser carregado se for compatível. Quando não há planejamentos salvos ou ocorre uma falha, o programa exibe uma mensagem; uma falha ao carregar preserva o planejamento em uso.

Para testar, salve dois planejamentos com nomes diferentes, feche e reabra o programa e carregue cada um pela lista. Os dados, vínculos e resultados devem voltar sem duplicação.

## Dados de exemplo

Veja `dados/CASO_REFERENCIA.md` para o roteiro de cadastro do caso da seção 4 do enunciado (Ana, Bruno e Carla). Se a equipe gerar um arquivo `dados/caso_referencia.dat` compatível com a versão final, ele deve ser versionado nesta pasta.

## Como reproduzir os testes

Veja `testes/ROTEIRO_TESTES.md`. Cada teste registra entrada, resultado esperado, resultado obtido e evidência (captura em `docs/evidencias/`).

## Modelagem

Diagrama de classes em `docs/uml/` e justificativas em `docs/MODELAGEM.md`.

## Objetivo extra: simulador de alternativas

O simulador ajuda a ajustar o planejamento quando os gastos ultrapassam os orçamentos. Ele sugere quais despesas opcionais manter ou retirar para que **cada participante** fique dentro do próprio limite, preservando as despesas essenciais.

### Como usar

1. Cadastre os participantes, seus orçamentos e as despesas do planejamento.
2. Na aba **Simulador**, deixe como **Essencial** as despesas que não podem ser retiradas. Todas começam nessa classificação.
3. Marque como **Opcional** as despesas que podem ser retiradas. Cada uma recebe prioridade 1; aumente esse número inteiro positivo para indicar maior importância.
4. Clique em **Gerar sugestão automática**. O resultado mostra as despesas mantidas e retiradas, os gastos de cada pessoa antes e depois e o custo total.

A sugestão não altera o planejamento: para adotá-la, é preciso editar ou excluir as despesas nas telas do aplicativo.

### Como a sugestão é escolhida

O simulador mantém todas as despesas essenciais e testa as combinações das opcionais. Entre as que respeitam todos os orçamentos, escolhe uma com a maior **soma das prioridades das opcionais mantidas**. Isso não significa necessariamente manter a despesa individual de maior prioridade: duas opcionais com prioridades 4 e 4 podem ser preferidas a uma de prioridade 6. Em caso de empate, qualquer combinação ótima pode ser escolhida.

Por exemplo, no [caso de referência](dados/CASO_REFERENCIA.md), Ana e Carla têm orçamento de R$ 70,00 cada e Bruno, de R$ 60,00. Com todas as despesas, os gastos são R$ 75,00, R$ 60,00 e R$ 75,00, respectivamente. Se transporte e almoço forem essenciais, e atividade recreativa e sobremesa forem opcionais com prioridades 3 e 1, a sugestão mantém a atividade e retira a sobremesa. Os gastos passam a R$ 65,00 para Ana, R$ 50,00 para Bruno e R$ 65,00 para Carla; o custo total passa de R$ 210,00 para R$ 180,00.

Se as despesas essenciais, sozinhas, ultrapassarem o orçamento de alguém, não há solução com a classificação atual. A tela mostra os excessos; se possível, marque alguma essencial como opcional e simule novamente.

São aceitas até **10 despesas opcionais**. A busca examina até `2^n` combinações, onde `n` é o número de opcionais: no máximo 1.024 combinações.

O caso de referência e os demais cenários estão em `testes/ROTEIRO_TESTES.md`. Para executar os testes automatizados no Windows, após compilar a aplicação:

```bat
javac -encoding UTF-8 -cp out -d out testes\TestesSimulador.java testes\TestesSimuladorVisao.java
java -cp out TestesSimulador
java -Djava.awt.headless=true -cp out cabenobolso.visao.TestesSimuladorVisao
```

## Uso de IA e fontes externas

Se houver código gerado por IA ou fontes externas, a equipe revisou, compreendeu e referenciou o material (ver relatório técnico).
