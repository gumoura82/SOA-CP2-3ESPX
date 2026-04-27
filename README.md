# 🏢 API de Gerenciamento de Salas de Reunião

> API RESTful para gerenciamento de salas de reunião e reservas com arquitetura SOA, autenticação JWT, banco H2 e documentação Swagger.

---

##  Aluno

| Campo | Valor |
|-------|-------|
| **Nome** | Gustavo Oliveira de Moura |
| **RM** | 555827 |
| **Turma** | 3ESPX |

---

## 📋 Descrição do Projeto

Aplicação desenvolvida em Java com Spring Boot seguindo os princípios de **Arquitetura Orientada a Serviços (SOA)**, com separação clara de responsabilidades em camadas:

- **Controller** → Entrada e resposta HTTP
- **Service** → Regras de negócio
- **Repository** → Acesso a dados
- **DTOs** → Transporte de dados entre camadas

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Função |
|------------|--------|--------|
| Java | 17+ | Linguagem principal |
| Spring Boot | 3.2.0 | Framework base |
| Spring Security | 6.x | Segurança e autenticação |
| JWT (jjwt) | 0.11.5 | Tokens de autenticação |
| H2 Database | — | Banco de dados em memória |
| Spring Data JPA | — | Persistência de dados |
| Springdoc OpenAPI | 2.3.0 | Documentação Swagger |
| Spring Cache | — | Cache em memória |
| Lombok | — | Redução de boilerplate |
| Maven | — | Gerenciamento de dependências |

---

## ✅ Diferenciais Implementados

- ✅ **Cache** com Spring Cache (`@Cacheable`, `@CacheEvict`)
- ✅ **Paginação** nas consultas de salas e reservas
- ✅ **Filtros de busca** por nome, capacidade, localização, data, sala, responsável e status
- ✅ **Logging estruturado** com SLF4J em todas as camadas
- ✅ **Tratamento global de exceções** via `@RestControllerAdvice`

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- Java 17+
- Maven 3.8+

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/SEU_USUARIO/meeting-room-api.git
cd meeting-room-api

# 2. Compile e execute
mvn spring-boot:run

# 3. Ou gere o JAR e execute
mvn clean package -DskipTests
java -jar target/meeting-room-api-1.0.0.jar
```

A aplicação estará disponível em: `http://localhost:8080`

---

## 🔗 URLs Importantes

| URL | Descrição |
|-----|-----------|
| `http://localhost:8080/swagger-ui.html` | Documentação Swagger UI |
| `http://localhost:8080/v3/api-docs` | OpenAPI JSON |
| `http://localhost:8080/h2-console` | Console H2 (JDBC URL: `jdbc:h2:mem:meetingroomdb`) |

---

## 🔐 Autenticação

A API usa JWT. Para acessar endpoints protegidos:

### 1. Fazer login e obter token:

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Usuários disponíveis:**

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| user | user123 | USER |

### 2. Usar o token nas requisições:

```http
Authorization: Bearer <seu_token_jwt>
```

---

## 📬 Endpoints Disponíveis

### 🔐 Autenticação

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/auth/login` | Gera token JWT | ❌ |

### 🏢 Salas de Reunião

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/salas` | Criar sala | ✅ |
| GET | `/api/salas` | Listar salas (com filtros e paginação) | ✅ |
| GET | `/api/salas/{id}` | Buscar sala por ID | ✅ |
| PUT | `/api/salas/{id}` | Atualizar sala | ✅ |
| DELETE | `/api/salas/{id}` | Remover sala | ✅ |

**Parâmetros de filtro para GET /api/salas:**
- `nome` (string) — filtra por nome (parcial, case-insensitive)
- `capacidadeMin` (int) — filtra por capacidade mínima
- `localizacao` (string) — filtra por localização (parcial)
- `page`, `size`, `sort` — paginação padrão do Spring

### 📅 Reservas

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/reservas` | Criar reserva | ✅ |
| GET | `/api/reservas` | Listar reservas (com filtros e paginação) | ✅ |
| GET | `/api/reservas/{id}` | Buscar reserva por ID | ✅ |
| PATCH | `/api/reservas/{id}/cancelar` | Cancelar reserva | ✅ |

**Parâmetros de filtro para GET /api/reservas:**
- `salaId` (long) — filtra por sala
- `responsavel` (string) — filtra por responsável
- `dataInicio` (ISO DateTime) — filtra a partir desta data
- `dataFim` (ISO DateTime) — filtra até esta data
- `status` (`ATIVA` ou `CANCELADA`) — filtra por status

---

## 🧪 Exemplos de Requisições

### 1. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "admin",
  "role": "ADMIN"
}
```

---

### 2. Criar Sala

```bash
curl -X POST http://localhost:8080/api/salas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "nome": "Sala de Projetos",
    "capacidade": 8,
    "localizacao": "Andar 3 - Ala Norte"
  }'
```

**Resposta (201 Created):**
```json
{
  "id": 5,
  "nome": "Sala de Projetos",
  "capacidade": 8,
  "localizacao": "Andar 3 - Ala Norte"
}
```

---

### 3. Listar Salas com Filtro e Paginação

```bash
curl "http://localhost:8080/api/salas?capacidadeMin=10&page=0&size=5" \
  -H "Authorization: Bearer {TOKEN}"
```

**Resposta:**
```json
{
  "content": [
    { "id": 1, "nome": "Sala Alfa", "capacidade": 10, "localizacao": "Andar 1 - Ala Norte" },
    { "id": 2, "nome": "Sala Beta", "capacidade": 20, "localizacao": "Andar 2 - Ala Sul" }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "size": 5,
  "number": 0
}
```

---

### 4. Criar Reserva

```bash
curl -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "salaId": 1,
    "dataHoraInicio": "2025-02-01T09:00:00",
    "dataHoraFim": "2025-02-01T10:30:00",
    "responsavel": "João Silva"
  }'
```

**Resposta (201 Created):**
```json
{
  "id": 1,
  "salaId": 1,
  "salaNome": "Sala Alfa",
  "dataHoraInicio": "2025-02-01T09:00:00",
  "dataHoraFim": "2025-02-01T10:30:00",
  "responsavel": "João Silva",
  "status": "ATIVA"
}
```

---

### 5. Conflito de Reserva (Erro esperado)

```bash
# Tenta reservar a mesma sala no mesmo horário
curl -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "salaId": 1,
    "dataHoraInicio": "2025-02-01T09:30:00",
    "dataHoraFim": "2025-02-01T11:00:00",
    "responsavel": "Maria Souza"
  }'
```

**Resposta (409 Conflict):**
```json
{
  "timestamp": "2025-01-15T14:30:00",
  "status": 409,
  "error": "Conflito de reserva",
  "message": "Conflito de reserva: a sala já está reservada de 2025-02-01T09:00 até 2025-02-01T10:30 por João Silva",
  "path": "/api/reservas"
}
```

---

### 6. Cancelar Reserva

```bash
curl -X PATCH http://localhost:8080/api/reservas/1/cancelar \
  -H "Authorization: Bearer {TOKEN}"
```

---

### 7. Listar Reservas com Filtros

```bash
curl "http://localhost:8080/api/reservas?salaId=1&status=ATIVA&page=0&size=10" \
  -H "Authorization: Bearer {TOKEN}"
```

---

## 🏗️ Arquitetura do Projeto

```
src/main/java/com/meetingroom/
├── MeetingRoomApiApplication.java    # Classe principal
├── config/
│   ├── SecurityConfig.java           # Configuração Spring Security
│   └── SwaggerConfig.java            # Configuração OpenAPI
├── controller/
│   ├── AuthController.java           # Endpoints de autenticação
│   ├── SalaController.java           # CRUD de salas
│   └── ReservaController.java        # Gestão de reservas
├── service/
│   ├── AuthService.java              # Lógica de autenticação
│   ├── SalaService.java              # Regras de negócio - salas
│   └── ReservaService.java           # Regras de negócio - reservas
├── repository/
│   ├── UserRepository.java           # Acesso a dados - usuários
│   ├── SalaRepository.java           # Acesso a dados - salas
│   └── ReservaRepository.java        # Acesso a dados - reservas
├── entity/
│   ├── User.java                     # Entidade usuário
│   ├── Sala.java                     # Entidade sala
│   └── Reserva.java                  # Entidade reserva
├── dto/
│   ├── AuthDTO.java                  # DTOs de autenticação
│   ├── SalaDTO.java                  # DTOs de sala
│   ├── ReservaDTO.java               # DTOs de reserva
│   └── ErrorResponseDTO.java         # DTO de erros
├── security/
│   ├── JwtService.java               # Geração e validação de JWT
│   └── JwtAuthenticationFilter.java  # Filtro de autenticação JWT
└── exception/
    ├── GlobalExceptionHandler.java   # Tratamento global de erros
    ├── ResourceNotFoundException.java
    ├── ReservaConflitanteException.java
    └── BusinessException.java
```

---

## 🧪 Testes

O projeto conta com **11 testes unitários** cobrindo:

- Criação bem-sucedida de sala e reserva
- Conflito de reserva no mesmo horário
- Validação de intervalo de datas inválido
- Cancelamento de reserva (ativa e já cancelada)
- Busca por ID inexistente
- Nome de sala duplicado

```bash
# Executar testes
mvn test
```

---

## 📝 Regras de Negócio

1. **Sem conflitos**: Não é possível reservar uma sala que já possui uma reserva ativa no mesmo período.
2. **Intervalo válido**: A data/hora de fim deve ser posterior à data/hora de início.
3. **Datas futuras**: Reservas só podem ser feitas para datas futuras.
4. **Nome único**: Não é possível criar duas salas com o mesmo nome.
5. **Cancelamento**: Uma reserva já cancelada não pode ser cancelada novamente.
