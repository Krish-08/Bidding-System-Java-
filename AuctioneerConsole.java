
import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
// import java.util.concurrent.TimeUnit;
import java.util.Scanner;


public class AuctioneerConsole {
    public static ArrayList <FineList> fineList = new ArrayList<FineList>();
    public static ArrayList <ProductList> productLists = new ArrayList<ProductList>();
    public static ArrayList <String> productNotSoldList = new ArrayList<String>();
    public static ArrayList <UserBids> userBids;
    public static String deadLine;
    public static boolean isCountDownMethod = true ;


    
   
    private  void runServer(int startingBid , String productName) throws IOException {
        ExecutorService executorService= Executors.newFixedThreadPool(20);
        ArrayList <BidderHandler> bidders = new ArrayList<>();
        ServerSocket serverSocket = new ServerSocket(3000);
        CountDown countDownThread = new CountDown();
        userBids = new ArrayList<>();
        System.out.println("------------------------------------");
        System.out.println("[Auctioneer's SERVER RUNNING]");
        System.out.println("Waiting for bidders to join");
        System.out.println("------------------------------------");
        System.out.println();
        BidderHandler.maxBid = startingBid;
        CanServerTerminate canServerTerminate = new CanServerTerminate(serverSocket);
        boolean isCountDownStarted = false;

        

        CountDown.countNumber = 0;
        CanServerTerminate.timeOutNumber = 0;
        
        new Thread(canServerTerminate).start();

        while ( !BidderHandler.isOverForAll && CanServerTerminate.timeOutNumber <= 119)   {
            System.out.println("Waiting for Connection");
            try {
                Socket socket = serverSocket.accept();

                if (isCountDownMethod && !isCountDownStarted) {

                    new Thread(countDownThread).start();
                    isCountDownStarted = true;
                }

                BidderHandler bidderThread = new BidderHandler(socket , bidders , productName);
                bidders.add(bidderThread);
            
            
        
                executorService.execute(bidderThread);
      

            }

            catch (Exception e) {
                
            }
            
            
            

        }
        executorService.shutdown();
        System.out.println("Auction Over");
        // try {
        //     while ( !executorService.awaitTermination(50, TimeUnit.MILLISECONDS) ) {
                
        //     }
        // } catch (InterruptedException e) {
            
        //     System.out.println("Error in waiting for threads to finish");
        // }
        serverSocket.close();
        

    }
    
    private  void showMenu() {
        System.out.println("------------------------------");
        System.out.println("1. Start Auction");
        System.out.println("2. Show Product Solded");
        System.out.println("3. Show Fines");
        System.out.println("4. Show Product Not Solded");
        System.out.println("5. Exit");
        System.out.println("------------------------------");
    }
    public static void main(String[] args) throws IOException{
        AuctioneerConsole auctioneerConsole = new AuctioneerConsole();
        Scanner scanner = new Scanner(System.in);
        
        boolean isAuctionOver = false;

        while ( !isAuctionOver ) {
            new AuctioneerConsole().showMenu();
            System.out.print("Enter Your Choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("------------------------------------");
                    System.out.println("1.CountDown Auction");
                    System.out.println("2.Fixed Time Auction");
                    System.out.println("------------------------------------");
                    System.out.print("Enter Choice: ");
                    choice = scanner.nextInt();
                    switch (choice) {
                        case 1 : 
                            isCountDownMethod = true;
                            BidderHandler.isOverForAll = false;
                            scanner.nextLine();
                            System.out.print("Enter the product name: ");
                            String productName = scanner.nextLine();
                            System.out.print("Enter the starting bid price: ");
                            int startingBid = scanner.nextInt();
                            
                
                            auctioneerConsole.runServer(startingBid , productName );

                            if (!BidderHandler.isOverForAll) {
                                System.out.println("The Product is Not Sold");
                                productNotSoldList.add(productName);

                            }
                            else {
                               System.out.println("The Product is Sold");
                            }

                            break;
 
                        case 2 :
                            BidderHandler.isOverForAll = false;
                            isCountDownMethod = false;
                            deadLine= " ";
                            scanner.nextLine();
                            System.out.print("Enter the name of the product: ");
                            productName = scanner.nextLine();
                            System.out.print("Enter the initial price of the product: ");
                            startingBid = scanner.nextInt();
                            scanner.nextLine();
                            System.out.println("Enter the deadline of the auction[dd-MM-yyyy HH:mm:ss]:");
                            deadLine = scanner.nextLine();
                            while ( !deadLine.matches("^[0-9]{2}-[0-9]{2}-[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}$") ) {
                                System.out.println("Invalid deadline format");
                                System.out.println("Enter the deadline of the auction[dd-MM-yyyy HH:mm:ss]:");
                                deadLine = scanner.nextLine();
                            }
                            System.out.println(deadLine);
                            auctioneerConsole.runServer(startingBid , productName );
                            break;

                        default:
                            break;
                    }
                    break;
                
                case 2 :
                    if (productLists.size() == 0){
                        System.out.println("No product solded yet");
                    }

                    else{
                        System.out.println("------------------------------------------------------------------------------------");
                        for (ProductList productList : productLists) {
                            System.out.println("The product " + productList.productName + " was bought at Rs: " + productList.finalPrice + " By " + productList.boughtBy + ".");
                            
                        }
                        System.out.println("------------------------------------------------------------------------------------");
                    }
                    System.out.println();
                    break;
                
                case 3 :
                    
                    if (fineList.size() == 0) {
                        System.out.println("No Fines");
                    }

                    else {
                        System.out.println("------------------------------------------------------------------------------------");
                        for (FineList fineList : fineList) {
                            System.out.println(fineList.bidderName + " has fine of Rs: " + fineList.fine + " for rejecting the product " + fineList.productName + " after winning the bid.");
                            
                        }
                        System.out.println("------------------------------------------------------------------------------------");
                    }
                    System.out.println();
                    break;

                case 4 :
                    if (productNotSoldList.size() == 0) {
                        System.out.println("No Unsolded Products");
                    }

                    else {
                        System.out.println("------------------------------------------------------------------------------------");
                        for (String notSolded : productNotSoldList) {
                           System.out.println("The product " + notSolded + " went unsold in the auction.");
                            
                        }
                        System.out.println("------------------------------------------------------------------------------------");
                    }
                    System.out.println();
                    
                    break;
            }



        }
        scanner.close();

    }
}