public class MainQuick {
    public static void main(String[] args) {
        int[] setupArraySize = {100,1500};
        int[] setupThreads = {1,4,6,8};
        for (int z = 1; z < 6; z++) {
            for (int size : setupArraySize) {
                int[] array = Main.createNewRandomArray(size);
                for (int threadQuantity : setupThreads) {
                    ParallelSort.quick(array, threadQuantity);
                }
            }
        }
    }
}
