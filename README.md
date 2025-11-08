# 🏍️ SafeYard — Sprint 4 (Java Advanced)

## 📘 Descrição do Projeto
O **SafeYard** é uma aplicação completa desenvolvida em **Java Spring Boot** com integração a um **app mobile React Native**.  
Seu objetivo é controlar a **entrada e saída de motos** em pátios de locação da **Mottu**, permitindo o **gerenciamento de motos, clientes e locações**, com foco em **segurança, rastreabilidade e eficiência**.

Durante o semestre, o grupo trabalhou na **integração entre disciplinas da FIAP** — Java Advanced, DevOps & Cloud, Quality Assurance, Mobile e Banco de Dados — entregando uma solução **multicamadas, funcional e em produção**.

---

## 🎯 Objetivos da Solução
- Automatizar o controle de motos nos pátios da Mottu.
- Facilitar o registro e acompanhamento de locações.
- Garantir **autenticação segura com JWT** e controle de perfis.
- Permitir **upload de imagens** (placas e QR Codes).
- Integrar **frontend (Thymeleaf)**, **mobile (React Native)** e **backend (API REST)**.
- Publicar a aplicação em nuvem (Render + PostgreSQL).

---

## 🧠 Tecnologias Utilizadas
| Tecnologia | Uso no Projeto |
|-------------|----------------|
| **Java 17** | Linguagem principal |
| **Spring Boot 3.4.5** | Framework de desenvolvimento |
| **Spring Security + JWT (Auth0)** | Autenticação e autorização |
| **Spring Data JPA / Hibernate** | Persistência e ORM |
| **Flyway** | Versionamento do banco |
| **PostgreSQL** | Banco de dados em nuvem |
| **Swagger (Springdoc)** | Documentação da API |
| **Thymeleaf + Bootstrap 5** | Interface web |
| **React Native + Expo** | Aplicativo mobile |
| **Actuator** | Monitoramento e health check |
| **Docker + Render** | Containerização e deploy em produção |

---

## ⚙️ Funcionalidades Principais

### 🔐 Autenticação e Perfis
- Login seguro com **JWT Token**.
- Perfis de acesso: **ADMIN**, **FUNCIONÁRIO** e **CLIENTE**.
- Sessões controladas e redirecionamento por tipo de usuário.

### 🏍️ Gestão de Motos
- CRUD completo com upload de imagem.
- Status automático: *Disponível*, *Em uso*, *Indisponível*.
- Validação de duplicidade de placa.

### 👤 Gestão de Clientes
- Cadastro, listagem e edição de clientes.
- Área exclusiva “Minha Área” para o cliente visualizar locações.

### 🔄 Locações
- Registro e devolução de motos.
- Histórico de locações e filtros dinâmicos.
- Bloqueio de múltiplas locações ativas por cliente.

### 📱 Integração Mobile
- Login no app React Native com a API real hospedada no Render.
- Consumo de endpoints REST.
- Armazenamento local de sessão com AsyncStorage.

---

## ☁️ Deploy e Ambientes
| Ambiente | URL | Banco | Observações |
|-----------|-----|--------|-------------|
| **Produção (Render)** | `https://safeyardapi-2.onrender.com` | PostgreSQL | Deploy ativo e funcional |
| **Desenvolvimento (Local)** | `http://localhost:8080/swagger-ui.html` | H2 | Testes e QA locais |

---

## 🧩 Integração Multidisciplinar
| Disciplina | Aplicação no Projeto |
|-------------|----------------------|
| **Java Advanced** | API REST com autenticação JWT, JPA, DTOs e arquitetura em camadas. |
| **DevOps & Cloud** | Deploy Docker no Render, logs e variáveis de ambiente. |
| **Banco de Dados** | Modelagem relacional e versionamento Flyway. |
| **Quality Assurance (QA)** | Testes de endpoints via Swagger/Postman. |
| **Mobile Development** | App React Native consumindo endpoints REST reais. |

---

## 💡 Decisões Técnicas
- **Arquitetura em camadas:** Controller → Service → Repository → Model → DTO.
- **Flyway:** scripts SQL versionados para migração automática.
- **Actuator:** health check da API para monitoramento no Render.
- **Thymeleaf:** interface web responsiva para validação visual.
- **Docker multi-stage:** build Maven + runtime leve (Temurin JRE 17).
- **Seed dinâmico:** popula usuários e motos no primeiro start.

---

## 🎨 Interface e Experiência
- Layout limpo e responsivo com **Bootstrap 5**.
- Painéis específicos para cada tipo de usuário.
- Alertas de status e feedback visual integrados.
- UI Mobile moderna com **ThemeProvider** e suporte a **i18n (tradução)**.

---

## 🧾 Evidências
| Tipo | Evidência |
|------|------------|
| 🌍 Deploy | Aplicação online no Render |
| ⚙️ Integração | Login e CRUD via API REST |
| 📱 Mobile | App React Native consumindo API |
| 🧠 Banco | PostgreSQL + Flyway versionado |
| 🔐 Segurança | JWT + Spring Security |
| 🧩 DevOps | Build e deploy Docker automatizados |

---

## 👩‍💻 Equipe
| Integrante | RM | Github |
|-------------|----|---------|
| **Thamires Ribeiro Cruz** | 558128 | [github.com/ThamiresRC](https://github.com/ThamiresRC) |
| **Adonay Rodrigues da Rocha** | 558782 | [github.com/AdonayRocha](https://github.com/AdonayRocha) |
| **Pedro Henrique Martins dos Reis** | 555306 | [github.com/pxxmdr](https://github.com/pxxmdr) |

---

## 📂 Repositório e Links
- **GitHub:** [github.com/ThamiresRC/SafeYardApi](https://github.com/ThamiresRC/SafeYardApi)
- **Deploy (Render):** [https://safeyardapi-2.onrender.com](https://safeyardapi-2.onrender.com)

---

## ✅ Checklist — Sprint 4 (Java Advanced)
| Requisito | Status |
|------------|---------|
| Deploy funcional (Render) | ✅ |
| Banco conectado (PostgreSQL) | ✅ |
| Autenticação JWT | ✅ |
| Upload de imagem | ✅ |
| Painéis Cliente/Admin | ✅ |
| Mobile integrado | ✅ |
| README completo | ✅ |

---

📅 **FIAP — 2º Ano | Java Advanced — Sprint 4 (2025)**  
💻 *Projeto desenvolvido para o desafio real da Mottu.*
"""


