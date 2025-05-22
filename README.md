# PizzaApp 🍕

![Versão](https://img.shields.io/badge/versão-1.0-green)
![Plataforma](https://img.shields.io/badge/plataforma-Android-brightgreen)
![SDK](https://img.shields.io/badge/SDK-30%2B-blue)
![Licença](https://img.shields.io/badge/licença-MIT-orange)

## 📱 Sobre o Aplicativo

PizzaApp é um aplicativo Android completo para gerenciamento de pizzaria, oferecendo funcionalidades tanto para clientes quanto para administradores. Com uma interface moderna e intuitiva, o aplicativo permite aos usuários visualizar o cardápio, realizar pedidos, aplicar cupons de desconto.

## ✨ Funcionalidades Principais

### Para Clientes
- **Autenticação Completa**: Sistema de login, cadastro e recuperação de senha
- **Navegação Intuitiva**: Interface com bottom navigation para acesso rápido às principais seções
- **Cardápio Digital**: Visualização de pizzas disponíveis com detalhes e preços
- **Carrinho de Compras**: Adição de itens e finalização de pedidos
- **Cupons de Desconto**: Aplicação de cupons promocionais


### Para Administradores
- **Painel Administrativo**: Acesso exclusivo para gerenciamento do sistema
- **Gerenciamento de Usuários**: Visualização, edição e remoção de contas
- **Gerenciamento de Produtos**: Adição, edição e remoção de pizzas do cardápio
- **Gerenciamento de Cupons**: Criação e controle de cupons promocionais


## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java
- **Plataforma**: Android SDK 30+
- **Arquitetura**: Fragments e Navigation Component
- **Persistência de Dados**: Firebase Firestore
- **Autenticação**: Firebase Authentication
- **Localização**: Google Maps API
- **UI/UX**: Material Design Components
- **Carregamento de Imagens**: Glide
- **Navegação**: BottomNavigationView e NavController

## 📋 Requisitos do Sistema

- Android 11.0 (API 30) ou superior
- Conexão com internet
- Serviços do Google Play atualizados
- Permissões de localização (para funcionalidades de mapa)

## 🚀 Instalação

1. Clone este repositório:
```bash
git clone https://github.com/seu-usuario/pizzaapp.git
```

2. Abra o projeto no Android Studio

3. Configure o Firebase:
   - Crie um projeto no [Firebase Console](https://console.firebase.google.com/)
   - Adicione um aplicativo Android com o pacote `br.com.estudos.myapplication`
   - Baixe o arquivo `google-services.json` e coloque-o na pasta `app/`
   - Ative o Firebase Authentication e o Firestore Database no console


4. Execute o build do projeto:
```bash
./gradlew build
```

5. Instale o aplicativo em um dispositivo ou emulador:
```bash
./gradlew installDebug
```

## 📱 Como Usar

### Acesso como Cliente
1. Abra o aplicativo e faça login com suas credenciais ou registre uma nova conta
2. Navegue pelo cardápio usando a barra de navegação inferior
3. Selecione pizzas para adicionar ao carrinho
4. Finalize o pedido informando endereço e forma de pagamento


### Acesso como Administrador
1. Faça login com as credenciais de administrador:
   - Email: admin@email.com
   - Senha: (definida durante a configuração)
2. Acesse o painel administrativo para gerenciar usuários, produtos e pedidos
3. Utilize as opções de cadastro e remoção conforme necessário.

## 🗂️ Estrutura do Projeto

```
app/
├── src/
│   ├── main/
│   │   ├── java/br/com/estudos/myapplication/
│   │   │   ├── ui/                           # Fragments e componentes de UI
│   │   │   │   ├── home/                     # Tela inicial e cardápio
│   │   │   │   ├── dashboard/                # Painel de pedidos
│   │   │   │   ├── notifications/            # Notificações e status
│   │   │   │   └── settings/                 # Configurações do usuário
│   │   │   ├── adapters/                     # Adaptadores para RecyclerViews
│   │   │   ├── models/                       # Classes de modelo (Pizza, User, etc.)
│   │   │   ├── MainActivity.java             # Activity principal com navegação
│   │   │   ├── Login.java                    # Tela de login
│   │   │   ├── Register.java                 # Tela de cadastro
│   │   │   ├── ForgotPassword.java           # Recuperação de senha
│   │   │   ├── AdminActivity.java            # Painel administrativo
│   │   │   ├── UserManagement.java           # Gerenciamento de usuários
│   │   │   └── InsertPizzas.java             # Cadastro de pizzas
│   │   ├── res/
│   │   │   ├── layout/                       # Arquivos de layout XML
│   │   │   ├── drawable/                     # Recursos gráficos
│   │   │   ├── values/                       # Strings, cores, estilos
│   │   │   └── navigation/                   # Gráficos de navegação
│   │   └── AndroidManifest.xml               # Configuração do aplicativo
│   └── test/                                 # Testes unitários
└── build.gradle                              # Configuração de build
```

## 🔐 Autenticação e Segurança

O aplicativo utiliza o Firebase Authentication para gerenciar o acesso de usuários, garantindo:

- Autenticação segura por email e senha
- Recuperação de senha por email
- Diferentes níveis de acesso (cliente e administrador)
- Proteção de rotas administrativas
- Armazenamento seguro de dados sensíveis

## 🗄️ Banco de Dados

O Firebase Firestore é utilizado como banco de dados NoSQL para armazenar:

- Informações de usuários
- Catálogo de pizzas
- Pedidos realizados
- Cupons promocionais
- Configurações do aplicativo


## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Faça commit das alterações (`git commit -m 'Adiciona nova funcionalidade'`)
4. Faça push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 📞 Contato

Para questões, sugestões ou problemas, entre em contato através das issues do GitHub ou pelo email: seu-email@exemplo.com

---

Desenvolvido com ❤️ para amantes de pizza 🍕
