package mobilesim.statistics;

import java.util.Hashtable;
import java.util.Set;

/** 
 * A group of statistic counters that are accessible via the named field identifier
 * 
 * @author Striegel
 */
public class StatisticGroup extends Hashtable<String, StatisticCounter> {
	
	public StatisticGroup ()
	{
	}

	// This assumes that everything stays in the right order (that might be a tall leap of faith)
	public String  getSummaryCSV_Fields ()
	{
		Set<String> 	theKeys;
		String 			sResult;
		
		theKeys = this.keySet();
		
		sResult = "";
		
		for(String sField : theKeys)
		{
			sResult += sField + ",";
		}
		
		return sResult;
	}

	// This assumes that everything stays in the right order (that might be a tall leap of faith)
	public String  getSummaryCSV ()
	{
		Set<String> 	theKeys;
		String 			sResult;
		
		theKeys = this.keySet();
		
		sResult = "";
		
		for(String sField : theKeys)
		{
			sResult += this.get(sField).getValue().toString() + ",";
		}
		
		return sResult;
	}
	
	public boolean dumpToConsole ()
	{
		Set<String> 	theKeys;
		
		theKeys = this.keySet();
		
		for(String sField : theKeys)
		{
			System.out.println("   " + sField + " -> " + this.get(sField).getValue());
		}
		
		return true;
	}
	
	/** 
	 * Reset an individual field to zero 
	 * 
	 * @param sName
	 * @return
	 */
	public boolean resetValue (String sName)
	{
		try
		{
			this.get(sName).resetValue();
		}
		catch (Exception e)
		{
			System.err.println("* Error in resetting statistic value to zero, field = " + sName);
			System.err.println(e);
			return false;
		}
		
		return true;
	}
	
	/** 
	 * Adjust one of the statistical counter fields appropriately
	 * 
	 * @param sName
	 * @param fValue
	 * @return
	 */
	public boolean adjustValue (String sName, Double fValue)
	{
		try
		{
			this.get(sName).changeValue(fValue);
		}
		catch (Exception e)
		{
			System.err.println("* Error in adjusting statistic value, field = " + sName);
			System.err.println(e);
			return false;
		}
		
		return true;
	}
	
	/** 
	 * Initialize a new element in the statistic group with a starting value of
	 * zero
	 * @param sName The name of the field (must be unique and a non-zero length string)
	 * @return True if successful, false otherwise
	 */
	public boolean initializeCounter (String sName)
	{
		if(sName.isEmpty())
		{
			return false;
		}
		
		if(this.containsKey(sName))
		{
			System.err.println("* Error: Cannot initialize field of name " + sName + ", the field already exists");
			return false;
		}
		
		StatisticCounter		theCounter;

		theCounter = new StatisticCounter();
		theCounter.setName(sName);
		theCounter.resetValue();

		this.put(theCounter.getName(), theCounter);
		
		return true;
	}
}
