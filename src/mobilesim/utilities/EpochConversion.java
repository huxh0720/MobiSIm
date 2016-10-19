package mobilesim.utilities;

import java.util.Date; 
import java.text.*;

public class EpochConversion {
    private static String format = "MM/dd/yyyy HH:mm:ss";
	
	public static Double getTimeIndex (long epochTime, int interval)
	{
		Double index;
		String date = new SimpleDateFormat(format).format(new Date(epochTime*1000));
		
		int hr = Integer.parseInt(date.substring(11, 13));
		int mi = Integer.parseInt(date.substring(14, 16));
		
		index = (double) (60*hr + mi) / interval;
		
		return index;
	}
}
