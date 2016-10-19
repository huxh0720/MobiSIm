package mobilesim.core;

/** 
 * A storage device provides an abstraction of a collection of content that is stored on a 
 * particular mobile device or other element
 * 
 * @author Striegel
 *
 */
public class StorageDevice extends SimObject {
	
	int			m_nCapacity;
	SimObject	m_Owner;

	// Is there a latency?
	Double		m_fLatencyWrite;
	Double		m_fLatencyRead;
	
	// Is there an energy cost to read or write?  Or simply to operate?
	//		Operation		Cost per second
	//		Read or Write	Cost per second for a solid read or write
	Double		m_fEnergyCost_Operation;
	Double		m_fEnergyCost_Read;
	Double		m_fEnergyCost_Write;
	
	// Is there a maximum read or write rate?
	Double		m_fRateRead;
	Double		m_fRateWrite;
	
	// What is the state of the device (reading, writing, idle)?
	int			m_nState;
	
	// The simple state ignores pretty much all of above settings (perhaps except for capacity)
	public static final int	STATE_STORAGEDEVICE_SIMPLE = 0;
	public static final int STATE_STORAGEDEVICE_IDLE = 1;
	public static final int STATE_STORAGEDEVICE_WRITE = 2;
	public static final int STATE_STORAGEDEVICE_READ = 3;
	// Low energy state
	public static final int STATE_STORAGEDEVICE_IDLE_LE = 4;
	
	boolean		m_bSimple;
	
	public StorageDevice ()
	{
		m_nCapacity = Integer.MIN_VALUE;
		m_Owner = null;
		m_nState = STATE_STORAGEDEVICE_SIMPLE;
	}
	
	public boolean setCapacity (int nCapacity)
	{
		m_nCapacity = nCapacity;
		return true;
	}
	
	public int getCapacity ()
	{
		return m_nCapacity;
	}
	
	public void setCapacityToInfinite ()
	{
		m_nCapacity = Integer.MIN_VALUE;
	}
	
	public boolean ignoreCapacity ()
	{
		if(m_nCapacity == Integer.MIN_VALUE){
			return true;
		}
		else
		{
			return false;
		}
	}
}
