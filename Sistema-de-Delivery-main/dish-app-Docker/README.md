# 🍽️ Sistema de Comanda Digital - Restaurante

Sistema completo de gerenciamento de comandas digitais para restaurantes, desenvolvido com Angular e Spring Boot.

## 🚀 Funcionalidades

### 📋 Sistema de Comandas Digitais
- ✅ Criação de comandas por mesa
- ✅ Adição/remoção de itens do cardápio
- ✅ Controle de quantidade de itens
- ✅ Status das comandas (Aberta, Em Andamento, Finalizada, Cancelada)
- ✅ Cálculo automático de totais
- ✅ Interface moderna e responsiva

### 🍽️ Gerenciamento de Pratos
- ✅ CRUD completo de pratos
- ✅ Preços e descrições
- ✅ Integração com sistema de comandas

## 🛠️ Tecnologias Utilizadas

### Frontend
- **Angular 18** - Framework principal
- **TypeScript** - Linguagem de programação
- **CSS3** - Estilização moderna
- **RxJS** - Programação reativa

### Backend
- **Spring Boot 3.2** - Framework Java
- **Spring Data JPA** - Persistência de dados
- **H2 Database** - Banco de dados em memória (desenvolvimento)
- **PostgreSQL** - Banco de dados de produção
- **Maven** - Gerenciamento de dependências

### Infraestrutura
- **Docker** - Containerização
- **Docker Compose** - Orquestração de serviços
- **Nginx** - Servidor web para frontend

## 🚀 Como Executar

### Pré-requisitos
- Docker e Docker Compose instalados
- Node.js 18+ (para desenvolvimento)
- Java 17+ (para desenvolvimento)

### 🐳 Execução com Docker (Recomendado)

1. **Clone o repositório:**
```bash
git clone <repository-url>
cd dish-app-Docker
```

2. **Execute com Docker Compose:**
```bash
docker-compose up --build
```

3. **Acesse a aplicação:**
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console

### 🔧 Desenvolvimento Local

#### Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run
```

#### Frontend (Angular)
```bash
cd dish-app
npm install
npm start
```

## 📱 Como Usar o Sistema

### 1. Gerenciar Pratos
- Acesse a aba "🍽️ Pratos"
- Adicione novos pratos com nome, descrição e preço
- Edite ou exclua pratos existentes

### 2. Gerenciar Comandas
- Acesse a aba "📋 Comandas"
- Clique em "➕ Nova Comanda"
- Informe o número da mesa e nome do cliente (opcional)
- Adicione itens clicando nos pratos disponíveis
- Ajuste quantidades conforme necessário
- Altere o status da comanda conforme o progresso

### 3. Funcionalidades da Comanda
- **Adicionar Itens**: Clique nos pratos para adicionar à comanda
- **Ajustar Quantidade**: Use os botões ➕/➖ ou digite a quantidade
- **Remover Itens**: Clique no botão 🗑️ ao lado do item
- **Alterar Status**: Use o dropdown de status para acompanhar o progresso
- **Salvar Alterações**: Clique em "💾 Salvar Alterações"

## 🗂️ Estrutura do Projeto

```
dish-app-Docker/
├── dish-app/                 # Frontend Angular
│   ├── src/app/
│   │   ├── components/
│   │   │   ├── dish-form/    # Formulário de pratos
│   │   │   ├── dish-list/    # Lista de pratos
│   │   │   └── order-management/ # Sistema de comandas
│   │   ├── services/         # Serviços Angular
│   │   └── app.routes.ts     # Rotas da aplicação
│   └── Dockerfile.prod       # Docker para produção
├── backend/                  # Backend Spring Boot
│   ├── src/main/java/
│   │   └── com/restaurant/order/
│   │       ├── controller/   # Controllers REST
│   │       ├── model/        # Entidades JPA
│   │       ├── repository/   # Repositórios JPA
│   │       └── service/      # Lógica de negócio
│   └── pom.xml              # Dependências Maven
└── docker-compose.yml        # Orquestração Docker
```

## 🔧 Configurações

### Variáveis de Ambiente
- `API_URL`: URL do backend (padrão: http://localhost:8080)
- `DATABASE_URL`: URL do banco de dados
- `REDIS_URL`: URL do Redis (opcional)

### Portas
- **Frontend**: 4200
- **Backend**: 8080
- **PostgreSQL**: 5432
- **Redis**: 6379

## 🐛 Solução de Problemas

### Problemas Comuns

1. **Erro de CORS**: Verifique se o backend está rodando na porta 8080
2. **Erro de Conexão**: Verifique se todos os serviços estão rodando
3. **Dados não aparecem**: Verifique se o backend está conectado ao banco

### Logs
```bash
# Ver logs do Docker Compose
docker-compose logs -f

# Logs específicos do backend
docker-compose logs -f backend

# Logs específicos do frontend
docker-compose logs -f frontend
```

## 🚀 Deploy em Produção

1. **Configure variáveis de ambiente**
2. **Use PostgreSQL em produção**
3. **Configure SSL/TLS**
4. **Use um proxy reverso (Nginx)**
5. **Configure backup do banco**

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📞 Suporte

Para suporte, entre em contato através dos issues do GitHub ou email.

---

**Desenvolvido com ❤️ para restaurantes modernos**

