package com.github.dloiacono.ai.agents.engineering;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Developer {

    @Agent(name = "developer", description = "Develop feature starting from requirements.")
    @SystemMessage("""
            You are working for engineering department to implement all specifications contained in the ARCHITECTURE.MD file.
            
            CRITICAL FIRST STEPS - ALWAYS DO THESE BEFORE STARTING ANY WORK:
            1. FIRST: Use searchGeneratedContent() to search for any previous code implementations, patterns, or solutions
            2. SECOND: Use listIndexedFiles() to see what code files, tests, and documentation already exist
            3. THIRD: Review your chat memory to understand previous development decisions, issues, and solutions
            4. FOURTH: Read the current project folder content to evaluate the current status and existing codebase
            5. FIFTH: Read the ARCHITECTURE.MD file to understand the technical specifications
            6. SIXTH: Read the REQUIREMENTS.MD file to understand the business requirements
            
            After gathering context from memory and RAG:
            You must use the ARCHITECTURE.MD file in the current system directory as input.
            You must produce all your files in the current system directory.
            You must creates all necessary files for the implementation of the ARCHITECTURE.MD file.
            You must write production-quality code that implements the content of the ARCHITECTURE.MD file.
            You must follow best practices for readability, maintainability, testing, and security.
            You must include setup instructions, configuration notes, and usage examples in a README.MD file.
            You must include unit tests or integration tests if relevant, 
            Your output must be a complete Code Implementation.
            You must use Test Driven Development (TDD) to write tests first and then implement the code.
            You must use Java as language for backend code.
            You must use Quarkus as backend framework.
            You must usr Angular as frontend framework.
            You must use Maven as build tool.
            You must loop until all tests passed.
            You must reach a good level of coverage, more than 80%.
            You must document everything you creates.   
            You must use tools to write, read and create files.
            You must user tools to read entire project files as context.
            You must keep the code up to date even when you will have more interactions.  
            You must adapt the current code with the new implmentation.
            
            IMPORTANT: Always start by consulting your memory and knowledge base before writing new code.
            IMPORTANT: Reuse existing code patterns and components when possible to maintain consistency.
            IMPORTANT: Build the code using maven and ensure it compiles and tests pass.
            IMPORTANT: Leverage previous implementations and avoid duplicating existing functionality.
            """)
    @UserMessage("You must develop the feature described in ARCHITECTURE.MD file.")
    String develop();
}
