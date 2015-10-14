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
	private int count = 0;
	
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
		
		//calculate V(S) of deep 2
		calculateVs(states, discount, topology);
		
		
		for(int i = 0; i < vStates.size(); i++)
		{
			System.out.println(vStates.get(i));
		}
		System.out.println(vStates.size() + " " + states.size());
		//58597376 & 2592
		System.out.println(count);
		
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
		
		//check if there is a task in the city
		if(availableTask != null)
		{
			City delCity = availableTask.deliveryCity;
			
			//find out which state the agent has
			//complexity(worst case) = states.size()=n
			for(int i = 0; i < states.size(); i++)
			{
				if(currentCity.id == states.get(i).getCityCurrent() && delCity.id == states.get(i).getCityDel())
				{
					//check if it is worth it to take the task or not, worth it->delivery=true
					if(vStates.get(i) == 0)
					{
						delivery = true;
					}
					break; //find the right state->break the loop
				}
			}
		}
		//do the movement 
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
	
	public void calculateVs(Vector<State> states, double discount, Topology topology)
	{
		//loop through all states
		for(int i = 0; i < states.size(); i++)
		{
			insertVstates(states, states.get(i), discount, topology);
		}
	}
	
	public void insertVstates(Vector<State> states, State s, double discount, Topology topology)
	{
		double temp1 = 0.0; //better to take the task
		double temp2 = 0.0; //better to not take the task
		
		//loop through all actions which are 2, take the task or move to a neighbor city
		for(int i = 0; i < 2; i++)
		{
			//System.out.println(s.getRewardDel() + " " + s.getRewardMove());
			//calculate the fist step
			if(i == 0) 
			{
				temp1 = s.getRewardDel() + calculateNext(states, s, i, discount, topology);//,2);
			}
			else
			{
				temp2 = s.getRewardMove() + calculateNext(states, s, i, discount, topology);//,2);
			}
		}
		//take the task if temp1>temp2, for that state, otherwise move to next city
		if(temp1 > temp2)
		{
			vStates.add(0.0);
		}
		else
		{
			vStates.add(1.0);
		}
	}
	
	public double calculateNext(Vector<State> states, State s, int j, double discount, Topology topology)//, int end)
	{
		double temp = 0.0;
		//loop through all cities. For each city calculate discount*T(S,a,S')*V(S') 
		//and add it do the sum(temp)
		for(int i = 0; i < topology.cities().size() ; i++)
		{
			if(j == 0)
			{
				//kolla på om detta stämmer
				//temp += discount * s.getProbDel().get(i) * lastVs(s, i, 0, discount);
				//temp += discount * s.getProbDel().get(i) * lastVs(s, i, 1, discount);
				/*if(end > 0)	
					for(int k = 0; k < states.size(); k++)
					{
						temp += discount * s.getProbDel().get(i) * calculateNext(states, states.get(k), 0, discount, topology, end-1);
						temp += discount * s.getProbDel().get(i) * calculateNext(states, states.get(k), 1, discount, topology, end-1);
					}
				else
				{*/
					temp += discount * s.getProbDel().get(i) * lastVs(s, i, 0, discount);
					temp += discount * s.getProbDel().get(i) * lastVs(s, i, 1, discount);
				//}
				
			}
			else
			{
				/*if(end > 0)
				{
					for(int k = 0; k < states.size(); k++)
					{
						temp += discount * s.getProbMove().get(i) * calculateNext(states, states.get(k), 0, discount, topology, end-1);
						temp += discount * s.getProbMove().get(i) * calculateNext(states, states.get(k), 1, discount, topology, end-1);
					}
				}
				else
				{*/
					temp += discount * s.getProbMove().get(i) * lastVs(s, i, 0, discount);
					temp += discount * s.getProbMove().get(i) * lastVs(s, i, 1, discount);
				//}
			}
		}
		return temp;
	}
	
	//Calculate the deepest step in the tree
	public double lastVs(State s, int j ,int i, double discount)
	{
		//If i==0 -> calculate for action1, take the task
		count++;
		if(i == 0)
		{
			return (double) s.getRewardDel() + s.getProbDel().get(j) * discount;
		}
		//If i==1 -> calculate for action2, don't take the task
		else
		{
			return (double) s.getRewardMove() + s.getProbMove().get(j) * discount;
		}
	}

}
