# Caso de referência (seção 4 do enunciado)

Roteiro para cadastrar os dados na aplicação e conferir os resultados.

## 1. Participantes

| Código | Nome  | Orçamento |
|--------|-------|-----------|
| 1      | Ana   | R$ 70,00  |
| 2      | Bruno | R$ 60,00  |
| 3      | Carla | R$ 70,00  |

## 2. Despesas

| Descrição            | Tipo       | Valor    | Participantes        |
|----------------------|------------|----------|----------------------|
| Transporte           | Fixa       | R$ 90,00 | Ana, Bruno e Carla   |
| Almoço               | Por pessoa | R$ 20,00 | Ana, Bruno e Carla   |
| Atividade recreativa | Fixa       | R$ 30,00 | Ana e Carla          |
| Sobremesa            | Por pessoa | R$ 10,00 | Ana, Bruno e Carla   |

## 3. Resultados esperados

| Pessoa | Transporte | Almoço | Atividade | Sobremesa | Total  | Situação                  |
|--------|-----------:|-------:|----------:|----------:|-------:|---------------------------|
| Ana    | 30,00      | 20,00  | 15,00     | 10,00     | 75,00  | Excede em R$ 5,00         |
| Bruno  | 30,00      | 20,00  | 0,00      | 10,00     | 60,00  | Dentro (igual ao limite)  |
| Carla  | 30,00      | 20,00  | 15,00     | 10,00     | 75,00  | Excede em R$ 5,00         |

**Custo total: R$ 210,00.**

## 4. Arquivo de exemplo

Depois que a persistência estiver pronta, cadastre os dados acima, salve e copie o arquivo
gerado para `dados/caso_referencia.dat`. Ele precisa ser gerado pela **versão final** do código
(serialização depende das classes e do `serialVersionUID`).
