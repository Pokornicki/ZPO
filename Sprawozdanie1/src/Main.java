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

        // Ścieżka do katalogu, w którym znajdują się pliki .txt
        Path dirPath = Paths.get("C:\\Users\\ThinkPad\\Desktop\\ZPO\\spraw1_ZPO");

        // Kolejka blokująca o pojemności 3
        BlockingQueue<Optional<Path>> queue = new LinkedBlockingQueue<>(3);

        // producent – przeszukuje katalog i dodaje ścieżki plików .txt do kolejki
        Runnable producent = () -> {
            try {
                Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>(){

                    // Metoda wywoływana dla każdego pliku w katalogu
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {

                        // Sprawdzamy czy plik ma rozszerzenie .txt
                        if (path.toString().endsWith(".txt")) {
                            try {
                                // Dodanie ścieżki do kolejki
                                queue.put(Optional.of(path));
                            } catch (InterruptedException e) {
                                // Przywrócenie flagi przerwania wątku
                                Thread.currentThread().interrupt();
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });

                // Dodanie sygnałów zakończenia dla konsumentów (po jednym dla każdego wątku)
                queue.put(Optional.empty());
                queue.put(Optional.empty());

            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        };

        // konsument – pobiera pliki z kolejki i przetwarza je
        Runnable konsument = () -> {
            try {
                while(true){

                    // Pobranie elementu z kolejki (blokujące)
                    Optional<Path> item = queue.take();

                    // Jeśli trafimy na pusty Optional – kończymy działanie wątku
                    if(!item.isPresent()){
                        break;
                    }

                    Path takenPath = item.get();

                    // Wywołanie metody liczącej najczęstsze słowa
                    Map<String, Long> result = getLinkedCountedWords(takenPath, 10);

                    // Wyświetlenie nazwy pliku
                    System.out.println(takenPath);
                    System.out.println();

                    // Wyświetlenie wyniku (mapa słowo -> liczba wystąpień)
                    System.out.println(result);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // ExecutorService zarządzający wątkami (1 producent + 2 konsumentów)
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Uruchomienie wątków
        executor.execute(producent);
        executor.execute(konsument);
        executor.execute(konsument);

        // Zakończenie przyjmowania nowych zadań
        executor.shutdown();
    }

    // Metoda licząca najczęściej występujące słowa w pliku
    public static Map<String, Long> getLinkedCountedWords(Path path, int wordsLimit) {

        // Try-with-resources – automatyczne zamknięcie strumienia pliku
        try (Stream<String> lines = Files.lines(path)) {

            // Tworzenie mapy: słowo -> liczba wystąpień
            Map<String, Long> countedWords = lines

                    // Podział każdej linii na słowa
                    .flatMap(line -> Arrays.stream(line.split("\\s+")))

                    // Usunięcie znaków specjalnych (zostają litery i cyfry)
                    .map(word -> word.replaceAll("[^a-zA-Z0-9ąęóśćżńźĄĘÓŚĆŻŃŹ]", ""))

                    // Zamiana na małe litery
                    .map(word -> word.toLowerCase())

                    // Filtrowanie słów krótszych niż 3 znaki
                    .filter(word -> word.length() >= 3)

                    // Grupowanie i liczenie wystąpień słów
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            // Sortowanie mapy malejąco po liczbie wystąpień i ograniczenie do N wyników
            Map<String, Long> sortedWords =
                    countedWords.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .limit(wordsLimit)
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (a, b) -> a,
                                    LinkedHashMap::new // zachowanie kolejności
                            ));

            return sortedWords;

        } catch(IOException e ){
            throw new RuntimeException(e);
        }
    }
}