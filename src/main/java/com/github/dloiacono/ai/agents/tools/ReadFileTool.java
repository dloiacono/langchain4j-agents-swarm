package com.github.dloiacono.ai.agents.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tool for reading files from the filesystem.
 */
public class ReadFileTool {

  /**
   * Reads the content of a file at the specified path.
   *
   * @param filePath the path to the file to read
   * @return the content of the file as a string
   */
  @Tool("Reads a file from the filesystem and returns its content as a string")
  public String readFile(
      @P("MANDATORY filePath (string) - the RELATIVE full file path") String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return "Error: File not found at path: " + filePath;
            }
            if (!Files.isReadable(path)) {
                return "Error: File is not readable: " + filePath;
            }
            return Files.readString(path);
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    /**
     * Checks if a file exists at the specified path.
     *
     * @param filePath the path to the file to check
     * @return "true" if the file exists, "false" if it doesn't exist
     */
    @Tool("Checks if a file exists at the specified path without reading its content")
    public String fileExists(
        @P("MANDATORY filePath (string) - the RELATIVE full file path to check") String filePath) {
        try {
            if (filePath == null || filePath.trim().isEmpty()) {
                return "false";
            }
            Path path = Paths.get(filePath);
            return Files.exists(path) ? "true" : "false";
        } catch (Exception e) {
            return "false";
        }
    }
}
