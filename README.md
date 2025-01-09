# ByteBox E-commerce Chatbot

[Read this in Portuguese](README.pt-BR.md)

A Spring Boot application that implements a customer service chatbot for ByteBox, a fictitious  e-commerce platform specializing in computer components and electronics.

## Features

- Real-time chat interface
- OpenAI integration for natural language processing
- ~~Chat history management~~ (Disabled due a bug)
- Markdown support for formatted responses
- Responsive design
- Shipping cost calculator (fake calculation logic)

## Tech Stack

- Backend:
  - Java 17
  - Spring Boot
  - Spring WebFlux
  - Thymeleaf

- Frontend:
  - HTML5
  - CSS3
  - JavaScript
  - jQuery
  - Marked.js for Markdown rendering

- AI Integration:
  - OpenAI API
  - Simple OpenAI Java client
  - JTokkit for token counting (indev)

## Prerequisites

- Java 17+
- Maven
- OpenAI API key
- OpenAI Assistant ID

## Configuration

1. Create an assistant on OpenAI's website

2. Enable File search and add the files `guidelines.md` and `about.md`in `src\main\resources\bytebox`

3. Add a function and paste the configuration from `functionCalling.json` in `src\main\resources\bytebox`

4. Enter the system instructions in the file `instructions.md` in `src\main\resources\bytebox`

5. Create a `.env` file in the root directory with:

```env
OPENAI_API_KEY=your_api_key_here
OPENAI_ASSISTANT_ID=your_assistant_id_here
```

## Building and deployment

### Recomended way

1. Install maven dependencies:

```shell
mvn clean install
```

2. Run Spring Boot

```shell
mvn spring-boot:run
```

3. Then access localhost:8080

### Alternative form

1. Build the project

```shell
mvn clean package -f pom.xml
```

2. Navigate to maven target folder

```shell
cd target
```

3. Run the application replacing with your variables

```shell
java "-Dspring.profiles.active=prod" "-DOPENAI_API_KEY=your_api_key_here" "-DOPENAI_ASSISTANT_ID=your_assistant_id_here" -jar chatbot-0.0.1-SNAPSHOT.jar
```

4. Then access localhost:8080

## Project Structure

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

## Dependencies

- [simple-openai](https://github.com/sashirestela/simple-openai) - Simple OpenAI Java client
- [jtokkit](https://github.com/knuddelsgmbh/jtokkit) - Token counting
