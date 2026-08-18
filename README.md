# Laboratório API REST e Microsserviços

Este laboratório faz parte da disciplina **Backend: Cloud Computing** e
apresenta, de forma prática e incremental, a implementação de uma **API
REST com Java e Spring Boot**.

O exemplo utiliza o **MyTracker**, uma solução integrada que
poderá evoluir ao longo da disciplina para uma arquitetura Cloud Native
composta por diferentes microsserviços.

Nesta primeira etapa será desenvolvido o microsserviço **`ms-task`**,
responsável pelo contexto de **Gestão de Tarefas**.

### Clonar a versão do Lab 01

Para clonar diretamente a versão utilizada no **Laboratório 01**, execute:

```bash
git clone --branch lab-01 https://gitlab.com/gilbriatore/2026/backend/my-tracker/ms-task.git
```
## Objetivo

Compreender como uma API REST é organizada em uma aplicação Spring Boot
e implementar um microsserviço simples utilizando separação de
responsabilidades entre **Domain**, **Service** e **Controller**.

Ao final do laboratório, a aplicação deverá disponibilizar operações
REST para cadastrar, consultar, atualizar e excluir tarefas.

## Tecnologias

-   Java 17 ou superior
-   Spring Boot
-   Spring Web
-   Maven
-   IntelliJ IDEA
-   HTTP/JSON
-   IntelliJ HTTP Client, Postman ou Insomnia

## Arquitetura desta versão

``` text
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

Nesta primeira versão os dados são mantidos **somente em memória**.
Ainda não são utilizados banco de dados, Docker, Kubernetes, RabbitMQ,
segurança ou mecanismos externos de Service Discovery.

Esses componentes serão incorporados progressivamente nas próximas
aulas.

## Projeto Spring Boot

Configuração utilizada no laboratório:

``` text
Language: Java
Type: Maven
Group: br.mytracker
Artifact: ms-task
Package: br.mytracker.mstask
Packaging: Jar
Java: 17 ou superior
```

Dependências:

-   **Spring Web** --- criação dos endpoints HTTP.
-   **Spring Boot DevTools** --- apoio ao desenvolvimento.
-   **Spring Boot Starter Test** --- normalmente incluído pelo Spring
    Initializr.

## Estrutura do projeto

``` text
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
│       │               ├── service
│       │               │   └── TaskService.java
│       │               └── MsTaskApplication.java
│       └── resources
│           └── application.properties
├── pom.xml
└── requests.http
```

### Responsabilidade das camadas

-   **domain** --- representa os conceitos e objetos do contexto.
-   **controller** --- recebe requisições HTTP e produz respostas
    REST/JSON.
-   **service** --- concentra as regras e operações da aplicação.
-   **repository** --- será utilizado posteriormente quando adicionarmos
    persistência.

## Recurso Task

Uma tarefa possui inicialmente a seguinte representação:

``` json
{
  "id": 1,
  "titulo": "Estudar Cloud Computing",
  "descricao": "Revisar APIs REST e microsserviços",
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

A URL identifica o **recurso**, enquanto o método HTTP representa a
**operação** realizada sobre ele.

## Executando a aplicação

Pelo IntelliJ IDEA, execute a classe principal da aplicação.

Ou, na raiz do projeto:

``` bash
mvn spring-boot:run
```

A aplicação estará disponível em:

``` text
http://localhost:8080
```

Para verificar inicialmente a API:

``` http
GET http://localhost:8080/tasks
```

Como os dados são mantidos em memória, a primeira resposta deverá ser:

``` json
[]
```

## Testando o CRUD

### Criar uma tarefa

``` http
POST http://localhost:8080/tasks
Content-Type: application/json

{
  "titulo": "Estudar Cloud Computing",
  "descricao": "Revisar APIs REST e microsserviços",
  "prioridade": "ALTA",
  "concluida": false
}
```

Resposta esperada: **201 Created**.

### Listar tarefas

``` http
GET http://localhost:8080/tasks
Accept: application/json
```

Resposta esperada: **200 OK**.

### Consultar uma tarefa

``` http
GET http://localhost:8080/tasks/1
Accept: application/json
```

Resposta esperada: **200 OK** ou **404 Not Found** caso o ID não exista.

### Atualizar uma tarefa

``` http
PUT http://localhost:8080/tasks/1
Content-Type: application/json

{
  "titulo": "Estudar Cloud Computing",
  "descricao": "Finalizar o laboratório de API REST",
  "prioridade": "MEDIA",
  "concluida": true
}
```

Resposta esperada: **200 OK**.

### Excluir uma tarefa

``` http
DELETE http://localhost:8080/tasks/1
```

Resposta esperada: **204 No Content**.

## Códigos HTTP utilizados

| Código | Significado |
|---|---|
| `200 OK` | Consulta ou atualização realizada |
| `201 Created` | Recurso criado |
| `204 No Content` | Recurso excluído |
| `404 Not Found` | Recurso não encontrado |

## Atividade

Depois de concluir o exemplo do **MyTracker**, adapte a implementação ao
cenário definido pela sua equipe.

1.  Identifique uma funcionalidade de negócio candidata a microsserviço.
2.  Defina o recurso principal e seus atributos.
3.  Defina os endpoints REST.
4.  Crie um projeto Spring Boot independente.
5.  Implemente as camadas `domain`, `service` e `controller`.
6.  Mantenha os objetos em memória nesta primeira versão.
7.  Teste `GET`, `POST`, `PUT` e `DELETE`.
8.  Versione o código no repositório da equipe.
9.  Relacione a implementação às Tasks técnicas correspondentes no Azure
    Boards.

## Checklist

-   [ ] A aplicação inicia sem erros.
-   [ ] O contexto do microsserviço está claramente definido.
-   [ ] `GET` lista os recursos.
-   [ ] `GET /{id}` consulta um recurso.
-   [ ] `POST` cria um recurso e retorna `201`.
-   [ ] `PUT` atualiza um recurso.
-   [ ] `DELETE` remove um recurso e retorna `204`.
-   [ ] Um ID inexistente retorna `404`.
-   [ ] O código foi versionado no repositório da equipe.

## DevLabs 

O passo a passo completo deste laboratório está disponível no **DevLabs**:

**Laboratório API REST e Microsserviços:**  
https://devlabs.escdodev.com.br/labs/backend-api-rest

---

**Disciplina:** Backend: Cloud Computing  
**Projeto de referência:** MyTracker  
**Microsserviço:** `ms-task`