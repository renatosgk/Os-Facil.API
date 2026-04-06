# 🔧 OsFacil API — Sistema de Gerenciamento de Ordens de Serviço Automotivo

O **OsFacil API** é uma API RESTful desenvolvida em **Spring Boot**, voltada para o gerenciamento completo de ordens de serviço no setor automotivo. A aplicação cobre todo o ciclo operacional de uma oficina: cadastro de clientes, veículos, produtos, funcionários e o acompanhamento das ordens de serviço do início ao fim.

🔗 **API em produção:** [https://osfacil.onrender.com](https://osfacil.onrender.com)  
📄 **Documentação Swagger:** [https://osfacil.onrender.com/swagger-ui.html](https://osfacil.onrender.com/swagger-ui.html)

---

## ✅ Funcionalidades

- **CRUD Completo** para Clientes, Funcionários, Veículos e Produtos
- **Controle de Ordens de Serviço** com acompanhamento de status e pagamento
- **Autenticação JWT** com geração e validação de tokens de acesso
- **HATEOAS** — respostas com hipermídia para navegação entre recursos
- **Controle de acesso por perfil** — Cliente, Funcionário e Admin com permissões distintas
- **Migração de banco com Flyway** — versionamento e rastreabilidade do schema
- **Documentação automática** via SpringDoc OpenAPI (Swagger UI)

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.4.4 | Base do projeto |
| Spring Security | — | Autenticação e autorização |
| Spring HATEOAS | — | Hipermídia nas respostas |
| Spring Data JPA | — | Persistência de dados |
| Oracle Database | 21c | Banco de dados em produção |
| Flyway | — | Versionamento do schema |
| JWT (Auth0) | 4.4.0 | Geração e validação de tokens |
| SpringDoc OpenAPI | 2.8.6 | Documentação da API |
| Lombok | 1.18.32 | Redução de boilerplate |
| Dotenv Java | 3.0.0 | Variáveis de ambiente |

---

## 🔐 Perfis de Acesso

| Perfil | Permissões |
|---|---|
| `ROLE_CLIENTE` | Consulta de veículos e ordens de serviço, realização de pagamentos |
| `ROLE_FUNCIONARIO` | Gerenciamento de clientes, veículos, produtos e ordens de serviço |
| `ROLE_ADMIN` | Acesso completo a todos os recursos |

---

## 📦 Pré-requisitos

Antes de rodar o projeto, certifique-se de ter instalado:

- [JDK 21+](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://maven.apache.org/) ou use o Maven Wrapper (`mvnw`) do projeto
- [Git](https://git-scm.com/)
- Banco de dados Oracle acessível (ou configure as variáveis de ambiente para apontar para o seu)

---

## 🚀 Como Rodar Localmente

### 1. Clone o repositório
```bash
git clone https://github.com/renatosgk/Os-Facil--sprint2.git
```

### 2. Entre na pasta do projeto
```bash
cd Os-Facil--sprint2
```

### 3. Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:
```env
DATABASE_URL=jdbc:oracle:thin:@localhost:1521/xepdb1
DATABASE_USERNAME=seu_usuario
DATABASE_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta
```

### 4. Rode o projeto
```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`  
O Swagger estará disponível em `http://localhost:8080/swagger-ui.html`

---

## 📡 Endpoints Principais

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/register` | Cadastro de novo cliente | Público |
| `POST` | `/login` | Autenticação e geração de token JWT | Público |
| `GET` | `/clientes` | Lista todos os clientes | Funcionário, Admin |
| `GET` | `/veiculos` | Lista todos os veículos | Cliente, Funcionário, Admin |
| `GET` | `/ordem-servicos` | Lista todas as ordens de serviço | Cliente, Funcionário, Admin |
| `POST` | `/ordem-servicos` | Cria uma nova ordem de serviço | Funcionário, Admin |
| `GET` | `/produtos` | Lista todos os produtos | Funcionário, Admin |
| `POST` | `/pagamentos` | Registra um pagamento | Cliente |

> Para acesso aos endpoints protegidos, inclua o token JWT no header:  
> `Authorization: Bearer {seu_token}`

---
