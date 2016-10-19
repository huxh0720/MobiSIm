package mobilesim.statistics;

import mobilesim.core.SimulationEngine;

/** 
 * The historical instance with one value and / or multiple historical values
 * 
 * @author Striegel
 *
 */
public class StatisticHistoricalInstance extends StatisticField {

	// When was the sample taken (simulation-wise)
	Double 	m_fSampleTime;
	
	// What range does this cover
	Double 	m_fRange;
	
	// The specific value (if applicable)
	Double  m_fValue;
	
	// Optionally we may also nest children in this instance as well (who could do the same)
	StatisticGroup 	m_ChildGroup;
	
	public StatisticHistoricalInstance () 
	{
		m_fSampleTime = -1.0;
		m_fRange = -1.0;
		m_fValue = 0.0;
		m_ChildGroup = null;
	}
	
	public boolean copyStatistic (StatisticGroup srcGroup, String sFiled)
	{
		// TODO: Error checking here
		StatisticCounter	theCounter;
		theCounter = srcGroup.get(sFiled);
		
		StatisticCounter	theCopiedCounter;
		
		theCopiedCounter = new StatisticCounter();
		theCopiedCounter.setName(theCounter.getName());
		theCopiedCounter.setValue(theCounter.getValue());
		
		m_ChildGroup.put(theCopiedCounter.getName(), theCopiedCounter);
		
		return true;
	}
	
	public boolean initializeNamedGroup (String sName, Double fRange)
	{
		setName(sName);
		setSampleTime(SimulationEngine.theEngine.getTime());
		setRange(fRange);
		
		m_ChildGroup = new StatisticGroup();
		
		return true;
	}
	
	/** 
	 * The value associated with this historical instance 
	 * 
	 * @param fValue
	 */
	public void setValue (Double fValue)
	{
		m_fValue = fValue;
	}
	
	/** 
	 * The value associated with this historical instance (may or may not be valid)
	 * 
	 * @return
	 */
	public Double getValue ()
	{
		return m_fValue;
	}
	
	/** 
	 * Set the time (simulation-wise) when this sample was gathered
	 * 
	 * @param fSampleTime
	 */
	public void setSampleTime (Double fSampleTime)
	{
		m_fSampleTime = fSampleTime;
	}
	
	public Double getSampleTime ()
	{
		return m_fSampleTime;
	}

	public void setRange (Double fSpan)
	{
		m_fRange = fSpan;
	}
	
	/** 
	 * Retrieve the range associated with this particular historical instance. The range is an attempt
	 * to describe (if applicable) the range of time over which this particular historical sample was
	 * gathered
	 * 
	 * @return
	 */
	public Double getRange ()
	{
		return m_fRange;
	}
	
	/** 
	 * Determine if this particular historical instance is based on a range of time. Range must always
	 * be positive denoting the time backwards from the sample time point on which this particular data
	 * was gathered / computed from.  For instance, if the instance has a sample time of X and a range of
	 * Y, the range means that the data was computed / gathered from time X-Y to time X.  
	 * @return True if it has a range, false if it does not
	 */
	public boolean hasRange ()
	{
		if(m_fRange > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/** 
	 * Retrieve the flag regarding if this instance has a child group
	 * 
	 * @return
	 */
	public boolean hasChildGroup ()
	{
		if(m_ChildGroup != null)
		{
			return true;			
		}
		else
		{
			return false;
		}			
	}
	
	public void setChildGroup (StatisticGroup theGroup)
	{
		m_ChildGroup = theGroup;
	}
	
	public StatisticGroup getChildGroup ()
	{
		return m_ChildGroup;
	}
	
	public boolean clearChildGroup ()
	{
		if(m_ChildGroup != null)
		{
			m_ChildGroup = null;
			return true;
		}
		else
		{
			return false;
		}
	}
}
