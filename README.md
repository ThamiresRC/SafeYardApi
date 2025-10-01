# SafeYardApi

API em **Spring Boot** para controle de entrada/saída de **motos em pátios** (yard), com autenticação **JWT**, documentação **OpenAPI/Swagger**, **migrations Flyway**, paginação/ordenação/filtros, seed inicial e **interfaces web (Thymeleaf)** para Cliente/Admin.

## 🎯 Objetivo do Projeto (adequado ao Java)

O **SafeYard** é uma aplicação Java (Spring Boot) desenvolvida tomando como cenário a **Mottu**, com o objetivo de **modernizar e otimizar a gestão de pátios de motos**. A solução oferece:

* **Camada REST** para operações de cadastro/consulta (**motos, clientes, locações e registros**) com **JWT**;
* **Interface web (Thymeleaf)** para que **Administradores** gerenciem a frota e as **locações** (check‑in/out) e **Clientes** solicitem e acompanhem suas locações;
* **Regras de negócio** para **evitar erros manuais** (ex.: impedir dupla locação da mesma moto, aceitar apenas motos `DISPONIVEL`, registrar encerramento);
* **Rastreabilidade** via banco de dados com **Flyway** e **seeds** reproducíveis; acesso a **H2 Console** para verificação durante a banca.

**Resultados esperados:** maior controle operacional, redução de inconsistências e **experiência simples** para o usuário, demonstrados por fluxos completos de **login → locação → encerramento → consultas/relatórios**.

**Metas demonstráveis na banca:**

* (1) Criar uma locação **sem permitir conflitos** (mesma moto já locada).
* (2) **Encerrar** a locação atualizando automaticamente o **status da moto**.
* (3) Exibir, para o cliente, **histórico de locações** e, para o admin, **visão da frota**.
* (4) Mostrar **documentação** no Swagger e **validação JWT** funcionando.

> **Sprint 3 – pronto para avaliação.** Este README contém tudo o que o professor pede (instalação, execução, acesso, endpoints, vídeo-roteiro e notas para a avaliação oral).

---

## 🔧 Stack

* Java **21** · Spring Boot **3.x** (Web, Validation, JPA/Hibernate, Security)
* Auth: **JWT** (`com.auth0:java-jwt`)
* Banco: **H2** (dev) · opcional Postgres/MySQL (prod)
* Migrações: **Flyway** (`src/main/resources/db/migration`)
* Views: **Thymeleaf** (layouts + páginas para cliente e admin)
* Docs: **springdoc-openapi** (`/swagger-ui.html`)
* Build: **Maven**
* DevOps: Dockerfile + script de deploy

---

## 🗂️ Domínio (resumo)

* **Cliente** – dados cadastrais (pode possuir motos)
* **Moto** – placa, modelo, chassi, fotoUrl, status: `DISPONIVEL`, `LOCADA`, `MANUTENCAO`
* **Locacao** – check-in/check-out da moto no pátio (datas, condições, cliente, moto)
* **RegistroMotoPatio** – histórico de movimentações (opcional/relatórios)
* **User**/**UserRole**/**Token** – autenticação/autorização via JWT

---

## 📁 Estrutura de pacotes

```
src/main/java/com/safeyard/safeyard_api/
 ├─ config/        # CORS, Security, Swagger, Web + seed e init de storage
 ├─ controller/    # REST + Web (Thymeleaf) – Auth, Cliente, Moto, Locacao, Registro
 ├─ dto/           # DTOs (Form, View e Responses de Auth)
 ├─ exception/     # Handler global e payload de erro
 ├─ model/         # Entidades JPA + enums
 ├─ repository/    # Spring Data JPA
 ├─ service/       # Regras de negócio + fachada de Locação + Auth/JWT
 └─ SafeyardApiApplication.java
```

Migrações em `resources/db.migration` (ex.: `V1__schema.sql`, índices, seeds, regras de locação, colunas, etc.).

---

## ▶️ Como executar (modo DEV)

1. **Pré-requisitos**: JDK **21**, Maven, porta **8080** livre.
2. **Clonar**: `git clone <repo>`
3. **Rodar**: `./mvnw spring-boot:run` (Linux/Mac) ou `mvnw spring-boot:run` (Windows)
4. Acesso:

   * Web (Thymeleaf): `http://localhost:8080/login`
   * Swagger: `http://localhost:8080/swagger-ui.html`
   * H2 Console: `http://localhost:8080/h2-console` (JDBC: `jdbc:h2:file:./data/safeyard`)

> **Dev profile**: `application-dev.properties` já configurado para H2 + Flyway. Na primeira execução, as *migrations* criam schema/índices/seeds.

---

## 🔐 Contas de teste (seed)

* **Cliente**: `cliente@safeyard.com` · **123456**
* **Admin**: `admin@safeyard.com` · **123456**
* **Funcionário**: `func@safeyard.com` · **123456**

> Ao logar pela interface web, você verá o **Dashboard** conforme o papel (cliente/admin) e poderá navegar para **Minhas locações**, **Motos**, **Relatórios** etc.

---

## 📘 Documentação da API

* **Swagger/OpenAPI**: `http://localhost:8080/swagger-ui.html`
* **Esquema de segurança**: Bearer **JWT**. Primeiro faça login no endpoint de Auth, copie o token e clique em **Authorize** no Swagger.

### Principais endpoints (REST)

**Auth**

* `POST /api/auth/login` – body: `{ "email", "password" }` → `LoginResponseDTO { token, name, role }`
* `POST /api/auth/register` *(opcional/ambiente de demo)*

**Motos** (ADMIN/FUNC · leitura CLIENTE)

* `GET /api/motos?page=&size=&sort=` – paginação/ordenação
* `GET /api/motos/{id}`
* `POST /api/motos` – cria moto (ADMIN/FUNC)
* `PUT /api/motos/{id}` · `DELETE /api/motos/{id}`

**Locações** (CLIENTE e ADMIN)

* `GET /api/locacoes` – pode aceitar filtros (p.ex. por período/status)
* `GET /api/locacoes/{id}`
* `POST /api/locacoes` – cria locação (valida moto **DISPONIVEL** e se não há locação ativa)
* `PUT /api/locacoes/{id}/encerrar` – faz check-out/encerra

**Clientes** / **Registros**

* `GET /api/clientes` · `GET /api/clientes/{id}` …
* `GET /api/registros` – listagem/histórico

> Os controllers Web (Thymeleaf) expõem rotas como `/dashboard`, `/cliente/area`, `/motos`, `/locacao/*` para a experiência de navegação via browser.

---

## 🧪 Exemplos (curl)

**Login**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"cliente@safeyard.com","password":"123456"}'
```

**Listar motos (com Bearer)**

```bash
TOKEN=ey... # copie do login
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/motos
```

**Criar locação**

```bash
curl -X POST http://localhost:8080/api/locacoes \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{
    "motoId": 1,
    "clienteId": 1,
    "condicaoEntrega": "tanque cheio, sem avarias",
    "devolucaoPrevista": "2025-10-05T18:00:00"
  }'
```

---

## 🐳 Docker (opcional)

Build local (JDK 21 base):

```dockerfile
# (resumo) veja Dockerfile no projeto
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY . .
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw -B -DskipTests clean package
EXPOSE 8080
CMD ["sh","-c","java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev} -jar target/safeyard-api-*.jar"]
```

Build & run:

```bash
docker build -t safeyard-api .
docker run --rm -p 8080:8080 --name safeyard safeyard-api
```

---

## 🔍 Troubleshooting

* **H2 Console não abre** → confira `spring.h2.console.enabled=true` e a URL JDBC acima.
* **JWT 401** → refaça o login; copie **somente** o token (sem aspas) no Authorize.
* **Flyway falhou** → apague a pasta `data/` (somente em dev) e rode novamente.
* **Porta 8080 ocupada** → `server.port=8081` em `application-dev.properties`.

---

## 👨‍💻 Desenvolvedores

* **Thamires Ribeiro Cruz** — RM558128 · [github.com/ThamiresRC](https://github.com/ThamiresRC)
* **Adonay Rodrigues da Rocha** — RM558782 · [github.com/AdonayRocha](https://github.com/AdonayRocha)
* **Pedro Henrique Martins dos Reis** — RM555306 · [github.com/pxxmdr](https://github.com/pxxmdr)

---

## 📜 Licença

Uso acadêmico/educacional.
