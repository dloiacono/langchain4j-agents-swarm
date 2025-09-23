package com.github.dloiacono.ai.agents;

import com.github.dloiacono.ai.agents.engineering.Architect;
import com.github.dloiacono.ai.agents.engineering.Developer;
import com.github.dloiacono.ai.agents.product.Analyst;
import com.github.dloiacono.ai.agents.tools.FileSystemTool;
import com.github.dloiacono.ai.agents.tools.MavenTool;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.agentic.supervisor.SupervisorContextStrategy;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.model.chat.ChatModel;
import util.ChatModelProvider;
import util.log.CustomLogging;
import util.log.LogLevels;

import java.io.IOException;

public class AgentsTeam {

    static {
        CustomLogging.setLevel(LogLevels.PRETTY, 200);
    }

    private static final ChatModel CHAT_MODEL = ChatModelProvider
            .createDefaultChatModel();

    /**
     * In this example we build a similar supervisor as in _7a_Supervisor_Orchestration,
     * but we explore a number of extra features of the Supervisor:
     * - typed supervisor,
     * - context engineering,
     * - output strategies,
     * - call chain observation,
     * - context evolution inspection
     */
    public static void main(String[] args) throws IOException {


        // 1. Define subagents
        Analyst analyst = AgenticServices.agentBuilder(Analyst.class)
                .chatModel(CHAT_MODEL)
                .tools(new FileSystemTool())
                .build();
        Architect architect = AgenticServices.agentBuilder(Architect.class)
                .chatModel(CHAT_MODEL)
                .tools(new FileSystemTool())
                .build();
        Developer developer = AgenticServices.agentBuilder(Developer.class)
                .chatModel(CHAT_MODEL)
                .tools(new FileSystemTool(), new MavenTool())
                .build();

        // 2. Build supervisor

        TeamSupervisor teamSupervisor = AgenticServices
                .supervisorBuilder(TeamSupervisor.class)
                .chatModel(CHAT_MODEL)
                .subAgents(analyst, architect, developer)
                .contextGenerationStrategy(SupervisorContextStrategy.CHAT_MEMORY_AND_SUMMARIZATION)
                // depending on what your supervisor needs to know about what the sub-agents have been doing,
                // you can choose contextGenerationStrategy CHAT_MEMORY, SUMMARIZATION, or CHAT_MEMORY_AND_SUMMARIZATION
                .responseStrategy(SupervisorResponseStrategy.SCORED) // this strategy uses a scorer model to decide weather the LAST response or the SUMMARY solves the user request best
                // an output function here would override the response strategy
                .supervisorContext("Policy: Always analyze the request, then delegate to the appropriate sub-agent, iterate if needed.")
                .build();

        String request = "Create a REST API using Quarkus that perform simple calculations between two numbers";

        // 4. Invoke supervisor
        long start = System.nanoTime();
        ResultWithAgenticScope<String> decision = teamSupervisor.invoke(request, "Successful build and test of the solution is a must.");
        long end = System.nanoTime();

        System.out.println("=== Team Supervisor finished in " + ((end - start) / 1_000_000_000.0) + "s ===");
        System.out.println(decision.result());

        // Print collected contexts
        System.out.println("\n=== Context as Conversation ===");
        System.out.println(decision.agenticScope().contextAsConversation()); // will work in next release

    }
}
