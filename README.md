# 📋 Papelogistic

Sistema de gerenciamento desenvolvido especificamente para uma papelaria, com o objetivo de centralizar e organizar os principais processos administrativos e operacionais do estabelecimento.

O sistema foi desenvolvido para funcionamento em ambiente local, sem necessidade de conexão com a internet ou com serviços externos. A aplicação utiliza Java e Java Swing para a interface e MySQL para armazenamento dos dados.

## 🎯 Objetivo

O Papelogistic tem como objetivo facilitar o controle e a organização das informações da papelaria, substituindo processos manuais e descentralizados por um sistema único de gerenciamento.

Entre os principais objetivos estão:

- Organizar o controle de estoque
- Registrar entradas e saídas de produtos
- Auxiliar no controle financeiro
- Gerenciar pedidos
- Centralizar informações da papelaria
- Disponibilizar uma visão geral das operações
- Armazenar os dados de forma estruturada em um banco de dados

## 🖥️ Tecnologias utilizadas

- Java
- Java Swing
- MySQL
- JDBC
- NetBeans
- Visual Studio Code

O projeto utiliza exclusivamente recursos e bibliotecas padrão do ecossistema Java, sem utilização de frameworks ou bibliotecas externas para desenvolvimento da aplicação.

## Organização dos pacotes

```text
src/
├── model/
│   ├── entidades/   # Objetos do domínio
│   ├── dao/         # Acesso e persistência de dados
│   └── conexao/     # Conexão JDBC
├── controller/
│   └── terminal/   # Coordenação das ações do terminal
├── view/
│   ├── terminal/   # Menus, entrada e saída de texto
│   └── swing/      # Telas gráficas e formulários NetBeans
└── images/         # Recursos da interface gráfica
```

Os pacotes usam letras minúsculas e correspondem às pastas sob `src`.
As classes usam UpperCamelCase; `LoginPage.java` e `LoginPage.form` permanecem
juntos para edição visual no NetBeans. Consulte [ORGANIZACAO-MVC.txt](ORGANIZACAO-MVC.txt)
para os nomes das classes e suas responsabilidades.

## Terminal de testes de conexão e CRUD

Execute o projeto no NetBeans (F6). A classe principal é `view.terminal.TerminalPrincipal`.
A interface Swing está disponível em `view.swing.Principal`.

O menu permite testar a conexão e cadastrar, listar, atualizar e excluir Cliente,
Produto, Usuario, Estoque, Pedido, ItemPedido e Financeiro. Atualizações solicitam
os identificadores e todos os demais campos; ItemPedido usa CodPedido e CodProduto.
Exclusões só acontecem após digitar SIM. A listagem de usuários não exibe senhas.
Valores decimais aceitam ponto ou vírgula, sem separador de milhares.
Erros do banco retornam ao menu principal. Registros não afetados são informados.

### Preparação do banco

- Instale o JDK 23, configurado no projeto NetBeans.
- Coloque o driver JDBC MySQL em `lib/mysql-connector-j-26.7.0.jar`.
- Inicie o MySQL e disponibilize o banco `Papelogistic` com as tabelas esperadas
  pelos DAOs. O ZIP original não contém o script SQL de criação das tabelas.
- Confira URL, usuário e senha em `src/model/conexao/Conexao.java`.
  O padrão atual é localhost:3306, usuário root e senha vazia.
- Cadastre os registros referenciados antes dos pedidos e itens, conforme as
  chaves estrangeiras do seu banco. As alterações do menu são persistidas.

### Execução pelo terminal (PowerShell, na raiz do projeto)

```powershell
New-Item -ItemType Directory -Force build/classes | Out-Null
Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' } | Set-Content -Encoding utf8 fontes.txt
javac -encoding UTF-8 -d build/classes '@fontes.txt'
Copy-Item src/images build/classes/images -Recurse -Force
java -cp 'build/classes;lib/*' view.terminal.TerminalPrincipal
```

### Validação realizada

As classes foram compiladas com o JDK 19 disponível no ambiente. Foram exercitados
menus, validação de números, decimais com vírgula, cancelamento de exclusão,
encerramento da entrada e tratamento de ausência do driver. A compilação com o
alvo 23 pelo NetBeans e o CRUD com MySQL real dependem da preparação acima.
