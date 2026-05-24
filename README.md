# OS Fácil — Backend

API REST para gestão de ordens de serviço em oficinas automotivas.  
Desenvolvida com **Spring Boot 3**, **Spring Security 6 + JWT**, **Oracle Database** e **Spring HATEOAS**.

---

## Tecnologias

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 22 | Linguagem principal |
| Spring Boot | 3.5.6 | Framework base |
| Spring Security | 6.x | Autenticação e autorização |
| Spring HATEOAS | — | Hipermídia nas respostas REST |
| Spring Data JPA | — | Persistência de dados |
| Oracle Database | 19c+ | Banco de dados principal |
| Flyway | — | Versionamento do schema |
| JWT (Auth0) | 4.4.0 | Geração e validação de tokens |
| SpringDoc OpenAPI | 2.8.14 | Documentação Swagger UI |
| Lombok | 1.18.32 | Redução de boilerplate |
| Apache PDFBox | 3.0.3 | Exportação de OS em PDF |
| Groq API (LLaMA 3.3 70B) | — | Assistente de mecânica com IA |
| dotenv-java | 3.0.0 | Variáveis de ambiente |
| Spring Actuator | — | Monitoramento da aplicação |
| Docker Compose | — | Containerização |

---

## Pré-requisitos

- **JDK 22** instalado e `JAVA_HOME` configurado
- **Maven 3.9+** (ou usar o wrapper `./mvnw` incluído no projeto)
- **Oracle Database** acessível (local ou nuvem FIAP)
- Conta **Groq** para o assistente de IA (opcional)

---

## Configuração

### 1. Variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto (mesmo nível do `pom.xml`):

```env
# Banco de dados Oracle
DATABASE_URL=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
DATABASE_USERNAME=seu_usuario
DATABASE_PASSWORD=sua_senha

# JWT — chave secreta (mínimo 32 caracteres)
JWT_SECRET=sua_chave_secreta_aqui_minimo_32_chars

# Groq AI (assistente de mecânica)
GROQ_API_KEY=gsk_sua_chave_groq

# CORS (origens permitidas)
CORS_ALLOWED_ORIGINS=http://localhost:4200,http://localhost:3000
```

> O arquivo `.env` é carregado automaticamente pelo `dotenv-java`.  


### 2. Porta

A aplicação sobe na porta **8081** por padrão.  

```properties
server.port=8081
```

---

## Executando

```bash
# Com Maven Wrapper 
./mvnw spring-boot:run


```

- **API:** `http://localhost:8081`
- **Swagger UI:** `http://localhost:8081/swagger-ui/index.html`

---

## Primeiro acesso — Admin padrão


| Campo | Valor |
|---|---|
| E-mail | `admin@osfacil.com` |
| Senha | `Admin@123` |
| Role | `ROLE_ADMIN` |


---

## Perfis de acesso

| Role | Permissões |
|---|---|
| `ROLE_CLIENTE` | Ver suas próprias OS e pagamentos, consultar veículos, usar assistente de IA |
| `ROLE_FUNCIONARIO` | Gestão completa de OS, clientes, veículos, produtos e pagamentos; assistente de IA |
| `ROLE_ADMIN` | Tudo do funcionário + cadastrar e remover funcionários e administradores |

O token JWT deve ser enviado no header de todas as requisições autenticadas:
```
Authorization: Bearer <token>
```

---

## Endpoints

### Autenticação

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/login` | Público | Login — retorna JWT + role |
| POST | `/register` | Público | Cadastro de cliente |
| POST | `/register-funcionario` | ADMIN | Cadastro de funcionário |
| POST | `/register-admin` | ADMIN | Cadastro de administrador |

**Payload de login:**
```json
{
  "email": "admin@osfacil.com",
  "password": "Admin@123"
}
```

**Resposta:**
```json
{
  "tokenAcesso": "eyJhbGc...",
  "nome": "Administrador",
  "email": "admin@osfacil.com",
  "role": "ROLE_ADMIN"
}
```

### Ordens de Serviço

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/ordem-servicos/minhas` | Autenticado | OS do cliente logado (ou todas, para staff) |
| GET | `/ordem-servicos` | FUNCIONARIO/ADMIN | Todas as OS |
| GET | `/ordem-servicos/{id}` | Autenticado | Detalhes de uma OS |
| POST | `/ordem-servicos` | FUNCIONARIO/ADMIN | Criar OS |
| PUT | `/ordem-servicos/{id}` | FUNCIONARIO/ADMIN | Atualizar OS |
| DELETE | `/ordem-servicos/{id}` | FUNCIONARIO/ADMIN | Remover OS |
| GET | `/ordem-servicos/{id}/pdf` | FUNCIONARIO/ADMIN | Exportar OS em PDF |

### Demais recursos

| Recurso | Rota base | GET (cliente) | Mutações |
|---|---|---|---|
| Clientes | `/clientes` | Não | FUNCIONARIO/ADMIN |
| Funcionários | `/funcionarios` | Não | FUNCIONARIO/ADMIN |
| Veículos | `/veiculos` | Sim | FUNCIONARIO/ADMIN |
| Produtos | `/produtos` | Não | FUNCIONARIO/ADMIN |
| Itens de produto | `/item-produtos` | Não | FUNCIONARIO/ADMIN |
| Pagamentos | `/pagamentos` | Sim | FUNCIONARIO/ADMIN |
| Assistente IA | `/assistente/mecanica` | Sim | — |

---

## Estrutura do projeto

```
src/main/java/com/oracle/OSfacil/
├── controller/               # Endpoints REST
│   ├── AuthController        # Login e registro
│   ├── OrdemServicoController
│   ├── ClienteController
│   ├── FuncionarioController
│   ├── VeiculoController
│   ├── ProdutoController
│   ├── ItemProdutoController
│   ├── PagamentoController
│   └── AssistenteMecanicaController
├── service/                  # Regras de negócio
├── repository/               # Spring Data JPA
├── model/                    # Entidades JPA
├── dto/
│   ├── request/              # Payloads de entrada
│   └── response/             # Payloads de saída (HATEOAS)
├── mapper/                   # Conversão entity ↔ DTO
├── enums/                    # Role, StatusOrdemServico, FormaPagamento, StatusPagamento
└── infra/
    ├── seguranca/            # Spring Security, JWT Filter, CORS
    │   ├── ConfiguracaoSeguranca.java
    │   └── FiltroTokenAcesso.java
    ├── groq/                 # Integração Groq AI
    ├── exeception/           # GlobalExceptionHandler
    └── DataInitializer.java  # Bootstrap do primeiro admin
```

---

## Migrações Flyway

Os scripts de migração ficam em `src/main/resources/db/migration/`.  
O Flyway aplica automaticamente as migrações pendentes ao subir a aplicação.

---

## Build para produção

```bash
./mvnw clean package -DskipTests
java -jar target/OSfacil-0.0.1-SNAPSHOT.jar
```

---

## Docker

```bash
docker build -t osfacil-api .
docker run -p 8081:8081 --env-file .env osfacil-api
```

---

## Autores

| Nome | RM |
|---|---|
| Fabio H S Eduardo | 560416 |
| Gabriel WU Castro | 560210 |
| Renato Kenji Sugaki | 559810 |

Projeto desenvolvido para a disciplina **Java Advanced — FIAP**

**API em produção:** https://osfacil.onrender.com

**Frontend em produção:** https://osfacil-angular.vercel.app

**Vídeo de demonstração java com katalon:** https://youtu.be/Yd9SBvOVWvY
