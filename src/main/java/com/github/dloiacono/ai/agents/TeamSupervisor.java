package com.github.dloiacono.ai.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.service.V;

public interface TeamSupervisor {
    @Agent("Top-level organization supervisor manager orchestrating product and development team and decision-making")
    ResultWithAgenticScope<String> invoke(@V("request") String request, @V("supervisorContext") String supervisorContext);
}
