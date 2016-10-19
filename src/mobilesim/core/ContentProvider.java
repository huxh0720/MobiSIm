package mobilesim.core;

import java.util.*;

/** 
 * A content provider serves up content that the mobile nodes and user elements may
 * wish to download
 * 
 * @author Striegel
 *
 */
public class ContentProvider extends SimObject {
	Double		m_fRTT;
	int			m_nMode;
	
	public static final int		MODE_CONTENTPROVIDER_NONE = 0;
	public static final int 	MODE_CONTENTPROVIDER_SERVER = 1;
	public static final int 	MODE_CONTENTPROVIDER_CLOUD = 2;
	public static final int 	MODE_CONTENTPROVIDER_STREAM = 3;
	public static final int 	MODE_CONTENTPROVIDER_TRANSMOGRIFY = 4;
	
	public static final String  DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
	
	public static final String	NAME_CONTENTPROVIDER = "CP";
	
	public ContentProvider ()
	{
		m_fRTT = 0.0;
		m_nMode = MODE_CONTENTPROVIDER_NONE;
	}
	
	public boolean initialize ()
	{
		enableTimers();
		initializeStatistics();
		
		return true;
	}
	
	/** 
	 * Enable timers for the content provider
	 * 
	 * @return
	 */
	public boolean enableTimers ()
	{
		return true;
	}
	
	public boolean initializeStatistics ()
	{
		return true;
	}
	
	public void resetStatistics ()
	{
		
	}
	
	public boolean setRTT (String sRTT)
	{
		return setRTT(Double.parseDouble(sRTT));
	}
	
	public boolean setRTT (Double fRTT)
	{
		m_fRTT = fRTT;
		return true;
	}
	
	public Double getRTT ()
	{
		return m_fRTT;
	}
	
	public boolean setMode (int nMode)
	{
		m_nMode = nMode;
		return true;
	}
	
	public int getMode ()
	{
		return m_nMode;
	}
}
