package mobilesim.core;

/** 
 * This is a proximity event in that a node has sensed other nearby nodes or
 *  access points nearby
 * 
 * @author Striegel
 */
public class EventProximitySense extends Event {

	// Remember it also contains m_BaseObject inherited from the parent class - Event
	// the m_BaseObject is the object associated with this event and can be resolved by name
	
	ListProximityInstances 		m_ProxInstances;  // the proximity instances of the m_BaseObject
	
	/**
	 * Constructor
	 */
	public EventProximitySense ()
	{
		m_ProxInstances = new ListProximityInstances();
	}
	
	/**
	 * 
	 */
	public void debugToConsole_Short ()
	{
		System.out.println("(t=" + this.getTime().toString() + ") Proximity Sense Event with " + m_ProxInstances.size() + " proximity events");		
	}
	
	/**
	 * @return
	 */
	public ListProximityInstances getProximityInstances ()
	{
		return m_ProxInstances;
	}

	/**
	 * Add a proximity instance into current instance collection of the event.
	 * Typically gets called by the SimEngine for creating event and then add
	 * it into the priority queue
	 * @param theInstance
	 * @return
	 */
	public boolean addProximityInstance (ProximityInstance theInstance)
	{
		// Sanity check
		// 	1. avoid duplicates
		//	2. only add "socs" node
		String theName = theInstance.getName();
		
		if(m_ProxInstances.containsKey(theName) || !theName.contains("socs"))
		{
			//System.err.println("*** In addProximityInstance: tried add an instance that already exists.");
			//either the node already exists in list, or it is not a valid "socs" device. 
			return false;
		} 
		else 
		{
			 m_ProxInstances.put(theName, theInstance);
			 return true;
		}
	}
	
}
