package mobilesim.console;

/**
 * The debug logger serves a central clearinghouse for coordinating error reporting
 * and general message information
 * @author Striegel
 *
 */
public class DebugLogger {
	public static final DebugLogger 	theLogger = new DebugLogger();
	
	// Important errors only, no warnings
	public static final int SIM_DEBUG_LEVEL_ERROR   = 0;
	
	// Warnings and errors
	public static final int SIM_DEBUG_LEVEL_WARNING = 1;
	public static final int SIM_DEBUG_LEVEL_NORMAL  = 1;
	
	public static final int SIM_DEBUG_LEVEL_VERBOSE = 5;
	
	// Note / capture all events that occur
	public static final int SIM_DEBUG_LEVEL_EVENTS = 7;
	
	// Trace means we watch most of the function calls
	public static final int SIM_DEBUG_LEVEL_TRACE   = 10;	

	DebugSetting	m_General;

	String 	m_sLogFile_Prepend;
	String	m_sLogFile_Normal;
	String 	m_sLogFile_Error;
	
	public DebugLogger ()
	{
		m_General = new DebugSetting();
		
		m_General.setDebugLevel(DebugLogger.SIM_DEBUG_LEVEL_NORMAL);
		m_General.setFlag_DisableConsole(false);
		m_General.setFlag_DisableLogFile(true);
				
		m_sLogFile_Prepend = "";
		m_sLogFile_Normal = "Log-MobileSim.txt";
		m_sLogFile_Error  = "Log-MobileSim-Error.txt";			
	}

	/** 
	 * The prepend is a string pre-pended to any file.  Examples might include a date or
	 * time stamp or other identifier for batch processing.  
	 * 
	 * @param sPrepend
	 */
	public void setLogFile_Prepend (String sPrepend)
	{
		m_sLogFile_Prepend = sPrepend;
	}
	
	public String getLogFile_Prepend ()
	{
		return m_sLogFile_Prepend;
	}
	
	public void setLogFile_Normal (String sNormal)
	{
		m_sLogFile_Normal = sNormal;
	}
	
	public String getLogFile_Normal ()
	{
		return m_sLogFile_Normal;
	}
	
	public void setLogFile_Error (String sError)
	{
		m_sLogFile_Error = sError;
	}
	
	public String getLogFile_Error ()
	{
		return m_sLogFile_Error;
	}
	
	/** 
	 * The general setting is the one universally applied unless an object explicitly requests
	 * a different debug setting
	 * 
	 * @return
	 */
	public DebugSetting getGeneralSetting ()
	{
		return m_General;
	}
	
	public void logInfo_Error (String sMessage, DebugSetting theItem)
	{
		logInfo(SIM_DEBUG_LEVEL_ERROR, sMessage, theItem);
	}
	
	public void logInfo_Error (String sMessage)
	{
		logInfo(SIM_DEBUG_LEVEL_ERROR, sMessage, null);
	}
	
	public void logInfo_Warning (String sMessage, DebugSetting theItem)
	{
		logInfo(SIM_DEBUG_LEVEL_WARNING, sMessage, theItem);
	}
	
	public void logInfo_Warning (String sMessage)
	{
		logInfo(SIM_DEBUG_LEVEL_WARNING, sMessage, null);
	}

	public void logInfo (int nLevel, String sMessage, DebugSetting theItem)
	{
		if(theItem != null)
		{
			// Use the item's debugging info
		}
		else
		{
			// Use the global debug setting values
			if(m_General.getDebugLevel() >= nLevel)
			{
				switch(nLevel)
				{
					case SIM_DEBUG_LEVEL_ERROR:
						if(!m_General.getFlag_DisableConsole())
						{
							System.err.println(sMessage);
						}
						
						if(!m_General.getFlag_DisableLogFile())
						{
							// TODO: Log to the file
						}
						
						break;
					default:
						if(!m_General.getFlag_DisableConsole())
						{
							System.out.println(sMessage);							
						}
						
						if(!m_General.getFlag_DisableLogFile())
						{
							// TODO: Log to the file
						}
						
						break;
				}
			}
			else
			{
				// Nope, just ignore it
			}
		}		
	}	
}
