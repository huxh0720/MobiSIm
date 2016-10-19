package mobilesim.core;

import mobilesim.statistics.*;

public class DataLoaderNetworkUsage extends DataLoader {
	
	EventDataUpdate		m_LastEvent;
	StatisticGroup		m_Stats;
	
	public DataLoaderNetworkUsage ()
	{
		m_LastEvent = null;
		m_Stats = new StatisticGroup();
		
		m_Stats.initializeCounter("Lines");
		m_Stats.initializeCounter("Consolidations");
		m_Stats.initializeCounter("TimeCollide-NoChange");
		m_Stats.initializeCounter("TimeCollide-Change");		
	}
	
	public void summarizeLoad ()
	{
		System.out.println(" File load completed");
		System.out.println("    " + m_Stats.get("Lines").getValue() + " lines processed");
		System.out.println("    " + m_Stats.get("Consolidations").getValue() + " consolidations (same time, different fields)");
		System.out.println("    " + m_Stats.get("TimeCollide-NoChange").getValue() + " muted consolidations (same time, same field, same value)");
		System.out.println("    " + m_Stats.get("TimeCollide-Change").getValue() + " collisions (same time, same fields, different values");		
	}
	
	public boolean processLine (String sLine, Double fTimeAdjust)
	{
		return processLine_RipCSV_1(sLine, fTimeAdjust);
	}
		
	public boolean processLine_RipCSV_1 (String sLine, Double fTimeOffset)
	{
		// File is formatted as follows
		
		//   Node, Time(UTC), Field, Value
		
		// Field is either
		// TotalRxBytes, TotalTxBytes, MobileRxBytes, MobileTxBytes
		//	 Total is overall (cellular + WiFi)
		//   Mobile is strictly cellular
				
		// Split it up
		String [] sFields = sLine.split(",");
		if(sFields.length < 4)
		{
			return false;
		}
		
		EventDataUpdate 	theNW_Usage;
				
		// Which node is it?
		String		sNode;
		Double		fEventTime;
		
		sNode = sFields[0];

		fEventTime = Double.parseDouble(sFields[1]) - fTimeOffset;

		m_Stats.adjustValue("Lines", 1.0);
				
		// Criterion
		//    The last event must not be null
		//    The name for this mobile node and the last mobile node must match
		//    The time must also match for the event
		if(m_LastEvent != null && m_LastEvent.getSimObject().getName().compareTo(sFields[0]) == 0 &&
		   m_LastEvent.getTime().equals(fEventTime))
		{
			// Just add this field + value as it is the same node and same sample time
			switch(m_LastEvent.addNameValue(sFields[2], sFields[3]))
			{
				// Totally cool - no worries
				case 1:
					m_Stats.adjustValue("Consolidations", 1.0);
					break;
					
				// Duplicate key but cool - values were the same
				case 0:
					m_Stats.adjustValue("TimeCollide-NoChange", 1.0);					
					break;
					
				// Ruh roh, we appeared to sample the same point in time multiple times
				case -1:
					m_Stats.adjustValue("Lines", -1.0);
					m_Stats.adjustValue("TimeCollide-Change", 1.0);					
					m_LastEvent = null;
					processLine_RipCSV_1(sLine, fTimeOffset);
					break;
			}
		}
		else
		{		
			theNW_Usage = new EventDataUpdate();
			m_LastEvent = theNW_Usage;
			
			// Try to find the node from the master list
			SimObject 	theSimObject;
			MobileNode	theMobileNode;
			
			theSimObject = SimulationEngine.theEngine.resolveObjectByProperName(sNode);
			
			if(theSimObject == null)
			{
				// Did not find it, should we add it?			
				theMobileNode = new MobileNode();
				theMobileNode.setName(sNode);
		
				SimulationEngine.theEngine.registerSimObject(sNode, theMobileNode);			
			}
			else
			{
				// Found it!
				theMobileNode = (MobileNode) theSimObject;
			}
			
			// At this point, we should have a good mobile node
			theNW_Usage.setSimObject(theMobileNode);			
			
			// Set the time as whatever time is specified in the file minus the offset
			theNW_Usage.setTime(fEventTime);
			
			// Set the field name and value
			theNW_Usage.addNameValue(sFields[2], sFields[3]);			
			
			theNW_Usage.setType(EventDataUpdate.EVENTDATA_UPDATE_NW_USAGE);
			
			SimulationEngine.theEngine.addEvent(theNW_Usage);		
		}
						
		return true;
	}
	
	public Double extractTimeFromLine (String sLine)
	{
		return extractTime_RipCSV_1(sLine);
	}
	
	public Double extractTime_RipCSV_1 (String sLine)
	{
		// Split it up
		String [] sFields = sLine.split(",");
		
		if(sFields.length < 4)
		{
			return -1.0;
		}

		return Double.parseDouble(sFields[1]);
	}
	
}