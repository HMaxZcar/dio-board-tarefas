# Board de Tarefas em Java

Projeto desenvolvido para o desafio **Criando seu Board de Tarefas com Java** da DIO.

A aplicação implementa um board estilo Kanban executado no terminal, utilizando Java, JDBC, MySQL, Maven e Liquibase.

## Funcionalidades

- Criar boards.
- Criar automaticamente as colunas `A Fazer`, `Em Andamento`, `Concluído` e `Cancelado`.
- Listar e selecionar boards.
- Criar cards na coluna inicial.
- Mover cards para a próxima coluna.
- Bloquear cards informando o motivo.
- Desbloquear cards informando o motivo.
- Cancelar cards.
- Visualizar cards separados por coluna.
- Excluir boards.
- Persistir os dados em MySQL.
- Criar/atualizar a estrutura do banco automaticamente com Liquibase.

## Regras de negócio

1. Todo card é criado na coluna inicial.
2. Um card bloqueado não pode ser movido nem cancelado.
3. Um card só pode avançar para a próxima coluna normal do fluxo.
4. Um card na coluna final não pode avançar nem ser cancelado.
5. Um card cancelado não pode voltar ao fluxo normal.
6. O bloqueio e o desbloqueio exigem um motivo.

## Tecnologias

- Java 21
- Maven
- JDBC
- MySQL 8
- Liquibase
- Docker Compose (opcional, para subir o MySQL rapidamente)

## Estrutura

```text
dio-board-tarefas/
├── src/main/java/br/com/dio/board/
│   ├── Main.java
│   ├── config/
│   │   ├── ConnectionConfig.java
│   │   └── DatabaseMigration.java
│   ├── dao/
│   │   ├── BoardDAO.java
│   │   ├── BoardColumnDAO.java
│   │   ├── CardDAO.java
│   │   └── CardBlockDAO.java
│   ├── exception/
│   │   └── BusinessException.java
│   ├── model/
│   │   ├── Board.java
│   │   ├── BoardColumn.java
│   │   ├── BoardColumnType.java
│   │   ├── Card.java
│   │   └── CardBlock.java
│   ├── service/
│   │   ├── BoardService.java
│   │   └── CardService.java
│   └── ui/
│       └── MainMenu.java
├── src/main/resources/db/changelog/
│   └── db.changelog-master.xml
├── docker-compose.yml
├── pom.xml
├── README.md
└── .gitignore
```

## Como executar

### 1. Pré-requisitos

Instale:

- JDK 21
- Maven 3.9+
- Docker Desktop, caso queira usar o MySQL via Docker

Confirme:

```bash
java -version
mvn -version
```

### 2. Subir o MySQL

Na raiz do projeto:

```bash
docker compose up -d
```

O `docker-compose.yml` cria:

- Banco: `board`
- Usuário: `board`
- Senha: `board`
- Porta: `3306`

### 3. Compilar

```bash
mvn clean compile
```

### 4. Executar

```bash
mvn exec:java
```

Na primeira execução, Liquibase cria automaticamente as tabelas.

## Configuração alternativa do banco

Você pode sobrescrever as credenciais com variáveis de ambiente:

```text
DB_URL=jdbc:mysql://localhost:3306/board?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USER=board
DB_PASSWORD=board
```

## Menu principal

```text
===========================
      BOARD DE TAREFAS
===========================
1 - Criar novo board
2 - Selecionar board
3 - Excluir board
4 - Sair
```

Dentro de um board:

```text
1 - Criar card
2 - Mover card
3 - Bloquear card
4 - Desbloquear card
5 - Cancelar card
6 - Visualizar board
7 - Voltar
```

## Banco de dados

As tabelas são:

- `boards`
- `board_columns`
- `cards`
- `card_blocks`

O histórico de bloqueio/desbloqueio fica registrado em `card_blocks`.

## Envio para o GitHub

Crie um repositório vazio no GitHub e execute:

```bash
git init
git add .
git commit -m "feat: implementa board de tarefas"
git branch -M main
git remote add origin https://github.com/SEU-USUARIO/dio-board-tarefas.git
git push -u origin main
```

Depois, entregue na DIO a URL do seu repositório.

## Autor

Hubert Zavaleta
