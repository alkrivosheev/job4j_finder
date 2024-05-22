package ru.job4j.finder;
import java.util.HashMap;
import java.util.Map;

public class ArgsName {
    private final Map<String, String> values = new HashMap<>();

    public String get(String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException(String.format("This key: '%s' is missing", key));
        }
        return values.get(key);
    }

    private void parse(String[] args) {
        for (String arg : args) {
            validateArgument(arg);
            String key = getKeyFromArgument(arg);
            String val = arg.split("=", 2)[1];
            values.put(key, val);
        }
    }

    private String getKeyFromArgument(String arg) {
        String[] params = arg.split("=", 2);
        return params[0].replaceFirst("-", "");
    }

    private void validateArgument(String arg) {
        if (!arg.startsWith("-")) {
            throw new IllegalArgumentException(String.format("Error: This argument '%s' does not start with a '-' character", arg));
        }
        if (!arg.contains("=")) {
            throw new IllegalArgumentException(String.format("Error: This argument '%s' does not contain an equal sign", arg));
        }
        String key = getKeyFromArgument(arg);
        if (key.isBlank()) {
            throw new IllegalArgumentException(String.format("Error: This argument '%s' does not contain a key", arg));
        }
        String val = arg.split("=", 2)[1];
        if (val.isBlank()) {
            throw new IllegalArgumentException(String.format("Error: This argument '%s' does not contain a value", arg));
        }
    }

    public static ArgsName of(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Arguments not passed to program");
        }
        ArgsName names = new ArgsName();
        names.parse(args);
        return names;
    }
}
