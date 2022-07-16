import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

    

class DateParser {

    public String parseDate() {
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        try {
            String currentDateString = formatter.format(new Date());
    
            return currentDateString;
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        return null;
        
    }
}