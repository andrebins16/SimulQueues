package app;

import java.util.ArrayList;

public class Queue {
    private String id;
    private int serversQuantity;
    private int capacity;
    public double tempoChegadaMin;
    public double tempoChegadaMax;
    public double tempoServicoMin;
    public double tempoServicoMax;
    public ArrayList<Double> queueTimes;

    private ArrayList<RoutingProbability> routingProbabilities;

    private int size = 0;
    private int lossCounter = 0;
    
    public Queue(String id,int serversQuantity, int capacity, double tempoChegadaMin, double tempoChegadaMax, double tempoServicoMin, double tempoServicoMax, ArrayList<RoutingProbability> routingProbabilities){
        this.id=id;
        this.serversQuantity=serversQuantity;
        
        this.tempoChegadaMin = tempoChegadaMin;
        this.tempoChegadaMax = tempoChegadaMax;
        this.tempoServicoMin = tempoServicoMin;
        this.tempoServicoMax = tempoServicoMax;
        this.routingProbabilities=routingProbabilities;
        if(capacity<0){
            this.capacity=Integer.MAX_VALUE;
        }else{
            this.capacity=capacity;
            
        }
        queueTimes = new ArrayList<>();
        
    }

    public void in() {
        size++;
    }

    public void out() {
        size--;
    }

    public int status() {
        return size;
    }

    public int servers() {
        return serversQuantity;
    }

    public int capacity() {
        return capacity;
    }

    public void loss(){
        lossCounter++;
    }

    public int getLossCounter(){
        return lossCounter;
    }

    public void reset(){
        size = 0;
        lossCounter = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getRoutingProbabilityByTarget(String targetId){
        for (RoutingProbability rP : this.routingProbabilities) {
            if (rP.getTarget().equals(targetId)) {
                return rP.getProbability();
            }
        }
        return 0; // Retorna null se a fila com o ID não for encontrada
    }

    public String getNextTarget(RandomGeneratorForQueue randomGenerator){
        String target=null;
        if(this.getRoutingProbabilities().size()>1){
            double sum =0.0;
            double probability = randomGenerator.NextRandom();
            for (RoutingProbability routingProbability : this.getRoutingProbabilities()) {
                sum += routingProbability.getProbability();
                if (sum > probability) {
                    target= routingProbability.getTarget();
                    break;
                }
            }
        }else{
            target = this.getRoutingProbabilities().get(0).getTarget();
        }
        return target;
    }

    public ArrayList<RoutingProbability> getRoutingProbabilities() {
        return routingProbabilities;
    }

    @Override
    public String toString() {
        return "Queue [id=" + id + ", serversQuantity=" + serversQuantity + ", capacity=" + capacity
                + ", tempoChegadaMin=" + tempoChegadaMin + ", tempoChegadaMax=" + tempoChegadaMax + ", tempoServicoMin="
                + tempoServicoMin + ", tempoServicoMax=" + tempoServicoMax + ", routingProbabilities=" + routingProbabilities + ", size=" + size
                + ", lossCounter=" + lossCounter + "]";
    }

    

    
}