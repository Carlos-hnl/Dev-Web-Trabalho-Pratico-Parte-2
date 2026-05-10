# Dev-Web-Trabalho-Pratico-Parte-1
Parte 1 do Trabalho Prático da disciplina de Introdução ao Desenvolvimento Web 2026.1 de Sistemas de Informação na UFF.
Desenvolvido pelos alunos Carlos Henrique Nascimento, Vinícius Araújo, Lucio Diniz e Andre Meschesi Dantas.

Task Manager — Sistema Web Seguro 

Este projeto consiste em um sistema completo de gerenciamento de tarefas, desenvolvido como requisito para a disciplina de Desenvolvimento Web. A aplicação permite que usuários criem contas, autentiquem-se com segurança e gerenciem suas listas de tarefas pessoais em uma interface responsiva e dinâmica.


 Arquitetura do Sistema
O sistema segue rigorosamente o padrão MVC, garantindo a separação de responsabilidades:

1.  Model (Modelo):
    JavaBeans:Encapsulam as regras de negócio.
    DAO (Data Access Object): Interação com o banco de dados.
    Connection Pool: Utilização do HikariCP para escalabilidade.
2.  View (Visão):
     Páginas JSP utilizando JSTL para renderização de dados.
     Front-end com HTML5, CSS3 e JavaScript.
3.  Controller (Controlador):
    Servlets: Interceptam requisições HTTP, interagem com o Model e utilizam o padrão Forward/Redirect para direcionar a navegação.

---

 Segurança Aplicada
O projeto implementa múltiplas camadas de proteção:

Autenticação e Filtros: Sistema baseado em formulário com proteção via AuthFilter, garantindo que páginas restritas exijam sessão ativa.
Criptografia de Senhas: Senhas armazenadas utilizando o algoritmo BCrypt (Hashing).
Sessões e Cookies: Uso de HttpSession para estado da navegação e Cookies para a funcionalidade de "Lembrar login".
Anti-Cache: Configuração de cabeçalhos HTTP (`Cache-Control`, `Pragma`) para evitar armazenamento de dados sensíveis no histórico do navegador.
Prevenção contra Injeção: Validações robustas no Front-end (JS) e Back-end (Servlets), além do uso de `PreparedStatement` nos DAOs.

---

Tecnologias Utilizadas
* Linguagem: Java 24
* Framework Web: Java Servlets 4.0 & JSP 2.3
* Servidor Embutido: Apache Tomcat (via Cargo Maven Plugin)
* Banco de Dados: PostgreSQL
* Gestão de Dependências: Maven
* Bibliotecas Principais: HikariCP (Pool de Conexões), jBCrypt (Hashing de Senhas), JSTL (Templates).

---

Como Executar o Projeto

### 1. Requisitos Prévios
Java JDK 24 instalado e configurado nas Variáveis de Ambiente.
Apache Maven configurado.
Banco de Dados PostgreSQL instalado e rodando localmente.
Git instalado.

### 2. Configuração do Banco de Dados
1. Abra o pgAdmin (ou ferramenta de preferência).
2. Crie um banco de dados chamado `taskmanager`.
3. Execute o script `schema.sql` (localizado na pasta do projeto) para criar as tabelas necessárias.

### 3. Variáveis de Ambiente (Segurança de Credenciais)
O sistema foi projetado para não expor senhas no código fonte. Configure a seguinte Variável de Ambiente no seu sistema operacional:
`DB_PASSWORD`: (Insira aqui a senha real do seu PostgreSQL)

*Nota: Por padrão, o sistema tentará conectar utilizando o usuário `postgres` na porta `5432` com a senha 'postgres'.

### 4. Como Baixar e Executar o Projeto

1.Abra o terminal na pasta onde deseja salvar o projeto e execute:

git clone https://github.com/Carlos-hnl/Dev-Web-Trabalho-Pratico-Parte-1.git
cd Dev-Web-Trabalho-Pratico-Parte-1

2.Instalar dependências e compilar

mvn clean install

3.Iniciar o servidor

mvn tomcat7:run

4.Acessar o sistema

Após iniciar o servidor, abra o navegador e acesse:
http://localhost:8080

