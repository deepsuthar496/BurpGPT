# Burp Suite AI Assistant Extension

A Burp Suite extension that uses AI to analyze HTTP requests and identify security vulnerabilities.

## Requirements

- Java 17 or higher
- Burp Suite Professional/Community Edition
- Gradle (for building)

## Building

```bash
./gradlew clean build
```

The extension JAR will be created in `build/libs/`.

## Installation

1. Open Burp Suite
2. Go to Extensions tab
3. Click "Add" button
4. Select the generated JAR file

## Features

- AI-powered request analysis
- Interactive chat interface
- Request scanning capabilities ( Beta )
- Configurable AI settings

## Configuration for model and System prompt

The extension uses the Pollinations AI API :
- AI model selection
- System prompts
