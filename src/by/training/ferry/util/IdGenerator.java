package by.training.ferry.util;

public class IdGenerator {
    public static long id = 0;

    public static long getId() {
        return id++;
    }
}
