## Contribuições da equipe

Cada integrante deve participar da implementação ou integração **e** da validação (enunciado, seção 1).

| Integrante | Matrícula | Implementação | Integração | Validação | Documentação |
|---|---|---|---|---|---|
| **Luiz Eduardo Paiva Ribeiro** | [2640860] | Configuração do repositório GitHub, estrutura inicial de pacotes MVC, desenvolvimento da camada Controller e interface gráfica Java Swing. Participação na implementação de parte da persistência durante os testes. Responsável pelo simulador de alternativas do objetivo extra. | Integração entre View, Controller e Model; organização da estrutura do projeto e suporte à correção de bugs. | Execução do caso de referência do professor e dos testes obrigatórios. Identificação de um problema na restauração de planejamentos, em que o sistema carrega sempre o primeiro planejamento salvo. | Elaboração do README.md, instruções de execução e organização das evidências para a entrega. |
| **Maria Clara da Silva Martins** | [2640859] | Implementação das classes Participante e Planejamento; criação da interface de persistência e serialização em arquivos. | Integração das operações de persistência com o planejamento e colaboração na integração das funcionalidades do modelo. | Conferência das operações de cadastro, gerenciamento de participantes e persistência de dados. | Elaboração e revisão do relatório técnico em PDF, conforme as normas ABNT. |
| **Nina Rafaela Araujo da Silva** | [2640875] | Implementação da hierarquia de despesas, com classe abstrata Despesa, DespesaFixa e DespesaPorPessoa; participação nas validações e tratamento de exceções. | Integração das classes de despesa com Planejamento e colaboração na integração das regras de persistência e validação. | Verificação dos cálculos polimórficos, validações de entradas e tratamento de exceções. | Colaboração na modelagem e justificativas dos conceitos de POO para o relatório técnico. |
| **Risoleta Pereira Pinto** | [2323869] | Elaboração do diagrama de classes UML, contemplando atributos, métodos, relacionamentos e herança. | Conferência da correspondência entre o diagrama UML e a estrutura implementada no projeto. | Validação dos relacionamentos, hierarquia de classes e representação dos conceitos de POO. | Documentação da modelagem UML e disponibilização do diagrama para inclusão no README e relatório. |
| **Lucas Cauã Ferreira Martins** | [2641642] | Implementação da lógica de rateio nas classes DespesaFixa e DespesaPorPessoa, incluindo a distribuição dos centavos por ordem crescente dos códigos dos participantes. | Integração da lógica de rateio com as regras de negócio e suporte à integração dos cálculos no Planejamento. | Testes e conferência dos cálculos de divisão, incluindo o cenário de R$ 100,00 dividido entre três participantes. | Colaboração com as evidências e informações técnicas dos testes de rateio. |
| **Maria Eduarda Bessa Braz** | [2640861] | Implementação dos métodos de resumo de orçamento, cálculo dos gastos individuais, recálculo de despesas e validações na classe Planejamento. | Integração das regras de negócio com as classes de despesas e colaboração na conferência dos cálculos do sistema. | Conferência dos totais do evento, gastos individuais, limites orçamentários e atualização dos resultados após alterações e exclusões. | Fornecimento de informações técnicas e justificativas sobre as regras de negócio para o relatório. |

### Melhorias identificadas durante os testes

Durante a validação da persistência, foi identificado que a funcionalidade de carregamento restaura sempre o primeiro planejamento salvo, sem permitir que o usuário escolha qual deseja recuperar.

Como melhoria, propõe-se implementar a identificação dos planejamentos por nomes personalizados, permitindo que o usuário salve e selecione planejamentos como:

- Férias de janeiro;
- Passeio para praia;
- Acampamento.

Essa funcionalidade tornaria a recuperação dos dados mais intuitiva e permitiria manter diferentes planejamentos salvos sem confundi-los.