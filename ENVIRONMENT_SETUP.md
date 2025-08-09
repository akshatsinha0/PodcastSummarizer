# Environment Setup

## API Keys Configuration

This app requires API keys for AI services. You have two options to configure them:

### Option 1: Using .env file (Recommended)

1. Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` and add your actual API keys:
   ```
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   OPENAI_API_KEY=your_actual_openai_api_key_here
   ```

### Option 2: Using local.properties (Android Standard)

1. Copy `local.properties.template` to `local.properties`:
   ```bash
   cp local.properties.template local.properties
   ```

2. Edit `local.properties` and add your API keys:
   ```
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   OPENAI_API_KEY=your_actual_openai_api_key_here
   ```

## Getting API Keys

### Gemini AI API Key
1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Create a new API key
3. Copy the key to your environment file

### OpenAI API Key (Optional)
1. Go to [OpenAI API Keys](https://platform.openai.com/api-keys)
2. Create a new secret key
3. Copy the key to your environment file

## Security Notes

- ✅ Both `.env` and `local.properties` are in `.gitignore`
- ✅ Never commit API keys to version control
- ✅ Use `.env.example` and `local.properties.template` for documentation
- ✅ API keys are injected at build time via BuildConfig

## Build Configuration

The app uses Gradle's `buildConfigField` to inject API keys:

```kotlin
buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
```

This allows secure access via `BuildConfig.GEMINI_API_KEY` in the code.