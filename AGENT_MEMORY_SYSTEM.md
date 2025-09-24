# Agent Memory System Documentation

## Overview

The Agent Memory System uses LangChain4j's native `chatMemory` configuration to provide persistent memory capabilities for each agent in the LangChain4j agents team. Each agent (Analyst, Architect, Developer) has its own dedicated chat memory with persistent file system storage that maintains conversation history and context between interactions.

## Architecture

### Core Components

1. **PersistentChatMemoryStore** - File system-based implementation of LangChain4j's ChatMemoryStore interface
2. **AgentChatMemoryFactory** - Factory class for creating agent-specific chat memory configurations
3. **MessageWindowChatMemory** - LangChain4j's built-in sliding window chat memory implementation
4. **Agent-Specific Memory IDs** - Unique identifiers for each agent's conversation history

### Memory Storage Structure

```
./generated-project/.agent-memory/
├── analyst_chat_memory.json      # Analyst's conversation history
├── architect_chat_memory.json    # Architect's conversation history
└── developer_chat_memory.json    # Developer's conversation history
```

Each file contains the complete chat message history for the respective agent in JSON format, managed automatically by LangChain4j's ChatMemoryStore system.

## LangChain4j ChatMemory Implementation

### PersistentChatMemoryStore

**Purpose**: Implements LangChain4j's `ChatMemoryStore` interface to provide file system-based persistence for chat messages.

**Key Features**:
- Automatic serialization/deserialization of chat messages to/from JSON
- File system-based storage with unique files per agent
- Safe file operations with proper error handling
- Memory ID sanitization for file system compatibility

**Core Methods**:
- `getMessages(Object memoryId)` - Retrieves all chat messages for a given memory ID
- `updateMessages(Object memoryId, List<ChatMessage> messages)` - Updates stored messages
- `deleteMessages(Object memoryId)` - Deletes all messages for a memory ID

### AgentChatMemoryFactory

**Purpose**: Factory class for creating properly configured chat memory instances for each agent.

**Key Features**:
- Pre-configured memory settings for each agent type
- Shared persistent storage backend
- Customizable message window sizes
- Agent-specific memory IDs

**Factory Methods**:
- `createAnalystMemory()` - Creates chat memory for the Analyst agent
- `createArchitectMemory()` - Creates chat memory for the Architect agent  
- `createDeveloperMemory()` - Creates chat memory for the Developer agent
- `createCustomMemory(String memoryId)` - Creates custom memory with specified ID

## Chat Memory Configuration

Each agent is configured with LangChain4j's native `chatMemory` parameter in the agent builder:

### Memory Configuration Features

1. **Persistent Storage**: All chat messages are automatically persisted to the file system
2. **Message Window**: Each agent retains the last 100 messages by default
3. **Automatic Management**: LangChain4j handles serialization, deserialization, and eviction policies
4. **Agent Isolation**: Each agent has its own separate conversation history

### Default Configuration

- **Memory Type**: `MessageWindowChatMemory` (sliding window approach)
- **Max Messages**: 100 messages per agent
- **Storage**: File system-based via `PersistentChatMemoryStore`
- **Format**: JSON serialization of chat messages
- **Eviction Policy**: Oldest messages are removed when limit is exceeded

## Usage Examples

### Creating Agent with Chat Memory

```java
// Each agent is configured with persistent chat memory
Analyst analyst = AgenticServices.agentBuilder(Analyst.class)
        .chatModel(CHAT_MODEL)
        .chatMemory(AgentChatMemoryFactory.createAnalystMemory())  // Persistent memory
        .tools(new FileSystemTool())
        .build();

Architect architect = AgenticServices.agentBuilder(Architect.class)
        .chatModel(CHAT_MODEL)
        .chatMemory(AgentChatMemoryFactory.createArchitectMemory())  // Persistent memory
        .tools(new FileSystemTool())
        .build();

Developer developer = AgenticServices.agentBuilder(Developer.class)
        .chatModel(CHAT_MODEL)
        .chatMemory(AgentChatMemoryFactory.createDeveloperMemory())  // Persistent memory
        .tools(new FileSystemTool(), new MavenTool())
        .build();
```

### Memory Persistence in Action

```java
// When agents interact, their conversation history is automatically persisted
String result1 = analyst.analyzeFeature("Create user authentication system");
// This conversation is stored in ./generated-project/.agent-memory/analyst_chat_memory.json

String result2 = architect.designSolution();
// This builds on the analyst's work and stores architect's conversation
// in ./generated-project/.agent-memory/architect_chat_memory.json

String result3 = developer.develop();
// Developer can reference both previous conversations through the supervisor
// and stores its own conversation in ./generated-project/.agent-memory/developer_chat_memory.json
```

### Custom Memory Configuration

```java
// Create custom memory with different settings
ChatMemory customMemory = AgentChatMemoryFactory.createCustomMemory("custom-agent", 50);

Agent customAgent = AgenticServices.agentBuilder(CustomAgent.class)
        .chatModel(CHAT_MODEL)
        .chatMemory(customMemory)
        .tools(new FileSystemTool())
        .build();
```

## Benefits

1. **Native LangChain4j Integration**: Uses LangChain4j's built-in chatMemory system for optimal compatibility
2. **Persistent Context**: Agents maintain conversation history between interactions and sessions
3. **Automatic Management**: LangChain4j handles serialization, eviction, and memory management automatically
4. **Agent Isolation**: Each agent has its own separate conversation history and context
5. **Standard Implementation**: Follows LangChain4j best practices and patterns
6. **Scalable Design**: File-based storage allows for easy backup, migration, and debugging

## Configuration

The memory system uses the following configuration:

- **Base Directory**: `./generated-project/.agent-memory/`
- **File Format**: JSON serialization via LangChain4j's ChatMessageSerializer
- **Memory Files**: `{agent-id}_chat_memory.json` per agent
- **Memory Type**: `MessageWindowChatMemory` with sliding window eviction
- **Default Window Size**: 100 messages per agent

## Integration with AgentsTeam

The `AgentsTeam.java` has been updated to use LangChain4j's native chatMemory configuration:

```java
// Each agent gets its own persistent chat memory
Analyst analyst = AgenticServices.agentBuilder(Analyst.class)
        .chatModel(CHAT_MODEL)
        .chatMemory(AgentChatMemoryFactory.createAnalystMemory())  // Native chatMemory
        .tools(new FileSystemTool())
        .build();

Architect architect = AgenticServices.agentBuilder(Architect.class)
        .chatModel(CHAT_MODEL)
        .chatMemory(AgentChatMemoryFactory.createArchitectMemory())  // Native chatMemory
        .tools(new FileSystemTool())
        .build();

Developer developer = AgenticServices.agentBuilder(Developer.class)
        .chatModel(CHAT_MODEL)
        .chatMemory(AgentChatMemoryFactory.createDeveloperMemory())  // Native chatMemory
        .tools(new FileSystemTool(), new MavenTool())
        .build();
```

## Future Enhancements

Potential improvements to consider:

1. **Token-Based Memory**: Switch to `TokenWindowChatMemory` for more precise memory management
2. **Memory Compression**: Implement message summarization for very long conversations
3. **Memory Analytics**: Add insights about conversation patterns and memory usage
4. **Configuration Options**: Make memory window size and storage location configurable
5. **Memory Backup**: Implement automated backup and restore functionality
6. **Cross-Agent Context**: Enable controlled sharing of relevant context between agents

## Troubleshooting

### Common Issues

1. **Memory Directory Creation Fails**: Ensure write permissions to the project directory
2. **JSON Serialization Errors**: Check for corrupted memory files and delete if necessary
3. **Performance Issues**: Consider reducing message window size for very active agents
4. **Memory File Conflicts**: Ensure unique memory IDs for different agent instances

### Debugging

Inspect agent memory files directly:

```bash
# View agent conversation history
cat ./generated-project/.agent-memory/analyst_chat_memory.json
cat ./generated-project/.agent-memory/architect_chat_memory.json
cat ./generated-project/.agent-memory/developer_chat_memory.json

# Check memory directory structure
ls -la ./generated-project/.agent-memory/
```

### Memory Management

```java
// Clear agent memory if needed (use with caution)
ChatMemory analystMemory = AgentChatMemoryFactory.createAnalystMemory();
analystMemory.clear(); // This will delete the memory file

// Create memory with custom settings
ChatMemory customMemory = AgentChatMemoryFactory.createCustomMemory("special-task", 50);
```
