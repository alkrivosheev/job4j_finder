package ru.job4j.finder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.nio.file.FileVisitResult.CONTINUE;

public class FileWalker implements FileVisitor<Path> {
    private static final Logger LOG = LoggerFactory.getLogger(SearchFile.class.getName());
    private Predicate<Path> condition;
    private List<Path> paths;
    public FileWalker(Predicate<Path
            > condition) {
        this.condition = condition;
        paths = new ArrayList<>();
    }
    @Override
    public FileVisitResult preVisitDirectory(Path directory,
            BasicFileAttributes attributes) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file,
            BasicFileAttributes attributes) throws IOException {
        System.out.println(file.toAbsolutePath());
        LOG.info("Found file: {}", file.toAbsolutePath());
        if (this.condition.test(file)) {
            paths.add(file);
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
            IOException exception) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path directory,
            IOException exception) throws IOException {
        return CONTINUE;
    }
    public List<Path> getPaths() {
        return paths;
    }
}
