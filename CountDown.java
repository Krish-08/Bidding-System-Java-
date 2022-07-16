

public class CountDown implements Runnable {;
    
    public static int  countNumber=0;


    @Override
    public void run() {
        
        while (countNumber < 60){

            countNumber++;

            if ( CountDown.countNumber % 20 == 0) BidderHandler.messageToAllBidders("Time Remaining: " + (60 - CountDown.countNumber));

            // if (CountDown.countNumber == 60) {
            //     BidderHandler.messageToClient("In Place");
            // }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        BidderHandler.messageToAllBidders("Time is up! Enter 'show result' to get the result");
        
    }
    
}
