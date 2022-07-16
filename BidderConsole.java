import java.net.Socket;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.io.BufferedReader;
import java.io.IOException;


public class BidderConsole {
    boolean isAuctionOver = false;
    Socket socket;
    public void runClient (String name) {
        try {
            CountDown.countNumber = 0;
            socket = new Socket("localhost", 3000);
            AuctioneerResponseConnection  auctioneerConnection = new AuctioneerResponseConnection(socket);
            BufferedReader bidderInput = new BufferedReader(new InputStreamReader(System.in));

            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            
            new Thread(auctioneerConnection).start();
            
            out.println(name);

            while ( !isAuctionOver ) {
                String message = bidderInput.readLine();
                
                if (message.equals("quit")) {

                    isAuctionOver = true;
                    out.println("quit");
                    Thread.sleep(10);
                    break;
                    
                }
                out.println(message);

            }
        }

        catch (Exception e) {
            System.out.println("In client catch block");
            e.printStackTrace();
        }

        finally {
            try {
                
                socket.close();
            }
            catch (IOException e) {
                System.out.println("Error closing socket in client console");
            }
        }

    }

    private  void showMenu () {
        System.out.println("------------------------------------");
        System.out.println("1.Enter Auction");
        System.out.println("2.Quit");
        System.out.println("------------------------------------");
    }
    public static void main(String[] args) {
        BidderConsole bidderConsole = new BidderConsole();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Your Name: ");
        String name = scanner.nextLine();
        
        boolean toExit = false ;
        while (!toExit){
            new BidderConsole().showMenu();
            System.out.println("Enter Your Choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 :
                    bidderConsole.runClient(name);
                    break;

                case 2 :
                    toExit = true;
                    break;
            }
        }
        
        scanner.close();
    }
}
