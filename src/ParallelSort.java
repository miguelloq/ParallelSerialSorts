import java.util.Arrays;
import java.util.concurrent.*;
public class ParallelSort {
    public static void bubble(int[] array, int numThreads){
        int n = array.length;
        int chunkSize = (int) Math.ceil((double) n / numThreads);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize;
            final int end = Math.min((i + 1) * chunkSize, n);

            executor.execute(() -> bubbleAux(array, start, end));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        SerialSort.bubble(array);
    }

    private static void bubbleAux(int[] array, int start, int end) {
        for (int i = start; i < end - 1; i++) {
            for (int j = start; j < end - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }

    public static void counting(int[] array, int numThreads){
        int max = Arrays.stream(array).max().getAsInt();
        int n = array.length;

        int[] count = new int[max + 1];

        for (int num : array) {
            count[num]++;
        }

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final int start = i * (max + 1) / numThreads;
            final int end = (i + 1) * (max + 1) / numThreads;

            executor.execute(() -> updateArray(array, count, start, end));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void updateArray(int[] array, int[] count, int start, int end) {
        for (int i = start; i < end; i++) {
            int value = i;
            int frequency = count[i];

            for (int j = 0; j < frequency; j++) {
                array[start + j] = value;
            }
        }
    }

    public static void merge(int[] array, int numThreads) {
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        SortTask mainTask = new SortTask(array);
        pool.invoke(mainTask);
    }

    private static class SortTask extends RecursiveAction {
        private int[] array;

        public SortTask(int[] array) {
            this.array = array;
        }
        @Override
        protected void compute() {
            if(array.length > 1) {
                int mid = array.length/2;

                int[] firstHalf = new int[mid];
                System.arraycopy(array, 0, firstHalf, 0, mid);

                int[] secondHalf = new int[array.length - mid];
                System.arraycopy(array, mid, secondHalf, 0, array.length - mid);

                SortTask firstHalfTask = new SortTask(firstHalf);
                SortTask secondHalfTask = new SortTask(secondHalf);

                invokeAll(firstHalfTask, secondHalfTask);

                mergeAux(firstHalf, secondHalf, array);
            }
        }
    }
    private static void mergeAux(int[] firstHalf, int[] secondHalf, int[] array) {
        int currentIndexFirst = 0;
        int currentIndexSecond = 0;
        int currentIndexArray = 0;

        while(currentIndexFirst < firstHalf.length && currentIndexSecond < secondHalf.length) {
            if(firstHalf[currentIndexFirst] < secondHalf[currentIndexSecond]) {
                array[currentIndexArray] = firstHalf[currentIndexFirst];
                currentIndexArray++;
                currentIndexFirst++;
            } else {
                array[currentIndexArray] = secondHalf[currentIndexSecond];
                currentIndexArray++;
                currentIndexSecond++;
            }
        }

        while(currentIndexFirst < firstHalf.length) {
            array[currentIndexArray] = firstHalf[currentIndexFirst];
            currentIndexArray++;
            currentIndexFirst++;
        }

        while(currentIndexSecond < secondHalf.length) {
            array[currentIndexArray] = secondHalf[currentIndexSecond];
            currentIndexArray++;
            currentIndexSecond++;
        }
    }

    public static void quick(int[] array, int numThreads) {
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        QuickSortTask mainTask = new QuickSortTask(array, 0, array.length - 1);
        pool.invoke(mainTask);
    }

    private static class QuickSortTask extends RecursiveAction {
        private final int[] array;
        private final int inicio, fim;
        private static final int THRESHOLD = 1000; // Tamanho limite para a execução sequencial

        public QuickSortTask(int[] array, int inicio, int fim) {
            this.array = array;
            this.inicio = inicio;
            this.fim = fim;
        }

        @Override
        protected void compute() {
            if (fim - inicio <= THRESHOLD) {
                quickSort(array, inicio, fim);
            } else {
                int pivotIndex = partition(array, inicio, fim);

                QuickSortTask leftTask = new QuickSortTask(array, inicio, pivotIndex - 1);
                QuickSortTask rightTask = new QuickSortTask(array, pivotIndex + 1, fim);

                invokeAll(leftTask, rightTask);
            }
        }

        private void quickSort(int[] array, int inicio, int fim) {
            if (inicio < fim) {
                int pivotIndex = partition(array, inicio, fim);
                quickSort(array, inicio, pivotIndex - 1);
                quickSort(array, pivotIndex + 1, fim);
            }
        }

        private int partition(int[] array, int inicio, int fim) {
            int pivot = array[fim];
            int i = inicio - 1;
            for (int j = inicio; j < fim; j++) {
                if (array[j] < pivot) {
                    i++;
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
            int temp = array[i + 1];
            array[i + 1] = array[fim];
            array[fim] = temp;
            return i + 1;
        }
    }
}
