package com.github.dloiacono.ai.agents.tools;

import dev.langchain4j.agent.tool.Tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Tool for writing content to files in the filesystem.
 */
public class WriteFileTool {

    /**
     * Writes the provided content to a file at the specified path.
     * Creates the file if it doesn't exist, or overwrites it if it does.
     *
     * @param content the content to write to the file
     * @param filePath the path to the file to write
     * @return a message indicating success or failure
     */
    @Tool("""
    Writes files in the filesystem. 
    
    REQUIRED:
    - content
    - filePath
    
    CRITICAL: You MUST provide BOTH parameters in the tool call JSON:
    {
      "content": "your complete file content here",
      "filePath": "relative/path/to/file.ext"
    }
    
    The content parameter must contain the complete file content you want to write.
    The filePath parameter must be a relative path like "research/report.md" or "src/main/java/Example.java".
    
    Example tool call:
    {
      "content": "# Research Report\\n\\n## Summary\\nThis is my research...",
      "filePath": "research/my-report.md"
    }
    """)
    public String writeFile(String content, String filePath) {
        try {
            // Silent handling of null parameters - do nothing if either is null
             if (filePath == null || content == null) {
                return "File operation completed";
            }
            
            // Silent handling of empty parameters - do nothing if either is empty
            if (filePath.trim().isEmpty() || content.trim().isEmpty()) {
                return "File operation completed";
            }
            
            // Convert to relative path if absolute path is provided
            String relativePath = convertToRelativePath(filePath);
            Path path = Paths.get(relativePath);
            
            // Create parent directories if they don't exist
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            
            // Write the content to the file
            Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return "Successfully wrote " + content.length() + " characters to file: " + relativePath;
        } catch (IOException e) {
            return "Error writing to file: " + e.getMessage();
        }
    }

    /**
     * Appends the provided content to a file at the specified path.
     * Creates the file if it doesn't exist.
     *
     * @param content the content to append to the file
     * @param filePath the path to the file to append to
     * @return a message indicating success or failure
     */
    @Tool("""
    Appends content to a file in the filesystem, creating the file if it doesn't exist.
    
    CRITICAL: BOTH parameters are MANDATORY and must never be null!
    - First parameter: content (the ACTUAL content to append)
    - Second parameter: filePath (the file path)
    
    Example: appendToFile("Some text to append\n", "src/main/java/Example.java")
    """)
    public String appendToFile(String content, String filePath) {
        try {
            // Enhanced validation with detailed error messages
            if (filePath == null) {
                return "CRITICAL ERROR: filePath parameter is NULL. You MUST provide a file path as the SECOND parameter. Correct usage: appendToFile(content, filePath)";
            }
            if (filePath.trim().isEmpty()) {
                return "CRITICAL ERROR: filePath parameter is EMPTY. You MUST provide a valid file path as the SECOND parameter. Example: appendToFile(content, \"src/main/java/Example.java\")";
            }
            if (content == null) {
                return "CRITICAL ERROR: content parameter is NULL. You MUST provide the actual content to append as the FIRST parameter. Correct usage: appendToFile(\"content to append\", filePath)";
            }
            
            // Log the call for debugging
            System.out.println("WriteFileTool.appendToFile called with:");
            System.out.println("  - content length: " + content.length() + " characters");
            System.out.println("  - filePath: " + filePath);
            
            // Convert to relative path if absolute path is provided
            String relativePath = convertToRelativePath(filePath);
            Path path = Paths.get(relativePath);
            
            // Create parent directories if they don't exist
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            
            // Append the content to the file
            Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return "Successfully appended " + content.length() + " characters to file: " + relativePath;
        } catch (IOException e) {
            return "Error appending to file: " + e.getMessage();
        }
    }

    /**
     * Converts an absolute path to a relative path.
     *
     * @param filePath the original file path (absolute or relative)
     * @return a relative path
     */
    private String convertToRelativePath(String filePath) {
        if (filePath == null) {
            return null;
        }
        
        // If it's already a relative path, return as-is
        if (!filePath.startsWith("/") && !filePath.matches("^[A-Za-z]:\\\\.*")) {
            return filePath;
        }

        return System.getProperty("user.dir") + "/target/run-test/" + filePath;
    }

}
