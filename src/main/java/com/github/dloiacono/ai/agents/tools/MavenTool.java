package com.github.dloiacono.ai.agents.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class MavenTool {

    private final static String BASE_DIR = "./generated-project";
    private final Path baseDir;

    public MavenTool() {
        this.baseDir = Paths.get(BASE_DIR).toAbsolutePath().normalize();
    }

    @Tool("Executes Maven commands in the project directory. Use this to run any Maven goal or command with parameters.")
    public String executeMaven(@P("The Maven command and parameters to execute (e.g., 'clean compile', 'test', 'package -DskipTests')") String mavenCommand) {
        try {
            // Build the full command
            String[] commandParts = ("mvn " + mavenCommand).split("\\s+");
            
            ProcessBuilder processBuilder = new ProcessBuilder(commandParts);
            processBuilder.directory(baseDir.toFile());
            processBuilder.redirectErrorStream(true); // Merge stderr with stdout
            
            Process process = processBuilder.start();
            
            // Read the output
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            // Wait for the process to complete with a timeout
            boolean finished = process.waitFor(5, TimeUnit.MINUTES);
            
            if (!finished) {
                process.destroyForcibly();
                return "Maven command timed out after 5 minutes: " + mavenCommand;
            }
            
            int exitCode = process.exitValue();
            String result = output.toString();
            
            if (exitCode == 0) {
                return "Maven command executed successfully:\n" + result;
            } else {
                return "Maven command failed with exit code " + exitCode + ":\n" + result;
            }
            
        } catch (IOException e) {
            return "Error executing Maven command: " + e.getMessage() + 
                   "\nMake sure Maven is installed and available in PATH.";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Maven command was interrupted: " + mavenCommand;
        } catch (Exception e) {
            return "Unexpected error executing Maven command: " + e.getMessage();
        }
    }
}
