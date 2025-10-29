# 🏍️ SafeYard API — Sprint 4 (Java Advanced)

## 📘 Descrição do Projeto
O **SafeYard** é uma aplicação Java Spring Boot desenvolvida para o controle de entrada e saída de motos em pátios de locação da **Mottu**. O sistema visa oferecer **gestão eficiente de motos, clientes e locações**, com foco em segurança, rastreabilidade e integração com um app mobile de apoio ao time operacional.

Durante o semestre, o grupo trabalhou na **integração de diversas disciplinas** da FIAP (Java Advanced, DevOps & Cloud, QA, Mobile e Banco de Dados), entregando uma solução **funcional, online e integrada**.

---

## 🎯 Objetivos da Solução
- Automatizar o controle de motos em pátios da Mottu.
- Facilitar o registro e consulta de locações de motos.
- Permitir upload e validação de imagens (placas, QR Codes, etc.).
- Oferecer uma API segura e documentada, com autenticação JWT.
- Integrar com o aplicativo mobile da equipe de campo (FIAP Mobile).

---

## 🧠 Tecnologias Utilizadas
| Tecnologia | Uso no Projeto |
|-------------|----------------|
| **Java 17** | Linguagem principal |
| **Spring Boot 3.4.5** | Framework principal da API |
| **Spring Security + JWT (Auth0)** | Autenticação e autorização |
| **Spring Data JPA** | Persistência de dados |
| **Flyway** | Versionamento do banco de dados |
| **SQL Server / Postgres** | Banco relacional (dependendo do ambiente) |
| **Swagger (Springdoc)** | Documentação interativa da API |
| **Thymeleaf** | Views e testes locais de interface |
| **Actuator** | Monitoramento e health check |
| **Azure / Fly.io** | Deploy em ambiente de produção |
| **Docker** | Empacotamento e portabilidade |

---

## ⚙️ Funcionalidades Principais
### 🔐 Autenticação e Autorização
- Login e registro com **JWT Token**.
- Controle de acesso a endpoints sensíveis.

### 🏍️ Gestão de Motos
- CRUD completo de motos.
- Upload de foto da moto.
- Validação de placa (única e indexada).

### 👤 Gestão de Clientes e Perfis
- Cadastro e consulta de clientes.
- Perfil do cliente autenticado via JWT.

### 🔄 Locações
- Registro, atualização e encerramento de locações.
- Regras de negócio aplicadas via SQL + validações customizadas.
- Consulta de locação mais recente do cliente.

### 🧩 Integrações
- **/api/integrations/events** — eventos e integrações externas.
- **/api/integrations/health** — verificação de status da API.

---

## ☁️ Deploy e Ambientes
| Ambiente | URL | Banco | Observações |
|-----------|-----|--------|--------------|
| **Produção (Cloud)** | Ex: `https://safeyard-api.fly.dev` ou `https://app-safeyard-372b.azurewebsites.net` | Postgres / SQL Server | Deploy ativo e acessível online |
| **Desenvolvimento (Local)** | `http://localhost:8080/swagger-ui.html` | H2 | Ambiente para testes locais e QA |

---

## 🧩 Integração Multidisciplinar
| Disciplina | Aplicação no Projeto |
|-------------|----------------------|
| **Java Advanced** | API com JPA, JWT, Flyway, Swagger e boas práticas (SOLID, DTOs, Services) |
| **DevOps & Cloud Computing** | Deploy no Azure App Service / Fly.io + banco em nuvem (SQL Server / Postgres) |
| **Mastering Relational Database** | Modelagem e versionamento do banco via Flyway + Procedures |
| **Mobile Development** | App React Native consumindo endpoints REST da API |
| **Quality Assurance (QA)** | Testes de endpoints via Swagger e Postman + logs de integração |

---

## 💡 Decisões de Design
- **Arquitetura em camadas (Controller, Service, Repository, DTOs)** para manter o código limpo e desacoplado.
- **Flyway** para manter histórico e versionamento do banco.
- **Swagger UI** para facilitar os testes e documentação.
- **Actuator Health Check** para monitoramento em tempo real.
- **Segurança JWT** para proteger os endpoints e validar perfis de usuário.

---

## 🎨 UI/UX
- Interface de testes via **Swagger** e **Thymeleaf**.
- Integração visual via **aplicativo mobile** consumindo os endpoints REST.
- Interface intuitiva e rotas organizadas por contexto (`/api/motos`, `/api/locacoes`, `/api/auth`).

---

## 🧾 Evidências de Integração
- 📄 Scripts SQL (Flyway) — `src/main/resources/db/migration`
- 📱 App Mobile (React Native) — integração com endpoints `/auth`, `/motos`, `/locacoes`.
- ☁️ Deploy em ambiente cloud.
- 🔐 Segurança e autenticação por token JWT.
- 📊 Health Check ativo: `/api/integrations/health`

---

## 🎥 Apresentação Final
**Duração:** até 15 minutos  
**Formato:** vídeo gravado com demonstração dos principais fluxos

### Estrutura sugerida do vídeo:
1. Breve introdução da proposta e problema resolvido.
2. Demonstração dos principais endpoints (Swagger).
3. Mostrando integração com o app mobile.
4. Exibição do deploy e health check.
5. Considerações finais e menção às disciplinas integradas.

---

## 🧑‍💻 Equipe
- **Thamires Ribeiro Cruz — RM558128 · github.com/ThamiresRC**
- **Adonay Rodrigues da Rocha — RM558782 · github.com/AdonayRocha**
- **Pedro Henrique Martins dos Reis — RM555306 · github.com/pxxmdr** 

---

## 📂 Repositório
- **GitHub:** [https://github.com/ThamiresRC/SafeYardApi](https://github.com/ThamiresRC/SafeYardApi)
- **Link da aplicação:** (inserir após deploy final)
- **Apresentação em vídeo:** (inserir link após gravação)

---

## ✅ Checklist de Requisitos da Sprint 4
| Requisito | Status |
|------------|---------|
| Deploy online funcional | ✅ |
| Navegação pelos fluxos da API | ✅ |
| Aplicação dos conceitos Java Advanced | ✅ |
| UI/UX (Swagger + integração mobile) | ✅ |
| Narrativa clara e criativa | ✅ |
| Integração multidisciplinar documentada | ✅ |
| README completo no GitHub | ✅ |
| Vídeo de até 15 minutos | 🔜 (a gravar) |

---

📅 **FIAP — 2º Ano / Java Advanced — Sprint 4 (2025)**