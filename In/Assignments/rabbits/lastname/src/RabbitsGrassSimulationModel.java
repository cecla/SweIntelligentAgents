import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.util.SimUtilities;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.analysis.BinDataSource;
import uchicago.src.sim.analysis.OpenHistogram;
import uchicago.src.sim.analysis.DataSource;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;

import java.awt.Color;
import java.util.ArrayList;

/**
 * Class that implements the simulation model for the rabbits grass
 * simulation.  This is the first class which needs to be setup in
 * order to run Repast simulation. It manages the entire RePast
 * environment and the simulation.
 *
 * @author 
 */


public class RabbitsGrassSimulationModel extends SimModelImpl {	
	//Schedule-object schedule-var
	private Schedule schedule;
	
	//must create get and set methods for each parameter in the list
	
	//Default values
	private static final int NUMAGENTS = 4;
	private static final int WORLDXSIZE = 20;
	private static final int WORLDYSIZE = 20;
	private static final int TOTALGRASS = 100;
	private static final int AGENT_MIN_LIFESPAN = 30;
	private static final int AGENT_MAX_LIFESPAN = 50;
	private static final int AMOUNT_OF_GRASS = 10;
	
	private int numAgents = NUMAGENTS;
	private int worldXSize = WORLDXSIZE;
	private int worldYSize = WORLDYSIZE;
	private int grass = TOTALGRASS;
	private int agentMinLifespan = AGENT_MIN_LIFESPAN;
	private int agentMaxLifespan = AGENT_MAX_LIFESPAN;
	private int amountOfGrass = AMOUNT_OF_GRASS;
	
	private RabbitsGrassSimulationSpace rgsSpace;
	
	//creating a list of agents
	private ArrayList agentList;
	
	private DisplaySurface displaySurf;
	
	//using for open new displays with graphs
	//private OpenSequenceGraph amountOfGrassInSpace;
	//private OpenHistogram agentWealthDistribution;
	
	
		public static void main(String[] args) 
		{
			
			System.out.println("Rabbit skeleton");
			
		}
		
		//statics
		/*class grassInSpace implements DataSource, Sequence
		{
			public Object execute()
			{
				return (double)rgsSpace.getTotalGrass();
			}
			
			public double getSValue()
			{
				return new Double(getSValue());
			}
		}
		
		class agentGrass implements BinDataSource
		{
			public double getBinValue(Object o)
			{
				RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent)o;
				return (double)rgsa.getGrass();
			}
		}
		*/
		
		public void begin() {
			// TODO Auto-generated method stub
			//call the buildModel
			buildModel();
			
			buildSchedule();
			buildDisplay();
			
			//displays, turn on the the display
			displaySurf.display();
			//amountOfGrassInSpace.display();
			//agentWealthDistribution.display();
		}
		
		public void buildModel()
		{
			System.out.println("Running BuildModel");
			
			//create the space-model, set the default grid 20x20
			rgsSpace = new RabbitsGrassSimulationSpace(worldXSize, worldYSize);
			//call spreadGrass to randomly deposit an amount of grass around the objectSPace
			rgsSpace.spreadGrass(grass);
			
			//call a function to create agents
			for(int i = 0; i < numAgents; i++)
			{
				addNewAgent();
			}
			
			//give a unique ID for each instance of an object
			for(int i = 0; i  < agentList.size(); i++)
			{
				RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent)agentList.get(i);
				rgsa.report();
			}
		}
		
		public void buildSchedule()
		{
			System.out.println("Running buildSchedule");
			
			class RabbitsGrassSimulationStep extends BasicAction
			{
				public void execute()
				{
					SimUtilities.shuffle(agentList);
					for(int i = 0; i < agentList.size(); i++)
					{
						RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent)agentList.get(i);
						rgsa.step();
					}
					
					int deadAgents = reapDeadAgents();
					for(int i = 0; i < deadAgents; i++)
					{
						addNewAgent();
					}
					
					displaySurf.updateDisplay();
				}
			}
			
			schedule.scheduleActionBeginning(0, new RabbitsGrassSimulationStep());
			
			class RabbitsGrassSimulationLiving extends BasicAction
			{
				public void execute()
				{
					countLivingAgents();
				}
			}
			
			schedule.scheduleActionAtInterval(10, new RabbitsGrassSimulationLiving());
			
			/*class RabbitsGrassSimulationUpdate extends BasicAction
			{
				public void execute()
				{
					amountOfGrassInSpace.step();
				}
			}
			
			schedule.scheduleActionAtInterval(10, new RabbitsGrassSimulationUpdate());
			
			class RabbitsGrassSimulationUpdateAgentWealth extends BasicAction
			{
				public void execute()
				{
					agentWealthDistribution.step();
				}
			}
			
			schedule.scheduleActionAtInterval(10, new RabbitsGrassSimulationUpdateAgentWealth());
			*/
		}
				
		public void buildDisplay()
		{
			System.out.println("Running BuildDisplay");
			
			//create a colorMap object
			ColorMap map = new ColorMap();
			
			//mapping between values and colors, creating grass
			for(int i = 1; i < getAmountOfGrass(); i++)
			{
				//color(r,g,b)
				map.mapColor(i, new Color(0, 150 , 0));
			}
			//zero grass is set to white = backgroundcolor
			map.mapColor(0, Color.black);
			
			//links a grid of cells and a color map, linnk the grassSpace grid of the RabbitsGrassSpace object
			Value2DDisplay displayGrass = new Value2DDisplay(rgsSpace.getCurrentGrassSpace(), map);
			
			//links a grid and something to be displayed on that grid
			Object2DDisplay displayAgents = new Object2DDisplay(rgsSpace.getCurrentAgentSpace());
			
			//add Value2DDisplay to the display surface
			displaySurf.addDisplayableProbeable(displayGrass, "Grass");
			//dislplay agents
			displaySurf.addDisplayableProbeable(displayAgents, "Agents");
			
			//amountOfGrassInSpace.addSequence("Grass In Space", new grassInSpace());
			//agentWealthDistribution.createHistogramItem("Agent Wealth", agentList, new agentGrass());
		}
		
		//creates numAgents and adds them to the list of agents
		private void addNewAgent()
		{
			RabbitsGrassSimulationAgent a = new RabbitsGrassSimulationAgent(agentMinLifespan, agentMaxLifespan);
			agentList.add(a);
			//add agent to the space
			rgsSpace.addAgent(a);
		}
		
		private int reapDeadAgents()
		{
			int count = 0;
			for(int i = (agentList.size() - 1); i >= 0; i--)
			{
				RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent)agentList.get(i);
				if(rgsa.getStepsToLive() < 1)
				{
					rgsSpace.removeAgentAt(rgsa.getX(), rgsa.getY());
					rgsSpace.spreadGrass(rgsa.getGrass());
					agentList.remove(i);
					count++;
				}
			}
			return count;
		}
		
		private int countLivingAgents()
		{
			int livingAgents = 0;
			for(int i = 0; i < agentList.size(); i++)
			{
				RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent)agentList.get(i);
				if(rgsa.getStepsToLive() > 0) livingAgents++;
			}
			
			System.out.println("Number of living agents is: " + livingAgents);
			
			return livingAgents;
		}

		public String[] getInitParam() {
			// TODO Auto-generated method stub
			String[] initParams = {"NumAgents", "WorldXSize", "WorldYSize", "Grass", "AgentMinLifespan","AgentMaxLifespan", "AmountOfGrass"};
			return initParams;
		}
		
		public int getAmountOfGrass()
		{
			return amountOfGrass;
		}
		
		public void setAmountOfGrass(int ag)
		{
			amountOfGrass = ag;
		}
		
		public String getName() {
			// TODO Auto-generated method stub
			return "Our awesome swedish model";
		}

		public Schedule getSchedule() {
			// TODO Auto-generated method stub
			
			return schedule;
		}

		public void setup() {
			// TODO Auto-generated method stub
			System.out.println("Running setup");
			
			//creating an object var. to hold the space object
			rgsSpace = null;
			
			agentList = new ArrayList();
			schedule = new Schedule(1);
			
			//tear down displays, check if the object has been instantiated
			if (displaySurf != null)
			{
				displaySurf.dispose();
			}
			//set the object to null
			displaySurf = null;
			
			/*if(amountOfGrassInSpace != null)
			{
				amountOfGrassInSpace.dispose();
			}
			amountOfGrassInSpace = null;
			
			if(agentWealthDistribution != null)
			{
				agentWealthDistribution.dispose();
			}
			agentWealthDistribution = null;*/
			
			//create displays
			//Instantiate a new display surface, 
			//the model object for which this display will be serving
			//the name of the display
			displaySurf = new DisplaySurface(this, "Awesome Window Model 1");
			//amountOfGrassInSpace = new OpenSequenceGraph("Amount Of Grass In Space", this);
			//agentWealthDistribution = new OpenHistogram("Agent Wealth", 8, 0);
			
			//register displays, tell the RePast that it exsist nad what its name is
			registerDisplaySurface("Awesome Window model 1", displaySurf);
			//this.registerMediaProducer("Plot",  amountOfGrassInSpace);
		}
		
		public int getNumAgents()
		{
			return numAgents;
		}
		
		public void setNumAgents(int na)
		{
			numAgents = na;
		}
		
		public int getWorldXSize()
		{
			return worldXSize;
		}
		
		public void setWorldXSize(int wxs)
		{
			worldXSize = wxs;
		}
		
		public int getWorldYSize()
		{
			return worldYSize;
		}
		
		public void setWorldYSize(int wys)
		{
			worldYSize = wys;
		}
		
		public int getGrass()
		{
			return grass;
		}
		
		public void setGrass(int i)
		{
			grass = i;
		}
		
		public int getAgentMaxLifespan()
		{
			return agentMaxLifespan;
		}
		
		public int getAgentMinLifespan()
		{
			return agentMinLifespan;
		}
		
		public void setAgentMaxLifespan(int i)
		{
			agentMaxLifespan = i;
		}
		
		public void setAgentMinLifespan(int i)
		{
			agentMinLifespan = i;
		}
}
