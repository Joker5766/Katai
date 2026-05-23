# Katai

Katai is an AI-powered Android application that allows users to interact with PDF-based study material through natural language conversations.

Users can upload PDFs, ask questions, and receive context-aware AI responses grounded in the uploaded content.

The app features a polished modern chat interface, persistent chat sessions, multi-chat support, and AI-powered document interaction.

---

## Features

- Upload and interact with PDF documents
- AI-powered contextual question answering
- Multiple chat sessions support
- Persistent chat history
- Copy AI responses instantly
- Modern polished chat interface
- Material 3 design system
- PDF-grounded AI responses
- Reactive UI with Jetpack Compose
- Secure API key handling

---

## Tech Stack

- Kotlin
- Jetpack Compose
- MVVM Architecture
- StateFlow
- Coroutines
- Retrofit
- PDFBox Android
- Groq API
- Material 3

---

## How It Works

```text
PDF Upload
    ↓
Text Extraction
    ↓
AI Context Injection
    ↓
AI Response Generation
    ↓
Chat-Based Interaction
```

The AI is instructed to answer questions strictly using the uploaded PDF content.

---

## Setup

### 1. Clone Repository

```bash
git clone https://github.com/Joker5766/Katai.git
```

### 2. Add API Key

Add your Groq API key inside `local.properties`:

```properties
GROQ_API_KEY=your_api_key_here
```

### 3. Build and Run

Open the project in Android Studio, sync Gradle, and run the application.

---

## Current Capabilities

- Multi-chat support
- Persistent chat storage
- AI-grounded PDF conversations
- Copy AI responses
- Modern responsive UI
- PDF upload and extraction
- Context-aware AI responses

---

## Planned Improvements

- Multi-PDF support
- Smart PDF chunk retrieval
- Markdown response rendering
- Source citations and page references
- Offline caching
- Enhanced animations and transitions

---

## License

MIT License

---

## Author

Pranav