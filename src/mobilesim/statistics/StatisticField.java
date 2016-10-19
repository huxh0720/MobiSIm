package mobilesim.statistics;

/** 
 * One individual statistical field
 * 
 * @author Striegel
 *
 */
public class StatisticField {
	private String 	m_sName;
	
	public StatisticField ()
	{
		m_sName = "";
	}
	
	StatisticField (String sName)
	{
		m_sName = sName;
	}
	
	/** 
	 * Set the name of the statistical field
	 * 
	 * @param sName The new name for the field
	 */
	public void setName (String sName)
	{
		m_sName = sName;
	}
	
	/** 
	 * Get the name of the statistical field
	 * 
	 * @return The name of the counter field
	 */
	public String getName ()
	{
		return m_sName;
	}
	
}
