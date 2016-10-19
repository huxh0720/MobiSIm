package mobilesim.console;

import java.io.*;

import mobilesim.core.*;

public class SimpleTest {
	
	public static void testContentProp48Hours ()
	{
		String [] files = {"20120206.csv", 
						   "20120207.csv", 
						   "20120208.csv",
						   "20120209.csv",
						   "20120210.csv"};
		
		DataLoaderBluetooth.theLoader.setPath("./BluetoothData/");
		DataLoaderBluetooth.theLoader.setFlag_AdjustTime(true);
		
		DataLoaderNetworkTraffic.theLoader.setPath("./TrafficData/Test/");
		
		for(int i = 0; i < files.length - SimulationEngine.SIMULATION_DURATION + 1; i++)
		{
			String dateComb = "";
			
			for(int j = 0; j < SimulationEngine.SIMULATION_DURATION; j++)
			{
				DataLoaderBluetooth.theLoader.addFile(files[i+j]);
				DataLoaderNetworkTraffic.theLoader.addFile(files[i+j]);
				
				dateComb += files[i+j].substring(0, 8);
			}
			
			DataLoaderBluetooth.theLoader.startFileLoad();
			DataLoaderNetworkTraffic.theLoader.startFileLoad();
			
			SimulationEngine.theEngine.summarizeQueue();
			SimulationEngine.theEngine.setDate(dateComb);
			
			EventSimDone	finishTime;
			finishTime = new EventSimDone();
			
			finishTime.setTime(SimulationEngine.SIMULATION_DURATION * 24.0 * 3600 - 1);
			SimulationEngine.theEngine.addEvent(finishTime);	
			
			SimulationEngine.theEngine.initializeRegisteredObjects();
			SimulationEngine.theEngine.doSimulation();
			
			SimulationEngine.theEngine.resetSimulation();
			
			DataLoaderBluetooth.theLoader.resetDataLoader();
			DataLoaderNetworkTraffic.theLoader.resetDataLoader();
		}
	}
	
	public static void testDailyContentProp ()
	{
		DataLoaderBluetooth dlBT;
		dlBT = new DataLoaderBluetooth();
		dlBT.setPath("./BluetoothData/");
		
		DataLoaderNetworkTraffic.theLoader.setPath("./TrafficData/HourlyTotal/");
		
		File directory = new File(dlBT.getPath());
		File [] fList = directory.listFiles();
		for(File file : fList) 
		{
			if(file.isFile())
			{
				String fileName = file.getName();
				dlBT.addFile(fileName);
				dlBT.setFlag_AdjustTime(true);
				dlBT.startFileLoad();
				
				DataLoaderNetworkTraffic.theLoader.addFile(fileName);
				DataLoaderNetworkTraffic.theLoader.startFileLoad();
				
				SimulationEngine.theEngine.summarizeQueue();
				SimulationEngine.theEngine.setDate(fileName.substring(0, 8));
				
				EventSimDone	finishTime;
				finishTime = new EventSimDone();
				
				finishTime.setTime(24.0*60*60-1);
				SimulationEngine.theEngine.addEvent(finishTime);	
				
				SimulationEngine.theEngine.initializeRegisteredObjects();
				SimulationEngine.theEngine.doSimulation();
				
				SimulationEngine.theEngine.resetSimulation();
				DataLoaderNetworkTraffic.theLoader.resetDataLoader();
			}
		}	
	}
	
	public static void testReadNetworkUsage ()
	{
		DataLoaderNetworkUsage 	dlNWU;
		
		dlNWU = new DataLoaderNetworkUsage();
		
		dlNWU.setPath("/Users/striegel/Documents/TestData/");
		dlNWU.addFile("19-DataTonnage.csv");
		
		//System.out.println("Start time is: " + dlNWU.scanFirstFileLowestTime());
		
		dlNWU.setFlag_AdjustTime(true);
		dlNWU.startFileLoad();		
		dlNWU.startNextFile(null);
		
		SimulationEngine.theEngine.summarizeQueue();
	
		EventSimDone	finishTime;
		
		finishTime = new EventSimDone();
		
		// Let's do the first hour
		//finishTime.setTime(60.0 * 60.0);
		finishTime.setTime(3600.0*24.0);
		SimulationEngine.theEngine.addEvent(finishTime);	
		
		SimulationEngine.theEngine.initializeRegisteredObjects();
		SimulationEngine.theEngine.doSimulation();		
		//SimulationEngine.theEngine.finishSimulation();
		
	}
	
	public static void test_StatValidation ()
	{
		MobileNode		UE1;
		MobileNode 		UE2;
		MobileNode		UE3;
		
		UE1 = new MobileNode();
		UE1.setName("UE1");
	
		UE2 = new MobileNode();
		UE2.setName("UE2");
		
		UE3 = new MobileNode();
		UE3.setName("UE3");
		
		SimulationEngine.theEngine.registerSimObject("UE1", UE1);
		SimulationEngine.theEngine.registerSimObject("UE2", UE2);
		SimulationEngine.theEngine.registerSimObject("UE3", UE3);
		
		SimulationEngine.theEngine.initializeRegisteredObjects();
		
		EventSimDone	finishTime;
		
		finishTime = new EventSimDone();
		finishTime.setTime(1000.0);
		SimulationEngine.theEngine.addEvent(finishTime);

		// Let's see all of the events whistle by for now
		DebugLogger.theLogger.getGeneralSetting().setDebugLevel(DebugLogger.SIM_DEBUG_LEVEL_EVENTS);
		
		SimulationEngine.theEngine.doSimulation();			
	}
	
	public static void testTimeResolution ()
	{
		System.out.println(resolveSecondsElapsed(3600*47+120));
	}
	
	private static String resolveSecondsElapsed (int nTimeElapsed)
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
	
	public static void main (String[] args)
	{
		//testTimeResolution();
		//testContentProp48Hours();
		testDailyContentProp();
		//test_StatValidation();
		//testReadNetworkUsage();
	}
}
