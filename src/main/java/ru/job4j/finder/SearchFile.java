package ru.job4j.finder;

import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SearchFile {
    private static final Logger LOG = LoggerFactory.getLogger(SearchFile.class.getName());
    private static final String DIRECTORY_KEY = "d";
    private static final String NAME_KEY = "n";
    private static final String TYPE_KEY = "t";
    private static final String OUTPUT_KEY = "o";

    public static List<Path> get(String[] args) throws IOException {
        ArgsName argsName = ArgsName.of(args);
        validate(argsName);

        Path start = Paths.get(argsName.get(DIRECTORY_KEY));
        Predicate<Path> predicate = createPredicate(argsName.get(NAME_KEY), argsName.get(TYPE_KEY));
        return search(start, predicate);
    }

    private static void validate(ArgsName argsName) {
        Path directory = Paths.get(argsName.get(DIRECTORY_KEY));
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            throw new IllegalArgumentException(String.format("Error: This Directory '%s' not exists. Use folder name for search. Usage: ' . ' or ' C:\\' ", directory));
        }

        Path output = Paths.get(argsName.get(OUTPUT_KEY));
        if (!Files.exists(output.toAbsolutePath().getParent())) {
            throw new IllegalArgumentException(String.format("Error: This Directory '%s' not exists. Use folder name for Log directory. Usage: ' logs\\ ' or ' C:\\' ", output));
        }

        if ("regex".equals(argsName.get(TYPE_KEY))) {
            try {
                Pattern.compile(argsName.get(NAME_KEY));
            } catch (PatternSyntaxException exception) {
                throw new PatternSyntaxException(String.format("Error: This regex '%s' does not valid", argsName.get(NAME_KEY)), argsName.get(NAME_KEY), -1);
            }
        }
    }

    private static List<Path> search(Path root, Predicate<Path> condition) throws IOException {
        FileWalker searcher = new FileWalker(condition);
        Files.walkFileTree(root, searcher);
        return searcher.getPaths();
    }

    private static Predicate<Path> createPredicate(String pattern, String typeSearch) {
        String prefix = "mask".equals(typeSearch) ? "glob:" : "regex:";
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(prefix + pattern);
        return p -> matcher.matches(p.getFileName());
    }

    private static void setLogProperties(String fileName) {
        FileAppender fa = new FileAppender();
        fa.setName("FileLogger");
        if (fileName != null) {
            fa.setFile(fileName);
        }
        fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
        fa.setAppend(true);
        fa.activateOptions();
        org.apache.log4j.Logger.getRootLogger().addAppender(fa);
    }

    public static void main(String[] args) throws IOException {
        ArgsName argsName = ArgsName.of(args);
        validate(argsName);

        List<Path> paths = get(args);
        setLogProperties(argsName.get(OUTPUT_KEY));

        LOG.info("Start program");
        for (Path path : paths) {
            System.out.println(path);
            LOG.info("Found file: {}", path);
        }
        LOG.info("Stop program");
    }
}