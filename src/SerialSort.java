import java.util.Arrays;

public class SerialSort {
    public static void bubble(int[] array) {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }

    public static void counting(int[] array) {
        if (array == null || array.length == 0) {
            return;
        }

        // Encontrar o valor máximo no array
        int max = Arrays.stream(array).max().getAsInt();

        int[] count = new int[max + 1];

        for (int num : array) {
            count[num]++;
        }

        int index = 0;
        for (int i = 0; i <= max; i++) {
            while (count[i] > 0) {
                array[index++] = i;
                count[i]--;
            }
        }
    }

    public static void merge(int[] array) {
        if(array.length > 1) {
            int mid = array.length/2;
            int[] firstHalf = new int[mid];
            System.arraycopy(array, 0, firstHalf, 0, mid);

            int[] secondHalf = new int[array.length - mid];
            System.arraycopy(array, mid, secondHalf, 0, array.length - mid);

            merge(firstHalf);
            merge(secondHalf);

            mergeAux(firstHalf, secondHalf, array);
        }
    }

    public static void mergeAux(int[] firstHalf, int[] secondHalf, int[] array) {
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

    public static void quick(int[] array, int inicio, int fim) {
        if (inicio < fim) {
            int pivotIndex = partition(array, inicio, fim);
            quick(array, inicio, pivotIndex - 1);
            quick(array, pivotIndex + 1, fim);
        }
    }

    private static int partition(int[] array, int inicio, int fim) {
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
