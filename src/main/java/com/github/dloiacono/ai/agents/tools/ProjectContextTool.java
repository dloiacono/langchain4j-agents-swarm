package com.github.dloiacono.ai.agents.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Tool for analyzing project folders and providing context about their structure and content.
 */
public class ProjectContextTool {



    /**
     * Read entire project files
     *
     * @param projectPath the path to the project folder
     * @return the content of all files in the project
     */
    @Tool("Read entire project files")
    public String readAllProjectFiles(
            @P("MANDATORY projectPath (string) - the RELATIVE full project path") String projectPath) {
        try {
            Path path = Paths.get(projectPath);
            if (!Files.exists(path) || !Files.isDirectory(path)) {
                return "Error: Invalid project path or not a directory: " + projectPath;
            }

            StringBuilder content = new StringBuilder();
            content.append("=== ALL PROJECT FILES CONTENT ===\n");
            content.append("Project Path: ").append(projectPath).append("\n\n");

            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relativePath = path.relativize(file);
                    content.append("=== FILE: ").append(relativePath).append(" ===\n");
                    content.append("Size: ").append(attrs.size()).append(" bytes\n");
                    content.append("Last Modified: ").append(attrs.lastModifiedTime()).append("\n");
                    content.append("--- CONTENT ---\n");
                    
                    try {
                        // Read file content without any size limitations
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
            return "Error reading all project files: " + e.getMessage();
        }
    }

    /**
     * Gets the content of specific files in the project.
     *
     * @param projectPath the path to the project folder
     * @param filePattern glob pattern to match files (e.g., "**//*.java", "pom.xml", "package.json")
     * @return the content of matching files
     */
    @Tool("Gets the content of specific files in a project based on a glob pattern")
    public String getProjectFiles(String projectPath, String filePattern) {
        try {
            Path path = Paths.get(projectPath);
            if (!Files.exists(path) || !Files.isDirectory(path)) {
                return "Error: Invalid project path or not a directory: " + projectPath;
            }

            StringBuilder content = new StringBuilder();
            content.append("=== FILES MATCHING PATTERN: ").append(filePattern).append(" ===\n\n");

            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + filePattern);
            
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relativePath = path.relativize(file);
                    if (matcher.matches(relativePath) || matcher.matches(file.getFileName())) {
                        content.append("--- ").append(relativePath).append(" ---\n");
                        try {
                            String fileContent = Files.readString(file);
                            content.append(fileContent).append("\n\n");
                        } catch (IOException e) {
                            content.append("Error reading file: ").append(e.getMessage()).append("\n\n");
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            return content.toString();

        } catch (IOException e) {
            return "Error getting project files: " + e.getMessage();
        }
    }




}
