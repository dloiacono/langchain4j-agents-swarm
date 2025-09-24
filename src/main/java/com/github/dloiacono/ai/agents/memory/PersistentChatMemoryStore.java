package com.github.dloiacono.ai.agents.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Collections;

/**
 * Persistent file system-based ChatMemoryStore implementation for LangChain4j agents.
 * Stores chat messages in JSON format in the file system for persistence across sessions.
 */
public class PersistentChatMemoryStore implements ChatMemoryStore {
    
    private static final String MEMORY_BASE_DIR = "./generated-project/.agent-memory";
    private final Path memoryDir;
    
    public PersistentChatMemoryStore() {
        this.memoryDir = Paths.get(MEMORY_BASE_DIR).toAbsolutePath().normalize();
        initializeMemoryDirectory();
    }
    
    private void initializeMemoryDirectory() {
        try {
            Files.createDirectories(memoryDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize memory directory: " + memoryDir, e);
        }
    }
    
    private Path getMemoryFilePath(Object memoryId) {
        String sanitizedId = sanitizeMemoryId(memoryId);
        return memoryDir.resolve(sanitizedId + "_chat_memory.json");
    }
    
    private String sanitizeMemoryId(Object memoryId) {
        if (memoryId == null) {
            return "default";
        }
        // Replace any characters that might be problematic in file names
        return memoryId.toString().replaceAll("[^a-zA-Z0-9_-]", "_");
    }
    
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        Path memoryFile = getMemoryFilePath(memoryId);
        
        if (!Files.exists(memoryFile)) {
            return Collections.emptyList();
        }
        
        try {
            String json = Files.readString(memoryFile);
            if (json.trim().isEmpty()) {
                return Collections.emptyList();
            }
            return ChatMessageDeserializer.messagesFromJson(json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read chat messages from file: " + memoryFile, e);
        }
    }
    
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        Path memoryFile = getMemoryFilePath(memoryId);
        
        try {
            // Ensure parent directory exists
            Files.createDirectories(memoryFile.getParent());
            
            // Serialize messages to JSON
            String json = ChatMessageSerializer.messagesToJson(messages);
            
            // Write to file
            Files.writeString(memoryFile, json, 
                StandardOpenOption.CREATE, 
                StandardOpenOption.TRUNCATE_EXISTING);
                
        } catch (IOException e) {
            throw new RuntimeException("Failed to update chat messages in file: " + memoryFile, e);
        }
    }
    
    @Override
    public void deleteMessages(Object memoryId) {
        Path memoryFile = getMemoryFilePath(memoryId);
        
        try {
            Files.deleteIfExists(memoryFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete chat messages file: " + memoryFile, e);
        }
    }
}
