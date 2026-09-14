# ORION

### Omni-Responsive Intelligent Operator Network

ORION is a personal AI assistant for Android, designed to provide a fast, modern, and intelligent assistant experience with a clean Material You-inspired interface.

ORION combines a native Android application, a lightweight cloud backend, and an AI model hosted through Groq.

## Overview

ORION consists of three main components:

```text
┌─────────────────────────┐
│       ORION APP         │
│        Android          │
│                         │
│  • AI Chat              │
│  • Voice Input          │
│  • Memory               │
│  • Settings             │
│  • Material 3 UI        │
└────────────┬────────────┘
             │
             │ HTTPS
             ▼
┌─────────────────────────┐
│    CLOUDFLARE WORKER    │
│                         │
│       orion-api         │
│                         │
│  • API Gateway          │
│  • Secure API Key       │
│  • Request Handling     │
└────────────┬────────────┘
             │
             │ HTTPS
             ▼
┌─────────────────────────┐
│          GROQ           │
│                         │
│      gpt-oss-20b        │
│                         │
│     AI Inference        │
└─────────────────────────┘

The Android application sends the user's message to the Cloudflare Worker. The Worker securely communicates with Groq, which runs the gpt-oss-20b model. The generated response is then returned to the Android application.

Current Features

Native Android application

Jetpack Compose UI

Material 3 design

Material You-inspired interface

Custom ORION app icon

AI-powered conversations

Groq AI integration

Cloudflare Worker backend

Secure server-side API key

Voice input using Android Speech Recognition

Chat thinking indicator

Multiple-message conversations

Network and API error handling

GitHub Actions automated APK builds


Technology Stack

Android

Kotlin

Jetpack Compose

Material 3

AndroidX

Android Speech Recognition

Gradle


Backend

Cloudflare Workers

JavaScript

HTTPS REST API


AI

Groq API

OpenAI gpt-oss-20b


Development

GitHub

GitHub Actions

Gradle 8.13

JDK 17


Architecture

ORION follows a simple client-server architecture:

User
 │
 ▼
ORION Android App
 │
 │ POST / JSON
 │
 ▼
Cloudflare Worker
 │
 │ Groq API
 │
 ▼
gpt-oss-20b
 │
 │ Generated response
 ▼
Cloudflare Worker
 │
 │ JSON
 ▼
ORION Android App
 │
 ▼
User

API Request

The Android application sends messages to the ORION backend using JSON.

Example:

{
  "message": "What is artificial intelligence?"
}

The Cloudflare Worker receives the request and forwards the message to Groq.

API Response

The Worker returns a simple JSON response:

{
  "reply": "Artificial intelligence is..."
}

This keeps the Android application independent from the underlying AI provider.

Security

The Groq API key is not stored inside the Android application.

Instead, it is stored as a Cloudflare Worker secret:

GROQ_API_KEY

The Worker accesses the secret using:

env.GROQ_API_KEY

This prevents the API key from being included directly in the Android source code.

Never commit API keys or other secrets to GitHub.

Voice Input

ORION supports voice input using Android's built-in speech recognition system.

The microphone button starts Android Speech Recognition and converts the user's speech into text.

Voice
  ↓
Android Speech Recognition
  ↓
Text
  ↓
ORION
  ↓
AI

Project Structure

ORION/
│
├── .github/
│   └── workflows/
│       └── build.yml
│
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── bijoyism/
│   │       │           └── orion/
│   │       │               └── MainActivity.kt
│   │       │
│   │       └── res/
│   │           ├── drawable/
│   │           ├── mipmap/
│   │           └── values/
│   │
│   └── build.gradle.kts
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md

Building ORION

The project can be built automatically using GitHub Actions.

Every push to the main branch triggers the build workflow.

The workflow:

1. Checks out the repository.


2. Installs JDK 17.


3. Sets up Gradle 8.13.


4. Builds the debug APK.


5. Uploads the APK as a GitHub Actions artifact.



Build command:

gradle assembleDebug

Generated APK:

app/build/outputs/apk/debug/app-debug.apk

Requirements

For Android development:

Android SDK

JDK 17

Gradle 8.13

Kotlin

Android Studio or another compatible development environment


The application currently uses:

compileSdk: 36
targetSdk: 35
minSdk: 23

Backend

The ORION backend is hosted using Cloudflare Workers.

The Worker is responsible for:

Receiving Android requests

Validating messages

Communicating with Groq

Protecting the Groq API key

Returning a consistent JSON response

Handling API errors


The Android application does not communicate directly with Groq.

AI Model

ORION currently uses:

openai/gpt-oss-20b

through Groq.

The model provides the conversational intelligence while ORION provides the application interface, assistant behavior, memory system, voice interaction, and future tool integrations.

Roadmap

ORION is an ongoing project.

Memory

Persistent user memory

Important facts

User preferences

Context-aware conversations

Local memory storage


Conversations

Persistent chat history

Multiple conversations

Conversation management

Clear and delete conversations


Voice

Text-to-speech

Full voice conversations

Wake-word support

Hands-free interaction


Assistant Actions

Future versions may allow ORION to interact with Android and external services.

Possible capabilities include:

Opening applications

Opening websites

Setting alarms

Creating reminders

Checking device information

Performing searches

Managing assistant settings


Intelligence

Tool calling

Web search

Context management

Better reasoning

Personalized responses

Task execution


UI

More Material You integration

Smooth animations

Dynamic theming

Improved chat interface

Assistant status indicators

Custom ORION animations


Design Philosophy

ORION is built around five principles.

Simple

The interface should remain clean and easy to use.

Fast

Requests should use a lightweight backend with minimal unnecessary processing.

Modular

The AI provider, backend, Android UI, memory system, and tools should remain replaceable components.

Personal

ORION should gradually become a personal assistant rather than simply another chatbot.

Secure

Secrets such as API keys should remain on the backend and never be embedded in the Android application.

Current Status

Status: Active Development

The core AI conversation pipeline is currently operational:

Android App
     ↓
Cloudflare Worker
     ↓
Groq
     ↓
gpt-oss-20b
     ↓
Cloudflare Worker
     ↓
Android App

The basic AI conversation system and voice input are functional.

Additional assistant capabilities are being developed incrementally.

License

This project is currently under active development.

License information will be added when the project reaches a stable release.

Author

Bijoy

ORION

Omni-Responsive Intelligent Operator Network

A personal AI assistant for Android.