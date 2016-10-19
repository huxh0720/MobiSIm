package mobilesim.core;

/**
 * A timer is geared for either a periodic set of invocations or a fixed number of invocation. 
 * The default invocation is 1. It is possible to set a timer with an interval of zero (process 
 * now but after all events waiting in the queue). 
 * @author Striegel
 *
 */
public class EventTimerSimObject extends Event {
	Double		m_fInterval;
	int			m_nRemainingInvocations;
	int			m_nTimerType;
	
	// Meta data is a free form way to pass information
	String		m_sMetaData;
	
	public EventTimerSimObject ()
	{
		m_fInterval = 1.0;
		m_nRemainingInvocations = 1;
		m_nTimerType = 0;
		
		m_sMetaData = "";
	}
	
	/**
	 * Adjust the timer by one interval forward in time. This must be done before the timer is
	 * added to the global processing queue. 
	 */
	public void adjustTimeByInterval ()
	{
		this.setTime(this.getTime() + getInterval());
	}
	
	public String getSummary_Simple ()
	{
		String sSummary; 
		sSummary = " Timer (Type = " + m_nTimerType + ", Interval = " + m_fInterval.toString() + ",";
		
		if(m_nRemainingInvocations < 0)
		{
			sSummary += " Infinite,";
		}
		else 
		{
			sSummary += m_nRemainingInvocations + " left,";
		}
					
		if(getSimObject() == null)
		{
			sSummary += "Callback = None";
		}
		else 
		{
			sSummary += "Callback = " + this.getSimObject().getName();
		}
		
		sSummary += ")";
		
		return sSummary;
	}
	
	public boolean setInterval (Double fInterval) 
	{
		if(fInterval < 0)
		{
			System.err.println("* Error: Timer interval was negative (" + fInterval + "), ignoring timer.");
			return false;
		}
		m_fInterval = fInterval;
		return true;
	}
	
	public Double getInterval ()
	{
		return m_fInterval;
	}
	
	public void addInvocations (int nAddInvocations)
	{
		m_nRemainingInvocations += nAddInvocations; 
	}
	
	public void setInvocations (int nInvocations)
	{
		m_nRemainingInvocations = nInvocations;
	}
	
	public void enableInfiniteInvocations ()
	{
		m_nRemainingInvocations = -1;
	}
	
	public boolean isInfinite ()
	{
		if(m_nRemainingInvocations < 0)
		{
			return true;
		} 
		else 
		{
			return false;
		}
	}
	
	public boolean hasMetaData ()
	{
		if(m_sMetaData.isEmpty()) {
			return false;
		} 
		else 
		{
			return true;
		}
	}
	
	public void setMetaData (String sMeta)
	{
		m_sMetaData = sMeta;
	}
	
	public String getMetaData ()
	{
		return m_sMetaData;
	}
	
	public void setType (int nType)
	{
		m_nTimerType = nType;
	}
	
	public int getType ()
	{
		return m_nTimerType;
	}
	
	public boolean cancelTimer ()
	{
		// Attempts to cancel this particular timer
		// TODO: this is kind of a bad idea usually
		return false;
	}
	
	/**
	 * Check to see if this timer should be renewed. As long as there is more than one
	 * invocations remaining, the timer will set its trigger time to NOW plus the interval
	 * and reinsert itself into the overall event queue for processing
	 * 
	 * @return True if re-enabled, false otherwise
	 */
	public boolean doTimerRenewal ()
	{
		if(!isInfinite())
		{
			m_nRemainingInvocations--;
			
			if(m_nRemainingInvocations <= 0)
			{
				return false;
			}
		}
		
		// Trigger again at NOW + interval
		this.setTime(getInterval() + SimulationEngine.theEngine.getTime());
		SimulationEngine.theEngine.addEvent(this);
		return true;
	}
}
