import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

public class AlgorithmBenchmark {
    
    static class PerformanceData {
        int operationsCount;
        int comparisonsCount;
        long executionTime;
        
        PerformanceData(int ops, int comps, long time) {
            this.operationsCount = ops;
            this.comparisonsCount = comps;
            this.executionTime = time;
        }
        
        double getTimeInMs() {
            return executionTime / 1_000_000.0;
        }
    }
    
    // Алгоритм 1: Поиск минимального элемента
    public static PerformanceData findMinSort(int[] data) {
        int ops = 0;
        int comps = 0;
        int size = data.length;
        
        for (int i = 0; i < size - 1; i++) {
            int minPos = i;
            for (int j = i + 1; j < size; j++) {
                comps++;
                if (data[j] < data[minPos]) {
                    minPos = j;
                    ops++;
                }
            }
            if (minPos != i) {
                int tmp = data[i];
                data[i] = data[minPos];
                data[minPos] = tmp;
                ops++;
            }
        }
        return new PerformanceData(ops, comps, 0);
    }
    
    // Алгоритм 2: Оптимизированный метод пузырька
    public static PerformanceData improvedBubbleMethod(int[] data) {
        int ops = 0;
        int comps = 0;
        int size = data.length;
        boolean hasChanges;
        
        for (int i = 0; i < size - 1; i++) {
            hasChanges = false;
            for (int j = 0; j < size - i - 1; j++) {
                comps++;
                if (data[j] > data[j + 1]) {
                    int tmp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = tmp;
                    ops++;
                    hasChanges = true;
                }
            }
            if (!hasChanges) break;
        }
        return new PerformanceData(ops, comps, 0);
    }
    
    // Алгоритм 3: Вставки с интервалом
    public static PerformanceData intervalInsertion(int[] data, int interval) {
        int ops = 0;
        int comps = 0;
        int size = data.length;
        
        for (int i = interval; i < size; i++) {
            int current = data[i];
            int j = i - interval;
            
            while (j >= 0) {
                comps++;
                if (data[j] > current) {
                    data[j + interval] = data[j];
                    ops++;
                    j -= interval;
                } else {
                    break;
                }
            }
            data[j + interval] = current;
            ops++;
        }
        return new PerformanceData(ops, comps, 0);
    }
    
    // Алгоритм 4: Слияние без рекурсии
    public static PerformanceData nonRecursiveMerge(int[] data) {
        int ops = 0;
        int comps = 0;
        int size = data.length;
        
        if (size <= 1) {
            return new PerformanceData(0, 0, 0);
        }
        
        for (int block = 1; block < size; block *= 2) {
            for (int left = 0; left < size; left += 2 * block) {
                int mid = Math.min(left + block, size);
                int right = Math.min(left + 2 * block, size);
                
                int[] leftPart = Arrays.copyOfRange(data, left, mid);
                int[] rightPart = Arrays.copyOfRange(data, mid, right);
                
                int i = 0, j = 0, k = left;
                
                while (i < leftPart.length && j < rightPart.length) {
                    comps++;
                    if (leftPart[i] <= rightPart[j]) {
                        data[k] = leftPart[i];
                        i++;
                    } else {
                        data[k] = rightPart[j];
                        j++;
                    }
                    k++;
                    ops++;
                }
                
                while (i < leftPart.length) {
                    data[k] = leftPart[i];
                    i++;
                    k++;
                    ops++;
                }
                
                while (j < rightPart.length) {
                    data[k] = rightPart[j];
                    j++;
                    k++;
                    ops++;
                }
            }
        }
        return new PerformanceData(ops, comps, 0);
    }
    
    // Алгоритм 5: Шелл с последовательностью Кнута
    public static PerformanceData shellMethod(int[] data) {
        int ops = 0;
        int comps = 0;
        int size = data.length;
        
        int gap = 1;
        while (gap < size / 3) {
            gap = 3 * gap + 1;
        }
        
        while (gap >= 1) {
            for (int i = gap; i < size; i++) {
                int tempVal = data[i];
                int j = i;
                
                while (j >= gap) {
                    comps++;
                    if (data[j - gap] > tempVal) {
                        data[j] = data[j - gap];
                        ops++;
                        j -= gap;
                    } else {
                        break;
                    }
                }
                data[j] = tempVal;
                ops++;
            }
            gap /= 3;
        }
        return new PerformanceData(ops, comps, 0);
    }
    
    // Алгоритм 6: Быстрая с медианным разделением
    private static int medianSplit(int[] data, int low, int high, PerformanceData results) {
        int center = low + (high - low) / 2;
        
        if (data[low] > data[center]) {
            swapElements(data, low, center);
            results.operationsCount++;
        }
        if (data[low] > data[high]) {
            swapElements(data, low, high);
            results.operationsCount++;
        }
        if (data[center] > data[high]) {
            swapElements(data, center, high);
            results.operationsCount++;
        }
        
        swapElements(data, center, high - 1);
        results.operationsCount++;
        int pivot = data[high - 1];
        
        int i = low;
        for (int j = low; j < high - 1; j++) {
            results.comparisonsCount++;
            if (data[j] <= pivot) {
                swapElements(data, i, j);
                results.operationsCount++;
                i++;
            }
        }
        swapElements(data, i, high - 1);
        results.operationsCount++;
        return i;
    }
    
    public static PerformanceData quickSortWithMedian(int[] data, int low, int high) {
        PerformanceData results = new PerformanceData(0, 0, 0);
        
        if (low < high) {
            if (high - low > 10) {
                int pivotIndex = medianSplit(data, low, high, results);
                PerformanceData leftResults = quickSortWithMedian(data, low, pivotIndex - 1);
                PerformanceData rightResults = quickSortWithMedian(data, pivotIndex + 1, high);
                
                results.operationsCount += leftResults.operationsCount + rightResults.operationsCount;
                results.comparisonsCount += leftResults.comparisonsCount + rightResults.comparisonsCount;
            } else {
                PerformanceData insertionResults = intervalInsertion(data, 1);
                results.operationsCount += insertionResults.operationsCount;
                results.comparisonsCount += insertionResults.comparisonsCount;
            }
        }
        return results;
    }
    
    // Алгоритм 7: Пирамидальная снизу-вверх
    private static void heapify(int[] data, int start, int end, PerformanceData results) {
        int current = start;
        
        while (2 * current + 1 <= end) {
            int child = 2 * current + 1;
            int swapIndex = current;
            
            results.comparisonsCount++;
            if (data[swapIndex] < data[child]) {
                swapIndex = child;
            }
            
            results.comparisonsCount++;
            if (child + 1 <= end && data[swapIndex] < data[child + 1]) {
                swapIndex = child + 1;
            }
            
            if (swapIndex == current) {
                return;
            } else {
                swapElements(data, current, swapIndex);
                results.operationsCount++;
                current = swapIndex;
            }
        }
    }
    
    public static PerformanceData heapMethod(int[] data) {
        int ops = 0;
        int comps = 0;
        int size = data.length;
        PerformanceData results = new PerformanceData(ops, comps, 0);
        
        for (int i = (size - 2) / 2; i >= 0; i--) {
            heapify(data, i, size - 1, results);
        }
        
        for (int i = size - 1; i > 0; i--) {
            swapElements(data, 0, i);
            results.operationsCount++;
            heapify(data, 0, i - 1, results);
        }
        
        return results;
    }
    
    // Вспомогательные методы
    private static void swapElements(int[] data, int i, int j) {
        int tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }
    
    private static PerformanceData measurePerformance(int[] data, Consumer<int[]> algorithm) {
        int[] testArray = data.clone();
        long start = System.nanoTime();
        algorithm.accept(testArray);
        long end = System.nanoTime();
        
        return new PerformanceData(0, 0, end - start);
    }
    
    // Сравнение производительности
    public static void comparePerformance() {
        System.out.println("\nСРАВНЕНИЕ ПРОИЗВОДИТЕЛЬНОСТИ АЛГОРИТМОВ");
        System.out.println("=============================================");
        
        int[] sizes = {10, 50, 100};
        String[] names = {
            "Поиск минимума",
            "Улучшенный пузырек", 
            "Вставки с интервалом",
            "Слияние без рекурсии",
            "Метод Шелла",
            "Быстрая с медианой",
            "Пирамидальная"
        };
        
        Consumer<int[]>[] algorithms = new Consumer[] {
            arr -> findMinSort(arr),
            arr -> improvedBubbleMethod(arr),
            arr -> intervalInsertion(arr, 1),
            arr -> nonRecursiveMerge(arr),
            arr -> shellMethod(arr),
            arr -> quickSortWithMedian(arr, 0, arr.length - 1),
            arr -> heapMethod(arr)
        };
        
        Random rand = new Random();
        
        for (int size : sizes) {
            System.out.println("\nРазмер данных: " + size + " элементов");
            System.out.println("---------------------------------------------");
            
            int[] testArray = new int[size];
            for (int i = 0; i < size; i++) {
                testArray[i] = rand.nextInt(1000) + 1;
            }
            
            for (int i = 0; i < algorithms.length; i++) {
                PerformanceData result = measurePerformance(testArray, algorithms[i]);
                System.out.printf("%-25s | Время: %6.3fms%n", 
                    names[i], result.getTimeInMs());
            }
        }
    }
    
    // Основной метод
    public static void main(String[] args) {
        int[] initialData = {64, 34, 25, 12, 22, 11, 90, 5, 77, 30};
        
        System.out.println("СРАВНИТЕЛЬНЫЙ АНАЛИЗ АЛГОРИТМОВ СОРТИРОВКИ");
        System.out.println("Начальный массив: " + Arrays.toString(initialData));
        System.out.println();
        
        String[] algorithmNames = {
            "Поиск минимума",
            "Улучшенный пузырек",
            "Вставки с интервалом", 
            "Слияние без рекурсии",
            "Метод Шелла",
            "Быстрая с медианой",
            "Пирамидальная"
        };
        
        Consumer<int[]>[] algorithms = new Consumer[] {
            arr -> findMinSort(arr),
            arr -> improvedBubbleMethod(arr),
            arr -> intervalInsertion(arr, 1),
            arr -> nonRecursiveMerge(arr),
            arr -> shellMethod(arr),
            arr -> quickSortWithMedian(arr, 0, arr.length - 1),
            arr -> heapMethod(arr)
        };
        
        int[][] outputs = new int[algorithms.length][];
        PerformanceData[] metrics = new PerformanceData[algorithms.length];
        
        for (int i = 0; i < algorithms.length; i++) {
            int[] arr = initialData.clone();
            long start = System.nanoTime();
            metrics[i] = measurePerformance(arr, algorithms[i]);
            long end = System.nanoTime();
            metrics[i].executionTime = end - start;
            outputs[i] = arr;
            
            System.out.println(algorithmNames[i] + ": " + Arrays.toString(arr));
            System.out.printf("%-25s Действий: %d, Сравнений: %d, Время: %.3fms%n%n", 
                "", metrics[i].operationsCount, metrics[i].comparisonsCount, metrics[i].getTimeInMs());
        }
        
        // Верификация результатов
        boolean allValid = true;
        int[] reference = initialData.clone();
        Arrays.sort(reference);
        
        for (int[] output : outputs) {
            if (!Arrays.equals(output, reference)) {
                allValid = false;
                break;
            }
        }
        
        System.out.println("РЕЗУЛЬТАТ ПРОВЕРКИ: " + 
            (allValid ? "Все алгоритмы работают правильно" : "Найдены несоответствия"));
        
        // Запуск сравнения
        comparePerformance();
    }
}
