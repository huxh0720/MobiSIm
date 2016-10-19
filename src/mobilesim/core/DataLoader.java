package mobilesim.core;

import java.util.*;
import java.io.*;

/** 
 * A data loader is a basic object that helps shepherd in behavior files into the
 * simulation. The base object (this one) captures the partial-load behavior for a 
 * file whereby one can effectively bridge in blocks of data (10 minutes, 1 hour, 1 day)
 * rather than reading in the entirety of a file (1 month, 1 year).  Data loader objects
 * are largely nameless but have the ability to register a timer whereby they will be
 * invoked to read more data.
 * 
 * @author Striegel
 *
 */
public class DataLoader extends SimObject {

	// General notes
	//
	// For now, we just support a single file - all in one big burst.  We will add in the partial
	// load behavior later

	String			m_sPath;
	Vector<String>	m_sFileList;
	FileReader		m_CurrentFile;
	BufferedReader	m_CurrentBuffer;
	
	// Base / overall mode
	int				m_nDataMode;
	
	// Sub-mode (typically used by the child classes - default is zero)
	int				m_nDataSubMode;
	
	public static final int DATALOADER_MODE_LINE = 0;
	public static final int DATALOADER_MODE_XML = 1;
	
	// How far should we read?
	Double			m_fMaxReadTime;
	
	// Time adjustment for simulation for offset of file
	boolean			m_bAdjustTime;	
	Double			m_fAdjustTime;
	
	Double			m_fLastReadTime;
	
	DataLoader ()
	{
		m_sPath = "";
		m_sFileList = new Vector<String>();
		m_CurrentFile = null;
		m_CurrentBuffer = null;
		m_nDataMode = DATALOADER_MODE_LINE;
		
		m_fMaxReadTime = Double.MAX_VALUE;
		
		m_bAdjustTime = false;
		m_fAdjustTime = 0.0;
		
		m_nDataSubMode = 0;
		
		m_fLastReadTime = Double.MIN_VALUE;
	}
	
	public void resetDataLoader ()
	{
		return;
	}
	
	/** 
	 * Set the path where data for this particular loader can be found
	 * 
	 * @param sPath The new path under which data for this path can be found
	 * @return True if successful, false otherwise
	 */
	public boolean setPath (String sPath)
	{
		m_sPath = sPath;		
		// TODO: Include validation for the path later?
		return true;
	}
	
	/** 
	 * The path points to the sub-directory where the data for this particular loader
	 * may be found.
	 * 
	 * @return
	 */
	public String getPath ()
	{
		return m_sPath;
	}
	
	/** 
	 * Set the data mode used for processing individual files between either a line by line
	 * version as typically done with a CSV or via tag-style processing as with XML.  
	 * 
	 * @param nMode The new mode
	 */
	public void setDataMode (int nMode)
	{
		m_nDataMode = nMode;
	}
	
	/** 
	 * Retrieve the mode that the data loader should use when accessing files for this particular
	 * data loader as either a line-by-line version (default, used by CSV-style files) or for a 
	 * XML style file (to be implemented).  
	 * @return
	 */
	public int getDataMode ()
	{
		return m_nDataMode;
	}
	
	/** 
	 * Set the data sub mode as used by child classes to distinguish file formats
	 * 
	 * @param nMode
	 */
	public boolean setDataSubMode (int nMode)
	{
		if(isValidSubMode(nMode))
		{
			m_nDataSubMode = nMode;
			return true;
		}
		else		
		{
			return false;
		}
	}
	
	/** 
	 * Retrieve the data sub mode typically used by the child classes to distinguish file formats
	 * 
	 * @return
	 */
	public int getDataSubMode ()
	{
		return m_nDataSubMode;
	}
	
	/** 
	 * Check if the particular sub mode specified is a valid one?
	 * 
	 * @param nMode
	 * @return
	 */
	public boolean isValidSubMode (int nMode)
	{
		if(nMode == DATALOADER_MODE_LINE || nMode == DATALOADER_MODE_XML)
		{
			return true;
		}
		else 
	    {
			return false;
	    }
	}
	
	public void setMaxReadTime (Double fTime)
	{
		// TO-DO sanity check?
		m_fMaxReadTime = fTime;	
	}

	public Double getMaxReadTime ()
	{
		return m_fMaxReadTime;
	}
	
	/** 
	 * Set the flag denoting if the date should be adjusted as the time since the base date
	 * 
	 * @param bAdjust
	 */
	public void setFlag_AdjustTime (boolean bAdjust)
	{
		m_bAdjustTime = bAdjust;
	}
	
	/** 
	 * Retrieve the flag denoting if the date should be adjusted to the base date specified.  If the
	 * flag is false, the UTC seconds value will be used for full date / time values.  
	 * 
	 * @return
	 */
	public boolean getFlag_AdjustTime ()
	{
		return m_bAdjustTime;
	}
	
	public void setAdjustTime (Double theBase)
	{
		m_fAdjustTime = theBase;
	}
	
	/** 
	 * Get the base time (in UTC) where the base date is used to correct / compute the time from a given
	 * start date as the simulator works in seconds.
	 * 
	 * @return
	 */
	public Double getAdjustTime ()
	{
		return m_fAdjustTime;
	}
	
	/** 
	 * Set the last read time to denote the last timestamp of the data line or element last processed.
	 * 
	 * @param fReadTime
	 */
	public void setLastReadTime (Double fReadTime)
	{
		m_fLastReadTime = fReadTime;
	}
	
	/** 
	 * Retrieve the time as last read by the data reader that typically denotes the timestamp of
	 * the data line or element recently processed.
	 * 
	 * @return
	 */
	public Double getLastReadTime ()
	{
		return m_fLastReadTime;
	}
	
	/** 
	 * Add a file or wildcard to the list for processing by this data loader with the 
	 * path pre-pended to the file. The filename may either be an absolute filename,
	 * a wildcard, or an absolute path (if the path setting is empty). Files are assumed
	 * to be sorted by time unless otherwise specified and also that the list of sorted
	 * file names will also be sorted by time in the case of a wildcard.  
	 * 
	 * @param sFile
	 * @return
	 */
	public boolean addFile (String sFile)
	{
		if(sFile.isEmpty())
		{
			return false;
		}
		
		m_sFileList.add(sFile);
		return true;
	}
	
	/** 
	 * Retrieve a reference to the file that is currently opened by the data loader.
	 * 
	 * @return A reference to the file that is current open
	 */
	protected FileReader getCurrentFile ()
	{
		return m_CurrentFile;
	}

	/** 
	 * The initial file load for all data loaders is invoked by the simulation engine at the
	 * start of the simulation. This code should not be overridden by a child as it handles
	 * the queuing of files (if there is a wildcard) and queuing of files / handoffs between
	 * different files. 
	 * @return
	 */
	public boolean startFileLoad ()
	{
		if(getFlag_AdjustTime())
		{
			setAdjustTime(scanFirstFileLowestTime());
		}
		
		return true;
	}
	
	public Double scanFirstFileLowestTime ()
	{
		FileReader		fileRead;
		BufferedReader	bufRead;
		
		// TODO: What happens if we are a wildcard?
		
		try 
		{
			fileRead = new FileReader(m_sPath + m_sFileList.get(0));			
			bufRead = new BufferedReader(fileRead);			
		}
		catch (Exception e)
		{
			System.err.println("* Error: Unable to open the file and buffer it");
			System.err.println(e);
			return -1.0;
		}

		Double 	fLowestTime;
		Double	fTime;
		
		fLowestTime = Double.MAX_VALUE;
		
		try 
		{
			while(bufRead.ready())
			{
				String 		sLine;
				sLine = bufRead.readLine();
				
				fTime = extractTimeFromLine(sLine);
				
				if(fTime > 0 && fTime < fLowestTime)
				{
					fLowestTime = fTime;
				}
			}
		}
		catch (Exception e)
		{
			// TODO: Handle it
			System.err.println(e);
		}
		
		try
		{
			bufRead.close();
		}
		catch(Exception e)
		{
			// TODO: Handle it
			System.err.println(e);
		}
		
		return fLowestTime;
	}
	
	public Double extractTimeFromLine (String sLine)
	{
		return -1.0;
	}
	
	public boolean startNextFile (String sLastFile)
	{
		// TODO: Fill in behavior
		
		System.out.println("Loading file: " + m_sPath + m_sFileList.get(0));
		
		if(m_CurrentFile == null)
		{
			try 
			{
				m_CurrentFile = new FileReader(m_sPath + m_sFileList.get(0));			
				m_CurrentBuffer = new BufferedReader(m_CurrentFile);			
			}
			catch (Exception e)
			{
				System.err.println("* Error: Unable to open the file and buffer it");
				System.err.println(e);
				return false;
			}
		}
		
		try 
		{
			while(m_CurrentBuffer.ready())
			{
				String 		sLine;
				sLine = m_CurrentBuffer.readLine();
				
				if(!getFlag_AdjustTime())
				{
					processLine(sLine, 0.0);
				}
				else
				{
					processLine(sLine, getAdjustTime());
				}
			}
		}
		catch (Exception e)
		{
			// TODO: Handle it
		}
		
		//SimulationEngine.theEngine.summarizeQueue();
		summarizeLoad();
		
		return true;
	}
	
	/** 
	 * Process a single line when we are in line-by-line processing.  This should generally be 
	 * overriden by the child class
	 * 
	 * @param sLine The data read from the line to process
	 * @return True if successful, false otherwise
	 */
	public boolean processLine (String sLine, Double fTimeAdjust)
	{
		return false;
	}
	
	/** Dump the state of the currently open file to the error stream for assistance with
	 * debugging and troubleshooting
	 * 
	 */
	public void dumpFileState ()
	{
		// We had an error, help a brother out
		if(m_CurrentFile != null)
		{		
			//System.err.println(" File: " + m_CurrentFile.getName());
			// TODO: Add more info
		}
	}

	public void summarizeLoad ()
	{
		return;
	}
}


