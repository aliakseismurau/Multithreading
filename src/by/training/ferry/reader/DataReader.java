package by.training.ferry.reader;

import by.training.ferry.exception.ReaderException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataReader {
    private static final Logger logger = LogManager.getLogger();
    private static final String DEFAULT_FILENAME = "res/data.txt";

    public List<String> readData(String fileName) throws ReaderException {
        List<String> lines = new ArrayList<>();
        Path path;
        if(fileName != null) {
            path = Paths.get(fileName);
        } else {
            path = Paths.get(DEFAULT_FILENAME);
        }
        try(Stream<String> lineStream = Files.lines(path)) {
            lines = lineStream.collect(Collectors.toList());
        } catch (IOException e) {
            logger.error(Level.ERROR, e);
            throw new ReaderException(e);
        }
        return lines;
    }
}
