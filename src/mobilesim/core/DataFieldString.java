package mobilesim.core;

public class DataFieldString extends DataField {
	String m_sValue;
	
	public DataFieldString ()
	{
		m_sValue = "";
	}
	
	protected void resetValue ()
	{
		m_sValue = "";
	}
	
	public boolean setValue (String sValue)
	{
		if(sValue == null)
		{
			return false;
		}
		m_sValue = sValue;
		return true;
	}
	
	public String getValue ()
	{
		return m_sValue;
	}
}
