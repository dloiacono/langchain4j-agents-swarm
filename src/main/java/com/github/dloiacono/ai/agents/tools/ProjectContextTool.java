package com.github.dloiacono.ai.agents.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Tool for analyzing project folders and providing context about their structure and content.
 */
public class ProjectContextTool {

    /**
     * Reads the entire content of the current directory and all its subdirectories.
     * This method recursively walks through all files in the current working directory
     * and returns their content as a single string.
     *
     * @return the content of all files in the current directory as a string
     */
    @Tool("Reads the entire content of the current directory and all its subdirectories")
    public String readCurrentDirectoryContent() {
        try {
            String currentPath = System.getProperty("user.dir");
            Path path = Paths.get(currentPath);
            
            if (!Files.exists(path) || !Files.isDirectory(path)) {
                return "Error: Current directory is invalid or not accessible: " + currentPath;
            }

            StringBuilder content = new StringBuilder();
            content.append("=== CURRENT DIRECTORY CONTENT ===\n");
            content.append("Directory Path: ").append(currentPath).append("\n\n");

            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relativePath = path.relativize(file);
                    content.append("=== FILE: ").append(relativePath).append(" ===\n");
                    content.append("Size: ").append(attrs.size()).append(" bytes\n");
                    content.append("Last Modified: ").append(attrs.lastModifiedTime()).append("\n");
                    content.append("--- CONTENT ---\n");
                    
                    try {
                        String fileContent = Files.readString(file);
                        content.append(fileContent);
                        if (!fileContent.endsWith("\n")) {
                            content.append("\n");
                        }
                    } catch (IOException e) {
                        content.append("Error reading file: ").append(e.getMessage()).append("\n");
                    } catch (OutOfMemoryError e) {
                        content.append("File too large to read into memory\n");
                    }
                    
                    content.append("\n--- END OF FILE ---\n\n");
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    // Skip hidden directories and common build/cache directories to avoid noise
                    String dirName = dir.getFileName().toString();
                    if (dirName.startsWith(".") && !dirName.equals(".") && !dirName.equals("..")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    if (dirName.equals("target") || dirName.equals("build") || dirName.equals("node_modules") || 
                        dirName.equals("__pycache__") || dirName.equals("dist") || dirName.equals("out")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            return content.toString();

        } catch (IOException e) {
            return "Error reading current directory content: " + e.getMessage();
        }
    }

    /**
     * Reads a specific file from the current directory.
     * The file path should be relative to the current working directory.
     *
     * @param filePath the relative path to the file from the current directory
     * @return the content of the specified file as a string
     */
    @Tool("Reads a specific file from the current directory")
    public String readSpecificFile(@P("The relative path to the file from the current directory") String filePath) {
        try {
            if (filePath == null || filePath.trim().isEmpty()) {
                return "Error: File path cannot be null or empty";
            }

            String currentPath = System.getProperty("user.dir");
            Path fullPath = Paths.get(currentPath, filePath);
            
            if (!Files.exists(fullPath)) {
                return "Error: File not found: " + filePath;
            }
            
            if (!Files.isReadable(fullPath)) {
                return "Error: File is not readable: " + filePath;
            }
            
            if (Files.isDirectory(fullPath)) {
                return "Error: Path points to a directory, not a file: " + filePath;
            }

            StringBuilder content = new StringBuilder();
            content.append("=== FILE: ").append(filePath).append(" ===\n");
            
            BasicFileAttributes attrs = Files.readAttributes(fullPath, BasicFileAttributes.class);
            content.append("Size: ").append(attrs.size()).append(" bytes\n");
            content.append("Last Modified: ").append(attrs.lastModifiedTime()).append("\n");
            content.append("--- CONTENT ---\n");
            
            try {
                String fileContent = Files.readString(fullPath);
                content.append(fileContent);
                if (!fileContent.endsWith("\n")) {
                    content.append("\n");
                }
            } catch (IOException e) {
                content.append("Error reading file content: ").append(e.getMessage()).append("\n");
            } catch (OutOfMemoryError e) {
                content.append("File too large to read into memory\n");
            }
            
            content.append("--- END OF FILE ---\n");
            return content.toString();

        } catch (IOException e) {
            return "Error reading file '" + filePath + "': " + e.getMessage();
        }
    }
}
