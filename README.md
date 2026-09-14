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