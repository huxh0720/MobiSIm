package mobilesim.core;

/**
 * A data field represents a current scalar value for a particular device. It can be
 * thought of as a name, value pair with the addition of a time value and update counter
 * 
 * @author Striegel
 *
 */
public class DataField {
	int		m_nUpdates;		// how many times it's been updated
	Double	m_fLastUpdate;	// last time get updated
	String	m_sName;		// field name
	
	public DataField ()
	{
		m_nUpdates = 0;
		m_fLastUpdate = Double.MIN_VALUE;
		m_sName = "";
	}
	
	public void markUpdate (Double fTime)
	{
		incrementUpdate();
		m_fLastUpdate = fTime;
	}
	
	public void incrementUpdate ()
	{
		m_nUpdates++;
	}
	
	public void reset ()
	{
		m_nUpdates = 0;
		m_fLastUpdate = -1.0;
		resetValue();
	}
	
	public void tagWithCurrentTime ()
	{
	    m_fLastUpdate = SimulationEngine.theEngine.getTime();
	}
	
	public void setUpdateTime (Double fTime)
	{
		m_fLastUpdate = fTime;
	}
	
	public Double getUpdateTime ()
	{
		return m_fLastUpdate;
	}
	
	public void setName (String sName)
	{
		m_sName = sName;
	}
			
	public String getName ()
	{
		return m_sName;
	}
	
	protected void resetValue ()
	{
		// For override
	}
	
	public boolean setValue (String sValue)
	{
		// For override
		return false;
	}
	
	public boolean setValue (Double fValue)
	{
		// For override
		return false;
	}
	
	public String getValueString ()
	{
		// For override
		return "";
	}
	
	public Double getValueDouble ()
	{
		// For override
		return 0.0;
	}
	
	public DataField computeDifference (String sValue, Double fTime)
	{
		// For override
		// Compute the differences v.s. this particular string which hopefully
		// contains a value
		return null;
	}
}
