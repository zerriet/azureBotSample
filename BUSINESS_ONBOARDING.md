# Azure Bot Sample - Business Analyst Onboarding Guide

## Executive Summary

This project is a **Proof of Concept (POC)** for a **Speech-to-Transaction AI Agent** that enables users to interact with an AI assistant through both text and voice. The system integrates Azure's OpenAI Chat API with Azure Speech Services to create a conversational interface that can understand user inputs and respond with both text and synthesized speech.

### Project Name
**Azure Bot Sample** (a.k.a. "Albus Dumbledore AI Assistant")

### Business Value
- Demonstrates end-to-end conversational AI capabilities
- Provides voice-enabled customer service simulation
- Showcases intelligent cost optimization through conversation memory management
- Implements data protection measures to prevent PII leakage

---

## System Overview

### What Does It Do?

The application allows users to:
1. **Type or speak** their queries to an AI assistant
2. Receive **intelligent responses** powered by Azure OpenAI (GPT-4o-mini model)
3. **Listen to responses** through synthesized speech (Singapore English voice)
4. Maintain **conversation context** across multiple interactions
5. Choose between two integration methods: **SDK-based** or **REST API-based**

### Key Capabilities

#### 1. Conversational Intelligence
- Powered by Azure OpenAI's GPT-4o-mini model
- Maintains conversation history for contextual responses
- Supports multi-turn conversations with memory retention

#### 2. Voice Integration
- **Speech-to-Text**: Users can speak their queries using browser-based voice recognition
- **Text-to-Speech**: AI responses are converted to natural-sounding speech using Azure's Neural Voice (Singapore English - Luna)
- Audio responses are delivered as WAV files encoded in Base64

#### 3. Cost Optimization Features

##### Memory Management
The system implements intelligent conversation history trimming:
- Only retains the **last 3 user messages** and their associated AI responses
- Automatically purges older conversations to minimize token usage
- Reduces API costs by sending only relevant context

##### Deterministic Handling & PII Protection
Built-in safeguards prevent unnecessary outbound calls:
- **Base64 Detection**: Filters out audio data that might accidentally be added to conversation memory
- Prevents sensitive audio streams from being sent to the Chat API
- Ensures only text-based content is processed

---

## Technical Architecture

### System Components

```
┌─────────────────┐
│   Web Browser   │ (User Interface)
│   index.html    │
└────────┬────────┘
         │
         │ HTTP POST /api/chat
         ▼
┌─────────────────────────────────────┐
│  AzureChatAPIController             │
│  (Entry Point)                      │
│  - Receives user input              │
│  - Routes to SDK or URL method      │
│  - Coordinates speech synthesis     │
└────────┬───────────────┬────────────┘
         │               │
         │               └──────────────┐
         ▼                              ▼
┌─────────────────────────┐    ┌──────────────────┐
│ AzureChatAPIService     │    │  SpeechClient    │
│ - Chat Memory Manager   │    │  - Azure TTS     │
│ - OpenAI Integration    │    │  - SSML Builder  │
│ - Context Trimming      │    └──────────────────┘
│ - Base64 Filtering      │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│  ChatMemory             │
│  - History Storage      │
│  - PII Detection        │
│  - Auto-trimming        │
└─────────────────────────┘
```

### Data Flow

#### Inbound Request Flow
1. User types/speaks a message in the web interface
2. Frontend sends POST request to `/api/chat` with:
   - `message`: User's text input
   - `method`: "sdk" or "url" (determines integration approach)
3. Controller receives request and routes to appropriate service method

#### Processing Flow
4. **Chat Memory Check**: System validates input (filters Base64 data)
5. **Context Assembly**: Retrieves last 3 user messages with responses
6. **OpenAI API Call**: Sends conversation context to Azure OpenAI
7. **Response Generation**: Receives AI-generated text response
8. **Memory Update**: Stores assistant's response in conversation history
9. **Speech Synthesis**: Converts text response to audio (WAV format)

#### Outbound Response Flow
10. Controller packages response:
    - `message`: Text response from AI
    - `audioBase64Wav`: Base64-encoded audio file
11. Frontend receives response and:
    - Displays text in chat history
    - Plays audio automatically

---

## Integration Methods

The system supports two methods for calling Azure OpenAI:

### Method 1: SDK-Based (Recommended)
**Technical Implementation**: Uses Azure's official Java SDK (`com.azure.ai.openai`)

**Benefits**:
- Type-safe, strongly-typed models
- Built-in error handling
- Automatic retry logic
- Official Microsoft support

**Configuration Required**:
- Azure OpenAI endpoint (SDK variant)
- Deployment name
- API key

### Method 2: URL-Based (Direct REST API)
**Technical Implementation**: Raw HTTP POST using Spring's RestTemplate

**Benefits**:
- More flexible for custom implementations
- Direct control over request/response format
- Useful for debugging API behavior

**Configuration Required**:
- Azure OpenAI endpoint URL
- API key
- Manual JSON request construction

---

## Business Features Explained

### 1. Conversation Memory Management

**Business Problem**: Sending entire conversation histories to OpenAI increases costs and latency.

**Solution**:
- System automatically trims conversation history to last 3 user interactions
- Maintains sufficient context for coherent conversations
- Reduces token consumption by ~60-80% for long conversations

**Implementation**:
Located in `ChatMemory.java:34-51`, the `trimToLast3UserMessagesWithContext()` method ensures memory efficiency.

### 2. PII & Data Protection

**Business Problem**: Audio data (Base64-encoded) could accidentally enter conversation context, causing:
- Wasted API tokens
- Potential data leakage
- System errors

**Solution**:
- **Base64 Detection Filter** (in `ChatMemory.java:13-26`)
- Automatically rejects any message that appears to be Base64-encoded
- Prevents audio streams from being processed as text
- Logs rejected messages for audit purposes

**Impact**:
- Protects against accidental PII transmission
- Prevents costly API calls with invalid data
- Ensures data integrity

### 3. Deterministic Handling

**Business Context**: POC demonstrates how to prevent excessive outbound calls through:

1. **Input Validation**: Only valid text messages are processed
2. **Context Limiting**: Maximum 3 user messages retained
3. **Deduplication**: Base64 filter prevents duplicate/invalid data

**Cost Savings**:
- Reduces unnecessary API calls by filtering invalid inputs early
- Minimizes token usage through context trimming
- Prevents error-related retry costs

---

## User Interface Features

### Web Interface (`index.html`)

**Key Features**:
1. **Chat History Display**: Shows conversation thread with timestamps
2. **Input Methods**:
   - Text input field
   - Microphone button for voice input
3. **Method Selector**: Toggle between SDK and URL methods
4. **Audio Playback**: Automatic speech playback for responses
5. **Dark Mode**: User preference toggle

**User Experience**:
- Clean, modern chat interface
- Real-time message display
- Automatic audio playback
- Browser-based voice recognition (Chrome/Edge)

---

## Configuration & Dependencies

### Azure Services Required

1. **Azure OpenAI Service**
   - Deployment: GPT-4o-mini model
   - Required credentials: API key, endpoint URL

2. **Azure Speech Services**
   - Region: Southeast Asia
   - Voice Model: en-SG-LunaNeural (Singapore English - Female)
   - Speech Style: Customer Service
   - Required credentials: Subscription key

### Key Dependencies (`pom.xml`)

| Dependency | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.4.5 | Web framework |
| Azure AI OpenAI | 1.0.0-beta.12 | Chat API SDK |
| Azure Speech SDK | 1.43.0 | Text-to-speech |
| Jackson | (Boot default) | JSON processing |
| Commons Codec | 1.15 | Base64 detection |

---

## API Endpoints

### POST `/api/chat`

**Purpose**: Main endpoint for conversational interactions

**Request Body**:
```json
{
  "message": "What is the weather today?",
  "method": "sdk"
}
```

**Request Fields**:
- `message` (string, required): User's text input
- `method` (string, required): "sdk" or "url"

**Response**:
```json
{
  "message": "I don't have access to real-time weather...",
  "audioBase64Wav": "UklGRiQAAABXQVZFZm10IBAAA..."
}
```

**Response Fields**:
- `message` (string): AI-generated text response
- `audioBase64Wav` (string): Base64-encoded WAV audio

---

## Security Considerations

### Implemented Protections

1. **Base64 Filtering**: Prevents audio data from entering chat context
2. **Input Validation**: Checks for null/empty messages
3. **Error Handling**: Graceful degradation on API failures

### Sensitive Data Alert

⚠️ **API Keys Exposed in Code**: The `SpeechClient.java` file contains hardcoded credentials that should be moved to environment variables or Azure Key Vault in production.

**Current Issues**:
- Speech API key visible in source code (line 27)
- Should use `application.properties` or environment variables

---

## Operational Characteristics

### Performance
- **Response Time**: Typically 1-3 seconds (depends on OpenAI API latency)
- **Audio Generation**: ~500ms for speech synthesis
- **Concurrent Users**: Limited by Spring Boot default thread pool

### Scalability Considerations
- Conversation memory is stored in-memory (not persistent)
- Each user session maintains separate conversation history
- No database persistence (POC limitation)

### Cost Drivers
1. **OpenAI API Calls**: Charged per token (input + output)
2. **Speech Synthesis**: Charged per character converted to audio
3. **Azure App Service**: Hosting costs (P1v2 tier)

---

## Deployment Information

### Current Deployment Target
**Azure App Service**
- Resource Group: `bot-test`
- App Name: `ms-pramit`
- Region: Southeast Asia
- Pricing Tier: P1v2
- Runtime: Java 21 on Linux

### Build & Deploy
```bash
mvn clean package
mvn azure-webapp:deploy
```

---

## Limitations & Known Issues

### POC Limitations
1. **No Persistent Storage**: Conversation history lost on server restart
2. **Single-Instance Memory**: Not suitable for multi-instance deployments
3. **Hardcoded Credentials**: Security risk (as noted above)
4. **No Authentication**: Public endpoint (suitable only for demos)

### Browser Compatibility
- Voice input requires Chrome, Edge, or Safari
- Webkit Speech Recognition API dependency

---

## Future Enhancement Opportunities

### Potential Business Features
1. **User Authentication**: Add login/session management
2. **Conversation Persistence**: Store history in database
3. **Analytics Dashboard**: Track usage metrics, costs, user satisfaction
4. **Multi-Language Support**: Expand beyond Singapore English
5. **Advanced PII Detection**: Implement regex/AI-based PII masking
6. **Transaction Integration**: Add actual business transaction capabilities

### Technical Improvements
1. **Secrets Management**: Move credentials to Azure Key Vault
2. **Caching Layer**: Redis for conversation memory (distributed)
3. **Rate Limiting**: Prevent abuse and control costs
4. **Monitoring**: Application Insights integration
5. **Load Balancing**: Support horizontal scaling

---

## Glossary

| Term | Definition |
|------|------------|
| **Azure OpenAI** | Microsoft's managed service for OpenAI models |
| **Base64** | Binary-to-text encoding scheme (used for audio data) |
| **Chat Completion** | OpenAI API method for conversational responses |
| **GPT-4o-mini** | OpenAI's optimized small language model |
| **Neural Voice** | Azure's advanced TTS with natural-sounding speech |
| **SSML** | Speech Synthesis Markup Language (controls TTS) |
| **Token** | Unit of text processing (roughly 4 characters) |
| **TTS** | Text-to-Speech conversion |

---

## Getting Started Checklist

For business analysts testing this POC:

- [ ] Access the deployed application URL
- [ ] Test text-based conversation
- [ ] Test voice input (microphone button)
- [ ] Switch between SDK and URL methods
- [ ] Observe conversation context retention (ask follow-up questions)
- [ ] Review chat history trimming (after 3+ messages)
- [ ] Test audio playback functionality
- [ ] Try dark mode toggle

---

## Support & Contacts

### Technical Documentation
- Azure OpenAI: https://learn.microsoft.com/en-us/azure/ai-services/openai/
- Azure Speech Services: https://learn.microsoft.com/en-us/azure/ai-services/speech-service/

### API Documentation
- Swagger UI: Available at `/swagger-ui.html` when application is running
- Interactive API testing available through Swagger interface

---

**Document Version**: 1.0
**Last Updated**: December 2025
**POC Status**: Active Development
