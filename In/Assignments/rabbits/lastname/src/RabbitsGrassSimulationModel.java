import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.reflector.RangePropertyDescriptor;
import uchicago.src.sim.analysis.BinDataSource;
import uchicago.src.sim.analysis.DataSource;
import uchicago.src.sim.analysis.OpenHistogram;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.util.SimUtilities;

/**
 * Class that implements the simulation model for the rabbits grass
 * simulation.  This is the first class which needs to be setup in
 * order to run Repast simulation. It manages the entire RePast
 * environment and the simulation.
 *
 * @author 
 */


public class RabbitsGrassSimulationModel extends SimModelImpl {		
	//Default values
	private static final int NUMAGENTS = 20;
	private static final int WORLDXSIZE = 20;
	private static final int WORLDYSIZE = 20;
	private static final int TOTALGRASS = 500;
	private static final int AGENT_MAX_ENERGY = 50;
	private static final int AGENT_MIN_ENERGY = 30;
	private static final int BIRTH_THRESHOLD = 50; // energy level required for giving birth
	private static final int BIRTHCOST = 20; // energy exhausted when spawning offspring. NOT ADDED TO INIT PARAMS.
	private static final int GRASS_GROWTH_RATE = 20;
	
	
	private Schedule schedule;
	
	// create space in the model-class
	private RabbitsGrassSimulationSpace rgsSpace;
	
	private DisplaySurface displaySurf;
	
	private ArrayList<RabbitsGrassSimulationAgent> agentList;
	
	private int numAgents = NUMAGENTS;
	private int worldXSize = WORLDXSIZE;
	private int worldYSize = WORLDYSIZE;
	private int grass = TOTALGRASS;
	private int maxBirthEnergy = AGENT_MAX_ENERGY;
	private int minBirthEnergy = AGENT_MIN_ENERGY;
	private int birthThreshold = BIRTH_THRESHOLD;
	private int birthCost = BIRTHCOST;
	private int grassGrowthRate = GRASS_GROWTH_RATE;
	
	private OpenHistogram agentPopulation;
	private OpenSequenceGraph livingAgents;
	private OpenSequenceGraph grassInSpace;
	
	class agentsOverTime implements BinDataSource{
		
		public double getBinValue(Object o) {

				RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent) o;
				
				int livingAgents = countLivingAgents();
				
				return livingAgents;
			
		    }
	}
	
	
	// creating two classes for creating charts of living agents and amount of grass in space.
	class livingAgentsGraph implements DataSource,Sequence{
		
		public Object execute(){
			
			return new Double(getSValue());
			
		}

		@Override
		public double getSValue() {
			// TODO Auto-generated method stub
			
			return (double)countLivingAgents();
		}
		
	}
	
	class grassInSpaceGraph implements DataSource,Sequence{
		
		public Object execute(){
			
			return new Double(getSValue());
			
		}
		@Override
		public double getSValue() {
			// TODO Auto-generated method stub
			
			return (double)getTotalGrassAmount();
		}
	}
	
	
		public static void main(String[] args) {
			
			System.out.println("Rabbit skeleton");
			
		}

		public void begin() {
			// TODO Auto-generated method stub
			buildModel();
			buildSchedule();
			buildDisplay();
			
			displaySurf.display();
			//agentPopulation.display();
			livingAgents.display();
			grassInSpace.display();
			
		}
		
		public void buildModel()
		{
			System.out.println("Running buildModel");
			// create a new space object 
			rgsSpace = new RabbitsGrassSimulationSpace(worldXSize, worldYSize);
			
			// grow grass randomly
			rgsSpace.growGrass(grass);
			
			// add agents
			for(int i = 0; i<numAgents;i++){
				addNewAgent();	
			}
		}
		
		public void buildSchedule()
		{
			System.out.println("Running buildSchedule");
			
			// Creating a class to handle each increment of step.
			class RabbitsGrassStep extends BasicAction{
				
				public void execute(){
					
					// Shuffling the agent list to so that they won't move in the same order every step
					SimUtilities.shuffle(agentList);
					for(int i = 0; i<agentList.size(); i++ ){
						RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent) agentList.get(i);
						rgsa.report();
						rgsa.step();
					}
					
					//remove dead agents
					removeDeadAgents();
					
					// current rabbit agents give birth to new agents
					spawnNewAgents();
					
					// grow new grass
					rgsSpace.growGrass(grassGrowthRate);
					
					// update display every step
					displaySurf.updateDisplay();
					
				}
			}
			
			// schedule action to be called every turn
			schedule.scheduleActionBeginning(0, new RabbitsGrassStep());
			
			/*class RabbitsGrassCountLiving extends BasicAction{
				
				public void execute(){
					
					countLivingAgents();
				
				}			
			}
			// count living every 10 steps
			schedule.scheduleActionAtInterval(10, new RabbitsGrassCountLiving());
			
			*/
			
			// update chart with number of living agents
			class RabbitsGrassUpdateLivingAgents extends BasicAction{
				public void execute(){
					livingAgents.step();
				}
				
			}
			
			schedule.scheduleActionAtInterval(10, new RabbitsGrassUpdateLivingAgents());
			
			// update grass in space chart
			class RabbitsGrassUpdateTotalGrass extends BasicAction{
				
				public void execute(){
					
					grassInSpace.step();
					
				}	
			}
			
			schedule.scheduleActionAtInterval(10, new RabbitsGrassUpdateTotalGrass());
			
			
			
		}
		
		public void buildDisplay()
		{
			System.out.println("Running buildDisplay");
			
			// Creating a new color mapping
			ColorMap map = new ColorMap();
			
			for(int i = 1; i<16;i++){
				// make the grass green
				map.mapColor(i, new Color(0,(int)(i*8+127),0));
			}
			map.mapColor(0, Color.black);
			
			Value2DDisplay displayGrass = new Value2DDisplay(rgsSpace.getCurrentGrassSpace(), map);
			
			Object2DDisplay displayAgents = new Object2DDisplay(rgsSpace.getCurrentAgentSpace());
			displayAgents.setObjectList(agentList);
			
			displaySurf.addDisplayableProbeable(displayGrass, "Grass");
			displaySurf.addDisplayableProbeable(displayAgents, "Agents");
			
		    //agentPopulation.createHistogramItem("Living Agents Over Time",agentList,new agentsOverTime());
		
		    livingAgents.addSequence("Living Agents", new livingAgentsGraph());
		    grassInSpace.addSequence("Grass in Space", new grassInSpaceGraph());
		    
		}

		//parameters that we can change before the setup
		public String[] getInitParam() 
		{
			String[] initParams = {"NumAgents", "WorldXSize", "WorldYSize", "TotalGrass",
					"MinBirthEnergy", "MaxBirthEnergy", "BirthThreshold", "GrassGrowthRate", "BirthCost"};
			return initParams;
		}
		
		public String getName() {
			// TODO Auto-generated method stub
			return "Rabbits Grass Simulation";
		}

		public Schedule getSchedule() {
			// TODO Auto-generated method stub
			return schedule;
		}

		public void setup() {
			// TODO Auto-generated method stub
			//System.out.println("Running setup");
			//reset
			rgsSpace = null;
			// create a new agentList
			agentList = new ArrayList<RabbitsGrassSimulationAgent>();
			schedule = new Schedule(1);
			
			// creating sliders for init parameters
			RangePropertyDescriptor pdAgents = new RangePropertyDescriptor("NumAgents",0,100,20);
			descriptors.put("NumAgents", pdAgents);
			
			RangePropertyDescriptor pdWorldXSize = new RangePropertyDescriptor("WorldXSize",20,100,20);
			descriptors.put("WorldXSize", pdWorldXSize);
			
			RangePropertyDescriptor pdWorldYSize = new RangePropertyDescriptor("WorldYSize",20,100,20);
			descriptors.put("WorldYSize", pdWorldYSize);
			
			RangePropertyDescriptor pdBirthThreshold = new RangePropertyDescriptor("BirthThreshold",0,70,10);
			descriptors.put("BirthThreshold", pdBirthThreshold);
			
			RangePropertyDescriptor pdGrassGrowthRate = new RangePropertyDescriptor("GrassGrowthRate",0,100,10);
			descriptors.put("GrassGrowthRate", pdGrassGrowthRate);
			
			
			// reset old display
			if (displaySurf != null)
			{
				displaySurf.dispose();
			}
			displaySurf = null;
			
			displaySurf = new DisplaySurface(this, "Rabbits Grass Model Window 1");
			
			//reset the graphs and histogram
			if (livingAgents != null){
				livingAgents.dispose();
			    }
			livingAgents = null;
			
			if (grassInSpace != null){
				grassInSpace.dispose();
			}
			grassInSpace = null;
			
			
			/*if (agentPopulation != null){
				//agentPopulation.dispose();
			    }
			agentPopulation = null;
		    
			//agentPopulation = new OpenHistogram("Living Agents Over Time", 10, 0);
			*/
			
			livingAgents = new OpenSequenceGraph("Living Agents Over Time",this);
			grassInSpace = new OpenSequenceGraph("Total Amount of Grass in Space",this);
			
			registerDisplaySurface("Rabbits Grass Model Window 1", displaySurf);
			
			this.registerMediaProducer("Plot", livingAgents);
			this.registerMediaProducer("Plot", grassInSpace);
			
		}
		
		private void addNewAgent(){
			
			RabbitsGrassSimulationAgent rgsAgent = new RabbitsGrassSimulationAgent(minBirthEnergy,maxBirthEnergy);
			agentList.add(rgsAgent);
			rgsSpace.addAgent(rgsAgent);
		
		}
		
		public void setNumAgents(int na)
		{
			numAgents = na;
		}
		
		public int getNumAgents()
		{
			return numAgents;
		}
		
		public int getWorldXSize()
		{
			return worldXSize;
		}
		
		public void setWorldXSize(int x)
		{
			worldXSize = x;
		}
		
		public int getWorldYSize()
		{
			return worldYSize;
		}
		
		public void setWorldYSize(int y)
		{
			worldYSize = y;
		}
		
		public int getTotalGrass()
		{
			return grass;
		}
		
		public void setTotalGrass(int tg)
		{
			grass = tg;
		}
		
		public void setMaxBirthEnergy(int e)
		{
			maxBirthEnergy = e;
		}
		
		public int getMaxBirthEnergy()
		{
			return maxBirthEnergy;
		}
		
		public void setMinBirthEnergy(int e)
		{
			minBirthEnergy = e;
		}
		
		public int getMinBirthEnergy()
		{
			return minBirthEnergy;
		}
		
		public void setBirthThreshold(int b)
		{
			birthThreshold = b;
		}
		
		public int getBirthThreshold()
		{
			return birthThreshold;
		}
		
		public int getGrassGrowthRate()
		{
			return grassGrowthRate;
		}
		
		public void setGrassGrowthRate(int gr)
		{
			grassGrowthRate = gr;
		}
		
		public int getBirthCost()
		{
			return birthCost;
		}
		
		public void setBirthCost(int bc)
		{
			birthCost = bc;
		}
		
		
		private int countLivingAgents(){
			int livingAgents = 0;
			for(int i = 0; i<agentList.size(); i++){
				RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent) agentList.get(i);
				int energy = rgsa.getEnergy();
				//if energy is higher than 0 - then the agent is still alive
				if(energy > 0){
					livingAgents++;
				}
			
			}
			System.out.println("Number of living agents: " + livingAgents);
			
			return livingAgents;	
		}
		
		
		private void removeDeadAgents(){
			// looping through agentList and checking energy levels, if equal to 0 or below -> remove agent
			for(int i =0; i<agentList.size();i++){
				RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent) agentList.get(i);
				if(rgsa.getEnergy() <= 0){
					rgsSpace.removeAgentAt(rgsa.getX(),rgsa.getY());
					agentList.remove(i); // removing agent from list which also removes pointer to the object
					
				}
			}		
		}
		
		private int getTotalGrassAmount(){
			
			int totalGrass = 0;
			
			for(int i = 0; i < worldXSize;i++){
				for(int j = 0; j < worldYSize; j++){
					
					totalGrass += rgsSpace.getGrassValueAt(i, j);
				}	
				
			}
			return totalGrass;
			
			
		}
		
		private void spawnNewAgents(){
			
			// check every agents energy and give birth if higher than threshold.
			for(int i = 0; i < agentList.size(); i++){
				
				RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent) agentList.get(i);
				int currentEnergy = rgsa.getEnergy();
				if(currentEnergy >= birthThreshold ){
					// spawn new agent and add to space. remove energy from parent.
					addNewAgent();
					rgsa.setEnergy(rgsa.getEnergy()-birthCost);

				}				
			}
		}
		
}
