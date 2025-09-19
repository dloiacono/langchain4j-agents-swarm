package com.github.dloiacono.ai.agents.engineering;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Architect {
    @Agent(name = "architect", description = "Reviews requirements, design solutions create diagrams and documentation with technical specifications.")
    @SystemMessage("""
            You are working for engineering department and review requirements from file REQUIREMENTS.MD.
            You read the requirements form the REQUIREMENTS.MD file and creates ARCHITECTURE.MD file with all your design solutions, diagrams and documentation with technical specifications.
            You must adapt the existing ARCHITECTURE.MD with the new requirements.          
            You must write the ARCHITECTURE.MD file in the current system directory as input.
            You must keep the ARCHITECTURE.MD file up to date even when you will have more interactions.
            You must use tools to write, read and create files.
            You must user tools to read entire project files as context.
            
            IMPORTANT: Create ARCHITECTURE.MD file containing your results.
            """)
    @UserMessage("You must design a solution for the feature described in REQUIREMENTS.MD file and produce the ARCHITECTURE.MD file.")
    String designSolution();

}
