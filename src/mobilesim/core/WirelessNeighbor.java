package mobilesim.core;

/** 
 * A wireless neighbor is the collection of data / state that a mobile node keeps with
 * regards to its neighbors. This is one particular neighbor.
 * 
 * @author Striegel
 */
public class WirelessNeighbor {
	// Which neighbor by string name is sensed?
	// we can use the SimEngine to resolve which SimObject this neighbor was sensed by
	// by passing the string name. 
	String		m_sName;
	
	// What was the last recorded / sensed instance of it? Need to with dangle with ProxmityInstance?
	
	// Historical data
	
	// 1. When was the first time this neighbor was sensed?
	Double		m_fFirstSensed;
	
	// 2. When was we latest time this neighbor was sensed? 
	Double		m_fLastSensed; 
	
	// 3. What is the most-recent detected rssi
	int			m_nSignalStrength;
	
	// 4. Whether the neighbor is currently active / present
	boolean		m_bPresenceFlag;
	
	public WirelessNeighbor ()
	{
		m_sName = "";
		m_fFirstSensed = -1.0;
		m_fLastSensed = -1.0;
		m_nSignalStrength = 0;
		m_bPresenceFlag = false;
	}
	
	public void setName (String sName) 
	{
		m_sName = sName;
	}
	
	public String getName ()
	{
		return m_sName;
	}
	
	public void setTime_FirstSensed (Double fTime)
	{
		m_fFirstSensed = fTime;
	}
	
	public Double getTime_FirstSensed ()
	{
		return m_fFirstSensed;
	}
	
	public void setTime_LastSensed (Double fTime)
	{
		m_fLastSensed = fTime;
	}
	
	public Double getTime_LastSensed ()
	{
		return m_fLastSensed;
	}
	
	public void setSignalStrength (int nSignalStrength)
	{
		m_nSignalStrength = nSignalStrength;
	}
	
	public int getSignalStrength ()
	{
		return m_nSignalStrength;
	}
	
	public void setPresenceFlag (boolean bFlag)
	{
		m_bPresenceFlag = bFlag;
	}
	
	public boolean getPresenceFlag ()
	{
		return m_bPresenceFlag;
	}
			
	public boolean isPresent ()
	{
		return m_bPresenceFlag;
	}
}
