# Roteiro de testes (seção 4 do enunciado)

Para cada teste, preencher **resultado obtido** e anexar **evidência** (captura em `docs/evidencias/`).
Para reproduzir: compile e execute conforme o README, cadastre o caso de `dados/CASO_REFERENCIA.md`
e siga os passos abaixo.

| # | Cenário | Entrada / passos | Resultado esperado | Resultado obtido | Evidência |
|---|---------|------------------|--------------------|------------------|-----------|
| 1 | Caso principal | Cadastrar Ana, Bruno, Carla e as 4 despesas | Ana 75,00; Bruno 60,00; Carla 75,00; total 210,00; Ana e Carla excedem R$ 5,00; Bruno dentro | _preencher_ | `docs/evidencias/t01.png` |
| 2 | Centavos no rateio | Despesa fixa de R$ 100,00 para códigos 1, 2 e 3 | R$ 33,34 (cód. 1), R$ 33,33 (cód. 2), R$ 33,33 (cód. 3); soma R$ 100,00 | _preencher_ | `docs/evidencias/t02.png` |
| 3 | Despesa sem participantes | Tentar incluir despesa sem selecionar ninguém | Recusa com mensagem; dados inalterados | _preencher_ | `docs/evidencias/t03a.png` |
| 3 | Valor negativo | Tentar incluir despesa com valor -10 | Recusa com mensagem; dados inalterados | _preencher_ | `docs/evidencias/t03b.png` |
| 3 | Campo vazio | Tentar incluir despesa sem descrição | Recusa com mensagem; dados inalterados | _preencher_ | `docs/evidencias/t03c.png` |
| 3 | Código duplicado | Cadastrar participante com código já existente | Recusa com mensagem; dados inalterados | _preencher_ | `docs/evidencias/t03d.png` |
| 4 | Alteração de orçamento | No caso principal, mudar orçamento de Ana para R$ 80,00 | Custo de Ana continua R$ 75,00 e agora está dentro do limite | _preencher_ | `docs/evidencias/t04.png` |
| 5 | Exclusão de despesa | No caso principal original, excluir "Atividade recreativa" | R$ 60,00 por pessoa; total R$ 180,00 | _preencher_ | `docs/evidencias/t05.png` |
| 6 | Salvar e reiniciar | Salvar dois planejamentos com nomes diferentes, fechar, abrir e escolher um na lista | O planejamento escolhido e seus dados, vínculos e resultados voltam sem duplicação | _preencher_ | `docs/evidencias/t06.png` |
| 7 | Arquivo ausente | Carregar com arquivo inexistente | Mensagem clara; permite iniciar planejamento vazio | _preencher_ | `docs/evidencias/t07a.png` |
| 7 | Arquivo inválido | Carregar um arquivo qualquer (ex.: .txt renomeado) | Mensagem clara; planejamento em uso preservado | _preencher_ | `docs/evidencias/t07b.png` |
| 8 | Excluir participante vinculado | (se implementado) excluir participante com despesas | Exclusão impedida com mensagem | _preencher_ | `docs/evidencias/t08.png` |
| 9 | Alterar nome | Selecionar participante, informar nome válido e alterar | Nome atualizado no cadastro e nos resumos, sem alterar orçamento ou despesas | _preencher_ | `docs/evidencias/t09.png` |
| 10 | Editar despesa | Selecionar despesa, alterar descrição, valor, tipo ou participantes | Despesa atualizada e totais/rateios recalculados | _preencher_ | `docs/evidencias/t10.png` |
| 11 | Edição inválida | Tentar editar despesa com valor inválido ou sem participantes | Recusa com mensagem; despesa e totais permanecem inalterados | _preencher_ | `docs/evidencias/t11.png` |

## Testes do objetivo extra

Na aba **Simulador**, escolha **Opcional** na classificação das despesas que podem ser retiradas. Cada uma recebe prioridade inicial 1; ajuste as prioridades, se necessário, e clique em **Gerar sugestão automática** para encontrar a combinação viável de maior prioridade total. O resultado mostra as despesas mantidas e retiradas e compara os totais antes e depois. Se as essenciais excederem algum orçamento, a tela informa que não há solução e recomenda revisar a classificação. **Marcar todas como essenciais** reinicia a classificação, que também é reiniciada quando o planejamento é alterado.

| # | Cenário | Esperado |
|---|---------|----------|
| E1 | Simulador no caso de referência (transporte e almoço essenciais; atividade prioridade 3; sobremesa prioridade 1) | Retira sobremesa, mantém atividade: Ana e Carla R$ 65,00, Bruno R$ 50,00, total R$ 180,00 |
| E2 | Orçamento de Ana em R$ 40,00 | Sem solução: essenciais de Ana custam R$ 50,00 (excesso de R$ 10,00) |
| E3 | Planejamento original após simular | Permanece inalterado |
| E4 | Orçamento R$ 100,00; opcionais de R$ 70,00 (prioridade 6), R$ 50,00 (4) e R$ 50,00 (4) | Mantém as duas de R$ 50,00: prioridade total 8 e custo R$ 100,00 |
| E5 | Dez opcionais válidas; depois tentar onze ou uma prioridade zero | Aceita dez; recusa onze ou prioridade não positiva sem modificar o planejamento |

Os cenários E1 a E5 também são verificados por `testes/TestesSimulador.java`. O resultado automatizado não substitui as capturas de tela e a descrição exigidas no relatório da equipe.

### Resultados automatizados obtidos

| Cenário | Resultado obtido |
|---------|------------------|
| E1 | Sobremesa retirada; Ana R$ 65,00, Bruno R$ 50,00, Carla R$ 65,00 e total R$ 180,00. Antes: R$ 75,00, R$ 60,00, R$ 75,00 e total R$ 210,00. |
| E2 | Sem solução; essenciais de Ana R$ 50,00, com excesso de R$ 10,00. |
| E3 | Despesas e custo original de R$ 210,00 preservados após as duas simulações. |
| E4 | Mantidas as duas despesas de R$ 50,00, prioridade total 8 e custo R$ 100,00. |
| E5 | Dez opcionais aceitas; onze opcionais e prioridade zero recusadas. |

O teste também verifica o rateio de R$ 100,00 em R$ 33,34, R$ 33,33 e R$ 33,33. `testes/TestesSimuladorVisao.java` verifica a interação com a aba e a exibição dos resultados sem abrir uma janela.
