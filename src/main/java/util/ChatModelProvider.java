package util;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;


public class ChatModelProvider {
    
    public enum AI_PROVIDER {
        ANTHROPIC
    }
    
    public static ChatModel createDefaultChatModel() {
        return createDefaultChatModel(AI_PROVIDER.ANTHROPIC, "claude-opus-4-1-20250805");
    }

    public static ChatModel createDefaultChatModel(AI_PROVIDER provider, String modelName) {
        switch (provider) {
            case ANTHROPIC:
                return AnthropicChatModel.builder()
                        .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                        .modelName(modelName)
                        .maxTokens(4096)  // Increased from default 1024 to allow complete tool calls
                        .logRequests(true)
                        .logResponses(true)
                        .build();
                default:
                    throw new IllegalArgumentException("Unknown provider: " + provider);
        }
    }
}