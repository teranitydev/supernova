package supernova.functional;

public record Warning(String message) {

    public static Warning of(String message) {
        return new Warning(message);
    }
}
