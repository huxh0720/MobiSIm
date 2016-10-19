package mobilesim.core;

import mobilesim.statistics.*;

import java.io.*;

/** 
 * This is the basic mobile node that exists in the simulation
 * 
 * @author Striegel
 *
 */
public class MobileNode extends SimObject {
    
	StorageDevice				m_Storage;
	ListContents				m_ContentObjects;
	
	////////////////////////////////////////////////////////////////
	// Current means that we are seeing them in the here and now
	
	// This mobile node's neighbors (so far) as reported by short-range radio (typically mobile nodes)
	ListWirelessNeighbors		m_WirelessNeighbors_SR;

	// This mobile node's current neighbors as reported by medium-range radio (typically APs)
	ListWirelessNeighbors		m_WirelessNeighbors_MR;
	
	public static final int		TIMER_MOBILENODE_NEIGHBOR_CHECK = 1;
	
	// The timer for wireless evaluation is a two stage timer. The first timer goes off frequently
	// at the interval of once per second and it captures the current state of the wireless interfaces
	// for the purpose of aggregate reporting values. An example would be the extent to which a device
	// sits in either WiFi or cellular attachment. The coarser timer (the second one) then uses the 
	// gathered state from the first timer to properly bucketize data. 
	public static final int		TIMER_MOBILENODE_WIRELESS_STATE_PTCHECK = 2;
	public static final int		TIMER_MOBILENODE_WIRELESS_STATE_AGGREGATE = 3;
	
	// The timers for content obtaining and exchange 
	public static final int		TIMER_MOBILENODE_CONTENT_CREATE = 4;
	
	// The number of evaluation reference points, hour-basis
	public static final int		SLOTS_MOBILENODE_PROPAGATION_EVAL = 24 * SimulationEngine.SIMULATION_DURATION;
	
	// How often do we record statistics regarding our current neighbor state?
	double						m_fInterval_NeighborCheck;
	
	// How often do we evaluate the point-wise state of wireless? 
	double						m_fInterval_Wireless_State_PtCheck;
	
	// How often do we aggregate/gather information regarding the wireless state?
	double						m_fInterval_Wireless_State_Evaluate;
	
	// How often do we create and exchange the content? 
	double 						m_fInterval_ContentCreate; 
	
	public static final Double	DEFAULT_THRESHOLD_LARGEGAP_NW_USAGE = 600.00;
	
	public static final Double	DEFAULT_NEIGHBOR_LIFETIME_BASELINE = 180.00; 
	
	StatisticGroup				m_Stats;
	StatisticHistory			m_Stats_History;

	// The various scalar fields that do not necessarily need a specific field or object
	// these are typically counters
	ListDataFields				m_Fields;
	
	StatisticGroup[]			m_PropagationStats;
	
	double []					m_HourlyTraffic;
	
	public MobileNode ()
	{
		m_Storage = new StorageDevice();
		m_ContentObjects = new ListContents();
		
		m_WirelessNeighbors_SR = new ListWirelessNeighbors();
		m_WirelessNeighbors_MR = new ListWirelessNeighbors();
		
		m_fInterval_NeighborCheck = 60;             // Check neighbors every 60 seconds
		m_fInterval_Wireless_State_PtCheck = 5;		// Check state every 5 seconds
		m_fInterval_Wireless_State_Evaluate = 300;	// Summarize every 300 seconds (5 mins)
		m_fInterval_ContentCreate = 3600;           // Obtain/Create content every 1 Hr
		
		m_Stats = new StatisticGroup();
		m_Stats_History = new StatisticHistory();
		
		m_Fields = new ListDataFields();
		// Fields with single values that are not necessary for statistical purpose directly
	    m_Fields.instantiateFieldWithValue("THRESHOLD_LARGEGAP_NW_USAGE", DEFAULT_THRESHOLD_LARGEGAP_NW_USAGE);
	    
	    m_PropagationStats = new StatisticGroup[SLOTS_MOBILENODE_PROPAGATION_EVAL];
	      
	    m_HourlyTraffic = new double[SLOTS_MOBILENODE_PROPAGATION_EVAL];
	    
	    for (int i = 0; i < SLOTS_MOBILENODE_PROPAGATION_EVAL; i++)
	    {
	    	m_PropagationStats[i] = new StatisticGroup();
	    	m_HourlyTraffic[i] = 0.0;
	    }
	    
	}
	
	public boolean initialize ()
	{
		enableTimers();
		initializeStatistics();
		
		return true;
	}
	
	/** 
	 * Enable timers for the mobile node 
	 * 
	 * @return
	 */
	public boolean enableTimers ()
	{
		enableTimer_NeighborCheck();
		//enableTimer_WirelessState();
		enableTimer_ContentCreate();
		
		return true;
	}
	
	/**
	 * For this node, enable a periodic timer that assesses the neighbor state of this mobile node
	 * relative to other nodes and to record this data in the appropriate locations. 
	 * @return
	 */
	public boolean enableTimer_NeighborCheck ()
	{
		EventTimerSimObject 	theTimer;
		
		theTimer = new EventTimerSimObject();
		
		// Configure it to call us back (we are the object)
		theTimer.setSimObject(this);

		// Set the interval to the current setting for our node
		theTimer.setInterval(m_fInterval_NeighborCheck);
		
		// This should go on forever and ever
		theTimer.enableInfiniteInvocations();
		
		// Set the timer to the right type
		theTimer.setType(TIMER_MOBILENODE_NEIGHBOR_CHECK);
		
		// Set the first invocation as NOW plus the interval
		theTimer.adjustTimeByInterval();
		
		// Add this event to the mix
		SimulationEngine.theEngine.addEvent(theTimer);		
		return true;
	}
	
	/**
	 * @return
	 */
	public boolean enableTimer_WirelessState ()
	{
		EventTimerSimObject 	theTimer;
		
		// Point check timer
		theTimer = new EventTimerSimObject();
		
		theTimer.setSimObject(this);				
		theTimer.setInterval(m_fInterval_Wireless_State_PtCheck);
		theTimer.enableInfiniteInvocations();		
		theTimer.setType(TIMER_MOBILENODE_WIRELESS_STATE_PTCHECK);		
		theTimer.adjustTimeByInterval();		
		SimulationEngine.theEngine.addEvent(theTimer);		
		
		// Aggregate state checking timer
		theTimer = new EventTimerSimObject();
		
		theTimer.setSimObject(this);				
		theTimer.setInterval(m_fInterval_Wireless_State_Evaluate);
		theTimer.enableInfiniteInvocations();		
		theTimer.setType(TIMER_MOBILENODE_WIRELESS_STATE_AGGREGATE);		
		theTimer.adjustTimeByInterval();
		SimulationEngine.theEngine.addEvent(theTimer);		
				
		return true;
	}
	
	/**
	 * Enable a periodic timer that obtains / creates local content for this mobile node
	 * @return
	 */
	public boolean enableTimer_ContentCreate ()
	{
		EventTimerSimObject theTimer;
		
		// content create timer
		theTimer = new EventTimerSimObject();
		
		theTimer.setSimObject(this);				
		theTimer.setInterval(m_fInterval_ContentCreate);
		theTimer.enableInfiniteInvocations();		
		theTimer.setType(TIMER_MOBILENODE_CONTENT_CREATE);		
		theTimer.setTime(SimulationEngine.theEngine.getTime());		
		SimulationEngine.theEngine.addEvent(theTimer);	
		
		return true;
	}
	
	public boolean initializeStatistics ()
	{
	    // Add / initialize our various statistics here
	    m_Stats.initializeCounter("Count_Wireless_PtCheck");
		m_Stats.initializeCounter("Window_Count_Wireless_PtCheck");
		m_Stats.initializeCounter("Window_Wireless_HasActiveWiFi");

		m_Stats.initializeCounter("Window_Wireless_WiFi_Rx_Bytes");
		m_Stats.initializeCounter("Window_Wireless_WiFi_Tx_Bytes");
		m_Stats.initializeCounter("Window_Wireless_Cell_Rx_Bytes");
		m_Stats.initializeCounter("Window_Wireless_Cell_Tx_Bytes");

		m_Stats.initializeCounter("Summary_Total_Tx_Bytes");
		m_Stats.initializeCounter("Summary_Total_Rx_Bytes");
		m_Stats.initializeCounter("Summary_Mobile_Tx_Bytes");
		m_Stats.initializeCounter("Summary_Mobile_Rx_Bytes");

		// Instantiate the various fields
		m_Fields.instantiateField("MobileTxBytes", ListDataFields.DATAFIELD_DOUBLE);
		m_Fields.instantiateField("MobileRxBytes", ListDataFields.DATAFIELD_DOUBLE);
		m_Fields.instantiateField("TotalTxBytes", ListDataFields.DATAFIELD_DOUBLE);
		m_Fields.instantiateField("TotalRxBytes", ListDataFields.DATAFIELD_DOUBLE);

		m_Fields.instantiateField("Ref:MobileTxBytes", ListDataFields.DATAFIELD_DOUBLE);
		m_Fields.instantiateField("Ref:MobileRxBytes", ListDataFields.DATAFIELD_DOUBLE);
		m_Fields.instantiateField("Ref:TotalTxBytes", ListDataFields.DATAFIELD_DOUBLE);
		m_Fields.instantiateField("Ref:TotalRxBytes", ListDataFields.DATAFIELD_DOUBLE);

		m_Fields.instantiateField("Delta:MobileTxBytes", ListDataFields.DATAFIELD_DOUBLE);
		m_Fields.instantiateField("Delta:MobileRxBytes", ListDataFields.DATAFIELD_DOUBLE);
		m_Fields.instantiateField("Delta:TotalTxBytes", ListDataFields.DATAFIELD_DOUBLE);
		m_Fields.instantiateField("Delta:TotalRxBytes", ListDataFields.DATAFIELD_DOUBLE);

		// Number of data updates received
		m_Stats.initializeCounter("Count_Data_Update_Usage");

		// Cumulative number of fields processed
		m_Stats.initializeCounter("Sum_Data_Update_Usage_Fields");

		// Cumulative time between successive updates
		m_Stats.initializeCounter("Sum_Data_Update_Usage_TimeGap");
		m_Stats.initializeCounter("Count_Data_Update_Usage_LargeGap");
		 
		for(int i = 0; i < m_PropagationStats.length; i++)
		{
			m_PropagationStats[i].initializeCounter("TotalNumSrcs");
			m_PropagationStats[i].initializeCounter("TotalLatency");
			m_PropagationStats[i].initializeCounter("TotalSrcHops");
			m_PropagationStats[i].initializeCounter("TotalPropVol");
			
			m_PropagationStats[i].initializeCounter("PropagRatio");
			m_PropagationStats[i].initializeCounter("MeanLatency");
			m_PropagationStats[i].initializeCounter("MeanSrcHops");
			m_PropagationStats[i].initializeCounter("VolumeRatio");
		}
        return true;
	}
	
	public void resetStatistics ()
    {
		m_Stats.clear();
		m_Stats_History.clear();
		m_Fields.clear();
		for(int i = 0; i < SLOTS_MOBILENODE_PROPAGATION_EVAL; i++)
		{
		    m_PropagationStats[i].clear();
		    m_HourlyTraffic[i] = 0;
		}
	}	 
	
	/**
	 * Retrieve the neighbors which were detected so far
	 * @return
	 */
	public ListWirelessNeighbors getNeighbors_SR ()
	{
		return m_WirelessNeighbors_SR;
	}
	
	/**
	 * Set the number of simulation units between checking on neighbors and
	 * computing various statistics regarding the state of neighbors
	 * @param fInterval The number of simulation units between checks
	 * @return
	 */
	public boolean setInterval_NeighborCheck (Double fInterval)
	{
		if(fInterval <= 0)
		{
			 System.err.println("* Error: Interval for neighbor checks cannot be less than or equal to zero.");
			 System.err.println("  setInterval_NeighborCheck attemped to set interval to " + fInterval);
			 return false;
		}
		m_fInterval_NeighborCheck = fInterval;
		return true;
	}
	
	/** 
	 * Retrieves the number of time units between checks on our respective neighbor states
	 * @return The number of simulation time units between checks
	 */
	public double getInterval_NeighborCheck () 
	{
		return m_fInterval_NeighborCheck;
	}
	
	public boolean setInterval_ContentCreate (double fInterval)
	{
		if(fInterval <= 0)
		{
			 System.err.println("* Error: Interval for content creating cannot be less than or equal to zero.");
			 System.err.println("    setInterval_ContentCreate attemped to set interval to " + fInterval);
			 return false;
		}
		m_fInterval_ContentCreate = fInterval;
		return true;
	}
	
	public double getInterval_ContentCreate ()
	{
		return m_fInterval_ContentCreate;
	}
	
	public boolean setHourlyTraffic (int nIndex, double fValue)
	{
		if(fValue < 0)
		{
			System.err.println("In Mobile Node: " + this.getName() + ": Negative traffic vol.");
			return false;
		}
		
		m_HourlyTraffic[nIndex] = fValue;
		return true;
	}
	
	public double getHourlyTraffic (int nIndex)
	{
		return m_HourlyTraffic[nIndex];
	}
			
	/** 
	 * Process timers based on types
	 */
	public boolean processTimer (EventTimerSimObject theTimer)
	{   
		// The timer renew code is part of the core timer and SimObject code, 
		// we just need to process things here (e.g. dispatch to the right function)	
        switch(theTimer.getType())
        {
        	case TIMER_MOBILENODE_NEIGHBOR_CHECK:
        		return processTimer_NeighborCheck();
        	case TIMER_MOBILENODE_WIRELESS_STATE_PTCHECK:
        		return processTimer_WirelessStatePointCheck();
        	case TIMER_MOBILENODE_WIRELESS_STATE_AGGREGATE:
        		return processTimer_WirelessStateAggregate();
        	case TIMER_MOBILENODE_CONTENT_CREATE:
        		return processTimer_ContentCreate();

        	default:
        		System.err.println("* Error: Timer received at mobile node " + getName() + " with an unknown type.");
        		System.err.println("  Timer = " + SimulationEngine.theEngine.getTime().toString() + " Timer type = " + theTimer.getType());
        		return false;
        }
	}
	
	/**
	 * @return
	 */
	public boolean processTimer_NeighborCheck ()
	{
	    evaluateNeighbors();
	    return true;
	}
	
	/**
	 * Periodically evaluate neighbors and invalidate/deactivate the expired ones 
	 */
	private void evaluateNeighbors ()
	{	
		for (String theName : m_WirelessNeighbors_SR.keySet())
		{
			WirelessNeighbor theNeighbor = m_WirelessNeighbors_SR.get(theName);
			
			if(isNeighborExpired(theNeighbor))
			{
				theNeighbor.setPresenceFlag(false);;
			}
		}
	}
	
	/**
	 * Return the list of active wireless neighbors, i.e. neighbors that are not expired yet. 
	 * @return
	 */
	public ListWirelessNeighbors retrieveActiveNeighbors_SR ()
	{
		ListWirelessNeighbors currentNeighbors = new ListWirelessNeighbors();
		currentNeighbors.clear();
		
		for (String name : m_WirelessNeighbors_SR.keySet())
		{
			WirelessNeighbor theNeighbor = m_WirelessNeighbors_SR.get(name);
			if (!isNeighborExpired(theNeighbor))
			{
				currentNeighbors.put(name,  theNeighbor);
			}
		}
		
		return currentNeighbors;
	}
	
	/**
	 * Check if a specific neighbor is still valid / expired
	 * We only know the snapshot of neighbors from each proximity sensing event and update the them in a  
	 * discrete manner. But have no idea what would happen between two consecutive proximity sensing events
	 * that took place at t1 and t2. The time duration between t1 and t2 could be hours, and practically 
	 * it's not likely that neighbors detected at t1 can stay that long until t2. So we need to set a default 
	 * lifetime for each neighbor to handle the such scenario. 
	 * 
	 * @param theNeighbor	the wireless neighbor to be checked
	 * @return
	 */
	private boolean isNeighborExpired (WirelessNeighbor theNeighbor)
	{
		Double evalTime = SimulationEngine.theEngine.getTime();
		Double sensedTime = theNeighbor.getTime_LastSensed();
		Double lifeTime = evalTime - sensedTime;
		
		return (lifeTime > DEFAULT_NEIGHBOR_LIFETIME_BASELINE);
	}
	
	/**
	 * Fine-grained timer for evaluating the wireless state in between the 
	 * aggregate state checks
	 * @return
	 */
	public boolean processTimer_WirelessStatePointCheck ()
	{
		// +1 sample point noted - overall - how many total fine-grained samples
		m_Stats.adjustValue("Count_Wireless_PtCheck", 1.0);

		// +1 sample point noted - this window
		m_Stats.adjustValue("Window_Count_Wireless_PtCheck", 1.0);

		// Are we currently associated with an AP?				
		if(hasActiveWiFi())
		{
			m_Stats.adjustValue("Window_Wireless_HasActiveWiFi", 1.0);
		}
		else
		{
			// Nothing to do, we get it by subtracting the window samples minus the 
			// number of active WiFi samples
		}
		
		return true;
	}
	
	/**
	 * The coarse timer for wireless state evaluation that allows us to 
	 * capture various diurnal types of behaviors 
	 * @return
	 */
	public boolean processTimer_WirelessStateAggregate ()
	{
	    StatisticHistoricalInstance  	savedStat;
		
		savedStat = new StatisticHistoricalInstance();

		// Group name plus duration that this sample covers
		savedStat.initializeNamedGroup("Wireless_State_Aggregate", m_fInterval_Wireless_State_PtCheck);

		// Tally the various state attributes
		
		// How many points did we record in this window?
		savedStat.copyStatistic(m_Stats, "Window_Count_Wireless_PtCheck");
		
		// How often were we attached to WiFi?
		savedStat.copyStatistic(m_Stats, "Window_Wireless_HasActiveWiFi");
		
		// What was the data tonnage across the various adapters?
		savedStat.copyStatistic(m_Stats, "Window_Wireless_WiFi_Rx_Bytes");
		savedStat.copyStatistic(m_Stats, "Window_Wireless_WiFi_Tx_Bytes");		
		savedStat.copyStatistic(m_Stats, "Window_Wireless_Cell_Rx_Bytes");
		savedStat.copyStatistic(m_Stats, "Window_Wireless_Cell_Tx_Bytes");		
				
		// Are there any computations?
			
		// Reset the various state attributes		
		m_Stats.resetValue("Window_Count_Wireless_PtCheck");
		m_Stats.resetValue("Window_Wireless_HasActiveWiFi");		
		
		m_Stats_History.add(savedStat);
		//m_Stats_History.dumpSummary();
		
		return true;
	}
	
	/**
	 * @return
	 */
	public boolean hasActiveWiFi ()
	{
		return false;
	}
	
	/**
	 * @return
	 */
	public boolean processTimer_ContentCreate ()
	{
		// Assume that simulation always starts at time 0.0
		int timeElapsed = SimulationEngine.theEngine.getTime().intValue();
		int nHourIndex = timeElapsed / 3600;
		
		double trafficVol = m_HourlyTraffic[nHourIndex];
		
		// Convert passed seconds into hh:mm:ss
	    String timeStamp = resolveSecondsElapsed(timeElapsed);
		
		String contentID = this.getName() + "_" + timeStamp;
		
		// Create and save the content
		Content content = new Content();
		content.setContentID(contentID);
		content.stampCreationTime();
		content.setOriginalSource(this);
		content.setSource(this);
		content.setSourceHops(0);
		content.setContentSize(trafficVol);
		
		saveContentObject(content);
		
		return true;
	}
	
	/**
	 * Given a number of elapsed seconds, return HH:MM:SS in format of string
	 * @param nTimeElapsed
	 * @return Elapsed time in format HH:MM:SS
	 */
	private String resolveSecondsElapsed (int nTimeElapsed)
	{
		if (nTimeElapsed < 0)
		{
			System.err.println("* In MobileNode.resolveSecondsElapsed: Cannot resolve seconds as a nagative value");
		}
		int sec = nTimeElapsed % 60;
		int min = (nTimeElapsed / 60) % 60;
		int hor = (nTimeElapsed / 3600);
		
		String sSec = String.format("%02d", sec);
		String sMin = String.format("%02d", min);
		String sHor = String.format("%02d", hor);
		
		String timeStamp = sHor + ":" + sMin + ":" + sSec;
		
		return timeStamp;
	}
	
	/**
	 * Use this function to directly receive and save a piece of content from the content provider
	 * @param content
	 * @return
	 */
	public boolean saveContentObject (Content content)
	{
		// Sanity and uniqueness check
		if (content == null)
		{
			return false;
		}
		
		String contentID = content.getContentID();
		
		if(m_ContentObjects.containsKey(contentID))
		{
			Double time = SimulationEngine.theEngine.getTime();
			System.err.println("  Mobile Node " + this.getName() + " t = " + time + ", ignore redudant content.");
			return false;
		}
		
		m_ContentObjects.put(contentID, content);
	
		return true;
	}
	
	/**
	 * Process event that applies to this mobile node, 
	 * based on the event type
	 * @return
	 */
	public boolean processEvent (Event theEvent)
	{
		if(theEvent instanceof EventProximitySense)
		{
			return processEvent_ProximitySense((EventProximitySense) theEvent);
		}
		else if(theEvent instanceof EventDataUpdate)
		{
			return processEvent_DataUpdate((EventDataUpdate) theEvent);
		}
		
		return false;	
	}
	
	/**
	 * @param theProxEvent
	 * @return
	 */
	public boolean processEvent_ProximitySense (EventProximitySense theProxEvent)
	{
		exchangeContents(theProxEvent);
		updateNeighbors_SR(theProxEvent);
		
		return true;		
	}
	
	/**
	 * @param theProxEvent
	 */
	private void exchangeContents (EventProximitySense theProxEvent)
	{
		ListProximityInstances instances;
		instances = theProxEvent.getProximityInstances();
		
		// Do a bi-directional content copy with each neighbor
		for(String neighborName : instances.keySet())
		{
			SimObject neighborObject = SimulationEngine.theEngine.resolveObjectByProperName(neighborName);
			if ((neighborObject != null) && (neighborObject instanceof MobileNode))
			{
				MobileNode neighborNode = (MobileNode)neighborObject;
				ProximityInstance theInstance =  instances.get(neighborName);
				
				if (theInstance.getSignalStrength() >= SimulationEngine.FILTER_RSSI)
				{
					 // Copy the content from the neighbor
				    this.copyContents(neighborNode);
				    // Copy the content to the neighbor
				    neighborNode.copyContents(this);
				}			
			}
		}
	}
	
	/**
	 * Use this function to make a copy of the content sent from a neighbor
	 * and then properly modify the details such as time, hops, etc. 
	 * @return
	 */
	private boolean copyContents (MobileNode srcNode)
	{
		ListContents localList = this.getContentList();
		ListContents copiedList = srcNode.getContentList();
		
		// Sanity check, must not copy content that already exist locally
		for(String contentID : copiedList.keySet())
		{
			Content copiedContent = copiedList.get(contentID);
			// number of hops so far from the source node
			int hops = copiedContent.getSourceHops() + 1;
			// propagation latency in mins
			double latency = (SimulationEngine.theEngine.getTime() - copiedContent.getCreationTime()) / 60;
			
			if(!localList.containsKey(contentID) &&
					hops <= SimulationEngine.FILTER_HOPS &&
					latency <= SimulationEngine.FILTER_LACY )
			{
				// Need to make a local copy, instead of using the reference!
				Content content = new Content();
				content.setContentID(contentID);
				content.setCreationTime(copiedContent.getCreationTime());
				content.stampArrivialTime();
				content.setOriginalSource(copiedContent.getOriginalSource());
				content.setSource(srcNode);
				content.setSourceHops(copiedContent.getSourceHops()+1);
				content.setContentSize(copiedContent.getContentSize());
				
				localList.put(contentID, content);
			}
		}
	    
		return true;
	}
	
	private ListContents getContentList ()
	{
		return m_ContentObjects;
	}
	
	/**
	 * Update the wireless neighbor list w.r.t. short range
	 * @param
	 */
	public void updateNeighbors_SR (EventProximitySense theProxEvent)
	{
		ListProximityInstances instances;
		instances = theProxEvent.getProximityInstances();
	
		// ---- Properly update the neighbor list
		for(String name : instances.keySet())
		{		
			ProximityInstance theInstance = instances.get(name);
			
			Double fTime = theInstance.getInstanceTime();
			int nRSSI = theInstance.getSignalStrength();
			
			if(!m_WirelessNeighbors_SR.containsKey(name))
			{	
				WirelessNeighbor neighbor = new WirelessNeighbor();
				neighbor.setName(name);
				neighbor.setTime_FirstSensed(fTime);
				neighbor.setTime_LastSensed(fTime);
				neighbor.setSignalStrength(nRSSI);
				neighbor.setPresenceFlag(true);
				
				m_WirelessNeighbors_SR.put(name, neighbor);
			}
			else
			{
				// neighbor already exists, need to update time
				WirelessNeighbor neighbor = m_WirelessNeighbors_SR.get(name);
				neighbor.setTime_LastSensed(fTime);
				neighbor.setSignalStrength(nRSSI);
				neighbor.setPresenceFlag(true);
			}
		}
		
    }
	 
	/**
	 * @param theUpdate
	 * @return
	 */
	public boolean processEvent_DataUpdate (EventDataUpdate theUpdate)
	{		
		switch(theUpdate.getType())
		{
			case EventDataUpdate.EVENTDATA_UPDATE_NW_USAGE:				
				return processDataUpdate_NW_Usage (theUpdate);
		}
		
		return false;
	}
	
	public boolean processDataUpdate_NW_Usage (EventDataUpdate theUpdate)
	{
		Boolean		bFirstValue;
		
		bFirstValue = false;
		
		// System.out.println("Processing the data update");
		
		// +1 to our counter for data updates
		m_Stats.adjustValue("Count_Data_Update_Usage", 1.0);
		
		// Keep a tally of the number of fields that we saw in each update.  This is used primarily
		//  for troubleshooting as the number should be fairly consistent
		m_Stats.adjustValue("Sum_Data_Update_Usage_Fields", (double) theUpdate.getNumberFields());
				
		
		ListDataFields		theDelta;
		
		theDelta = m_Fields.computeChanges(theUpdate);		

		// This is only for debugging
		theDelta.dumpInfoToConsole();

		// If any of our tally fields are the minimum value, this is the first one
		if(m_Fields.get("MobileTxBytes").getUpdateTime() == Double.MIN_VALUE ||
		   m_Fields.get("MobileRxBytes").getUpdateTime() == Double.MIN_VALUE || 
		   m_Fields.get("TotalTxBytes").getUpdateTime() == Double.MIN_VALUE || 
		   m_Fields.get("TotalRxBytes").getUpdateTime() == Double.MIN_VALUE)
		{
			bFirstValue = true;
		}
		
		// Adjust the time to reflect the average gap with regards to data sampling. 
		//
		// Note that this is a bit tricky as when the device turns off and comes back on, we may see
		//  a fairly large gap. The average gap can be computed by dividing this value by the
		//  count of the number of updates
		if(theDelta.getLastUpdate() > Double.MIN_VALUE)
		{			
			m_Stats.adjustValue("Sum_Data_Update_Usage_TimeGap", theDelta.getLastUpdate());

			// Is the gap too large? 
			if(theDelta.getLastUpdate() >= m_Fields.get("THRESHOLD_LARGEGAP_NW_USAGE").getValueDouble())
			{
				m_Stats.adjustValue("Count_Data_Update_Usage_LargeGap", 1.0);
				
				// TODO: Check if we need to re-initialize the values
			}
		}
				
		// Let's grab all of the various data fields
		m_Fields.copyAllFields(theUpdate);
		
		if(bFirstValue)
		{
			System.out.println("Initializing values");
			m_Fields.copyFieldValue("Ref:MobileTxBytes", "MobileTxBytes");
			m_Fields.copyFieldValue("Ref:MobileRxBytes", "MobileRxBytes");
			m_Fields.copyFieldValue("Ref:TotalTxBytes",  "TotalTxBytes");
			m_Fields.copyFieldValue("Ref:TotalRxBytes",  "TotalRxBytes");			
		}
		else
		{			
			// If we are not the first, compute the difference (how much sent since we initialized)
			System.out.println("Computing delta");
			m_Fields.get("Delta:MobileTxBytes").setValue(m_Fields.get("MobileTxBytes").getValueDouble() - m_Fields.get("Ref:MobileTxBytes").getValueDouble());
			m_Fields.get("Delta:MobileRxBytes").setValue(m_Fields.get("MobileRxBytes").getValueDouble() - m_Fields.get("Ref:MobileRxBytes").getValueDouble());
			m_Fields.get("Delta:TotalTxBytes").setValue(m_Fields.get("TotalTxBytes").getValueDouble() - m_Fields.get("Ref:TotalTxBytes").getValueDouble());
			m_Fields.get("Delta:TotalRxBytes").setValue(m_Fields.get("TotalRxBytes").getValueDouble() - m_Fields.get("Ref:TotalRxBytes").getValueDouble());			
		}
		
		// TODO: Add something that catches when we have missing fields
		
		return true;
	}
	
	public boolean summarize (boolean bFieldsOnly, BufferedWriter theWriter) 
	{
		// For now, just dump, dump, dump the stats to the console
		// System.out.println(" Statistic for " + getName());
		//finalizeContentStats();
		//SummarizeContentCSV(bFieldsOnly, theWriter, "PropagRatio");

		//finalizeStats();
		//summarizeCSV(bFieldsOnly, theWriter);
		
		return true;
	}
	
	public boolean summarize (boolean bFieldsOnly, BufferedWriter theWriter, String sField) 
	{
		SummarizePropagationCSV(bFieldsOnly, theWriter, sField);
		return true;
	}
	
	public boolean finalizePropagationStats ()
	{
		for (String contentID : m_ContentObjects.keySet())
		{
			Content theContent = m_ContentObjects.get(contentID);
			String srcName = theContent.getSource().getName();
			double creationTime = theContent.getCreationTime();
			double arrivalTime = theContent.getArrivialTime();
			double deliveryDuration = (arrivalTime - creationTime)/60;
			double srcHops = theContent.getSourceHops();
			double size = theContent.getContentSize();
			
			// Only consider content from other devices
			if(srcName.contains("socs") && !srcName.equals(this.getName()))
			{
				int indexStatArray = ((int)creationTime / 3600);
				m_PropagationStats[indexStatArray].adjustValue("TotalNumSrcs", 1.0);
				m_PropagationStats[indexStatArray].adjustValue("TotalLatency", deliveryDuration);
				m_PropagationStats[indexStatArray].adjustValue("TotalSrcHops", srcHops);
				m_PropagationStats[indexStatArray].adjustValue("TotalPropVol", size);
			}
		}
		
		// Calculate the num of content files that created by other nodes / devices during an hour
		int numNodes = SimulationEngine.theEngine.getNumberMobileNodes();
		int numFiles = (int)((numNodes - 1)*(3600 / m_fInterval_ContentCreate));
		double[] totalHrlyVol = DataLoaderNetworkTraffic.theLoader.getHourlyTrafficOverall();
		
		for (int i = 0; i < m_PropagationStats.length; i++)
		{
			double totalNumSrcs = m_PropagationStats[i].get("TotalNumSrcs").getValue();
			double totalLatency = m_PropagationStats[i].get("TotalLatency").getValue();
			double totalSrcHops = m_PropagationStats[i].get("TotalSrcHops").getValue();
			double totalPropVol = m_PropagationStats[i].get("TotalPropVol").getValue();
			
			m_PropagationStats[i].get("PropagRatio").setValue(totalNumSrcs / numFiles);
			m_PropagationStats[i].get("TotalPropVol").setValue(totalPropVol / 1024);   // convert to MB
			
			if(totalNumSrcs > 0.0)
			{
				m_PropagationStats[i].get("MeanLatency").setValue(totalLatency/totalNumSrcs);
				m_PropagationStats[i].get("MeanSrcHops").setValue(totalSrcHops/totalNumSrcs);
			}
			else 
			{
				m_PropagationStats[i].get("MeanLatency").setValue(Double.NaN);
				m_PropagationStats[i].get("MeanSrcHops").setValue(Double.NaN);
			}
			
			if(totalHrlyVol[i] > 0.0)
			{
				m_PropagationStats[i].get("VolumeRatio").setValue(totalPropVol/totalHrlyVol[i]);
			}
			else
			{
				m_PropagationStats[i].get("VolumeRatio").setValue(Double.NaN);
			}
		}
		
		return true;
	}
	
	public boolean SummarizePropagationCSV (boolean bFieldsOnly, BufferedWriter theWriter, String sField)
	{
		try 
		{
			if (bFieldsOnly) 
			{
				theWriter.write("MobileNode,");
				theWriter.write(m_Stats.getSummaryCSV_Fields());
				theWriter.write("\n");
			} 
			else 
			{
				theWriter.write(getName() + ",");
			    for(int i = 0; i < m_PropagationStats.length; i++)
			    {
			    	double value = m_PropagationStats[i].get(sField).getValue();
			    	theWriter.write(value + ",");
			    }
			    theWriter.write("\n");
			}
		} 
		catch (IOException e) 
		{
			// TODO: Plumb this correctly
			System.err.println("* Error: I/O exception of " + e);
		}

		return true;
	}
	
	public boolean finalizeStats ()
	{
		m_Stats.get("Summary_Total_Tx_Bytes").setValue(m_Fields.get("Delta:TotalTxBytes").getValueDouble());
		m_Stats.get("Summary_Total_Rx_Bytes").setValue(m_Fields.get("Delta:TotalRxBytes").getValueDouble());
		m_Stats.get("Summary_Mobile_Tx_Bytes").setValue(m_Fields.get("Delta:MobileTxBytes").getValueDouble());
		m_Stats.get("Summary_Mobile_Rx_Bytes").setValue(m_Fields.get("Delta:MobileRxBytes").getValueDouble());
						
		return true;
	}
		
	public boolean summarizeCSV(boolean bFieldsOnly, BufferedWriter theWriter) 
	{
		try {
			if (bFieldsOnly) {
				theWriter.write("MobileNode,");
				theWriter.write(m_Stats.getSummaryCSV_Fields());
				theWriter.write("\n");
			} else {
				theWriter.write(getName() + ",");
				theWriter.write(m_Stats.getSummaryCSV());
				theWriter.write("\n");
			}
		} catch (IOException e) {
			// TODO: Plumb this correctly
			System.err.println("* Error: I/O exception of " + e);
		}

		return true;
	}
}
