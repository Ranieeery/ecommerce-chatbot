# ByteBox E-commerce Chatbot

[Leia em inglês](README.md)

Uma aplicação Spring Boot que implementa um chatbot de atendimento ao cliente para a ByteBox, uma plataforma fictícia de comércio eletrônico especializada em componentes de computador e eletrônicos.

## Recursos

- Interface de bate-papo em tempo real
- Integração com OpenAI para processamento de linguagem
- ~~Gerenciamento do histórico de bate-papo~~ (desativado devido a um bug)
- Suporte a markdown para respostas formatadas
- Design responsivo
- Calculadora de custos de envio (lógica de cálculo fictícia)

## Tecnologias

- Backend:
  - Java 17
  - Spring Boot
  - Spring WebFlux
  - Thymeleaf

- Front-end:
  - HTML5
  - CSS3
  - JavaScript
  - jQuery
  - Marked.js para renderização de Markdown

- Integração de IA:
  - OpenAI API
  - Simple OpenAI Java
  - JTokkit para contagem de tokens (indev)

## Pré-requisitos

- Java 17+
- Maven
- Chave da API do OpenAI
- ID do assistente do OpenAI

## Configuração

1. Crie um assistente no site da OpenAI

2. Ative a pesquisa de arquivos e adicione os arquivos `guidelines.md` e `about.md` localizados em `src\main\resources\bytebox`

3. Adicione uma função e cole a configuração de `functionCalling.json` localizada em `src\main\resources\bytebox`

4. Insira as instruções do sistema disponíveis no arquivo `instructions.md` localizados em `src\main\resources\bytebox`

5. Crie um arquivo `.env` no diretório raiz com:

```env
OPENAI_API_KEY=sua_chave_de_api_aqui
OPENAI_ASSISTANT_ID=seu_assistente_id_aqui
```

## Build e implementação

### Maneira recomendada

1. Instale as dependências do maven:

```shell
mvn clean install
```

2. Executar Spring Boot

```shell
mvn spring-boot:run
```

3. Em seguida, acesse localhost:8080

### Forma alternativa

1. Crie o package

```shell
mvn clean package -f pom.xml
```

2. Navegue até a pasta de destino do maven

```shell
cd target
```

3. Execute a aplicação, substituindo por suas variáveis

```shell
java “-Dspring.profiles.active=prod” “-DOPENAI_API_KEY=sua_api_key_aqui” “-DOPENAI_ASSISTANT_ID=sua_assistant_id_aqui” -jar chatbot-0.0.1-SNAPSHOT.jar
```

4. Em seguida, acesse localhost:8080

## Estrutura do projeto

```structure
src/
└── main/
    ├── java/
    │   └── dev/raniery/chatbot/
    │       ├── web/
    │       │   ├── controller/
    │       │   └── dto/
    │       ├── domain/
    │       │   └── service/
    │       ├── infra/
    │       │   └── openai/
    │       └── ChatbotApplication.java
    └── resources/
        ├── static/
        │   ├── css/
        │   └── js/
        ├── templates/
        └── bytebox/
```

## Dependências

- [simple-openai](https://github.com/sashirestela/simple-openai) - Simple OpenAI Java client
- [jtokkit](https://github.com/knuddelsgmbh/jtokkit) - Contagem de tokens
