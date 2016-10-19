package mobilesim.core;

import mobilesim.console.*;

public class DataFieldDouble extends DataField {
	Double m_fValue;
	
	public DataFieldDouble ()
	{
		m_fValue = 0.0;
	}
	
	protected void resetValue ()
	{
		m_fValue = 0.0;
	}
	
	public boolean setValue (String sValue)
	{
		if(sValue == null)
		{
			System.out.println("Error: Can't use null String to set data field.");
			return false;
		}
		
		try
		{
			m_fValue = Double.parseDouble(sValue);
			return true;
		}
		catch(NumberFormatException e)
		{
			DebugLogger.theLogger.logInfo_Warning("Unable to parse string in DataFieldDouble, provided value was " + sValue);
			return false;
		}
	}
	
	public boolean setValue (Double fValue)
	{
		m_fValue = fValue;
		return true;
	}
	
	public String getValueString ()
	{
		return m_fValue.toString();
	}
	
	public Double getValueDouble ()
	{
		return m_fValue;
	}
	
	public DataField computeDifference (String sValue, Double fTime)
	{
		DataFieldDouble theCompDouble;
		theCompDouble = new DataFieldDouble();
		
		if(theCompDouble.setValue(sValue))
		{
			theCompDouble.setUpdateTime(fTime - getUpdateTime());
			return theCompDouble;
		}
		else 
		{
			return null;
		}
	}
}
