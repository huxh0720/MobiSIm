package mobilesim.core;

/** This is an observed instance of proximity
 * 
 * @author Striegel
 *
 */
public class ProximityInstance {

	// Type
	// Who is it via a string name (may or may not be resolvable)
	// MAC address
	// Signal strength
		
	// Optional
	// Source node (who did we read this from) - provenance
	// When did it happen
	
	// Type (0 = Bluetooth, 1 = WiFi, fix the enum later)
	int 		m_nType;
	
	// String name
	String 		m_sName;  
		
	// MAC address
	String		m_sMacAddress;
	// Maybe a byte representation??
	
	// Signal strength
	int			m_nSignalStrength;
	
	// When did it happen
	Double		m_fInstanceTime;
	
	/**
	 * 
	 */
	public ProximityInstance ()
	{
		m_nType = -1;
		m_sName = "";
		m_sMacAddress = "";
		m_nSignalStrength = 0;
		m_fInstanceTime = 0.0;
	}
	
	public void setType(int nType)
	{
		m_nType = nType;
	}
	
	public int getType()
	{
		return m_nType;
	}
	
	public void setName(String sName) 
	{
	    m_sName = sName;	
	}
	
	public String getName() 
	{
		return m_sName;
	}
	
	public void setMacAddress (String sMac)
	{
		m_sMacAddress = sMac;
	}
	
	public String getMacAddress ()
	{
		return m_sMacAddress;
	}
	
	public void setSignalStrength(int nValue) 
	{
		m_nSignalStrength = nValue;
	}
		
	public int getSignalStrength()
	{
		return m_nSignalStrength;
	}
	
	public void setInstanceTime(Double fTime)
	{
		m_fInstanceTime = fTime;
	}
	
	public Double getInstanceTime()
	{
		return m_fInstanceTime;
	}
}
