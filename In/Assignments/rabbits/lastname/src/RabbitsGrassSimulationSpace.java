import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * @author 
 */

public class RabbitsGrassSimulationSpace 
{
	private Object2DGrid grassSpace;
	private Object2DGrid agentSpace;
	
	
	// creating the window
	public RabbitsGrassSimulationSpace(int x, int y)
	{
		// setting up space for grass and agents.
		grassSpace = new Object2DGrid(x,y);
		agentSpace = new Object2DGrid(x,y);
		
		for(int i = 0; i < x; i++)
		{
			for(int j = 0; j < y; j++)
			{
				// every coord. set to zero
				grassSpace.putObjectAt(i, j, new Integer(0));
			}
		}
	}
	
	public void growGrass(int grass)
	{
		//grow grass randomly in the space
		for(int i = 0; i < grass; i++)
		{
			// choose the coord. for the spec. grass
			int x = (int)(Math.random()*(grassSpace.getSizeX()));
			int y = (int)(Math.random()*(grassSpace.getSizeY()));
			
			// get the value of the object at those coord.
			int currentValue = getGrassValueAt(x,y);
			
			// replace the value of the grass object to current currentValue + 1
			grassSpace.putObjectAt(x, y, new Integer(currentValue + 1));
		}
	}
	
	public int getGrassValueAt(int x, int y)
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
	
	public Object2DGrid getCurrentGrassSpace(){		
		return grassSpace;
	}
	
	public boolean isCellOccupied(int x, int y){
		
		boolean retValue = false;
		if(agentSpace.getObjectAt(x, y)!= null){
			retValue = true;
		}
		return retValue;
		
	}

	// adding agent to space, return true if added.
	public boolean addAgent(RabbitsGrassSimulationAgent rgsA){
		
		boolean retValue = false;
		int count = 0;
		int countLimit = 10 * agentSpace.getSizeX() * agentSpace.getSizeY();
		
		//trying to add agent to different places
		while((retValue == false) && (count < countLimit)){
			
			int x = (int) (Math.random()*(agentSpace.getSizeX()));
			int y = (int) (Math.random()*(agentSpace.getSizeY()));
		
			if(isCellOccupied(x,y)== false){
				agentSpace.putObjectAt(x, y, rgsA);
		
				// assigning the agent it's current position
				rgsA.setXY(x, y);
				rgsA.setRabbitsGrassSimulationSpace(this);
				retValue = true;
			}
			count++;
		}
	
		return retValue;
	}
	
	public Object2DGrid getCurrentAgentSpace(){
		
		return agentSpace;
	}
	
	public void removeAgentAt(int x,int y){
		// set position in agentspace to null which will remove the pointer to that agent object.
		//System.out.println("X: " + x + " Y: " + y);
		agentSpace.putObjectAt(x, y, null);
		
	}
	
	// method that returns the grass amount at a specified position
	public int eatGrassAt(int x,int y){
		
		int grass = getGrassValueAt(x,y);
		grassSpace.putObjectAt(x, y, new Integer(0));

		return grass;
	}
	
	
	// checking if cell is occupied, if not move agent and return value true. 
	public boolean moveAgentAt(int x, int y, int newX, int newY){
		boolean retValue = false;
		
		System.out.println("x: " + x + " y: " + y + " newX: " + newX + " newY: " + y);
		if (!isCellOccupied(newX,newY)){

			RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent) agentSpace.getObjectAt(x, y);
			removeAgentAt(x,y);
			rgsa.setXY(newX, newY);
			agentSpace.putObjectAt(newX, newY, rgsa);
			retValue = true;
			
		}	
		return retValue;
	}
	
}
