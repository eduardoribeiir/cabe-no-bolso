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

## Testes do objetivo extra (somente se implementado)

| # | Cenário | Esperado |
|---|---------|----------|
| E1 | Simulador no caso de referência (transporte e almoço essenciais; atividade prioridade 3; sobremesa prioridade 1) | Retira sobremesa, mantém atividade: Ana e Carla R$ 65,00, Bruno R$ 50,00, total R$ 180,00 |
| E2 | Orçamento de Ana em R$ 40,00 | Sem solução: essenciais de Ana custam R$ 50,00 (excesso de R$ 10,00) |
| E3 | Planejamento original após simular | Permanece inalterado |
