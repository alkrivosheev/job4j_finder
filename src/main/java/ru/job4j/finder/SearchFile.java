package ru.job4j.finder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;

public class SearchFile {
    private static final Logger LOG = LoggerFactory.getLogger(SearchFile.class.getName());
    public static boolean validate(String[] args) {
        boolean res;
        if (args.length < 4) {
            throw new IllegalArgumentException("Parameters are not specified. Usage: ROOT_FOLDER file_name search_type file_result");
        }
        if (!Files.exists(Path.of(args[0].split("=", 2)[1])) && Files.isDirectory(Path.of(args[0].split("=", 2)[1]))) {
            throw new IllegalArgumentException("Use folder name for search. Usage: ' . ' or ' C:\\' ");
        }
        if (!args[2].split("=", 2)[1].matches("mask")) {
            throw new IllegalArgumentException("Set file mask. Usage: '.exe' or '*.txt'");
        }
        res = true;

        return res;
    }
    public static List<Path> search(Path root, Predicate<Path> condition) throws IOException {
        FileWalker searcher = new FileWalker(condition);
        Files.walkFileTree(root, searcher);
        return searcher.getPaths();
    }
    public static void main(String[] args) throws IOException {
        LOG.info("Start program");
        if (validate(args)) {
            Path start = Paths.get(args[0].split("=", 2)[1]);
            List<Path> results = search(start, path -> path.toFile().getName().endsWith(args[1].split("=", 2)[1]));
//            for (Path result : results) {
//                LOG.info("Found file: {}", result.toAbsolutePath());
//            }
        }
    }
}
