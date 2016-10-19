package mobilesim.statistics;

/** 
 * One individual statistic field which is simply a counter / tally mechanism that is
 * adjusted typically upwards
 * 
 * @author Striegel
 *
 */
public class StatisticCounter extends StatisticField {
	private Double m_fValue;
	
	StatisticCounter ()
	{
		m_fValue = 0.0;
	}

	/** 
	 * Set the value in the counter to a specific value
	 * 
	 * @param fValue The new value for the counter
	 */
	public void setValue (Double fValue)
	{
		m_fValue = fValue;
	}
	
	/** 
	 * Retrieve the value associated with the counter
	 * 
	 * @return The value held in the field
	 */
	public Double getValue ()
	{
		return m_fValue;
	}
	
	/** 
	 * Reset the value in the counter to zero
	 */
	public void resetValue ()
	{
		m_fValue = 0.0;
	}

	/** 
	 * Adjust the value by a certain amount, if the passed-in amount is negative then 
	 * represent a decrease, otherwise it's an increase
	 * 
	 * @param fValue The amount to add to the current counter value
	 */
	public void changeValue (Double fValue)
	{
		m_fValue += fValue;
	}
}
