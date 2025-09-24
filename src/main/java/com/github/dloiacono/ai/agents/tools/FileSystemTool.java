package com.github.dloiacono.ai.agents.tools;

import com.github.dloiacono.ai.agents.rag.SimpleRAGStore;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSystemTool {

    private final static String  BASE_DIR = "./generated-project";
    private final Path baseDir;
    private final List<Pattern> gitignorePatterns;

    public FileSystemTool() {
        this.baseDir = Paths.get(BASE_DIR).toAbsolutePath().normalize();
        try {
            Files.createDirectories(baseDir);
        }  catch (IOException e) {
            throw new RuntimeException("Failed to create base directory: " + baseDir, e);
        }
        // Load gitignore patterns once during construction
        this.gitignorePatterns = loadGitignorePatterns();
    }

    // Helper method to load gitignore patterns
    private List<Pattern> loadGitignorePatterns() {
        List<Pattern> patterns = new ArrayList<>();
        Path gitignoreFile = baseDir.resolve(".gitignore");
        
        if (Files.exists(gitignoreFile)) {
            try {
                List<String> lines = Files.readAllLines(gitignoreFile);
                for (String line : lines) {
                    line = line.trim();
                    // Skip empty lines and comments
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        patterns.add(gitignorePatternToRegex(line));
                    }
                }
            } catch (IOException e) {
                // If we can't read .gitignore, continue without filtering
            }
        }
        
        return patterns;
    }

    // Convert gitignore pattern to regex pattern
    private Pattern gitignorePatternToRegex(String gitignorePattern) {
        StringBuilder regex = new StringBuilder();
        
        // Handle directory patterns (ending with /)
        boolean isDirectory = gitignorePattern.endsWith("/");
        if (isDirectory) {
            gitignorePattern = gitignorePattern.substring(0, gitignorePattern.length() - 1);
        }
        
        // Handle patterns starting with /
        boolean isAbsolute = gitignorePattern.startsWith("/");
        if (isAbsolute) {
            gitignorePattern = gitignorePattern.substring(1);
            regex.append("^");
        }
        
        // Convert gitignore wildcards to regex
        for (int i = 0; i < gitignorePattern.length(); i++) {
            char c = gitignorePattern.charAt(i);
            switch (c) {
                case '*':
                    if (i + 1 < gitignorePattern.length() && gitignorePattern.charAt(i + 1) == '*') {
                        // ** matches any number of directories
                        regex.append(".*");
                        i++; // skip next *
                    } else {
                        // * matches anything except /
                        regex.append("[^/]*");
                    }
                    break;
                case '?':
                    regex.append("[^/]");
                    break;
                case '.':
                case '(':
                case ')':
                case '+':
                case '{':
                case '}':
                case '^':
                case '$':
                case '|':
                case '\\':
                    regex.append("\\").append(c);
                    break;
                default:
                    regex.append(c);
            }
        }
        
        if (isDirectory) {
            regex.append("(/.*)?$");
        } else if (!isAbsolute) {
            regex.insert(0, "(^|.*/)");
            regex.append("(/.*)?$");
        } else {
            regex.append("(/.*)?$");
        }
        
        return Pattern.compile(regex.toString());
    }

    // Check if a path should be ignored based on gitignore patterns
    private boolean shouldIgnore(String relativePath, List<Pattern> gitignorePatterns) {
        for (Pattern pattern : gitignorePatterns) {
            if (pattern.matcher(relativePath).matches()) {
                return true;
            }
        }
        return false;
    }

    // Helper to resolve relative paths safely
    private Path resolve(String relativePath) throws IOException {
        Path resolved = baseDir.resolve(relativePath).normalize();
        if (!resolved.startsWith(baseDir)) {
            throw new IOException("Access outside base directory is not allowed: " + relativePath);
        }
        return resolved;
    }
    
    // Helper method to determine the current agent ID for indexing purposes
    private String getCurrentAgentId() {
        // Try to determine agent ID from thread name or other context
        String threadName = Thread.currentThread().getName();
        if (threadName.toLowerCase().contains("analyst")) {
            return "analyst";
        } else if (threadName.toLowerCase().contains("architect")) {
            return "architect";
        } else if (threadName.toLowerCase().contains("developer")) {
            return "developer";
        }
        // Default to "unknown" if we can't determine the agent
        return "unknown";
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
            if (null == content || content.isEmpty())
                return  "Content is empty. No write operation performed.";
            Path path = resolve(relativePath);
            Files.createDirectories(path.getParent());
            Files.writeString(path, content,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            
            // Automatically index the file for RAG functionality
            try {
                SimpleRAGStore.indexFile(path, getCurrentAgentId());
            } catch (Exception e) {
                // Log but don't fail the write operation if indexing fails
                System.err.println("Warning: Failed to index file " + relativePath + " for RAG: " + e.getMessage());
            }
            
            return "File written successfully: " + relativePath;
        } catch (IOException e) {
            return "Error writing file: " + e.getMessage();
        }
    }

    @Tool("Appends content to a file (path is relative to the current folder)")
    public String appendToFile(@P("The relative path to the file to append to") String relativePath, 
                              @P("The content to append to the file") String content) {
        try {
            Path path = resolve(relativePath);
            Files.writeString(path, content,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
            
            // Automatically index the file for RAG functionality
            try {
                SimpleRAGStore.indexFile(path, getCurrentAgentId());
            } catch (Exception e) {
                // Log but don't fail the append operation if indexing fails
                System.err.println("Warning: Failed to index file " + relativePath + " for RAG: " + e.getMessage());
            }
            
            return "Content appended successfully: " + relativePath;
        } catch (IOException e) {
            return "Error appending to file: " + e.getMessage();
        }
    }

    @Tool("Lists the entire content of the project folder as a tree structure with file contents, respecting .gitignore patterns")
    public String listProjectFiles() {
        try (Stream<Path> paths = Files.walk(baseDir)) {
            StringBuilder result = new StringBuilder();
            
            paths
                .filter(path -> !path.equals(baseDir)) // skip base directory itself
                .filter(path -> {
                    String relativePath = baseDir.relativize(path).toString();
                    return !shouldIgnore(relativePath, gitignorePatterns);
                })
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
            
            // Return a meaningful message if the folder is empty or all files are ignored
            return result.length() == 0 ? "The project folder is empty or all files are ignored by .gitignore." : result.toString();
        } catch (IOException e) {
            return "Error listing project files: " + e.getMessage();
        }
    }
}
