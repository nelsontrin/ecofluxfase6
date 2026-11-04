# language: pt
Funcionalidade: Operações de resíduos e alertas
  Para manter o inventário dos pontos consistente
  Como operador autenticado
  Quero registrar descartes e coletas e consultar alertas de capacidade

  Cenário: Listar descartes
    Dado que estou autenticado
    Quando envio GET para "/descartes"
    Então o status HTTP deve ser 200
    E o corpo da resposta deve obedecer ao contrato "descartes.schema.json"

  Cenário: Consultar descartes de um ponto
    Dado que estou autenticado
    E que existe um ponto com capacidade 100 e volume atual 10
    Quando envio GET para "/descartes/ponto/{id}"
    Então o status HTTP deve ser 200
    E o corpo da resposta deve obedecer ao contrato "descartes.schema.json"
    E a resposta deve ser uma lista não vazia

  Cenário: Registrar descarte e atualizar o inventário
    Dado que estou autenticado
    E que existe um ponto com capacidade 100 e volume atual 40
    Quando envio POST para "/descartes" com JSON:
      """
      {"pontoColetaId":{{pontoId}},"tipoResiduo":"PAPEL","volume":20,"reciclavel":true}
      """
    Então o status HTTP deve ser 201
    E o corpo da resposta deve obedecer ao contrato "descarte.schema.json"
    E o volume atual do ponto deve ser 60

  Cenário: Rejeitar descarte acima da capacidade
    Dado que estou autenticado
    E que existe um ponto com capacidade 100 e volume atual 90
    Quando envio POST para "/descartes" com JSON:
      """
      {"pontoColetaId":{{pontoId}},"tipoResiduo":"PLASTICO","volume":11,"reciclavel":true}
      """
    Então o status HTTP deve ser 400
    E o corpo da resposta deve obedecer ao contrato "error.schema.json"
    E o volume atual do ponto deve ser 90

  Cenário: Rejeitar descarte em ponto inativo
    Dado que estou autenticado
    E que existe um ponto ativo com capacidade 100
    Quando envio DELETE para "/pontos-coleta/{id}"
    Então o status HTTP deve ser 204
    Quando envio POST para "/descartes" com JSON:
      """
      {"pontoColetaId":{{pontoId}},"tipoResiduo":"VIDRO","volume":5,"reciclavel":true}
      """
    Então o status HTTP deve ser 400
    E o corpo da resposta deve obedecer ao contrato "error.schema.json"

  Cenário: Gerar alerta médio ao atingir 85 por cento da capacidade
    Dado que estou autenticado
    E que existe um ponto com capacidade 100 e volume atual 80
    Quando envio POST para "/descartes" com JSON:
      """
      {"pontoColetaId":{{pontoId}},"tipoResiduo":"PAPEL","volume":5,"reciclavel":true}
      """
    Então o status HTTP deve ser 201
    Quando envio GET para "/alertas/pendentes"
    Então o status HTTP deve ser 200
    E o corpo da resposta deve obedecer ao contrato "alertas.schema.json"
    E o primeiro alerta deve ter nível "MEDIO"

  Cenário: Consultar alertas gerais e pendentes
    Dado que estou autenticado
    E que existe um alerta pendente
    Quando envio GET para "/alertas"
    Então o status HTTP deve ser 200
    E o corpo da resposta deve obedecer ao contrato "alertas.schema.json"
    E o primeiro alerta deve ter nível "ALTO"
    Quando envio GET para "/alertas/pendentes"
    Então o status HTTP deve ser 200
    E o corpo da resposta deve obedecer ao contrato "alertas.schema.json"
    E o primeiro alerta deve ter nível "ALTO"

  Cenário: Registrar coleta reduz inventário e resolve alerta pendente
    Dado que estou autenticado
    E que existe um alerta pendente
    Quando envio POST para "/coletas" com JSON:
      """
      {"pontoColetaId":{{pontoId}},"empresaResponsavel":"Coleta BDD","volumeColetado":10,"destinoFinal":"Cooperativa de reciclagem"}
      """
    Então o status HTTP deve ser 201
    E o corpo da resposta deve obedecer ao contrato "coleta.schema.json"
    E o volume atual do ponto deve ser 85
    E a lista de alertas pendentes deve estar vazia

  Cenário: Rejeitar coleta superior ao volume armazenado
    Dado que estou autenticado
    E que existe um ponto com capacidade 100 e volume atual 10
    Quando envio POST para "/coletas" com JSON:
      """
      {"pontoColetaId":{{pontoId}},"empresaResponsavel":"Coleta BDD","volumeColetado":11,"destinoFinal":"Cooperativa de reciclagem"}
      """
    Então o status HTTP deve ser 400
    E o corpo da resposta deve obedecer ao contrato "error.schema.json"
    E o volume atual do ponto deve ser 10

  Cenário: Listar coletas registradas
    Dado que estou autenticado
    E que existe um ponto com capacidade 100 e volume atual 20
    Quando envio POST para "/coletas" com JSON:
      """
      {"pontoColetaId":{{pontoId}},"empresaResponsavel":"Coleta BDD","volumeColetado":5,"destinoFinal":"Cooperativa de reciclagem"}
      """
    Então o status HTTP deve ser 201
    Quando envio GET para "/coletas"
    Então o status HTTP deve ser 200
    E o corpo da resposta deve obedecer ao contrato "coletas.schema.json"
