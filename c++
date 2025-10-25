#include <iostream>
#include <vector>
#include <algorithm>
#include <chrono>
#include <random>
#include <functional>
using namespace std;

struct AlgoStats {
    int swap_count;
    int compare_count;
    double exec_time;
};

class SortAlgorithms {
public:
    static void minElementSort(vector<int>& data, int& swaps, int& compares) {
        swaps = 0;
        compares = 0;
        int n = data.size();
        
        for (int i = 0; i < n - 1; i++) {
            int min_pos = i;
            for (int j = i + 1; j < n; j++) {
                compares++;
                if (data[j] < data[min_pos]) {
                    min_pos = j;
                    swaps++;
                }
            }
            if (min_pos != i) {
                std::swap(data[i], data[min_pos]);
                swaps++;
            }
        }
    }
    
    static void optimizedBubble(vector<int>& data, int& swaps, int& compares) {
        swaps = 0;
        compares = 0;
        int n = data.size();
        bool has_swapped;
        
        for (int i = 0; i < n - 1; i++) {
            has_swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                compares++;
                if (data[j] > data[j + 1]) {
                    std::swap(data[j], data[j + 1]);
                    swaps++;
                    has_swapped = true;
                }
            }
            if (!has_swapped) return;
        }
    }
    
    static void insertionWithStep(vector<int>& data, int step_size, int& swaps, int& compares) {
        swaps = 0;
        compares = 0;
        int n = data.size();
        
        for (int i = step_size; i < n; i++) {
            int current_val = data[i];
            int j = i - step_size;
            
            while (j >= 0) {
                compares++;
                if (data[j] > current_val) {
                    data[j + step_size] = data[j];
                    swaps++;
                    j -= step_size;
                } else {
                    break;
                }
            }
            data[j + step_size] = current_val;
            swaps++;
        }
    }
    
    static void iterativeMerge(vector<int>& data, int& swaps, int& compares) {
        swaps = 0;
        compares = 0;
        int n = data.size();
        
        for (int block_size = 1; block_size < n; block_size *= 2) {
            for (int left_start = 0; left_start < n; left_start += 2 * block_size) {
                int mid_point = min(left_start + block_size, n);
                int right_end = min(left_start + 2 * block_size, n);
                
                vector<int> left_block(data.begin() + left_start, data.begin() + mid_point);
                vector<int> right_block(data.begin() + mid_point, data.begin() + right_end);
                
                int i = 0, j = 0, k = left_start;
                
                while (i < left_block.size() && j < right_block.size()) {
                    compares++;
                    if (left_block[i] <= right_block[j]) {
                        data[k] = left_block[i];
                        i++;
                    } else {
                        data[k] = right_block[j];
                        j++;
                    }
                    k++;
                    swaps++;
                }
                
                while (i < left_block.size()) {
                    data[k] = left_block[i];
                    i++;
                    k++;
                    swaps++;
                }
                
                while (j < right_block.size()) {
                    data[k] = right_block[j];
                    j++;
                    k++;
                    swaps++;
                }
            }
        }
    }
    
    static void shellSort(vector<int>& data, int& swaps, int& compares) {
        swaps = 0;
        compares = 0;
        int n = data.size();
        
        int gap_val = 1;
        while (gap_val < n / 3) {
            gap_val = 3 * gap_val + 1;
        }
        
        while (gap_val >= 1) {
            for (int i = gap_val; i < n; i++) {
                int temp_val = data[i];
                int j = i;
                
                while (j >= gap_val) {
                    compares++;
                    if (data[j - gap_val] > temp_val) {
                        data[j] = data[j - gap_val];
                        swaps++;
                        j -= gap_val;
                    } else {
                        break;
                    }
                }
                data[j] = temp_val;
                swaps++;
            }
            gap_val /= 3;
        }
    }
    
private:
    static int medianPartition(vector<int>& data, int low_idx, int high_idx, int& swaps, int& compares) {
        int mid_idx = low_idx + (high_idx - low_idx) / 2;
        
        if (data[low_idx] > data[mid_idx]) {
            std::swap(data[low_idx], data[mid_idx]);
            swaps++;
        }
        if (data[low_idx] > data[high_idx]) {
            std::swap(data[low_idx], data[high_idx]);
            swaps++;
        }
        if (data[mid_idx] > data[high_idx]) {
            std::swap(data[mid_idx], data[high_idx]);
            swaps++;
        }
        
        std::swap(data[mid_idx], data[high_idx - 1]);
        swaps++;
        int pivot_val = data[high_idx - 1];
        
        int i = low_idx;
        for (int j = low_idx; j < high_idx - 1; j++) {
            compares++;
            if (data[j] <= pivot_val) {
                std::swap(data[i], data[j]);
                swaps++;
                i++;
            }
        }
        std::swap(data[i], data[high_idx - 1]);
        swaps++;
        return i;
    }
    
public:
    static void quickSortMedian(vector<int>& data, int low_idx, int high_idx, int& swaps, int& compares) {
        if (low_idx < high_idx) {
            if (high_idx - low_idx > 10) {
                int pivot_idx = medianPartition(data, low_idx, high_idx, swaps, compares);
                quickSortMedian(data, low_idx, pivot_idx - 1, swaps, compares);
                quickSortMedian(data, pivot_idx + 1, high_idx, swaps, compares);
            } else {
                insertionWithStep(data, 1, swaps, compares);
            }
        }
    }
    
    static void heapifyDown(vector<int>& data, int start_idx, int end_idx, int& swaps, int& compares) {
        int current = start_idx;
        
        while (2 * current + 1 <= end_idx) {
            int child_idx = 2 * current + 1;
            int swap_idx = current;
            
            compares++;
            if (data[swap_idx] < data[child_idx]) {
                swap_idx = child_idx;
            }
            
            compares++;
            if (child_idx + 1 <= end_idx && data[swap_idx] < data[child_idx + 1]) {
                swap_idx = child_idx + 1;
            }
            
            if (swap_idx == current) {
                return;
            } else {
                std::swap(data[current], data[swap_idx]);
                swaps++;
                current = swap_idx;
            }
        }
    }
    
    static void heapSort(vector<int>& data, int& swaps, int& compares) {
        swaps = 0;
        compares = 0;
        int n = data.size();
        
        for (int i = (n - 2) / 2; i >= 0; i--) {
            heapifyDown(data, i, n - 1, swaps, compares);
        }
        
        for (int i = n - 1; i > 0; i--) {
            std::swap(data[0], data[i]);
            swaps++;
            heapifyDown(data, 0, i - 1, swaps, compares);
        }
    }
};

AlgoStats testAlgorithm(vector<int> data, function<void(vector<int>&, int&, int&)> algo_func) {
    auto time_start = chrono::high_resolution_clock::now();
    
    int swap_cnt, compare_cnt;
    algo_func(data, swap_cnt, compare_cnt);
    
    auto time_end = chrono::high_resolution_clock::now();
    double time_elapsed = chrono::duration_cast<chrono::microseconds>(time_end - time_start).count() / 1000.0;
    
    return {swap_cnt, compare_cnt, time_elapsed};
}

void runBenchmarks() {
    cout << "\nСРАВНЕНИЕ ЭФФЕКТИВНОСТИ АЛГОРИТМОВ СОРТИРОВКИ" << endl;
    cout << "======================================================" << endl;
    
    vector<int> test_sizes = {10, 50, 100};
    vector<pair<string, function<void(vector<int>&, int&, int&)>>> algorithms = {
        {"Min Selection", SortAlgorithms::minElementSort},
        {"Optimized Bubble", SortAlgorithms::optimizedBubble},
        {"Insertion Sort", [](vector<int>& arr, int& op, int& comp) { 
            SortAlgorithms::insertionWithStep(arr, 1, op, comp); 
        }},
        {"Iterative Merge", SortAlgorithms::iterativeMerge},
        {"Shell Sort", SortAlgorithms::shellSort},
        {"Quick Sort", [](vector<int>& arr, int& op, int& comp) { 
            SortAlgorithms::quickSortMedian(arr, 0, arr.size() - 1, op, comp); 
        }},
        {"Heap Sort", SortAlgorithms::heapSort}
    };
    
    random_device rd;
    mt19937 generator(rd());
    
    for (int size_val : test_sizes) {
        cout << "\nТестируемый размер: " << size_val << " элементов" << endl;
        cout << "------------------------------------------------------" << endl;
        
        vector<int> test_array(size_val);
        uniform_int_distribution<> dist(1, 1000);
        generate(test_array.begin(), test_array.end(), [&]() { return dist(generator); });
        
        for (const auto& [algo_name, algo_func] : algorithms) {
            AlgoStats stats = testAlgorithm(test_array, algo_func);
            cout << algo_name << " | ";
            cout << "Время: " << stats.exec_time << "ms | ";
            cout << "Обмены: " << stats.swap_count << " | ";
            cout << "Сравнения: " << stats.compare_count << endl;
        }
    }
}

int main() {
    vector<int> test_data = {64, 34, 25, 12, 22, 11, 90, 5, 77, 30};
    
    cout << "СРАВНИТЕЛЬНЫЙ АНАЛИЗ АЛГОРИТМОВ СОРТИРОВКИ" << endl;
    cout << "Начальный набор: ";
    for (int val : test_data) cout << val << " ";
    cout << endl << endl;
    
    vector<pair<string, function<void(vector<int>&, int&, int&)>>> algorithms = {
        {"Min Selection", SortAlgorithms::minElementSort},
        {"Optimized Bubble", SortAlgorithms::optimizedBubble},
        {"Insertion Sort", [](vector<int>& arr, int& op, int& comp) { 
            SortAlgorithms::insertionWithStep(arr, 1, op, comp); 
        }},
        {"Iterative Merge", SortAlgorithms::iterativeMerge},
        {"Shell Sort", SortAlgorithms::shellSort},
        {"Quick Sort", [](vector<int>& arr, int& op, int& comp) { 
            SortAlgorithms::quickSortMedian(arr, 0, arr.size() - 1, op, comp); 
        }},
        {"Heap Sort", SortAlgorithms::heapSort}
    };
    
    vector<vector<int>> sorted_results;
    
    for (const auto& [name, algorithm] : algorithms) {
        vector<int> current_data = test_data;
        int swap_count, compare_count;
        
        auto start_time = chrono::high_resolution_clock::now();
        algorithm(current_data, swap_count, compare_count);
        auto end_time = chrono::high_resolution_clock::now();
        double time_taken = chrono::duration_cast<chrono::microseconds>(end_time - start_time).count() / 1000.0;
        
        sorted_results.push_back(current_data);
        
        cout << name << ": ";
        for (int num : current_data) cout << num << " ";
        cout << endl;
        cout << "  Перестановок: " << swap_count << ", Сравнений: " << compare_count;
        cout << ", Затрачено времени: " << time_taken << "ms" << endl << endl;
    }
    
    bool all_sorted_correctly = true;
    vector<int> reference_sorted = test_data;
    sort(reference_sorted.begin(), reference_sorted.end());
    
    for (const auto& result : sorted_results) {
        if (result != reference_sorted) {
            all_sorted_correctly = false;
            break;
        }
    }
    
    cout << "РЕЗУЛЬТАТ ПРОВЕРКИ: ";
    cout << (all_sorted_correctly ? "Все алгоритмы отсортировали корректно" : "Найдены расхождения") << endl;
    
    runBenchmarks();
    
    return 0;
}
