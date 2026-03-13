import java.nio.file.*;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Main {
    public static void main(String[] args){
        Path dirPath = Paths.get("C:\\Users\\ThinkPad\\Desktop\\ZPO\\spraw1_ZPO");
        BlockingQueue<Optional<Path>> queue = new LinkedBlockingQueue<>(3);
        Runnable producent = () -> {
            try {
                Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>(){
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                        if (path.toString().endsWith(".txt")) {
                            try {
                                queue.put(Optional.of(path));
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
                queue.put(Optional.empty());
                queue.put(Optional.empty());
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        };
        Runnable konsument = () -> {
            try {
                while(true){
                    Optional<Path> item = queue.take();
                    if(!item.isPresent()){
                        break;
                    }
                    Path takenPath = item.get();
                    Map<String, Long> result = getLinkedCountedWords(takenPath, 10);
                    System.out.println(takenPath);
                    System.out.println();
                    System.out.println(result);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.execute(producent);
        executor.execute(konsument);
        executor.execute(konsument);
        executor.shutdown();
    }
    public static Map<String, Long> getLinkedCountedWords(Path path, int wordsLimit) {
        try (Stream<String> lines = Files.lines(path)) {
        Map<String, Long> countedWords = lines
                .flatMap(line -> Arrays.stream(line.split("\\s+")))
                .map(word -> word.replaceAll("[^a-zA-Z0-9ąęóśćżńźĄĘÓŚĆŻŃŹ]", ""))
                .map(word -> word.toLowerCase())
                .filter(word -> word.length() >= 3)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Map<String, Long> sortedWords =
                countedWords.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(wordsLimit)
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                Map.Entry::getValue,
                                (a, b) -> a,
                                LinkedHashMap::new));
            return sortedWords;
    }
    catch(IOException e ){
        throw new RuntimeException(e);
        }
    }
}

