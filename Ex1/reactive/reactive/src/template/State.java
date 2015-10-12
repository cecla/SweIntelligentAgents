package template;

import java.util.ArrayList;

import logist.agent.Agent;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;

public class State {
	private int city;
	private int taskTo;
	private double rewardDel;
	private double rewardMove;
	private double distance;
	private double distribution;
	private double reward;
	private double totalCost;
	private double avgCostNeighbor = 0;
	private double totalDistance = 0;
	private ArrayList<Double> probDel;
	private ArrayList<Double> probMove;
	
	public static int stateID = 0;
	private int ID;
	
	public State(City c1, City c2, TaskDistribution tb, Agent agent)
	{
		ID = stateID;
		stateID++;
		
		city = c1.id;
		taskTo = c2.id;
		distance = c1.distanceTo(c2);
		distribution = tb.probability(c1, c2);
		totalCost = distance*agent.vehicles().get(0).costPerKm();
		reward = tb.reward(c1, c2);
		
		for(int i = 0; i < c1.neighbors().size(); i++)
		{
			totalDistance += c1.neighbors().get(i).distanceTo(c1);
		}
		avgCostNeighbor = totalDistance / c1.neighbors().size() * agent.vehicles().get(0).costPerKm();
		
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
			if(c2 != topology.cities().get(i))
			{
				probDel.add(tb.probability(c2, topology.cities().get(i)));
			}
			else
			{
				probDel.add(0.0);
			}
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
				if(c1.neighbors().get(i) != topology.cities().get(j))
				{
					probMove.add(tb.probability(c1.neighbors().get(i), topology.cities().get(j)) / (c1.neighbors().size()));
				}
				else
				{
					probMove.add(0.0);
				}
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
		return city;
	}
	public int getCityDel()
	{
		return taskTo;
	}
	
	public int getID()
	{
		return ID;
	}
}
