import java.io.IOException;
import java.util.Arrays;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Main{
    public static int[] setupThreads = {1,4,6,8,12};
    public static int[] setupArrayLength = new int[]{100000, 5000000};
    public static String formatArrayForRowCSV(int[] arr) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            result.append(arr[i]);

            if (i < arr.length - 1) {
                result.append(",");
            }
        }

        return result.toString();
    }

    public static int[] createNewRandomArray(int size) {
        int[] array = new int[size];

        for (int i = 0; i < array.length; i++) {
            array[i] = (int) (Math.random() * array.length);
        }

        return array;
    }

    interface InputSort{
        public void run(int[] array,int threadQuantity);
    }
    public static void registerSortCSV(String sortName,BufferedWriter writer,int[] setupArraySize, InputSort inputSort) throws IOException {
        writer.write("\n" + sortName + ":\n");
        writer.write(","+ formatArrayForRowCSV(setupThreads) + "\n");

        for(int size:setupArraySize) {
            int[] array = createNewRandomArray(size);
            int[] dataForThisSize = new int[setupThreads.length];
            for(int i=0;i<setupThreads.length;i++) {
                int[] arrayCopy = array.clone();
                int numThreads = setupThreads[i];
                long tempoInicial = System.currentTimeMillis();
                inputSort.run(arrayCopy,numThreads);
                long tempoFinal = System.currentTimeMillis();
                dataForThisSize[i]= (int) (tempoFinal-tempoInicial);
            }
            writer.write(size + "," + formatArrayForRowCSV(dataForThisSize) + "\n");
        }
    }
    public static void main(String[] args) {
        int[] setupArrayLengthBubble= new int[]{1000,10000};
        int[] setupArrayLengthQuick = new int[]{100,1000};

        String filePath = "ParallelSerialSortsGenerated.csv";
        System.out.println("Gerando csv...");
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

            for (int i = 1; i < 6; i++) {
                registerSortCSV("BubbleSort (Caso" + i + ")" , writer, setupArrayLengthBubble,
                        (array, threadQuantity) -> {
                            if ((threadQuantity == 1)) {SerialSort.bubble(array);}
                            else {ParallelSort.bubble(array, threadQuantity);}
                        });
            }
            for (int i = 1; i < 6; i++) {
                registerSortCSV("MergeSort (Caso" + i + ")" , writer,  setupArrayLength,
                        (array, threadQuantity) -> {
                            if ((threadQuantity == 1)) {SerialSort.merge(array);}
                            else {ParallelSort.merge(array, threadQuantity);}
                        });
            }
            for (int i = 1; i < 6; i++) {
                registerSortCSV("QuickSort (Caso" + i + ")" , writer, setupArrayLengthQuick,
                        (array, threadQuantity) -> {
                            if ((threadQuantity == 1)) {SerialSort.quick(array,0,array.length-1);}
                            else {ParallelSort.quick(array,threadQuantity);}
                        });
            }
            for (int i = 1; i < 6; i++) {
                registerSortCSV("CountingSort (Caso" + i + ")" , writer, setupArrayLength,
                        (array, threadQuantity) -> {
                            if ((threadQuantity == 1)) {SerialSort.counting(array);}
                            else {ParallelSort.counting(array, threadQuantity);}
                        });
            }

            writer.close();
            System.out.println("Csv gerado");
        }catch(IOException e){
            System.err.println("Erro ao escrever no arquivo CSV: " + e.getMessage());
        }
    }
}