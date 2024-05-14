package ru.job4j.finder;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.nio.file.FileVisitResult.CONTINUE;

public class FileWalker implements FileVisitor<Path> {
    private final Predicate<Path> condition;
//    private final PathMatcher matcher;
    private final List<Path> paths;
//    public FileWalker(String pattern, String  mode) {
public FileWalker(Predicate<Path> condition) {
//        String syntax = "";
        paths = new ArrayList<>();

//        switch (mode) {
//            case "mask", "name" -> syntax = "glob:";
//            case "regex" -> syntax = "regex:";
//        }
//        matcher = FileSystems.getDefault().getPathMatcher(syntax + pattern);
        this.condition = condition;
    }
    @Override
    public FileVisitResult preVisitDirectory(Path directory,
            BasicFileAttributes attributes) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file,
            BasicFileAttributes attributes) throws IOException {

        if (this.condition.test(file.getFileName())) {
//        if (this.matcher.matches(file.getFileName())) {
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
