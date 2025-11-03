# 🏍️ SafeYard — Sprint 4 (Java Advanced)

## 📘 Descrição do Projeto
O **SafeYard** é uma aplicação web desenvolvida em **Java Spring Boot**, com foco no **controle de entrada e saída de motos** nos pátios de locação da **Mottu**.  
O sistema possibilita o **gerenciamento de motos, clientes e locações**, garantindo segurança, rastreabilidade e integração com o app mobile da equipe operacional.

Durante o semestre, o grupo trabalhou na **integração entre disciplinas da FIAP** — Java Advanced, DevOps & Cloud, Quality Assurance, Mobile e Banco de Dados — resultando em uma solução **funcional, online e integrada**.

---

## 🎯 Objetivos da Solução
- Automatizar o controle de motos nos pátios da Mottu.
- Facilitar o registro e o acompanhamento de locações.
- Garantir segurança e controle de acesso com **JWT**.
- Permitir **upload de imagens** (placas, QR Codes etc.).
- Oferecer **painéis separados para Cliente e Administração**.
- Integrar com o app mobile para acompanhamento em tempo real.

---

## 🧠 Tecnologias Utilizadas
| Tecnologia | Uso no Projeto |
|-------------|----------------|
| **Java 17** | Linguagem principal |
| **Spring Boot 3.4.5** | Framework principal |
| **Spring Security + JWT (Auth0)** | Autenticação e autorização |
| **Spring Data JPA / Hibernate** | Persistência de dados |
| **Flyway** | Versionamento do banco de dados |
| **PostgreSQL / SQL Server** | Banco de dados relacional |
| **Swagger (Springdoc)** | Documentação interativa |
| **Thymeleaf + Bootstrap 5** | Interface web e templates |
| **Actuator** | Monitoramento e health check |
| **Docker + Azure / Render** | Deploy e containerização |

---

## ⚙️ Funcionalidades Principais
### 🔐 Autenticação e Perfis
- Login seguro com **JWT Token**.
- Perfis de acesso: **Admin / Funcionário / Cliente**.

### 🏍️ Gestão de Motos
- CRUD completo de motos.
- Upload e exibição de imagem.
- Status automático: “Disponível”, “Ativa”, “Indisponível”.

### 👤 Gestão de Clientes
- Cadastro, listagem e perfil detalhado.
- Área exclusiva “**Minha área**” para visualizar locações.

### 🔄 Locações
- Registro e devolução de locações com data/hora.
- Filtro por cliente, moto e período.
- Botão “Zerar locações ativas (manter histórico)”.
- Validação para impedir locações duplicadas.

### 🌐 Integrações
- `/api/integrations/events` — eventos externos.
- `/api/integrations/health` — monitoramento da API.
- Integração com o **app mobile** da equipe de campo.

---

## ☁️ Deploy e Ambientes
| Ambiente | URL | Banco | Observações |
|-----------|-----|--------|-------------|
| **Produção (Render)** | `https://safeyardapi-2.onrender.com` | PostgreSQL | Deploy ativo e acessível |
| **Desenvolvimento (Local)** | `http://localhost:8080/swagger-ui.html` | H2 | Ambiente para testes e QA |

---

## 🧩 Integração Multidisciplinar
| Disciplina | Aplicação no Projeto |
|-------------|----------------------|
| **Java Advanced** | API REST com autenticação JWT, JPA, DTOs e boas práticas (SOLID, DRY, Clean Code). |
| **DevOps & Cloud** | Deploy no Render com banco PostgreSQL, logs e variáveis de ambiente. |
| **Mastering Relational DB** | Modelagem de entidades e versionamento com Flyway. |
| **Quality Assurance (QA)** | Testes via Swagger e Postman, logs e validações. |
| **Mobile Development** | App React Native consumindo endpoints REST. |

---

## 💡 Decisões de Design
- **Arquitetura em camadas:** Controller → Service → Repository → Model → DTO.
- **Flyway:** versionamento automático do banco.
- **Thymeleaf:** interface leve e integrada ao Spring.
- **Actuator:** health checks automáticos para monitoramento.
- **COALESCE no Postgres:** evita erro 42P18 em filtros nulos.
- **JWT:** separação de perfis e proteção de endpoints sensíveis.

---

## 🎨 UI e UX
- Painéis separados para Cliente e Admin.
- Feedback visual com **alerts**, **badges de status** e **botões de ação claros**.
- Layout responsivo (Bootstrap 5).
- Interface de apoio via **Thymeleaf** para demonstração local.

---

## 🧾 Evidências e Prints
| Tipo | Evidência |
|------|------------|
| 📦 Deploy | Aplicação online no Render |
| 🧩 Integração | API e app mobile consumindo endpoints |
| 📸 Prints | Locações ativas/encerradas, upload de imagem e login |
| 🧠 Banco | Scripts Flyway e chaves relacionais |
| 🔐 Segurança | JWT + Perfis com restrição de menus |
| 📊 Health Check | `/api/integrations/health` OK |

---

## 👩‍💻 Equipe
| Integrante | RM | Github                 |
|-------------|----|------------------------|
| **Thamires Ribeiro Cruz** | 558128 | github.com/ThamiresRC  |
| **Adonay Rodrigues da Rocha** | 558782 | github.com/AdonayRocha |
| **Pedro Henrique Martins dos Reis** | 555306 | github.com/pxxmdr      |

---

## 📂 Repositório e Links
- **GitHub:** [github.com/ThamiresRC/SafeYardApi](https://github.com/ThamiresRC/SafeYardApi)
- **Deploy Render:** [https://safeyardapi-2.onrender.com](https://safeyardapi-2.onrender.com)

---

## ✅ Checklist da Sprint 4 — Java Advanced
| Requisito | Status |
|------------|---------|
| Deploy online funcional | ✅ |
| Fluxos principais navegáveis | ✅ |
| Aplicação de conceitos Java Advanced | ✅ |
| UI/UX (Bootstrap + Thymeleaf) | ✅ |
| Narrativa da solução clara | ✅ |
| Integração multidisciplinar documentada | ✅ |
| README completo e organizado | ✅ |


---

📅 **FIAP — 2º Ano | Java Advanced — Sprint 4 (2025)**  
💻 *Projeto desenvolvido para o desafio real da Mottu.*
