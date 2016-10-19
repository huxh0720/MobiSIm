package mobilesim.core;

import java.io.*;

public class DataLoaderNetworkTraffic extends DataLoader {
	
	public static DataLoaderNetworkTraffic theLoader = new DataLoaderNetworkTraffic();
	
	public static final int DATALOADER_TRAFFIC_MODE_TEST = 0;
	
	String			m_sCurrentFile;
	
	double []		m_HourlyTrafficOverall;
	
	public DataLoaderNetworkTraffic ()
	{
		m_sCurrentFile = "";
		m_HourlyTrafficOverall = new double[24 * SimulationEngine.SIMULATION_DURATION];
		for (int i = 0; i < 24 * SimulationEngine.SIMULATION_DURATION; i++)
		{
			m_HourlyTrafficOverall[i] = 0.0;
		}	
	}
	
	public void resetDataLoader ()
	{
		m_sFileList.clear();
		m_sCurrentFile = "";
		for (int i = 0; i < 24*SimulationEngine.SIMULATION_DURATION; i++)
		{
			m_HourlyTrafficOverall[i] = 0.0;
		}
	}
	
	public double[] getHourlyTrafficOverall ()
	{
		return m_HourlyTrafficOverall;
	}
	
	public boolean startFileLoad ()
	{
		for(int i = 0; i < m_sFileList.size(); i++)
		{
			String theNextFile = m_sFileList.get(i);
			m_sCurrentFile = theNextFile;
			startNextFile(theNextFile);
		}
		
		return true;
	}
	
	public boolean startNextFile (String sFile)
	{
		int sFileIndex = m_sFileList.indexOf(sFile);
		try 
		{
			FileReader fr = new FileReader(m_sPath + sFile);
			BufferedReader br = new BufferedReader(fr);
			
			while(br.ready())
			{
				String 		sLine;
				sLine = br.readLine();
				processLine(sLine, sFileIndex);
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
	 * Process a single line when we are in line-by-line processing.  
	 * 
	 * @param sLine The data read from the line to process
	 * @return True if successful, false otherwise
	 */
	public boolean processLine (String sLine, int nFileIndex)
	{
		switch(getDataSubMode())
		{
			case DATALOADER_TRAFFIC_MODE_TEST:
			    return processLine_RipCSV(sLine, nFileIndex);
		}
		
		return false;
	}
	
	protected boolean processLine_RipCSV (String sLine, int nFileIndex)
	{
		// File format:
	    //     Node, rx vol at hour 0-1, ..., rx vol at hour 23-24
	    
		// Split it up
		String [] sFields = sLine.split(",");
		if(sFields.length < 25)
		{
			System.out.println("** Skip line in traffic file.");
			return false;
		}
		
		String		sNode;
		SimObject   theSimObject;
		MobileNode 	theMobileNode;
		
		sNode = sFields[0];
		theSimObject = SimulationEngine.theEngine.resolveObjectByProperName(sNode);
			
		if(theSimObject != null && theSimObject instanceof MobileNode)
		{
			theMobileNode = (MobileNode) theSimObject;
			for(int i = 1; i < sFields.length; i++)
			{
				double traffic = Double.parseDouble(sFields[i]);
				theMobileNode.setHourlyTraffic(i + 24*nFileIndex - 1, traffic);
				m_HourlyTrafficOverall[i + 24*nFileIndex - 1] += traffic;
			}
		}
		
		return true;
	}
	
	public void dumpHourlyTrafficToConsole ()
	{
		for(int i = 0; i < m_HourlyTrafficOverall.length; i++)
		{
			System.out.println("Traffi at Hour " + i + ": " + m_HourlyTrafficOverall[i]);
		}
	}

}
