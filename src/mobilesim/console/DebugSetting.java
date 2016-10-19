package mobilesim.console;

/** 
 * This allows for fine-grained debug settings to be passed along.  An object can
 * be configured to output to the console, to a log file, to both, or perhaps can
 * allow for a conditional switch that triggers enhanced debugging
 * 
 * @author Striegel
 *
 */

public class DebugSetting {
	int			m_nDebugLevel;
	
	int			m_nDebug_Console;
	int			m_nDebug_LogFile;
	boolean		m_bDebug_Triggered;
	boolean 	m_bDisable_Console;
	boolean 	m_bDisable_LogFile;
	
	public DebugSetting ()
	{
		m_nDebugLevel = DebugLogger.SIM_DEBUG_LEVEL_NORMAL;
		
		m_bDisable_Console = false;
		m_bDisable_LogFile = true;	
	}
	
	public void setFlag_DisableConsole (boolean bFlag)
	{
		m_bDisable_Console = bFlag;
	}
	
	public boolean getFlag_DisableConsole ()
	{
		return m_bDisable_Console;
	}
	
	public void setFlag_DisableLogFile (boolean bFlag)
	{
		m_bDisable_LogFile = bFlag;
	}
		
	public boolean getFlag_DisableLogFile ()
	{
		return m_bDisable_LogFile;
	}
	
	public void setDebugLevel (int nLevel)
	{
		m_nDebugLevel = nLevel;
		m_nDebug_Console = nLevel;
		m_nDebug_LogFile = nLevel;
	}
	
	public void setDebugLevel (int nGeneral, int nConsole, int nLogFile)
	{
		m_nDebugLevel = nGeneral;
		m_nDebug_Console = nConsole;
		m_nDebug_LogFile = nLogFile;
	}
	
	public int getDebugLevel ()
	{
		return m_nDebugLevel;
	}
	
	public void setDebugLevel_Console (int nLevel)
	{
		m_nDebug_Console = nLevel;
	}
	
	public int getDebugLevel_Console ()
	{
		return m_nDebug_Console;		
	}
	
	public void setDebugLevel_LogFile (int nLevel)
	{
		m_nDebug_LogFile = nLevel;
	}
	
	public int getDebugLevel_LogFile ()
	{
		return m_nDebug_LogFile;
	}
}
