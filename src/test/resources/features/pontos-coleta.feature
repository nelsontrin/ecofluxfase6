# language: pt
Funcionalidade: Gestão de pontos de coleta
  Para controlar a capacidade dos locais de descarte
  Como operador autenticado
  Quero consultar, cadastrar, atualizar e desativar pontos

  Cenário: Listar pontos por rota pública
    Quando envio GET para "/pontos-coleta"
    Então o status HTTP deve ser 200
    E o corpo da resposta deve obedecer ao contrato "pontos.schema.json"

  Cenário: Consultar ponto existente sem autenticação
    Dado que estou autenticado
    E que existe um ponto ativo com capacidade 100
    Quando envio GET para "/pontos-coleta/{id}" sem token
    Então o status HTTP deve ser 200
    E o corpo da resposta deve obedecer ao contrato "ponto.schema.json"
    E o campo "ativo" deve ser "true"
    E o campo numérico "volumeAtual" deve ser 0

  Cenário: Retornar 404 para ponto inexistente
    Quando envio GET para "/pontos-coleta/999999999"
    Então o status HTTP deve ser 404
    E o corpo da resposta deve obedecer ao contrato "error.schema.json"

  Cenário: Cadastrar ponto com dados válidos
    Dado que estou autenticado
    Quando envio POST para "/pontos-coleta" com JSON:
      """
      {"nome":"Ponto BDD válido","endereco":"Rua das Flores, 50","capacidadeMaxima":100}
      """
    Então o status HTTP deve ser 201
    E o corpo da resposta deve obedecer ao contrato "ponto.schema.json"
    E o campo "ativo" deve ser "true"
    E o campo numérico "volumeAtual" deve ser 0

  Cenário: Rejeitar capacidade máxima igual a zero
    Dado que estou autenticado
    Quando envio POST para "/pontos-coleta" com JSON:
      """
      {"nome":"Ponto inválido","endereco":"Rua sem saída, 1","capacidadeMaxima":0}
      """
    Então o status HTTP deve ser 400
    E o corpo da resposta deve obedecer ao contrato "validation-error.schema.json"

  Cenário: Atualizar os dados de um ponto
    Dado que estou autenticado
    E que existe um ponto ativo com capacidade 100
    Quando envio PUT para "/pontos-coleta/{id}" com JSON:
      """
      {"nome":"Ponto atualizado","endereco":"Avenida Central, 200","capacidadeMaxima":150}
      """
    Então o status HTTP deve ser 200
    E o corpo da resposta deve obedecer ao contrato "ponto.schema.json"
    E o campo "nome" deve ser "Ponto atualizado"
    E o campo numérico "capacidadeMaxima" deve ser 150

  Cenário: Desativar ponto sem removê-lo fisicamente
    Dado que estou autenticado
    E que existe um ponto ativo com capacidade 100
    Quando envio DELETE para "/pontos-coleta/{id}"
    Então o status HTTP deve ser 204
    E o corpo da resposta deve estar vazio
    E o ponto deve continuar consultável e inativo
