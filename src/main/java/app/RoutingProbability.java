package app;
public class RoutingProbability{
    private String target;
    private double probability;
    
    public RoutingProbability(String target, double probability) {
        this.target = target;
        this.probability = probability;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public String toString() {
        return "RoutingProbability [target=" + target + ", probability=" + probability + "]";
    }

    

    
}