import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class Client2 {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9999);
        System.out.println("Connected to server");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        int count = 0;
        int finalArr[];

        while(true){
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();

            out.println(message);
            if(message.equals("exit")){
                break;
            }else if(message.equals("sort")){
                System.out.println("Receiving sorted array...");
                finalArr = new int[count];
                for(int i = 0; i < finalArr.length; i++){
                    String num = in.readLine();
                    finalArr[i] = Integer.parseInt(num);
                    System.out.print(finalArr[i] + " ");
                }
                System.out.println();
                count = 0;
            }else if(isNumber(message)){
                count++;
            }else{
                continue;
            }
        }

        socket.close();
    }



    public static Boolean isNumber(String s) {
        for (int i = 0; i < s.length(); i++){
            if(i == 0 && s.charAt(0) == '-'){
                continue;
            }
            if(s.charAt(i) < '0' || s.charAt(i) > '9'){
                return false;
            }
        }
        return true;
    }
}