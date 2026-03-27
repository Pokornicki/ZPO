import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        // Ścieżka do katalogu z plikami wejściowymi
        Path dirPath = Paths.get("C:\\Users\\ThinkPad\\Desktop\\ZPO\\spraw1_ZPO");

        // Pętla nieskończona
        while (true) {

            // Kolejka blokująca – synchronizuje producenta i konsumentów
            BlockingQueue<Optional<Path>> queue = new LinkedBlockingQueue<>(3);

            // producent – przeszukuje katalog i dodaje pliki do kolejki
            Runnable producent = () -> {
                try {

                    // Rekurencyjne przejście po katalogu
                    Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {

                        @Override
                        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {

                            // Dodajemy tylko pliki .txt
                            if (path.toString().endsWith(".txt")) {
                                try {
                                    // Wstawienie elementu do kolejki
                                    queue.put(Optional.of(path));
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    return FileVisitResult.TERMINATE;
                                }
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });

                    // Sygnał zakończenia pracy dla konsumentów (po jednym na wątek)
                    queue.put(Optional.empty());
                    queue.put(Optional.empty());

                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            };

            // konsument – pobiera pliki i przetwarza je
            Runnable konsument = () -> {
                try {
                    while (true) {

                        // Pobranie elementu z kolejki
                        Optional<Path> item = queue.take();

                        // Jeśli trafimy na pusty element – kończymy pracę wątku
                        if (item.isEmpty()) {
                            break;
                        }

                        Path takenPath = item.get();

                        // Przetworzenie pliku – zliczenie najczęstszych słów
                        Map<String, Long> result = getLinkedCountedWords(takenPath, 10);

                        // Wyświetlenie wyników
                        System.out.println("Plik: " + takenPath);
                        System.out.println(result);
                        System.out.println();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };

            // Pula wątków: 1 producent + 2 konsumenci
            ExecutorService executor = Executors.newFixedThreadPool(3);

            // Uruchomienie zadań równoległych
            executor.execute(producent);
            executor.execute(konsument);
            executor.execute(konsument);

            // Zablokowanie możliwości dodawania nowych zadań
            executor.shutdown();

            try {
                // Oczekiwanie na zakończenie wszystkich wątków
                executor.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println("Koniec jednej iteracji. Program uruchamia się ponownie...\n");

            try {
                // Przerwa między kolejnymi iteracjami
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // Metoda analizująca plik i zwracająca najczęstsze słowa
    public static Map<String, Long> getLinkedCountedWords(Path path, int wordsLimit) {

        // Automatyczne zamknięcie strumienia po zakończeniu pracy
        try (Stream<String> lines = Files.lines(path)) {

            // Grupowanie słów i zliczanie ich wystąpień
            Map<String, Long> countedWords = lines
                    .flatMap(line -> Arrays.stream(line.split("\\s+"))) // podział na słowa
                    .map(word -> word.replaceAll("[^a-zA-Z0-9ąęóśćżńźĄĘÓŚĆŻŃŹ]", "")) // czyszczenie
                    .map(String::toLowerCase) // normalizacja
                    .filter(word -> word.length() >= 3) // filtr długości
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            // Sortowanie malejąco po liczbie wystąpień i ograniczenie wyników
            return countedWords.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(wordsLimit)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (a, b) -> a,
                            LinkedHashMap::new // zachowanie kolejności
                    ));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}