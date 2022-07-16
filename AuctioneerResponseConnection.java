import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStreamReader;

public class AuctioneerResponseConnection implements Runnable {
    
    private Socket serverSocket;
    private BufferedReader input;
    private PrintWriter out;
    boolean isAuctionOver = false;  
    public AuctioneerResponseConnection(Socket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
        input = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream()));
        out = new PrintWriter(this.serverSocket.getOutputStream(), true);

    }

    @Override
    public void run() {
       
            try {
                while ( !isAuctionOver ) {
                    String auctioneerResponse = input.readLine();
                    
                    try {
                        if (auctioneerResponse.equals("Auction Over") || auctioneerResponse.equals("Thank you for attending the auction")) {
                            isAuctionOver = true;
                            System.out.println( "Enter 'quit' to exit the auction console");
                        }
                        System.out.println(auctioneerResponse);
                    }
                    catch (Exception e) {
                        
                    }

                }
                    
            }

            catch (IOException e) {
                
            }
            
            finally {
                try {
                    input.close();
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing socket in auctioneer response connection");
                
                }
                out.close();
            }
        
    }
}
