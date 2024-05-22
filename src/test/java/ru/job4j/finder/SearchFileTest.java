package ru.job4j.finder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class SearchFileTest {
    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    static Path tempDir;
    static Path file;
    @BeforeAll
    public static void initTmpFiles() throws IOException {
        Path subDir = Files.createDirectory(tempDir.resolve("sub"));
        Files.createTempFile(tempDir, "test1-", ".tmp");
        file = Files.createTempFile(subDir, "file-", ".sql");
    }

    @Test
    void whenGetRegexOnePath() throws IOException {
        String dirName = "-d=" + tempDir.toAbsolutePath();
        List<Path> paths = SearchFile.get(new String[] {dirName, "-n=^test[A-Za-z0-9\\-]{1,24}\\.[A-Za-z]{1,3}$", "-t=regex", "-o=logs\\log.txt"});
        assertThat(paths)
                .hasSize(1);
    }
    @Test
    void whenGetRegexAllPaths() throws IOException {
        String dirName = "-d=" + tempDir.toAbsolutePath();
        List<Path> paths = SearchFile.get(new String[] {dirName, "-n=^[А-ЯA-Za-z0-9\\-]{1,40}\\.[A-Za-z]{1,3}$", "-t=regex", "-o=log.txt"});
        assertThat(paths)
                .hasSize(2);
    }
    @Test
    void whenGetNameTwoPaths() throws IOException {
        String dirName = "-d=" + tempDir.toAbsolutePath();
        String filename = "-n=" + file.toFile().getName();
        List<Path> paths = SearchFile.get(new String[]{dirName, filename, "-t=name", "-o=log.txt"});
        assertThat(paths)
                .hasSize(1)
                .contains(file);
    }

    @Test
    void whenGetMaskOnePath() throws IOException {
        String dirName = "-d=" + tempDir.toAbsolutePath();
        List<Path> paths = SearchFile.get(new String[]{dirName, "-n=*.s?l", "-t=mask", "-o=log.txt"});
        assertThat(paths)
                .hasSize(1)
                .contains(file);
    }
    @Test
    void whenGetMaskPaths() throws IOException {
        String dirName = "-d=" + tempDir.toAbsolutePath();
        List<Path> paths = SearchFile.get(new String[]{dirName, "-n=*.*", "-t=mask", "-o=log.txt"});
        assertThat(paths)
                .hasSize(2);
    }

    @Test
    void whenNoPrefixThenExceptionThrown() {
        String dirName = "-d=" + tempDir.toAbsolutePath();
        assertThatThrownBy(() -> SearchFile.get(new String[]{dirName, "n=*.sql", "-t=mask", "-o=log.txt"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Error: This argument 'n=*.sql' does not start with a '-' character");
    }

    @Test
    void whenNoEqualThenExceptionThrown() {
        String dirName = "-d=" + tempDir.toAbsolutePath();
        assertThatThrownBy(() -> SearchFile.get(new String[]{dirName, "-n=*.sql", "-tmask", "-o=log.txt"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Error: This argument '-tmask' does not contain an equal sign");
    }

    @Test
    void whenDirectoryNotExistThrown() {
        assertThatThrownBy(() -> SearchFile.get(new String[]{"-d=g:\\test", "-n=*.sql", "-t=mask", "-o=log.txt"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Error: This Directory 'g:\\test' not exists. Use folder name for search. Usage: ' . ' or ' C:\\' ");
    }

    @Test
    void whenGetRegexError() throws IOException {
        String dirName = "-d=" + tempDir.toAbsolutePath();
        assertThatThrownBy(() -> SearchFile.get(new String[]{dirName, "-n=^[A-Za-z]{1,12\\.[A-Za-z]{1,3}$", "-t=regex", "-o=log.txt"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Error: This regex '^[A-Za-z]{1,12\\.[A-Za-z]{1,3}$' does not valid");
    }

    @Test
    void whenGetLogFileError() throws IOException {
        String dirName = "-d=" + tempDir.toAbsolutePath();
        assertThatThrownBy(() -> SearchFile.get(new String[]{dirName, "-n=^[A-Za-z]{1,12}\\.[A-Za-z]{1,3}$", "-t=regex", "-o=logs1/debug1.txt"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Error: This Directory 'logs1\\debug1.txt' not exists. Use folder name for Log directory. Usage: ' logs\\ ' or ' C:\\' ");
    }
}