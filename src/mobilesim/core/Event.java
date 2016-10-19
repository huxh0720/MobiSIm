package mobilesim.core;


/** 
 * This is the root event class. The key aspects of an event are that if it contains an 
 * invocation time as well as the object that is associated with this particular event. When
 * the time arrives, the SimulationEngine pops the event off the front of the event queue and calls
 * the particular object that the event is referencing. That object is then responsible for 
 * reacting to that event. Timers are considered a special kind of event and have a specific
 * child dedicated to that purpose    
 * 
 * @author Striegel
 *
 */
public class Event implements Comparable<Event> {

	// The time (in seconds which the event will be processed. Ties are resolved on
	// a first come first serve basis
	private Double 	  m_fTime;              
	
	// The object that will be called when this particular event needs to be processed. 
	// This should not be null
	private SimObject m_BaseObject;       
	
	// An event ID solely for the purpose of tracking / troubleshooting
	private long 	  m_lEventID;           // event ID
	
	public Event ()
	{
		m_fTime = 0.0;
		m_BaseObject = null;

		m_lEventID = SimulationEngine.theEngine.getEventID();
		SimulationEngine.theEngine.incrementEventID();
	}

	@Override
	public int compareTo (Event e) 
	{
		final int BEFORE = -1;
		final int SIMULT = 0;
		final int AFTER = 1;
		
		if(this.getTime() < e.getTime()) 
		{
			return BEFORE;
		} 
		else if(this.getTime() == e.getTime()) 
		{
			return SIMULT;
		} 
		else 
		{
			return AFTER;
		}
	}
	
	/**
	 * Set the time at which this event will occur
	 * @param fTime the time (in seconds) where the event will occur
	 */
	public void setTime (Double fTime)
	{
		m_fTime = fTime;
	}
	
	/**
	 * Sets the event time specified in string format
	 * @param sTime
	 */
	public void setTime (String sTime)
	{
		// TODO: Fill in a conversion from a string for reading a CSV
	}
	
	/**
	 * Retrieve the time that this event will occur
	 * @return Time the event will occur (in seconds)
	 */
	public Double getTime ()
	{
		return m_fTime;
	}
	
	/**
	 * Sets the base object that will be called when the event is processed
	 * @param theObj
	 */
	public void setSimObject (SimObject theObj)
	{
		m_BaseObject = theObj;
	}
	
	/**
	 * Retrieve a reference to the object that will be called when the event is processed
	 * @return Reference to the object, null if set, a valid reference otherwise. 
	 */
	public SimObject getSimObject ()
	{
		return m_BaseObject;
	}
	
	public void dumpFullObject ()
	{
		System.out.println("* Debug dump of object " + this);
	}
	
	/**
	 * Retrieve a summary of the contents of this event that should not contain a line break
	 * 
	 * @return Formatted string for the event which defaults to the toString result for the class
	 */
	public String getSummary_Simple ()
	{
		return this.toString();
	}
}
