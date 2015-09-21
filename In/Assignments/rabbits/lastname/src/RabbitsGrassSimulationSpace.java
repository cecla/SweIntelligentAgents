/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * @author 
 */
import uchicago.src.sim.space.Object2DGrid;

public class RabbitsGrassSimulationSpace 
{
	private Object2DGrid grassSpace;
	private Object2DGrid agentSpace;
	
	public RabbitsGrassSimulationSpace(int xSize, int ySize)
	{
		grassSpace = new Object2DGrid(xSize, ySize);
		agentSpace = new Object2DGrid(xSize, ySize);
		
		for(int i = 0; i < xSize; i++)
		{
			for(int j = 0; j < ySize; j++)
			{
				grassSpace.putObjectAt(i, j, new Integer(0));
			}
		}
	}
	
	public void spreadGrass(int grass)
	{
		//randomly place grass in grassSpace
		for(int i = 0; i < grass; i++)
		{
			//choose coord.
			int x = (int)(Math.random()*(grassSpace.getSizeX()));
			int y = (int)(Math.random()*(grassSpace.getSizeY()));
			
			//Get the value of the object at those coord.
			int currentValue = getGrassAt(x,y);
			
			//Replace the Integer objcet with another one with the new value 
			grassSpace.putObjectAt(x,y, new Integer(currentValue + 1));
		}
	}
	
	public int getGrassAt(int x, int y)
	{
		int i;
		
		if(grassSpace.getObjectAt(x, y) != null)
		{
			i = ((Integer)grassSpace.getObjectAt(x, y)).intValue();
		}
		else
		{
			i = 0;
		}
		return i;
	}
	
	public RabbitsGrassSimulationAgent getAgentAt(int x, int y)
	{
		RabbitsGrassSimulationAgent retVal = null;
		if(agentSpace.getObjectAt(x, y) != null)
		{
			retVal = (RabbitsGrassSimulationAgent)agentSpace.getObjectAt(x, y);
		}
		return retVal;
	}
	
	//return a specific grid, to link the grassSpace grid of the RabbitsGrassSpace object
	public Object2DGrid getCurrentGrassSpace()
	{
		return grassSpace;
	}
	
	public Object2DGrid getCurrentAgentSpace()
	{
		return agentSpace;
	}
	
	//check if the cell is occupied
	public boolean isCellOccupied(int x, int y)
	{
		boolean retVal = false;
		if(agentSpace.getObjectAt(x, y) != null) retVal = true;
		return retVal;
	}
	
	//add agent to an empty cell
	public boolean addAgent(RabbitsGrassSimulationAgent agent)
	{
		boolean retVal = false;
		int count = 0;
		int countLimit = 10 * agentSpace.getSizeX() * agentSpace.getSizeY();
		
		//find an empty cell, go trough the space
		while((retVal == false) && (count < countLimit))
		{
			int x = (int)(Math.random() * (agentSpace.getSizeX()));
			int y = (int)(Math.random() * (agentSpace.getSizeY()));
			
			if(isCellOccupied(x,y) == false) //add if the cell is empty
			{
				agentSpace.putObjectAt(x, y, agent);
				agent.setXY(x, y);
				agent.setRabbitsGrassSimulationSpace(this);
				retVal = true;
			}
			count++;
		}
		
		return retVal;
	}
	
	public void removeAgentAt(int x, int y)
	{
		agentSpace.putObjectAt(x,y,null);
	}
	
	public int takeGrassAt(int x, int y)
	{
		int grass = getGrassAt(x,y);
		grassSpace.putObjectAt(x, y, new Integer(0));
		return grass;
	}
	
	public boolean moveAgentAt(int x, int y, int newX, int newY)
	{
		boolean retVal = false;
		if(!isCellOccupied(newX, newY))
		{
			RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent)agentSpace.getObjectAt(x, y);
			removeAgentAt(x,y);
			rgsa.setXY(newX, newY);
			agentSpace.putObjectAt(newX, newY, rgsa);
			retVal = true;
		}
		return retVal;
	}
	
	public int getTotalGrass()
	{
		int totalGrass = 0;
		for(int i = 0; i < agentSpace.getSizeX(); i++)
		{
			for(int j = 0; j < agentSpace.getSizeY(); j++)
			{
				totalGrass += getGrassAt(i,j);
			}
		}
		return totalGrass;
	}
}
