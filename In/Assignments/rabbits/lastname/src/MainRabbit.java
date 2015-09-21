import uchicago.src.sim.engine.SimInit;

public class MainRabbit {

    public static void main(String[] args){

    	RabbitsGrassSimulationModel.main(args);
    	SimInit init = new SimInit();
    	RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
    	init.loadModel(model, "", false);
	
    } 

}
