package ru.job4j.finder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;

public class SearchFile {
    private static final Logger LOG = LoggerFactory.getLogger(SearchFile.class.getName());
    private static final Map<String, String> values = new HashMap<String, String>();
    public static boolean validate(String[] args) {
        boolean res;
        if (args.length < 4) {
            throw new IllegalArgumentException("Parameters are not specified. Usage: ROOT_FOLDER file_name search_type file_result");
        }
        for (String parameter : args) {
            String[] words = parameter.split("=", 2);
            words[0] = words[0].replace("-", "");
            if ("d".equals(words[0]) && !Files.exists(Path.of(words[1])) && Files.isDirectory(Path.of(words[1]))) {
                throw new IllegalArgumentException("Use folder name for search. Usage: ' . ' or ' C:\\' ");
            }
//            if ("n".equals(words[0]) && !words[1].matches("^[a-zA-Z0-9_\\.\\-\\*\\?]+$")) {
//                throw new IllegalArgumentException("Set file mask. Usage: '.exe' or '*.txt'");
//            }
            values.put(words[0], words[1]);
        }
        res = true;

        return res;
    }

    public static String value(String key) {
        return values.get(key);
    }

//    public static List<Path> search(Path root, String pattern, String mode) throws IOException {
public static List<Path> search(Path root, Predicate<Path> condition) throws IOException {
//        FileWalker searcher = new FileWalker(pattern, mode);
        FileWalker searcher = new FileWalker(condition);
        Files.walkFileTree(root, searcher);
        return searcher.getPaths();
    }
    public static void main(String[] args) throws IOException {
        if (validate(args)) {
            LOG.info("Start program");
            Path start = Paths.get(value("d"));
//            List<Path> res = search(start, value("n"), value("t"));
//            List<Path> res = search(start, path -> path.toFile().getName().endsWith(value("n")));
            String syntax = "";
            switch (value("t")) {
            case "mask", "name" -> syntax = "glob:";
            case "regex" -> syntax = "regex:";
        }
            PathMatcher matcher =  FileSystems.getDefault().getPathMatcher(syntax + value("n"));
            List<Path> res = search(start, path -> matcher.matches(path));
            for (Path path : res) {
                System.out.println(path);
                LOG.info("Found file: {}", path);
            }
            LOG.info("Stop program");
        }
    }
}
