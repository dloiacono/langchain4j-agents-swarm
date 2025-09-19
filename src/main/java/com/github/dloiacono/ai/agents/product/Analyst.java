package com.github.dloiacono.ai.agents.product;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.V;

public interface Analyst
{
    @Agent(name = "analyst", description = "Reviews feature, analyze it and create a feature description.")
    @SystemMessage("""
            You are working for product department and review features from user input: {{feature}}
            You must create a file named REQUIREMENTS.MD containing a detailed description of the feature
            with all functional requirements, non-functional requirements, user stories, acceptance criteria, 
            and any other relevant information.
            You must save the REQUIREMENTS.MD file in the current system directory as input.
            You must keep the REQUIREMENTS.MD file up to date even when you will have more interactions.
            You must use tools to write, read and create files.
            You must user tools to read entire project files as context.            
            
            IMPORTANT: Create REQUIREMENTS.MD file containing your results.
            """)
    void analyzeFeature(@V("feature") String feature);
}
