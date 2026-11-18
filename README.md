# Cabe no bolso – Planejador de despesas compartilhadas

Atividade final de Programação Orientada a Objetos (EAD Unifor).
Aplicação Java com interface gráfica Swing para planejar despesas de um passeio em grupo, ratear os custos e verificar se cada participante respeita seu orçamento.

> **Equipe:**


> 1. LUCAS CAUÃ FERREIRA MARTINS – matrícula
> 2. LUIZ EDUARDO PAIVA RIBEIRO – matrícula
> 3. MARIA CLARA DA SILVA MARTINS – matrícula
> 4. MARIA EDUARDA BESSA BRAZ – matrícula
> 5. NINA RAFAELA ARAUJO DA SILVA – matrícula
> 6. RISOLETA PEREIRA PINTO – matrícula
> **Repositório GitHub:** preencher com a URL após publicar e liberar acesso para correção

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

O planejamento é salvo por **serialização de objetos** em arquivo local (padrão: `planejamento.dat`, na pasta de execução).
Para testar: cadastre os dados, use *Salvar*, feche o programa, abra de novo e use *Carregar*. Os dados, vínculos e resultados devem voltar sem duplicação.
Se o arquivo não existir ou for inválido, o programa exibe mensagem e permite iniciar um planejamento vazio; uma falha ao carregar preserva o planejamento em uso.

## Dados de exemplo

Veja `dados/CASO_REFERENCIA.md` para o roteiro de cadastro do caso da seção 4 do enunciado (Ana, Bruno e Carla). Se a equipe gerar um arquivo `dados/caso_referencia.dat` compatível com a versão final, ele deve ser versionado nesta pasta.

## Como reproduzir os testes

Veja `testes/ROTEIRO_TESTES.md`. Cada teste registra entrada, resultado esperado, resultado obtido e evidência (captura em `docs/evidencias/`).

## Modelagem

Diagrama de classes em `docs/uml/` e justificativas em `docs/MODELAGEM.md`.

## Objetivo extra (opcional)

Se realizado, o simulador fica em `src/cabenobolso/extra/`, com entradas, sugestões e testes (incluindo o caso sem solução) documentados em `testes/`.

## Uso de IA e fontes externas

Se houver código gerado por IA ou fontes externas, a equipe revisou, compreendeu e referenciou o material (ver relatório técnico).
