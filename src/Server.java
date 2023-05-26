import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    public static void main(String[] args) throws IOException {

        long startTime = System.nanoTime();
        int testSize = 100000000;
        Random rand = new Random();
        int[] testArray = new int[testSize];
        for (int i = 0; i < testSize ; i++){
            testArray[i] = rand.nextInt(100000);
        }
        parallelQuickSort(testArray);
        /*for (int i = 0; i < testSize ; i++){
            System.out.print(testArray[i] + " ");
        }*/
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;
        System.out.println(duration);


        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("Server started on port 9999");
        int countOfClients = 0;

        while (true) {
            Socket socket = serverSocket.accept();
            countOfClients++;
            System.out.println("Client " + countOfClients + " connected: " + socket.getInetAddress().getHostAddress());

            // Start a new thread to handle the new client
            new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    int MAX = 10000;
                    int myArr[]= new int[MAX];
                    int count = 0;

                    while(true){
                        String message = in.readLine();
                        if(message.equals("exit")){
                            System.out.println("client exited");
                            break;
                        }else if(isNumber(message)){
                            System.out.println("Received number: " + message);
                            myArr[count] = Integer.parseInt(message);
                            count++;
                        }else if(message.equals("sort")){

                            int finalArr[] = new int[count];
                            for(int i = 0; i < count; i++){
                                finalArr[i] = myArr[i];
                            }
                            System.out.println("Sorting array..." + "\n" + "Sorted array:");
                            parallelQuickSort(finalArr);
                            for (int i = 0; i < finalArr.length; i++) {
                                System.out.print(finalArr[i] + " ");
                            }
                            System.out.println();
                            System.out.println("Sending back sorted array...");
                            //Sending back sorted array...
                            for(int i = 0; i < finalArr.length; i++){
                                out.println(Integer.toString(finalArr[i]));
                            }
                            System.out.println("Array sent!");
                            myArr = new int[MAX];
                            count = 0;
                        }else{
                            continue;
                        }
                    }

                    socket.close();

                    // Handle client communication here

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }


       // BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
       // PrintWriter out = new PrintWriter(socket.getOutputStream(), true);


    }

    public static void parallelQuickSort(int[] array) {
        int n = 1;
        ForkJoinPool pool = new ForkJoinPool(n);
        pool.invoke(new QuickSortTask(array, 0, array.length - 1));
    }

    private static class QuickSortTask extends RecursiveAction {
        private final int[] array;
        private final int left;
        private final int right;

        QuickSortTask(int[] array, int left, int right) {
            this.array = array;
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            if (left < right) {
                int pivotIndex = partition(array, left, right);
                QuickSortTask leftTask = new QuickSortTask(array, left, pivotIndex - 1);
                QuickSortTask rightTask = new QuickSortTask(array, pivotIndex + 1, right);
                leftTask.fork();
                rightTask.compute();
                leftTask.join();
            }
        }

        private int partition(int[] array, int left, int right) {
            int pivotIndex = left + ThreadLocalRandom.current().nextInt(right - left + 1);
            int pivot = array[pivotIndex];

            swap(array, pivotIndex, right);

            int partitionIndex = left;
            for (int i = left; i < right; i++) {
                if (array[i] < pivot) {
                    swap(array, i, partitionIndex);
                    partitionIndex++;
                }
            }

            swap(array, partitionIndex, right);
            return partitionIndex;
        }

        private void swap(int[] array, int i, int j) {
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static Boolean isNumber(String s) {
        for (int i = 0; i < s.length(); i++){
            if(i==0 && s.charAt(0)=='-'){
                continue;
            }
            if(s.charAt(i) < '0' || s.charAt(i) > '9'){
                return false;
            }
        }
        return true;
    }

}