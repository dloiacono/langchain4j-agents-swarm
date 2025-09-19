# LangChain4j Agents Team

A sophisticated AI agents orchestration system built with LangChain4j that implements a collaborative team of specialized agents working together under supervisor coordination to analyze requirements, design solutions, and develop complete software implementations.

## 🎯 Overview

This project demonstrates advanced agentic AI patterns using LangChain4j's supervisor orchestration capabilities. It features a team of three specialized agents that collaborate to transform feature requests into fully implemented solutions:

- **Analyst** (Product Team): Analyzes feature requests and creates detailed requirements
- **Architect** (Engineering Team): Reviews requirements and designs technical solutions  
- **Developer** (Engineering Team): Implements the architecture using Test-Driven Development

## 🏗️ Architecture

The system uses a **Supervisor Pattern** where a `TeamSupervisor` orchestrates the collaboration between specialized agents. Each agent has access to powerful tools for file operations and project context analysis.

```
┌─────────────────┐
│  TeamSupervisor │
│   (Orchestrator) │
└─────────┬───────┘
          │
    ┌─────┴─────┐
    │           │
┌───▼───┐   ┌───▼────────┐
│Analyst│   │ Engineering│
│       │   │    Team    │
└───────┘   └─┬──────────┘
              │
        ┌─────┴─────┐
        │           │
    ┌───▼───┐   ┌───▼───┐
    │Architect│ │Developer│
    └───────┘   └───────┘
```

## 🚀 Features

- **Intelligent Agent Orchestration**: Supervisor automatically delegates tasks to appropriate agents
- **Context-Aware Processing**: Agents maintain project context and adapt to existing codebases
- **File System Integration**: Complete file read/write capabilities for project management
- **Test-Driven Development**: Developer agent implements TDD practices with 80%+ coverage
- **Multi-Technology Support**: Supports Java/Quarkus backend and Angular frontend
- **Iterative Refinement**: Agents can iterate and improve their outputs based on feedback

## 🛠️ Technology Stack

- **Java 21**: Modern Java features and performance
- **Maven**: Dependency management and build automation
- **LangChain4j 1.5.0**: Core agentic AI framework
- **Anthropic Integration**: Claude AI model integration
- **Logback**: Comprehensive logging system

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- Anthropic API key (for Claude integration)

## ⚙️ Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd langchain4j-agents-team
   ```

2. **Configure API Keys**
   
   Set up your Anthropic API key as an environment variable:
   ```bash
   export ANTHROPIC_API_KEY=your_api_key_here
   ```
   
   Or configure it in your IDE's run configuration.

3. **Build the project**
   ```bash
   ./mvnw clean compile
   ```

4. **Run the application**
   ```bash
   ./mvnw exec:java -Dexec.mainClass="com.github.dloiacono.ai.agents.AgentsTeam"
   ```

## 🎮 Usage

### Basic Usage

The system is designed to handle feature requests and transform them into complete implementations. Here's how it works:

1. **Submit a Feature Request**: The system accepts natural language feature descriptions
2. **Automatic Analysis**: The Analyst agent creates detailed requirements (REQUIREMENTS.MD)
3. **Solution Design**: The Architect agent designs the technical solution (ARCHITECTURE.MD)
4. **Implementation**: The Developer agent implements the complete solution with tests

### Example Request

```java
String request = "Create a REST API using Quarkus that perform simple calculations between two numbers";
```

### Agent Workflow

1. **Analyst Phase**:
   - Analyzes the feature request
   - Creates `REQUIREMENTS.MD` with functional/non-functional requirements
   - Defines user stories and acceptance criteria

2. **Architect Phase**:
   - Reviews the requirements from `REQUIREMENTS.MD`
   - Creates `ARCHITECTURE.MD` with technical specifications
   - Designs system architecture and component interactions

3. **Developer Phase**:
   - Implements the architecture from `ARCHITECTURE.MD`
   - Uses Test-Driven Development (TDD)
   - Creates production-quality code with 80%+ test coverage
   - Generates comprehensive documentation

### Customizing Requests

You can modify the request in `AgentsTeam.java`:

```java
String request = "Your custom feature request here";
ResultWithAgenticScope<String> decision = teamSupervisor.invoke(request, "Successful build and test of the solution is a must.");
```

## 🔧 Configuration

### Supervisor Configuration

The `TeamSupervisor` can be configured with different strategies:

```java
TeamSupervisor teamSupervisor = AgenticServices
    .supervisorBuilder(TeamSupervisor.class)
    .chatModel(CHAT_MODEL)
    .subAgents(analyst, architect, developer)
    .contextGenerationStrategy(SupervisorContextStrategy.CHAT_MEMORY_AND_SUMMARIZATION)
    .responseStrategy(SupervisorResponseStrategy.SCORED)
    .supervisorContext("Policy: Always analyze the request, then delegate to the appropriate sub-agent, iterate if needed.")
    .build();
```

### Context Generation Strategies

- `CHAT_MEMORY`: Uses conversation history
- `SUMMARIZATION`: Uses summarized context
- `CHAT_MEMORY_AND_SUMMARIZATION`: Combines both approaches

### Response Strategies

- `SCORED`: Uses a scorer model to select the best response
- Custom output functions can override response strategies

## 🛠️ Tools Available to Agents

### ProjectContextTool
- `readAllProjectFiles()`: Reads entire project structure and content
- `getProjectFiles(relativePath, filePattern)`: Gets specific files matching patterns

### WriteFileTool
- `writeFile(content, filePath)`: Creates or overwrites files
- `appendToFile(content, filePath)`: Appends content to existing files

### ReadFileTool
- `readFile(filePath)`: Reads file content
- `fileExists(filePath)`: Checks file existence

## 📁 Project Structure

```
src/main/java/
├── com/github/dloiacono/ai/agents/
│   ├── AgentsTeam.java              # Main orchestrator
│   ├── TeamSupervisor.java          # Supervisor interface
│   ├── engineering/
│   │   ├── Architect.java           # Architecture design agent
│   │   └── Developer.java           # Implementation agent
│   ├── product/
│   │   └── Analyst.java             # Requirements analysis agent
│   └── tools/
│       ├── ProjectContextTool.java  # Project analysis tool
│       ├── ReadFileTool.java        # File reading tool
│       └── WriteFileTool.java       # File writing tool
└── util/
    ├── ChatModelProvider.java       # AI model configuration
    └── log/                         # Logging utilities
```

## 🔍 Monitoring & Debugging

The system includes comprehensive logging to track agent interactions:

```java
// Enable detailed logging
CustomLogging.setLevel(LogLevels.PRETTY, 200);
```

### Context Inspection

After execution, you can inspect the full conversation context:

```java
System.out.println(decision.agenticScope().contextAsConversation());
```

## 🎯 Best Practices

1. **Clear Feature Requests**: Provide detailed, specific feature descriptions
2. **Environment Setup**: Ensure all prerequisites are properly configured
3. **API Key Security**: Never commit API keys to version control
4. **Resource Management**: Monitor token usage for large projects
5. **Iterative Development**: Allow agents to iterate and refine their outputs

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Troubleshooting

### Common Issues

**API Key Issues**
```
Error: Anthropic API key not configured
Solution: Set ANTHROPIC_API_KEY environment variable
```

**Memory Issues**
```
Error: OutOfMemoryError when reading large projects
Solution: Increase JVM heap size: -Xmx4g
```

**File Permission Issues**
```
Error: Cannot write to file
Solution: Check file permissions and directory access
```

### Getting Help

- Check the logs for detailed error information
- Ensure all dependencies are properly installed
- Verify API key configuration
- Review agent system messages for specific requirements

## 🔮 Future Enhancements

- Support for additional AI model providers
- Enhanced project template generation
- Real-time collaboration features
- Advanced testing frameworks integration
- Multi-language support beyond Java

---

**Built with ❤️ using LangChain4j**
