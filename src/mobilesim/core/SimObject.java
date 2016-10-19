package mobilesim.core;

import java.io.*;
import mobilesim.console.*;

/** 
 * This is the root object class, base class for all simulation objects
 * 
 * @author Striegel
 *
 */
public class SimObject {

	private String			m_sName;	  // string name of the simulation object
	
	private DebugSetting	m_Debug;
	
	private long			m_lObjectID;  // object ID
	
	/**
	 * Constructor 
	 */
	public SimObject ()
	{
		m_sName = "";
		m_Debug = null;
		m_lObjectID = SimulationEngine.theEngine.getObjectID();
		SimulationEngine.theEngine.incrementObjectID();
	}
	
	/**
	 * Sets the object's string name
	 * @param sName
	 */
	public void setName (String sName)
	{
		if(sName != null)
		{
			m_sName = sName;
		}
	}
	
	/**
	 * @return
	 */
	public String getName ()
	{
		return m_sName;
	}
	
	public DebugSetting getDebugSetting ()
	{
		return m_Debug;
	}
	
	/**
	 * The base functionality for an event when it is processed. This should never
	 * be called except by the overall SimulationEngine object. Child classes must not
	 * override this function. 
	 * @param theEvent
	 * @return
	 */
	public boolean processEvent_Base (Event theEvent)
	{
		if(theEvent instanceof EventTimerSimObject)
		{
			return processTimer_Base((EventTimerSimObject) theEvent);
		}
		
		return processEvent(theEvent);
	}
	
	/**
	 * The simple event placeholder for processing events. This is the function that child
	 * classes should override to process events. 
	 * @param theEvent
	 * @return
	 */
	public boolean processEvent (Event theEvent)
	{
		return false;		
	}
	
	/**
	 * The base functionality for a timer when it is processed. This should never
	 * be called except by the processEvent_Base function. Child classes must not
	 * override this function
	 * @param theTimer
	 * @return
	 */
	public boolean processTimer_Base (EventTimerSimObject theTimer)
	{
		theTimer.doTimerRenewal();		
		return processTimer(theTimer);
	}
	
	/**
	 * The simple placeholder for processing timer. This is the function that
	 * child classes should override to process timers. 
	 * @param theTimer
	 * @return
	 */
	public boolean processTimer (EventTimerSimObject theTimer)
	{
		return false;
	}
	
	public boolean initializeBase ()
	{
		initialize();
		return true;
	}
	
	// Override this function
	public boolean initialize ()
	{
		return true;
	}
	
	public boolean summarizeBase ()
	{
		return summarize();
	}
	
	public boolean summarizeBase (boolean bFieldsOnly, BufferedWriter theWriter)
	{
		return summarize(bFieldsOnly, theWriter);
	}
	
	public boolean summarizeBase (boolean bFieldsOnly, BufferedWriter theWriter, String sField)
	{
		return summarize(bFieldsOnly, theWriter, sField);
	}
	
	// Override this function
	public boolean summarize ()
	{
		return true;
	}
	
	public boolean summarize (boolean bFieldsOnly, BufferedWriter theWriter)
	{
		return true;
	}
	
	public boolean summarize (boolean bFieldsOnly, BufferedWriter theWriter, String sField)
	{
		return true;
	}
}
