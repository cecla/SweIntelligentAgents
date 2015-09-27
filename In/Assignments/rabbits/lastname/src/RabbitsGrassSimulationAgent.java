import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;


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
	
	private RabbitsGrassSimulationSpace rgsSpace;
	
	public RabbitsGrassSimulationAgent(int minBirthEnergy, int maxBirthEnergy){
		// initialize energy,position and assigning a unique ID
		x = -1;
		y = -1;
		
		energy = (int)(Math.random() * (maxBirthEnergy-minBirthEnergy) + minBirthEnergy);
		
		IDNumber++;
		ID = IDNumber;
		
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
	
	
}
