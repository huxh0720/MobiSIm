package mobilesim.core;

import java.util.*;
import java.io.*;

import mobilesim.console.DebugLogger;

/**
 * The SimulationEngine class is responsible for making the overall mobile simulation
 * go. It serves as the central rendezvous point for most objects and is the reference 
 * point for the current simulation time. Note that the SimulationEngine is not 
 * multi-threaded nor will it ever be as the intention is to preserve precise computation
 * order. 
 * 
 * @author Striegel
 */
public class SimulationEngine {

	public static SimulationEngine	theEngine = new SimulationEngine();
	
	PriorityQueue<Event>			m_EventQueue;    // event queue ordered by time
	
	Hashtable<String, SimObject>	m_SimObjects;    // each sim object should have a unique string name
	
	String							m_sCurrentDate;  // current simulation date, a day-by-day simulation
	
	Double							m_fCurrentTime;  // current simulation time
    // A unique enumerator for events generated over the course of the simulation
	long							m_lNextEventID;  
	// A unique enumerator for objects generated over the course of the simulation
	long							m_lNextObjectID;     
	
	boolean							m_bSimulationComplete;
	
	int								m_nCountMobileNodes;
	
	public static final int FILTER_RSSI = -100;
	public static final int FILTER_HOPS = 4000;
	public static final double FILTER_LACY = 120;
	
	// Simulation duration in days
	public static int SIMULATION_DURATION = 1;

	/**
	 * Constructor
	 */
	SimulationEngine ()
	{
		m_EventQueue = new PriorityQueue<Event>();	
		m_SimObjects = new Hashtable<String, SimObject>();
		m_sCurrentDate = "";
		m_fCurrentTime = 0.0;
		m_lNextEventID = 0;
		m_lNextObjectID = 0;
		m_bSimulationComplete = false;
		m_nCountMobileNodes = 0;
	}
	
	/**
	 * Reset the simulation engine to initial state
	 */
	public void resetSimulation ()
	{
		m_EventQueue.clear();
		m_SimObjects.clear();
		m_sCurrentDate = "";
        m_fCurrentTime = 0.0;
		m_lNextEventID = 0;
		m_lNextObjectID = 0;
		m_bSimulationComplete = false;
		m_nCountMobileNodes = 0;
	}
	
	/**
	 * Retrieve the enumerated ID for the next event. Make sure to increment the
	 * event ID when using this particular function just after invoking this call via
	 * incrementEventID
	 * @return
	 */
	public long getEventID ()
	{
		return m_lNextEventID;
	}
	
	/**
	 * Increment the enumerated value for the event ID
	 */
	public void incrementEventID ()
	{
		m_lNextEventID++;
	}

	/**
	 * Retrieve the enumerated ID for the next object. Make sure to increment the 
	 * object ID when using this particular function just after invoking this call via
	 * incrementObjectID
	 * @return
	 */
	public long getObjectID ()
	{
		return m_lNextObjectID;
	}
	
	/**
	 * Increment the enumerated value for the object ID
	 */
	public void incrementObjectID ()
	{
		m_lNextObjectID++;
	}
	
	/**
	 * Initialize the registered simulation objects
	 * @return
	 */
	public boolean initializeRegisteredObjects ()
	{
		for(String sName : m_SimObjects.keySet())
		{
			SimObject o = m_SimObjects.get(sName);
			try
			{
				o.initializeBase();
			}
			catch(Exception e)
			{
				System.err.println("* Error: Unable to initialize object " + o.getName());
				System.err.println(e);
			}
		}
		return true;
	}
	
	/**
	 * @return
	 */
	public Hashtable<String, SimObject> retrieveSimObjects ()
	{
		return m_SimObjects;
	}
	
	/**
	 * Retrieve the number of registered mobile nodes (socs devices)
	 * @return
	 */
	public int getNumberMobileNodes ()
	{
		return m_nCountMobileNodes;
	}
	
	/**
	 * Search the list / hash table of objects registered with the simulation engine to find
	 * an object by its proper name
	 * @param	sName Proper name of the object
	 * @return	a resolved object if found, otherwise null 
	 */
	public SimObject resolveObjectByProperName (String sName)
	{
		if(m_SimObjects.containsKey(sName))
		{
			return m_SimObjects.get(sName);
		}

		return null;
	}
	
	/**
	 * @param theObj
	 * @return
	 */
	public boolean registerSimObject (String sName, SimObject theObj)
	{
		// TODO: Check for uniqueness first
		if(!m_SimObjects.keySet().contains(sName) && !m_SimObjects.values().contains(theObj))
		{
			m_SimObjects.put(sName, theObj);
			if(sName.contains("socs"))
			{
				m_nCountMobileNodes++;
			}
			
			return true;
		} 
		else
		{
			if(m_SimObjects.keySet().contains(sName)) 
			{
				System.err.println("*** Name " + sName + " is already used by another registered object.");
			}
			
			if(m_SimObjects.values().contains(theObj))
			{
				System.err.println("*** SimObject you tried to add already been registered.");
			}
			return false;
		}
	}
	
	/**
	 * Set current simulation date, format: YYYYMMDD
	 * @param sDate
	 */
	public void setDate (String sDate)
	{
		m_sCurrentDate = sDate;
	}
	
	/**
	 * @return
	 */
	public String getDate ()
	{
		return m_sCurrentDate;
	}
	
	/**
	 * @param fNewTime
	 */
	private void setTime (Double fNewTime)
	{
		m_fCurrentTime = fNewTime;
	}
	
	/**
	 * @return
	 */
	public Double getTime ()
	{
		return m_fCurrentTime;
	}
	
	/**
	 * Retrieve the formatted current simulation time
	 * @return
	 */
	public String getFormattedTime ()
	{
		return m_fCurrentTime.toString();
	}
	
	/**
	 * @param theAddEvent
	 * @return
	 */
	public boolean addEvent (Event theAddEvent)
	{
		// TODO: Sanity check on the event
		if(theAddEvent != null && theAddEvent instanceof Event) 
		{
			// Figure out where this goes 		
			m_EventQueue.add(theAddEvent);
			return true;
		} 
		else 
		{
			System.err.println("Error: Type error when trying to add an event.");
			return false;
		}
	}
	
	public void doSimulation ()
	{
		// TODO: Add in some sanity checking that ensures we are making forward progress and
		//       perhaps code regarding an overall simulation bounds
		
		while(m_EventQueue.size() > 0)
		{
			processNextEvent();
			if(isSimulationComplete())
			{
				break;
			}
		}	
		finishSimulation();
	}
	
	/**
	 * Processes the next event in the queue
	 * @return
	 */
	private boolean processNextEvent ()
	{
		Event		nextEvent;
		
		if(m_EventQueue.size() == 0)
		{
			DebugLogger.theLogger.logInfo_Error("*** Function: SimulationEngine.processEvent");			
			DebugLogger.theLogger.logInfo_Error("**  Warning: The queue is empty, not processing an event");
			DebugLogger.theLogger.logInfo_Error("*   Time = " + m_fCurrentTime.toString());
			return false;
		}
		
		// Pop the event at the front of the queue
		nextEvent = m_EventQueue.poll();
		
		if(nextEvent == null)
		{
			DebugLogger.theLogger.logInfo_Error("*** Function: SimulationEngine.processEvent.");
			DebugLogger.theLogger.logInfo_Error("**  Error: Somehow I got a null event from the event queue.");
			DebugLogger.theLogger.logInfo_Error("*   Time = " + m_fCurrentTime.toString());
			return false;
		}
		
		DebugLogger.theLogger.logInfo(DebugLogger.SIM_DEBUG_LEVEL_EVENTS, "t = " + getFormattedTime() + ": Event of type " + nextEvent.getSummary_Simple(), null);
		
		// Change the time to the new time as nothing has occurred between whatever time was before
		// and when time is now
		setTime(nextEvent.getTime());
		
		if(nextEvent.getSimObject() == null)
		{
			if(nextEvent instanceof EventSimDone)
			{
				markSimulationComplete();
				return true;
			}
			
			System.err.println("* Error at t = " + getTime() + " --> No linked object to process");
			nextEvent.dumpFullObject();
			return false;
		}
		
		// Process the event
		boolean bResult;
		bResult = nextEvent.getSimObject().processEvent_Base(nextEvent);		
		
		if(!bResult)
		{
			System.err.println("** Warning: The processed event retured false.");
			System.err.println("*  " + nextEvent);
		}
		
		return bResult;
	}
	
	public boolean isSimulationComplete ()
	{
		return m_bSimulationComplete;
	}
	
	public void markSimulationComplete ()
	{
		m_bSimulationComplete = true;
	}
    
	public boolean finishSimulation ()
	{
		// TODO: Finish whatever statistics stuff that we need to gather
		System.out.println("Finishing up the simulation ( t= " + getTime().toString() + ")");
		System.out.println();
		//summarizeRegisteredObjects();
		summarizeMobileNodes();
		return true;
	}
		
	public void summarizeQueue ()
	{
		System.out.println("The simulation queue has " + this.m_EventQueue.size() + " events.");
	}
	
	/**
	 * Summarize the statistics of mobile nodes
	 */
	public void summarizeMobileNodes ()
	{
		for(String sName : m_SimObjects.keySet())
		{
			SimObject o = m_SimObjects.get(sName);
		    if(o instanceof MobileNode)
		    {
		    	((MobileNode) o).finalizePropagationStats();
		    }
		}
		
		File			file;
		BufferedWriter	bw;
		FileWriter		fw;
		
		String [] statsNames = {"PropagRatio", "TotalPropVol", "VolumeRatio"};
		
		for(int i = 0; i < statsNames.length; i++)
		{
			String sField = statsNames[i];
			try 
			{
			    file = new File("./TestOut" + SIMULATION_DURATION + "/" + sField + "/" + m_sCurrentDate + ".csv");
				
				// if file does not exists, then create it
				if(!file.exists())
				{
					file.createNewFile();
				}
				
				fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);
			}
			catch(IOException e)
			{
				bw = null;
				System.err.println(e);
			}
			
			for(String sName : m_SimObjects.keySet())
			{
				SimObject o = m_SimObjects.get(sName);
				try
				{
					if(o instanceof MobileNode)
					{
						o.summarizeBase(false, bw, sField);
					}
					
				}
				catch (Exception e)
				{
					System.err.println("* Error: Unable to summarize object " + sName);
					//System.err.println(e);
					e.printStackTrace();
				}
			}
			
			try
			{
				bw.close();
			}
			catch (IOException e)
			{
				System.err.println(e);
			}
		}
		
		// Reset the content statistics
		for(String sName : m_SimObjects.keySet())
		{
			SimObject o = m_SimObjects.get(sName);
		    if(o instanceof MobileNode)
		    {
		    	((MobileNode) o).resetStatistics();
		    }
		}
	}
	
	/**
	 * Summarize the statistics of registered simulation objects
	 */
	public void summarizeRegisteredObjects ()
	{
		// Open up the file for CSV output
		File			file;
		BufferedWriter	bw;
		FileWriter		fw;
		
		try
		{
			file = new File("./TestData/TestOut.csv");
			
			// if file does not exists, then create it
			if(!file.exists())
			{
				file.createNewFile();
			}
			
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		}
		catch(IOException e)
		{
			bw = null;
			System.err.println(e);
		}
		//bw.wtrite(content);
		
		// Need to really split by type but for now, rock on
		if(m_SimObjects.size() > 0)
		{
			// TO-DO m_SimObjects.get(0).summarizeBase(true, bw);
		}
		
		for(String sName : m_SimObjects.keySet())
		{
			SimObject o = m_SimObjects.get(sName);
			try
			{
				o.summarizeBase(false, bw);
			}
			catch (Exception e)
			{
				System.err.println("* Error: Unable to summarize object " + sName);
				//System.err.println(e);
				e.printStackTrace();
			}
		}
		
		try
		{
			bw.close();
		}
		catch (IOException e)
		{
			System.err.println(e);
		}
	}
}
