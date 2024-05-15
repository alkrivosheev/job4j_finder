package ru.job4j.finder;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class SearchFileTest {
    @Test
    void whenGetRegexOnePath() throws IOException {
        List<Path> paths = SearchFile.get(new String[] {"-d=c:\\test", "-n=^[A-Za-z]{1,8}\\.[A-Za-z]{1,3}$", "-t=regex", "-o=logs\\log.txt"});
        assertThat(paths)
                .hasSize(1)
                .contains(Path.of("c:\\test\\Login.sql"));
    }
    @Test
    void whenGetRegex() throws IOException {
        List<Path> paths = SearchFile.get(new String[] {"-d=c:\\test", "-n=^[A-Za-z]{1,12}\\.[A-Za-z]{1,3}$", "-t=regex", "-o=log.txt"});
        assertThat(paths)
                .hasSize(3)
                .contains(Path.of("c:\\test\\Login.sql"));
    }
    @Test
    void whenGetNameTwoPaths() throws IOException {
        List<Path> paths = SearchFile.get(new String[]{"-d=c:\\test", "-n=Новый текстовый документ.txt", "-t=name", "-o=log.txt"});
        assertThat(paths)
                .hasSize(2)
                .contains(Path.of("c:\\test\\Новый текстовый документ.txt"))
                .contains(Path.of("c:\\test\\11v\\Новый текстовый документ.txt"));
    }

    @Test
    void whenGetMaskOnePath() throws IOException {
        List<Path> paths = SearchFile.get(new String[]{"-d=c:\\test", "-n=*75.s?l", "-t=mask", "-o=log.txt"});
        assertThat(paths)
                .hasSize(1)
                .contains(Path.of("c:\\test\\CustomersAdd75.sql"));
    }
    @Test
    void whenGetMaskPaths() throws IOException {
        List<Path> paths = SearchFile.get(new String[]{"-d=c:\\test", "-n=*.sql", "-t=mask", "-o=log.txt"});
        assertThat(paths)
                .hasSize(14);
    }

    @Test
    void whenNoPrefixThenExceptionThrown() {
        assertThatThrownBy(() -> SearchFile.get(new String[]{"-d=c:\\test", "n=*.sql", "-t=mask", "-o=log.txt"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Error: This argument 'n=*.sql' does not start with a '-' character");
    }

    @Test
    void whenNoEqualThenExceptionThrown() {
        assertThatThrownBy(() -> SearchFile.get(new String[]{"-d=c:\\test", "-n=*.sql", "-tmask", "-o=log.txt"}))
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
        assertThatThrownBy(() -> SearchFile.get(new String[]{"-d=c:\\test", "-n=^[A-Za-z]{1,12\\.[A-Za-z]{1,3}$", "-t=regex", "-o=log.txt"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Error: This regex '^[A-Za-z]{1,12\\.[A-Za-z]{1,3}$' does not valid");
    }

    @Test
    void whenGetLogFileError() throws IOException {
        assertThatThrownBy(() -> SearchFile.get(new String[]{"-d=c:\\test", "-n=^[A-Za-z]{1,12}\\.[A-Za-z]{1,3}$", "-t=regex", "-o=logs1/debug1.txt"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Error: This Directory 'logs1/debug1.txt' not exists. Use folder name for Log directory. Usage: ' logs\\ ' or ' C:\\' ");
    }
}