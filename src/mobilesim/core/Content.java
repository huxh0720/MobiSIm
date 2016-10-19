package mobilesim.core;

/** 
 * One individual piece of content that is part of the simulation
 * 
 * @author Striegel
 *
 */
public class Content extends SimObject {
	// We get a content ID from the SimObject class.  It should be unique simulation wide.
	// Typically an ID look like src_socs043_0001 --> (original source)_(mobile node name)_(hr and min)
	String		m_sContentID;
	Double		m_fCreationTime;
	Double		m_fArrivialTime;
	int			m_nAccesses;
	Double		m_fLastAccessTime;

	// How big is this content? (in KB)
	double		m_fSize;
	
	// Who did we get it from and how many hops away were they?
	SimObject			m_OriginalSource;
	SimObject			m_Source;
	int					m_nSourceHops;

	// TODO: Segments or subsections?

	public Content ()
	{
		m_sContentID = "";
		m_fSize = 0;
		m_OriginalSource = null;
		m_Source = null;
		m_nSourceHops = -1;
		m_nAccesses = 0;
		
		m_fCreationTime = Double.MIN_VALUE;
		m_fArrivialTime = Double.MIN_VALUE;
		m_fLastAccessTime = Double.MIN_VALUE;
	}
	
	public void setContentID (String sID)
	{
		m_sContentID = sID;
	}
	
	public String getContentID ()
	{
		return m_sContentID;
	}
	
	/** 
	 * The creation time is when the object was created at this particular storage
	 * instance or object. Set the specific creation time. 
	 * 
	 * @param fCreate
	 */
	public void setCreationTime (Double fCreate)
	{
		m_fCreationTime = fCreate;
	}
	
	/** 
	 * Retrieve the creation time for this particular object
	 * 
	 * @return Creation time of the object (in seconds)
	 */
	public Double getCreationTime ()
	{
		return m_fCreationTime;
	}
	
	/** 
	 * Stamp the creation time with the current time of the simulation
	 */
	public void stampCreationTime ()
	{
		m_fCreationTime = SimulationEngine.theEngine.getTime();
	}
	
	public void setArrivialTime (Double fArrive)
	{
		m_fArrivialTime = fArrive;
	}
	
	public Double getArrivialTime ()
	{
		return m_fArrivialTime;
	}
	
	public void stampArrivialTime ()
	{
		m_fArrivialTime = SimulationEngine.theEngine.getTime();
	}
	
	public void setLastAccessTime (double fLastAccess)
	{
		m_fLastAccessTime = fLastAccess;
	}
	
	public Double getLastAccessTime ()
	{
		return m_fLastAccessTime;
	}
	
	public void stampLastAccessTime ()
	{
		m_fLastAccessTime = SimulationEngine.theEngine.getTime();
	}
	
	public void incrementAccesses ()
	{
		m_nAccesses++;
	}
	
	public boolean setContentSize (double fSize)
	{
		if (fSize < 0.0)
		{
			return false;
		}
		m_fSize = fSize;
		return true;
	}
	
	public double getContentSize ()
	{
		return m_fSize;
	}
	
	public void setOriginalSource (SimObject oSource)
	{
		m_OriginalSource = oSource;
	}
	
	public SimObject getOriginalSource ()
	{
		return m_OriginalSource;
	}
	
	public void setSource (SimObject theSource)
	{
		m_Source = theSource;
	}
	
	public SimObject getSource ()
	{
		return m_Source;
	}
	
	public void setSourceHops (int nHops)
	{
		m_nSourceHops = nHops;
	}
	
	public int getSourceHops ()
	{
		return m_nSourceHops;
	}
	
	public boolean isStreaming ()
	{
		if(m_fSize < 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
