import java.net.ServerSocket;

public class CanServerTerminate implements Runnable {
    static int timeOutNumber = 0;
    ServerSocket serverSocket;
    CanServerTerminate ( ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
       
        while ( true) {
            try {
                Thread.sleep(1000);
                timeOutNumber ++;
                System.out.println("timeOutNumber: " + timeOutNumber);
                if (BidderHandler.isOverForAll || timeOutNumber > 120) {
                    
                    try{
                        serverSocket.close();
                        break;
                    }
                    catch (Exception e) {
                        break;
                    }
                    
                }
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
    }
    
}
