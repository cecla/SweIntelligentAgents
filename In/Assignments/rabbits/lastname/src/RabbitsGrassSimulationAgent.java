import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;


/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author
 */

public class RabbitsGrassSimulationAgent implements Drawable {

	private int x;
	private int y;
	private int energy;
	private static int IDNumber = 0;
	private int ID;
	
	private int vX;
	private int vY;
	
	private RabbitsGrassSimulationSpace rgsSpace;
	
	public RabbitsGrassSimulationAgent(int minBirthEnergy, int maxBirthEnergy){
		// initialize energy,position and assigning a unique ID
		x = -1;
		y = -1;
		
		energy = (int)(Math.random() * (maxBirthEnergy-minBirthEnergy) + minBirthEnergy);
		setVxVy();
		
		IDNumber++;
		ID = IDNumber;	
	}
	
	
	private void setVxVy(){
		
		vX = 0;
		vY = 0;
		
		while((vX == 0) && (vY == 0)){
			
			// pick x or y to begin randomization
			int dir =  (int) Math.round(Math.random());
			
			
			// one of x or y must be 0, in order to get directions north,south,east, west
			if(dir == 0){
				
				vX = (int) Math.floor(Math.random()*3)-1;

				
			}else{
				
				vY = (int) Math.floor(Math.random()*3)-1;
				
			}
			
			
			
			
		}
		
		
	}
	
	public void draw(SimGraphics G) {
		// TODO Auto-generated method stub
		
		G.drawFastRoundRect(Color.white);
	}

	public int getX() {
		// TODO Auto-generated method stub
		return x;
	}

	public int getY() {
		// TODO Auto-generated method stub
		return y;
	}
	
	
	public int getEnergy(){
		return energy;
	}
	
	public void setXY(int newX, int newY){
		x = newX;
		y = newY;
	}
	
	public String getID(){
		
		return "Rabbit-" + ID;
	}
	
	public void report(){
		
		System.out.println(getID() + " at " + 
				x + ", " + y + " has " + getEnergy() +
				" energy left");
	}
	
	
	public void step(){
		
		// creating new x and y by adding direction variables to current position, test if move is permitted
		// if permitted move and eat grass at position, otherwise set new direction for next step.
		int newX = x + vX;
		int newY = y + vY;
		
		Object2DGrid grid =  rgsSpace.getCurrentAgentSpace();
		newX = (newX + grid.getSizeX()) % grid.getSizeX();
		newY = (newY + grid.getSizeY()) % grid.getSizeY();
		
		if(tryMove(newX,newY)){
			int grassValue = rgsSpace.eatGrassAt(newX, newY);
			// give the rabbit differing amounts of energy when eating grass
			int energyValue = (int) Math.round(((grassValue/2) + (Math.random()*(grassValue/2))));
	
			energy += energyValue;
			
		}
		
		// update direction variables after each step
		setVxVy();
		
		
		// decrease energy by 1 each step
		energy--;
		// increase energy by amount of grass at position after step
		energy += rgsSpace.eatGrassAt(x, y);
	}
	
	// this function is needed in order to be able to remove energy after giving birth
	public void setEnergy(int e){
		energy = e;
	}
	
	public void setRabbitsGrassSimulationSpace(RabbitsGrassSimulationSpace rgss){
		rgsSpace = rgss;
	}
	
	// testing whether a move is permitted or not
	private boolean tryMove(int newX, int newY){
		return rgsSpace.moveAgentAt(x,y,newX,newY);
	
	}
	
}
