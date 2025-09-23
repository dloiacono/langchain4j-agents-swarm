package com.github.dloiacono.ai.agents.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSystemTool {

    private final static String  BASE_DIR = "./generated-project";
    private final Path baseDir;

    public FileSystemTool() {
        this.baseDir = Paths.get(BASE_DIR).toAbsolutePath().normalize();
        try {
            Files.createDirectories(baseDir);
        }  catch (IOException e) {
            throw new RuntimeException("Failed to create base directory: " + baseDir, e);
        }
    }

    // Helper to resolve relative paths safely
    private Path resolve(String relativePath) throws IOException {
        Path resolved = baseDir.resolve(relativePath).normalize();
        if (!resolved.startsWith(baseDir)) {
            throw new IOException("Access outside base directory is not allowed: " + relativePath);
        }
        return resolved;
    }

    @Tool("Reads the content of a file (path is relative to the current folder)")
    public String readFile(@P("The relative path to the file to read") String relativePath) {

        try {
            Path  filePath = resolve(relativePath);
            if (Files.size(filePath) == 0) {
                return "The file is empty";
            }
            return Files.readString(filePath);
        } catch (FileNotFoundException | NoSuchFileException e) {
            return "No file " + relativePath + " found";
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    @Tool("Writes content to a file (path is relative to the current folder). Overwrites if the file exists.")
    public String writeFile(@P("The relative path to the file to write") String relativePath, 
                           @P("The content to write to the file") String content) {
        try {
            if (null == content ) content = "";
            Path path = resolve(relativePath);
            Files.createDirectories(path.getParent());
            Files.writeString(path, content,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            return "File written successfully: " + relativePath;
        } catch (IOException e) {
            return "Error writing file: " + e.getMessage();
        }
    }

    @Tool("Appends content to a file (path is relative to the current folder)")
    public String appendToFile(@P("The relative path to the file to append to") String relativePath, 
                              @P("The content to append to the file") String content) {
        try {
            Files.writeString(resolve(relativePath), content,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
            return "Content appended successfully: " + relativePath;
        } catch (IOException e) {
            return "Error appending to file: " + e.getMessage();
        }
    }

    @Tool("Lists the entire content of the project folder as a tree structure with file contents")
    public String listProjectFiles() {
        try (Stream<Path> paths = Files.walk(baseDir)) {
            StringBuilder result = new StringBuilder();
            
            paths
                .filter(path -> !path.equals(baseDir)) // skip base directory itself
                .sorted()
                .forEach(path -> {
                    String relativePath = baseDir.relativize(path).toString();
                    result.append("=== ").append(relativePath).append(" ===\n");
                    
                    if (Files.isDirectory(path)) {
                        result.append("[DIRECTORY]\n");
                    } else {
                        try {
                            String content = Files.readString(path);
                            if (content.trim().isEmpty()) {
                                result.append("[EMPTY FILE]\n");
                            } else {
                                result.append(content).append("\n");
                            }
                        } catch (IOException e) {
                            result.append("[ERROR READING FILE: ").append(e.getMessage()).append("]\n");
                        }
                    }
                    result.append("\n");
                });
            
            // Return a meaningful message if the folder is empty
            return result.length() == 0 ? "The project folder is empty." : result.toString();
        } catch (IOException e) {
            return "Error listing project files: " + e.getMessage();
        }
    }
}
