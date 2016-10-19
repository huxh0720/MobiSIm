package mobilesim.core;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import mobilesim.statistics.StatisticGroup;

/** Read in files for Bluetooth-based proximity data
 * 
 * @author Striegel
 *
 */
public class DataLoaderBluetooth extends DataLoader {
	
	public static DataLoaderBluetooth theLoader = new DataLoaderBluetooth();
	
	public static final int DATALOADER_BLUETOOTH_MODE_TEST = 0;
	
	String					m_sCurrentFile;
	
	EventProximitySense		m_LastEvent;
	
	StatisticGroup			m_Stats;

	public DataLoaderBluetooth ()
	{
		m_sCurrentFile = "";
		m_LastEvent = null;
		m_Stats = new StatisticGroup();
		m_Stats.initializeCounter("Lines");
		m_Stats.initializeCounter("Ignored");
	}
	
	public void resetDataLoader ()
	{
		m_sFileList.clear();
		m_sCurrentFile = "";
		m_LastEvent = null;
		m_Stats.resetValue("Lines");
		m_Stats.resetValue("Ignored");
	}
	
	public boolean startFileLoad ()
	{
		if(getFlag_AdjustTime())
		{
			setAdjustTime(extractFirstFileStartTime());
		}
		
		for(int i = 0; i < m_sFileList.size(); i++)
		{
			String theNextFile = m_sFileList.get(i);
			m_sCurrentFile = theNextFile;
			startNextFile(theNextFile);
		}
		
		summarizeLoad();
		return true;
	}
	
	public boolean startNextFile (String sFile)
	{
		System.out.println("Loading BT file: " + m_sPath + sFile);
		try
		{
			FileReader fr = new FileReader(m_sPath + sFile);			
			BufferedReader br = new BufferedReader(fr);
			
			while(br.ready())
			{
				String 		sLine;
				sLine = br.readLine();
				
				if(!getFlag_AdjustTime())
				{
					processLine(sLine, 0.0);
				}
				else
				{
					processLine(sLine, getAdjustTime());
				}
			}
			
			br.close();
			fr.close();
		}
		catch (IOException e)
		{
			System.err.println(e);
			return false;
		}
	
		return true;
	}
	
	/**
	 * If the function is used, the proximity files have to be
	 * named in date format as YYYYMMDD, e.g. 20120206.
	 * @return
	 */
	public Double extractFirstFileStartTime ()
	{
		String fileName = m_sFileList.get(0);
		String dt = fileName.substring(0, 8) + " 00:00:00";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date date = null;
        
		try 
		{
			date = df.parse(dt);
		} 
		catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Long epoch = date.getTime();
	    Long seconds = TimeUnit.MILLISECONDS.toSeconds(epoch);
		return seconds.doubleValue();
	}
	
	/** 
	 * Process a single line when we are in line-by-line processing.  
	 * 
	 * @param sLine The data read from the line to process
	 * @return True if successful, false otherwise
	 */
	public boolean processLine (String sLine, Double fTimeAdjust)
	{
		switch(getDataSubMode())
		{
			case DataLoaderBluetooth.DATALOADER_BLUETOOTH_MODE_TEST:
			    return processLine_RipCSV(sLine, fTimeAdjust);
		}
		
		return false;
	}

	public boolean processLine_RipCSV (String sLine, Double fTimeOffset)
	{
		// File format
		//     Node, Time (UTC), Neighbor, Mac Addr, RSSI
		// Comma-separated as follows
		//	   socs043, 1383368329, socs050, E8:99:C4:5E:D1:DD, -89
		// Split it up
		String [] sFields = sLine.split(",");
		if(sFields.length < 5)
		{
			m_Stats.adjustValue("Ignored", 1.0);
			return false;
		}
		
		// Create our event 
		EventProximitySense		theProxEvent;
		
		// Which node is it
		String		sNode;
		Double		fEventTime;
		String 		sNeighbor;
		int			nRSSI;
		
		sNode = sFields[0];
		fEventTime = Double.parseDouble(sFields[1]) - fTimeOffset;
		sNeighbor = sFields[2];
		nRSSI = 0;
		try
		{
			nRSSI = Integer.parseInt(sFields[4]);
		} 
		catch (NumberFormatException e)
		{
			//System.out.println(sLine);
		}
		
		m_Stats.adjustValue("Lines", 1.0);
		
		// Criterion
		//    The last event must not be null
		//    The name for this mobile node and the last mobile node must match
		//    The time must also match for the event
		//	  For now both nodes must be socs devices
		if(m_LastEvent != null && 
				m_LastEvent.getSimObject().getName().compareTo(sNode) == 0 && 
				m_LastEvent.getTime().equals(fEventTime) && 
				sNode.contains("socs") && 
				sNeighbor.contains("socs"))
		{	
			ProximityInstance	theInstance;
			theInstance = new ProximityInstance();
			theInstance.setName(sNeighbor);
			theInstance.setInstanceTime(fEventTime);
			theInstance.setSignalStrength(nRSSI);
			
			m_LastEvent.addProximityInstance(theInstance);
		}
		else 
		{
			theProxEvent = new EventProximitySense();
			m_LastEvent = theProxEvent;
			
			// Try to find the node from the master list
			SimObject theSimObject;
			MobileNode theMobileNode;

			theSimObject = SimulationEngine.theEngine.resolveObjectByProperName(sNode);

			if (theSimObject == null) {
				// Did not find it, should we add it?
				theMobileNode = new MobileNode();
				theMobileNode.setName(sNode);
				
				SimulationEngine.theEngine.registerSimObject(sNode, theMobileNode);
			} else {
				// Found it!
				theMobileNode = (MobileNode) theSimObject;
			}
			
			// At this point we should have a good mobile node
			theProxEvent.setSimObject(theMobileNode);
			
			// Set the time as whatever time is specified in the file minus the offset
			theProxEvent.setTime(fEventTime);
			
			// Add the proximity instance
			ProximityInstance	theInstance;
			theInstance = new ProximityInstance();
			theInstance.setName(sNeighbor);
			theInstance.setInstanceTime(fEventTime);
			theInstance.setSignalStrength(nRSSI);
			
			theProxEvent.addProximityInstance(theInstance);
			
			// Add the current event into the queue
			SimulationEngine.theEngine.addEvent(theProxEvent);
		}
			
		// Also need to register the neighbor mobile node, for now only "socs"
		SimObject simObject;
		MobileNode mobileNode;
		
		simObject = SimulationEngine.theEngine.resolveObjectByProperName(sNeighbor);
		
		if (simObject == null && sNeighbor.contains("socs"))
		{
			// Did not find it, should we add it?
			mobileNode = new MobileNode();
			mobileNode.setName(sNeighbor);
			
			SimulationEngine.theEngine.registerSimObject(sNeighbor, mobileNode);
		}
		
		return true;
	}
	
	public void summarizeLoad ()
	{
		System.out.println(" B/T File load completed");
		System.out.println("    " + m_Stats.get("Lines").getValue() + " lines processed");
		System.out.println("    " + m_Stats.get("Ignored").getValue() + " lines ignored");
	}
}
