# language: pt
Funcionalidade: Autenticação e proteção das operações
  Como cliente da API EcoFlux
  Quero autenticar com credenciais válidas
  Para obter um token JWT e acessar as operações protegidas

  Cenário: Emitir token para credenciais válidas
    Quando envio POST para "/auth/login" com JSON:
      """
      {"login":"bdd-operador","senha":"EcoFlux-BDD-2026!"}
      """
    Então o status HTTP deve ser 200
    E o corpo da resposta deve obedecer ao contrato "token.schema.json"
    E o campo "tipo" deve ser "Bearer"
    E o campo "token" deve conter texto

  Cenário: Rejeitar credenciais inválidas
    Quando envio POST para "/auth/login" com JSON:
      """
      {"login":"bdd-operador","senha":"senha-incorreta"}
      """
    Então o status HTTP deve ser 403

  Esquema do Cenário: Negar acesso sem token em operações protegidas
    Dado que não estou autenticado
    Quando envio <metodo> para "<rota>" sem token
    Então o status HTTP deve ser 403

    Exemplos:
      | metodo | rota                         |
      | POST   | /pontos-coleta               |
      | PUT    | /pontos-coleta/{id}          |
      | DELETE | /pontos-coleta/{id}          |
      | GET    | /descartes                   |
      | GET    | /descartes/ponto/{id}        |
      | POST   | /descartes                   |
      | GET    | /coletas                     |
      | POST   | /coletas                     |
      | GET    | /alertas                     |
      | GET    | /alertas/pendentes           |
