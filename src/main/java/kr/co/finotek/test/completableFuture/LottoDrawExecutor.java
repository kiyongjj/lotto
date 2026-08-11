package kr.co.finotek.test.completableFuture;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LottoDrawExecutor {

    private static final int NUMBER_COUNT = 6;
    private static final int MAX_NUMBER = 45;
    private static final long DRAW_COUNT = 120_000_000L; // 1억회 이상 가능
    private static final int THREAD_POOL_SIZE = 8;
    private static final int BATCH_SIZE = 10_000; // 메모리 절약용 배치 단위

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // thread-safe 누적용 맵
        ConcurrentMap<List<Integer>, AtomicLong> countMap = new ConcurrentHashMap<>();

        long totalBatches = (DRAW_COUNT + BATCH_SIZE - 1) / BATCH_SIZE;

        System.out.printf("총 %,d회 추첨을 %,d회 배치로 처리합니다.%n", DRAW_COUNT, totalBatches);

        for (long batch = 0; batch < totalBatches; batch++) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int i = 0; i < BATCH_SIZE && (batch * BATCH_SIZE + i) < DRAW_COUNT; i++) {
                futures.add(CompletableFuture.runAsync(() -> {
                    List<Integer> numbers = drawLottoNumbers();
                    countMap.computeIfAbsent(numbers, k -> new AtomicLong(0)).incrementAndGet();
                }, executor));
            }

            // 배치 완료 대기
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            if ((batch + 1) % 100 == 0) {
                System.out.printf("진행률: %,d / %,d 배치 완료%n", batch + 1, totalBatches);
            }
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // LottoResult 리스트 생성
        List<LottoResult> lottoResults = countMap.entrySet().stream()
                .map(entry -> new LottoResult(
                        entry.getKey(),
                        entry.getKey().stream().mapToInt(Integer::intValue).sum(),
                        entry.getValue().get()
                ))
                .sorted(Comparator.comparingLong(LottoResult::getCount).reversed())
//                .filter(count -> count.getCount() == 10)
                .limit(5) // 상위 100개만 출력
                .collect(Collectors.toList());

        System.out.printf("%-20s %-5s %-5s%n", "Numbers", "Sum", "Count");
        System.out.println("------------------------------------------");
        lottoResults.forEach(r ->
                System.out.printf("%-20s %-5d %-5d%n", r.getNumbers(), r.getSum(), r.getCount())
        );
    }

    private static List<Integer> drawLottoNumbers() {
        List<Integer> numbers = IntStream.rangeClosed(1, MAX_NUMBER)
                                         .boxed()
                                         .collect(Collectors.toList());
        Collections.shuffle(numbers);
        return numbers.stream()
                      .limit(NUMBER_COUNT)
                      .sorted()
                      .collect(Collectors.toList());
    }

    static class LottoResult {
        private final List<Integer> numbers;
        private final int sum;
        private final long count;

        public LottoResult(List<Integer> numbers, int sum, long count) {
            this.numbers = numbers;
            this.sum = sum;
            this.count = count;
        }

        public List<Integer> getNumbers() {
            return numbers;
        }

        public int getSum() {
            return sum;
        }

        public long getCount() {
            return count;
        }
    }
}
