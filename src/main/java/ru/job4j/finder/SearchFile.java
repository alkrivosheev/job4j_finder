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
    private static final Map<String, String> MAP_VAL = new HashMap<>();

    public static boolean validate(String[] args) {
        boolean res;
        if (args.length < 4) {
            throw new IllegalArgumentException("Parameters are not specified. Usage: ROOT_FOLDER file_name search_type file_result");
        }
        for (String parameter : args) {
            if (!parameter.startsWith("-")) {
                throw new IllegalArgumentException(String.format("Error: This argument '%s' does not start with a '-' character", parameter));
            }
            if (!parameter.contains("=")) {
                throw new IllegalArgumentException(String.format("Error: This argument '%s' does not contain an equal sign", parameter));
            }
            String[] words = parameter.split("=", 2);
            words[0] = words[0].replace("-", "");
            if ("d".equals(words[0]) && (!Files.exists(Path.of(words[1])) || !Files.isDirectory(Path.of(words[1])))) {
                throw new IllegalArgumentException(String.format("Error: This Directory '%s' not exists. Use folder name for search. Usage: ' . ' or ' C:\\' ", words[1]));
            }
            if ("o".equals(words[0]) && !Files.exists(Path.of(words[1]).toAbsolutePath().getParent())) {
                throw new IllegalArgumentException(String.format("Error: This Directory '%s' not exists. Use folder name for Log directory. Usage: ' logs\\ ' or ' C:\\' ", words[1]));
            }
            MAP_VAL.put(words[0], words[1]);
        }
        if ("regex".equals(value("t"))) {
            String regex = value("n");
            try {
                Pattern.compile(regex);
            } catch (PatternSyntaxException exception) {
                throw new PatternSyntaxException(String.format("Error: This regex '%s' does not valid", regex), regex, -1);
            }
        }
        res = true;

        return res;
    }

    public static String value(String key) {
        return MAP_VAL.get(key);
    }

    public static List<Path> search(Path root, Predicate<Path> condition) throws IOException {
        FileWalker searcher = new FileWalker(condition);
        Files.walkFileTree(root, searcher);
        return searcher.getPaths();
    }

        public static List<Path> get(String[] args) throws IOException {
            List<Path> res = new ArrayList<>();
            if (validate(args)) {
                Path start = Paths.get(value("d"));
                Predicate<Path> predicate = createPredicate(value("t"), value("n"));
                res = search(start, predicate);
            }
            return res;
        }

    private static Predicate<Path> createPredicate(String type, String pattern) {
        PathMatcher matcher;
        switch (type) {
            case "name":
                return path -> path.toFile().getName().endsWith(pattern);
            case "mask":
                matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
                return path -> matcher.matches(path);
            case "regex":
                matcher = FileSystems.getDefault().getPathMatcher("regex:" + pattern);
                return path -> matcher.matches(path);
            default:
                throw new IllegalArgumentException(String.format("Parameter '%s' are not specified. ", type));
        }
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
        List<Path> paths = get(args);
        setLogProperties(value("o"));
        LOG.info("Start program");
        for (Path path : paths) {
            System.out.println(path);
            LOG.info("Found file: {}", path);
        }
        LOG.info("Stop program");
    }
}
