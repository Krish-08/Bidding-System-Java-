
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Comparator;



public class BidderHandler implements Runnable {

    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter out;
    private static ArrayList<BidderHandler> bidders;
    static int maxBid;
    public static int uniqueId = 0;
    boolean isAuctionOver = false;
    private String productName;
    static boolean isOverForAll;
    boolean hasResponed = false;
    boolean isHighestBidder = false;

    public BidderHandler(Socket clientSocket , ArrayList <BidderHandler> biddersArray , String productName) throws IOException {
        this.clientSocket = clientSocket;
        bidders = biddersArray;
        this.productName = productName;
        input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        out = new PrintWriter(this.clientSocket.getOutputStream(), true);
    }

    // Auctioneer's response to all bidders
    public static  void messageToAllBidders(String message) {
        for (BidderHandler bidder : bidders) {
            bidder.out.println("[Auctioneer] > " + message);
        }
    }

    // Auctioneer's response to the bidder
    public void messageToClient(String message) {
        
        out.println("[Auctioneer] > " + message);
    }

    public void messageToAllBiddersExceptCurrent(String message) {
        for (BidderHandler bidder : bidders) {
            if (bidder != this) {
                bidder.out.println("[Auctioneer] > " + message);
            }
        }
    }

    // For sorting array list of bidders by their bid price in descending order
    class sortUserBids implements Comparator<UserBids> {

        @Override
        public int compare(UserBids userBid1, UserBids userBid2) {
            if (userBid1.highestBiddingAmount > userBid2.highestBiddingAmount) return -1;
            else return 1;
           
        }
        
    }

    // To update the max bid amount in the array list of bidders
    public void updateBidUsers(int bid , String bidderId) {
        
        for ( UserBids userBid : AuctioneerConsole.userBids ) {
            if ( userBid.bidderID.equals(bidderId)  ) {
                userBid.highestBiddingAmount = bid;
            }
        }
        
    }

   


    @Override
    public void run()  {
        String name =" ";
        try {
            String welcomeMessage = "Welcome to the auction house!!";
            messageToClient(welcomeMessage);
            if (!AuctioneerConsole.isCountDownMethod) {
                messageToClient("deadline is " + AuctioneerConsole.deadLine);
            }
            messageToClient("Prodct Name: " + productName);
            messageToClient("Intial Bid : " + maxBid);
            int bid = 0;
            name = input.readLine();
            
            System.out.println("Bidder Connected: " + name);
            
            AuctioneerConsole.userBids.add(new UserBids(name,0, Thread.currentThread().toString()));
            
            while ( !isAuctionOver ) {
              
                String bidderResponse = input.readLine();
                CanServerTerminate.timeOutNumber = 0;

                if ( isOverForAll ) {
                    isAuctionOver = true;
                    break;
                }
                
                if (hasResponed) {
                    isAuctionOver = true;
                    break;
                }
                if (!AuctioneerConsole.isCountDownMethod && AuctioneerConsole.deadLine.compareTo(new DateParser().parseDate()) <= 0) {
                    messageToAllBidders("deadline reached");
                    messageToAllBiddersExceptCurrent("type 'result' to check the result");
                    bidderResponse = "result";
                }

                if (AuctioneerConsole.isCountDownMethod && CountDown.countNumber >= 60 && !bidderResponse.equals("show result")) {
                    bidderResponse = "Invalid" ;
                }

                if (bid == maxBid) {
                    isHighestBidder = true;
                }
                else {
                    isHighestBidder = false;
                }

                try {
                    bid = Integer.parseInt(bidderResponse);

                    
                    
                    if (bid > maxBid) {
                        CountDown.countNumber=0;
                        maxBid = bid;
                        System.out.println("[Auctioneer] > " + name + " bids : " + bid);
                        System.out.println("[Auctioneer] > Current Bid is " + bid);
                        System.out.println();
                        

                        updateBidUsers(bid , Thread.currentThread().toString());
                        Collections.sort(AuctioneerConsole.userBids , new sortUserBids());
                        
        
                        messageToAllBidders("Current Bid is " + bidderResponse);
                    }
                    else {
                        messageToClient("Bid is low than current bid");
                    }

                    
                    

                }
                catch (NumberFormatException e) {
                    try{

                        if (  (AuctioneerConsole.isCountDownMethod &&  bidderResponse.equals("show result") && CountDown.countNumber >= 60) 
                                || (!AuctioneerConsole.isCountDownMethod && AuctioneerConsole.deadLine.compareTo(new DateParser().parseDate()) <= 0 )) {
                            
                            if (bid == maxBid && name.equals(AuctioneerConsole.userBids.get(0).bidderName)) {
                                messageToClient("You won the Bid");
                                messageToAllBiddersExceptCurrent("You lost the Bid");
                                messageToAllBidders("Auction Over type 'quit' to exit");

                                if (AuctioneerConsole.isCountDownMethod) acceptOrNot();
                                else {
                                    AuctioneerConsole.productLists.add(new ProductList(productName, maxBid, name));
                                    isOverForAll = true;
                                }

                            }
                            else {
                                messageToClient("You lost the Bid");
                                if (AuctioneerConsole.isCountDownMethod) messageToClient("Waiting for the winner to accept the bid");
                                else break;
                            }
                            
                        }

                        else if (bidderResponse.equals("quit")) {
                            if (!isHighestBidder) {
                                messageToClient("Thank you for attending the auction");
                                updateBidUsers(0 , Thread.currentThread().toString());
                                isAuctionOver = true;
                                break;
                            }
                            else {
                                messageToClient("Since you are the highest bidder and quitting the auction ");
                                payFine();
                                updateBidUsers(0 , Thread.currentThread().toString());
                                isAuctionOver = true;
                                break;
                                
                            }
                            
                            
                        }

                        else if (bidderResponse.equals("Invalid")) {
                            messageToClient("Time Over Cannot Bid");
                        }
                        
                        else {
                            out.println("Invalid Bid");
                        }
                    }
                    catch (Exception e1) {
                        
                    }
                }
                
                

            }
            

        }

        catch (IOException e) {
             
        }

        finally {
            if (!isOverForAll){
                System.out.println();
                System.out.println("[Auctioneer] > Bidder Quited :" + name);
            }
            
            
            // messageToClient("Auction Over");
            out.close();
            try {
                clientSocket.close();
                input.close();
                
                
            } catch (Exception e) {
                System.out.println("client socket nit closed..");
            }
            
        }
        
    }
    
    // Method to add the bidder who reject their winning bid product to the array list of FinedBidders
    void payFine() {
        int fine = (int) ((int) maxBid * 0.25);
        messageToClient("You have to pay a fine of " + fine);
        AuctioneerConsole.fineList.add(new FineList(productName, fine , AuctioneerConsole.userBids.get(0).bidderName ));
    }


    // Method to ask the bidder whether he/she has accepted or not;
    private void acceptOrNot() {
        messageToClient("DO you want to accept the bid? (y/n)");
        messageToClient("If not accepted ...You will need to pay 30 % of the bid as fine");
        boolean isValid = false;
        hasResponed = true;
        try {
            while (!isValid) {
                String response = input.readLine();
                if (response.equals("y")) {
                    messageToAllBidders("bid is accepted");
                    messageToAllBidders("Auction over so enter 'quit' to exit the auction");
                    
                    AuctioneerConsole.productLists.add(new ProductList(productName , maxBid, AuctioneerConsole.userBids.get(0).bidderName));
                    isOverForAll = true;
                    isValid = true;
                    
                }
                else if (response.equals("n")) {
    
                    messageToAllBiddersExceptCurrent("Bid is not accepted");
                    payFine();
                    AuctioneerConsole.userBids.remove(0);
                    maxBid = AuctioneerConsole.userBids.get(0).highestBiddingAmount;
                    messageToAllBiddersExceptCurrent("type 'show result' to see if you have chance to win");
                    messageToClient("Auction Over so enter 'quit' to exit the auction");
                    isValid = true ;
                    
                    
                    
                }
                else {
                    messageToClient("Invalid Response");
                }
            }
           
        }
        catch (IOException e) {
            System.out.println("Error in accept or not function");
        }
        
    }
    
}
