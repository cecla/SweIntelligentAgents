package template;

import java.util.ArrayList;

import logist.agent.Agent;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;

public class State {
	private int cityID;
	private int taskToID;
	private City city;
	private City taskTo;
	private double rewardDel;
	private double rewardMove;
	private double distance;
	//private double distribution;
	private double reward;
	private double totalCost;
	private double avgCostNeighbor = 0;
	private double totalDistance = 0;
	private ArrayList<Double> probDel;
	private ArrayList<Double> probMove;
	
	public static int stateID = 0;
	private int ID;
	
	public State(City c1, City c2)
	{
		ID = stateID;
		stateID++;
		
		city = c1;
		taskTo = c2;
		cityID = c1.id;
		taskToID = c2.id;
	}
	
	public void createRewardTable(TaskDistribution td, Agent agent)
	{
		distance = city.distanceTo(taskTo);
		//distribution = tb.probability(c1, c2);
		totalCost = distance*agent.vehicles().get(0).costPerKm();
		reward = td.reward(city, taskTo);
		
		for(int i = 0; i < city.neighbors().size(); i++)
		{
			totalDistance += city.neighbors().get(i).distanceTo(city);
		}
		avgCostNeighbor = totalDistance / city.neighbors().size() * agent.vehicles().get(0).costPerKm();
		
		setRewardDel(reward, totalCost);
		setRewardMove(avgCostNeighbor);
	}
	
	public void setRewardDel(double reward, double cost)
	{
		rewardDel = reward - cost;
	}
	public double getRewardDel()
	{
		return rewardDel;
	}
	
	public void setRewardMove(double cost)
	{
		rewardMove = -cost;
	}
	
	public double getRewardMove()
	{
		return rewardMove;
	}
	
	public void setTransitionDel(TaskDistribution tb, Topology topology, City c2)
	{
		probDel = new ArrayList<Double>();
		for(int i = 0; i < topology.cities().size(); i++)
		{
			probDel.add(tb.probability(c2, topology.cities().get(i)));
		}
		//add on the last spot, if the city has no tasks at all
		probDel.add(tb.probability(c2, null));
	}
	
	public void setTransitionMove(TaskDistribution tb, Topology topology, City c1)
	{
		probMove = new ArrayList<Double>();
		for(int i = 0; i < c1.neighbors().size(); i++)
		{
			for(int j = 0; j < topology.cities().size(); j++)
			{
				probMove.add(tb.probability(c1.neighbors().get(i), topology.cities().get(j)) / (c1.neighbors().size()));
			}
			//add on the last spot, if the city has no tasks at all
			probMove.add(tb.probability(c1, null));
		}
	}
	
	public ArrayList<Double> getProbDel()
	{
		return probDel;
	}
	public ArrayList<Double> getProbMove()
	{
		return probMove;
	}
	public int getCityCurrent()
	{
		return cityID;
	}
	public int getCityDel()
	{
		return taskToID;
	}
	
	public int getID()
	{
		return ID;
	}
}
