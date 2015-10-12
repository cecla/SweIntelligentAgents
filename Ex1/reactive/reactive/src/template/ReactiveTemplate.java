package template;



import java.awt.List;
import java.util.Random;
import java.util.Vector;

import logist.simulation.Vehicle;
import logist.agent.Agent;
import logist.behavior.ReactiveBehavior;
import logist.plan.Action;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;

public class ReactiveTemplate implements ReactiveBehavior {

	private Random random;
	private double pPickup;
	private Vector<State> states;
	private Vector<Double> vStates;
	
	// ADDED CODE - this variable keeps reference of the Agent object
	Agent agent;
	
	// Called once, before the simulation starts and before any other method is called
	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {

		// Reads the discount factor from the agents.xml file.
		// If the property is not present it defaults to 0.95
		
		Double discount = agent.readProperty("discount-factor", Double.class,
				0.95);
		
		createStates(topology, td, agent);
		vStates = new Vector<Double>();
		
		double dummy = calculateVs(states, discount,0);
		
		for(int i = 0; i < vStates.size(); i++)
		{
			System.out.println(vStates.get(i));
		}
		System.out.println(vStates.size() + " " + states.size());
		
		this.random = new Random();
		this.pPickup = discount;
		
		// ADDED CODE
		this.agent = agent;
	}
	
	// ADDED CODE - this variable counts how many actions have passed so far
	int counterSteps = 0;

	// this function is called every time an agent arrives in a new city
	@Override
	public Action act(Vehicle vehicle, Task availableTask) {
		Action action;
		
		// ADDED CODE - this output gives information about the "goodness" of your agent (higher values are preferred)
		if ((counterSteps > 0)&&(counterSteps%100 == 0)) {
			System.out.println("The total profit after "+counterSteps+" steps is "+agent.getTotalProfit()+".");
			System.out.println("The profit per action after "+counterSteps+" steps is "+((double)agent.getTotalProfit() / counterSteps)+".");
		}
		counterSteps++;
		// END OF ADDED CODE
		
		// if there is no task, move to next city
		// or if a random number is larger than the discount, also move
		// to the next city
		/*if (availableTask == null || random.nextDouble() > pPickup) 
		{
			City currentCity = vehicle.getCurrentCity();
			action = new Move(currentCity.randomNeighbor(random));
		} 
		else 
		{
			action = new Pickup(availableTask);
		}
		return action;*/
		
		boolean delivery = false;
		City currentCity = vehicle.getCurrentCity();
		
		if(availableTask != null)
		{
			City delCity = availableTask.deliveryCity;
			
			for(int i = 0; i < states.size(); i++)
			{
				if(currentCity.id == states.get(i).getCityCurrent() && delCity.id == states.get(i).getCityDel())
				{
					if(vStates.get(i) == 0)
					{
						delivery = true;
					}
					break;
				}
			}
		}
		if(!delivery)
		{
			action = new Move(currentCity.randomNeighbor(random));
		}
		else
		{
			action = new Pickup(availableTask);
		}
		return action;
	}
	
	public void createStates(Topology tp, TaskDistribution td, Agent agent)
	{
		states = new Vector<State>();
		for(int i = 0; i < tp.cities().size(); i++)
		{
			for(int j = 0; j < tp.cities().size(); j++)
			{
				if(i != j)
				{	
					State s = new State(tp.cities().get(i), tp.cities().get(j), td, agent);
					states.add(s);
					s.setTransitionDel(td, tp, tp.cities().get(j));
					s.setTransitionMove(td, tp, tp.cities().get(i));
				}
			}
		}
	}
	
	public double calculateVs(Vector<State> states, double discount, int c)
	{
		double temp1 = 0;
		double temp2 = 0;
		double tDisc = discount;
		
		
		for(int i = 0; i < states.size(); i++)
		{
			for(int j = 0; j < 2; j++)
			{
				//action 1 = deliver
				if(j == 0)
				{
					for(int l = 0; l < states.get(i).getProbDel().size(); l++)
					{
						temp1 += states.get(i).getProbDel().get(l) * tDisc;
						if(c < 10)
						{
							c++;
							tDisc -= 0.05;
							temp1 *= calculateVs(states, tDisc, c);
						}
						System.out.println(c);
						if(c > 1)return temp1;
					}
					//vStates.add(states.get(i).getRewardDel() + temp1);
				}
				//action 2 = move to a neighbor city
				if(j == 1)
				{
					for(int l = 0; l < states.get(i).getProbMove().size(); l++)
					{
						temp2 += states.get(i).getProbMove().get(l) * tDisc;
						if(c < 10)
						{
							c++;
							tDisc -= 0.05;
							temp2 *= calculateVs(states, tDisc, c);
						}
						if(c > 1)return temp2;
					
					}
					//vStates.add(states.get(i).getRewardMove() + temp2);
					
				}
			}
			
			if(temp1 > temp2)
			{
				vStates.add(0.0);
			}
			else vStates.add(1.0);
		}
		return 0.0;
	}

}
