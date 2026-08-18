# Laboratório 02 — API REST, MySQL, Swagger e Docker

Este laboratório faz parte da disciplina **Backend: Cloud Computing** e dá continuidade à evolução incremental do microsserviço **`ms-task`** do projeto **MyTracker**.

No **Lab 01**, o microsserviço disponibilizava uma API REST com Java e Spring Boot e mantinha as tarefas somente em memória. Nesta segunda etapa, a aplicação passa a utilizar **persistência com MySQL**, documentação e testes da API com **Swagger/OpenAPI** e execução em **contêiner Docker**.

## Clonar a versão do Lab 02

Para clonar diretamente a versão utilizada no **Laboratório 02**, execute:

```powershell
git clone --branch lab-02 https://gitlab.com/gilbriatore/2026/backend/my-tracker/ms-task.git
```

> A tag `lab-02` preserva a versão do `ms-task` utilizada neste laboratório.

## Objetivo

Evoluir o microsserviço `ms-task` construído no Lab 01, adicionando persistência de dados e conteinerização, mantendo a separação de responsabilidades da aplicação.

Ao final do laboratório, o aluno deverá ser capaz de:

- persistir tarefas em um banco **MySQL**;
- utilizar **Spring Data JPA** para acesso aos dados;
- documentar e testar a API com **Swagger/OpenAPI**;
- gerar uma imagem Docker do microsserviço;
- executar o Spring Boot e o MySQL em contêineres conectados pela mesma rede Docker;
- versionar e publicar a imagem do microsserviço no Docker Hub.

## Tecnologias

- Java 17 ou superior
- Spring Boot
- Spring Web
- Spring Data JPA
- MySQL
- MySQL Driver
- Maven
- Swagger / OpenAPI (`springdoc-openapi`)
- Docker
- Docker Hub
- IntelliJ IDEA
- HTTP / JSON

## Evolução em relação ao Lab 01

No Lab 01, a arquitetura era:

```text
Cliente HTTP
     |
     v
TaskController
     |
     v
TaskService
     |
     v
List<Task> / memória
```

No Lab 02, a persistência passa a fazer parte da aplicação:

```text
Cliente HTTP
     |
     v
TaskController
     |
     v
TaskService
     |
     v
TaskRepository
     |
     v
   MySQL
```

A classe `Task` também passa a ser uma **entidade JPA**.

## Estrutura do projeto

```text
ms-task
├── src
│   └── main
│       ├── java
│       │   └── br
│       │       └── mytracker
│       │           └── mstask
│       │               ├── controller
│       │               │   └── TaskController.java
│       │               ├── domain
│       │               │   └── Task.java
│       │               ├── repository
│       │               │   └── TaskRepository.java
│       │               ├── service
│       │               │   └── TaskService.java
│       │               └── MsTaskApplication.java
│       └── resources
│           └── application.properties
├── Dockerfile
├── pom.xml
└── requests.http
```

### Responsabilidade das camadas

- **domain** — representa os conceitos do domínio e contém a entidade JPA.
- **controller** — recebe requisições HTTP e produz respostas REST/JSON.
- **service** — concentra as operações e regras da aplicação.
- **repository** — realiza o acesso aos dados utilizando Spring Data JPA.
- **MySQL** — mantém os dados persistidos.

## Recurso Task

Uma tarefa continua sendo representada pela API da seguinte forma:

```json
{
  "id": 1,
  "titulo": "Estudar Cloud Computing",
  "descricao": "Revisar persistência e Docker",
  "prioridade": "ALTA",
  "concluida": false
}
```

## API REST

| Método | Endpoint | Operação |
|---|---|---|
| `GET` | `/tasks` | Listar todas as tarefas |
| `GET` | `/tasks/{id}` | Consultar uma tarefa |
| `POST` | `/tasks` | Cadastrar uma tarefa |
| `PUT` | `/tasks/{id}` | Atualizar uma tarefa |
| `DELETE` | `/tasks/{id}` | Excluir uma tarefa |

## Persistência com MySQL

O MySQL é executado em um contêiner Docker conectado à rede `mytracker-net`.

Crie a rede:

```powershell
docker network create mytracker-net
```

Execute o MySQL no **PowerShell (Windows)**:

```powershell
docker run -d `
  --name mysql-task `
  --network mytracker-net `
  -p 3306:3306 `
  -e MYSQL_ROOT_PASSWORD=root `
  -e MYSQL_DATABASE=taskdb `
  mysql:8.4
```

No **Bash / Linux / macOS**:

```bash
docker run -d \
  --name mysql-task \
  --network mytracker-net \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=taskdb \
  mysql:8.4
```

Confirme:

```powershell
docker ps
```

## Configuração da aplicação

O `application.properties` utiliza variáveis de ambiente para permitir que a mesma aplicação seja executada localmente ou dentro de um contêiner.

Exemplo:

```properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:taskdb}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:root}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Quando a aplicação é executada diretamente pelo IntelliJ, o valor padrão de `DB_HOST` é `localhost`.

Quando o Spring Boot também é executado em contêiner, `DB_HOST` passa a receber `mysql-task`, nome pelo qual o banco pode ser localizado dentro da rede Docker.

## Swagger / OpenAPI

Com a dependência `springdoc-openapi-starter-webmvc-ui`, a documentação da API pode ser acessada em:

```text
http://localhost:8080/swagger-ui/index.html
```

A especificação OpenAPI fica disponível em:

```text
http://localhost:8080/v3/api-docs
```

O projeto também pode redirecionar a URL raiz:

```text
http://localhost:8080
```

para o Swagger UI.

No Swagger, teste as operações `GET`, `POST`, `PUT` e `DELETE`.

## Gerando a aplicação

Antes de construir a imagem Docker, gere o arquivo JAR:

**PowerShell (Windows):**

```powershell
.\mvnw.cmd clean package
```

**Bash / Linux / macOS:**

```bash
./mvnw clean package
```

Se o Maven estiver instalado globalmente:

```powershell
mvn clean package
```

O arquivo será gerado no diretório `target/`.

## Criando a imagem Docker

Na raiz do projeto:

```powershell
docker build -t ms-task:1.0.0 .
```

Confirme:

```powershell
docker images
```

## Mudança de arquitetura

Até este momento, ao executar o Spring Boot diretamente pelo IntelliJ, a arquitetura é:

```text
ms-task executando no IntelliJ
        |
        | localhost:3306
        v
+---------------------+
| Container MySQL     |
| mysql-task          |
| porta 3306          |
+---------------------+
```

Nesse cenário, `localhost` representa a máquina onde o IntelliJ está executando a aplicação.

Ao executar também o Spring Boot em contêiner, a arquitetura muda:

```text
              mytracker-net
+--------------------------------------+
|                                      |
|  +--------------+    +------------+  |
|  |   ms-task    |--->| mysql-task |  |
|  | Spring Boot  |    |   MySQL    |  |
|  |    :8080     |    |   :3306    |  |
|  +--------------+    +------------+  |
|                                      |
+--------------------------------------+
        |
        | -p 8080:8080
        v
 http://localhost:8080
```

Dentro do contêiner `ms-task`, `localhost` representa o próprio contêiner do Spring Boot. Por isso, o banco deve ser acessado pelo nome `mysql-task`.

Como ambos estão conectados à rede `mytracker-net`, o Docker fornece resolução de nomes entre os contêineres.

## Executando o ms-task em contêiner

**PowerShell (Windows):**

```powershell
docker run -d `
  --name ms-task `
  --network mytracker-net `
  -p 8080:8080 `
  -e DB_HOST=mysql-task `
  -e DB_PORT=3306 `
  -e DB_NAME=taskdb `
  -e DB_USER=root `
  -e DB_PASSWORD=root `
  ms-task:1.0.0
```

O parâmetro:

```text
DB_HOST=mysql-task
```

faz com que o Spring Boot encontre o contêiner MySQL pela rede Docker.

Confirme:

```powershell
docker ps
```

Depois acesse:

```text
http://localhost:8080
```

ou:

```text
http://localhost:8080/swagger-ui/index.html
```

## Publicando no Docker Hub

Faça login:

```powershell
docker login
```

Substitua `<seu-usuario-dockerhub>` pelo seu usuário real do Docker Hub.

Exemplo:

```powershell
docker tag ms-task:1.0.0 <seu-usuario-dockerhub>/ms-task:1.0.0
docker tag ms-task:1.0.0 <seu-usuario-dockerhub>/ms-task:latest
```

Publique:

```powershell
docker push <seu-usuario-dockerhub>/ms-task:1.0.0
docker push <seu-usuario-dockerhub>/ms-task:latest
```

> O nome do usuário e do repositório Docker deve estar em letras minúsculas.

## Atividade

Depois de concluir a evolução do `ms-task`, aplique os mesmos conceitos ao microsserviço definido pela sua equipe:

1. transforme a classe de domínio em uma entidade JPA;
2. crie o `Repository`;
3. substitua o armazenamento em memória pela persistência;
4. configure um banco de dados;
5. documente e teste a API com Swagger/OpenAPI;
6. crie o `Dockerfile`;
7. gere a imagem Docker;
8. execute aplicação e banco em contêineres;
9. publique a imagem no Docker Hub;
10. versione o código no repositório da equipe.

## Checklist

- [ ] A aplicação inicia sem erros.
- [ ] `Task` está configurada como entidade JPA.
- [ ] `TaskRepository` está implementado.
- [ ] Os dados são persistidos no MySQL.
- [ ] O CRUD REST continua funcionando.
- [ ] O Swagger UI está disponível.
- [ ] A imagem `ms-task:1.0.0` foi criada.
- [ ] MySQL e `ms-task` executam em contêineres.
- [ ] Os contêineres estão conectados à `mytracker-net`.
- [ ] O `ms-task` acessa o banco utilizando `DB_HOST=mysql-task`.
- [ ] A imagem foi versionada e publicada no Docker Hub.
- [ ] O código foi versionado no repositório da equipe.

## DevLabs

O passo a passo completo deste laboratório está disponível no **DevLabs — Escola de Dev**:

**Laboratório API REST, MySQL e Docker:**  
https://devlabs.escdodev.com.br/labs/backend-api-mysql-docker

---

**Disciplina:** Backend: Cloud Computing  
**Projeto de referência:** MyTracker  
**Microsserviço:** `ms-task`
