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
    public double[] queueTimes;

    private ArrayList<RoutingProbability> routingProbabilities;

    private int size = 0;
    private int lossCounter = 0;
    
    public Queue(String id,int serversQuantity, int capacity, double tempoChegadaMin, double tempoChegadaMax, double tempoServicoMin, double tempoServicoMax, ArrayList<RoutingProbability> routingProbabilities){
        this.id=id;
        this.serversQuantity=serversQuantity;
        this.capacity=capacity;
        this.tempoChegadaMin = tempoChegadaMin;
        this.tempoChegadaMax = tempoChegadaMax;
        this.tempoServicoMin = tempoServicoMin;
        this.tempoServicoMax = tempoServicoMax;
        this.routingProbabilities=routingProbabilities;
        queueTimes = new double[capacity + 1];
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

    public String getNextQueue(RandomGeneratorForQueue rnd) {
        double sum = 0;
        double prob = rnd.NextRandom();
        for(RoutingProbability rP : this.routingProbabilities){
            sum += rP.getProbability();
            if(prob <= sum){
                return rP.getTarget();
            }
        }
        return null;
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