package com.github.dloiacono.ai.agents.memory;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

/**
 * Factory class for creating agent-specific chat memory configurations.
 * Each agent gets its own persistent chat memory with a unique memory ID.
 */
public class AgentChatMemoryFactory {
    
    private static final int DEFAULT_MAX_MESSAGES = 100;
    
    /**
     * Creates a chat memory for the Analyst agent
     * @return ChatMemory configured for the analyst
     */
    public static ChatMemory createAnalystMemory() {
        return MessageWindowChatMemory.builder()
                .id("analyst")
                .maxMessages(DEFAULT_MAX_MESSAGES)
                .chatMemoryStore(new PersistentChatMemoryStore())
                .build();
    }
    
    /**
     * Creates a chat memory for the Architect agent
     * @return ChatMemory configured for the architect
     */
    public static ChatMemory createArchitectMemory() {
        return MessageWindowChatMemory.builder()
                .id("architect")
                .maxMessages(DEFAULT_MAX_MESSAGES)
                .chatMemoryStore(new PersistentChatMemoryStore())
                .build();
    }
    
    /**
     * Creates a chat memory for the Developer agent
     * @return ChatMemory configured for the developer
     */
    public static ChatMemory createDeveloperMemory() {
        return MessageWindowChatMemory.builder()
                .id("developer")
                .maxMessages(DEFAULT_MAX_MESSAGES)
                .chatMemoryStore(new PersistentChatMemoryStore())
                .build();
    }
    
    /**
     * Creates a chat memory with a custom memory ID
     * @param memoryId The unique identifier for this memory
     * @return ChatMemory configured with the specified ID
     */
    public static ChatMemory createCustomMemory(String memoryId) {
        return MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(DEFAULT_MAX_MESSAGES)
                .chatMemoryStore(new PersistentChatMemoryStore())
                .build();
    }
    
    /**
     * Creates a chat memory with custom configuration
     * @param memoryId The unique identifier for this memory
     * @param maxMessages Maximum number of messages to retain
     * @return ChatMemory configured with the specified parameters
     */
    public static ChatMemory createCustomMemory(String memoryId, int maxMessages) {
        return MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(maxMessages)
                .chatMemoryStore(new PersistentChatMemoryStore())
                .build();
    }
}
