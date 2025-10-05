# P$oft Wallet 💼

Sistema de carteira de investimentos desenvolvido como projeto acadêmico para aplicação de **padrões de projeto e boas práticas de programação**.

## 📋 Sobre o Projeto

O P$oft Wallet é uma plataforma completa para gerenciamento de investimentos que permite:

- **Acompanhamento em tempo real** da evolução dos investimentos
- **Alertas automáticos** sobre eventos relevantes do mercado
- **Cálculo de impostos** personalizado por tipo de ativo
- **Controle total** sobre a carteira de investimentos

## 👥 Públicos e Funcionalidades

### 👨‍💼 Administradores
- Cadastro, edição e remoção de ativos (Tesouro Direto, Ações, Criptomoedas)
- Ativação/desativação de ativos
- Atualização de cotações (variação mínima de 1%)
- Confirmação de compras e resgates
- Consulta de todas as operações do sistema

### 👤 Clientes
- **Cadastro** com planos Normal ou Premium
- **Visualização** de ativos de acordo com o plano
- **Compras** com fluxo de estados: *Solicitado → Disponível → Comprado → Em carteira*
- **Resgates** com cálculo automático de impostos
- **Sistema de notificações** para variações de preço e disponibilidade
- **Acompanhamento** de carteira e histórico completo
- **Exportação** de extrato em CSV

## 🚀 Como Usar

### Pré-requisitos
- **Java 17** ou superior
- **Gradle** (incluído no projeto)
- Banco de dados compatível

### Executando o Projeto

```bash
# Clone o repositório
git clone [url-do-repositorio]

# Navegue até o diretório do projeto
cd psoft-wallet

# Execute o projeto usando o Gradle Wrapper
./gradlew bootRun

# Para Windows use:
gradlew.bat bootRun
```

### Build do Projeto

```bash
# Compilar o projeto
./gradlew build

# Executar testes
./gradlew test

# Aplicar formatação de código
./gradlew spotlessApply
```

### Acesso à Aplicação
Após iniciar, a aplicação estará disponível em: `http://localhost:8080`

## 🛠️ Tecnologias Utilizadas

- **Spring Boot 3.0.5** - Framework principal
- **Java 17** - Linguagem de programação
- **Gradle** - Gerenciamento de dependências
- **Spring Data JPA** - Persistência de dados
- **H2 Database** - Banco em memória (desenvolvimento)
- **PostgreSQL** - Banco de dados produção
- **SpringDoc OpenAPI** - Documentação da API

## 📚 Contexto Acadêmico

Projeto desenvolvido na **Disciplina de Projeto de Software** para aprimoramento e aplicação prática de conceitos avançados de engenharia de software.

---

*Sistema educativo desenvolvido para fins acadêmicos* 🎓