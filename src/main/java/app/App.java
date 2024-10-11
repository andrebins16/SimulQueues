package app;

import java.util.PriorityQueue;

public class App {
    public static void main(String[] args) {

        YamlParser parser = new YamlParser("src/main/java/app/model.yaml");
        parser.run();

        PriorityQueue<Event> scheduler = parser.getScheduler();
        RandomGeneratorForQueue randomGenerator = parser.getRandomGenerator(); ;
        Queue[] queues = parser.getQueues();


        Simulator sim = new Simulator(queues,randomGenerator,scheduler);
        while(randomGenerator.hasNumbers()) {
            Event event = sim.nextEvent();

            if (event.tipo == EventType.CHEGADA) {
                sim.chegada(event);
            } else if (event.tipo == EventType.SAIDA) {
                sim.saida(event);
            }else if (event.tipo == EventType.PASSAGEM) {
                sim.passagem(event);
            }
        }
        sim.displayResults();
    }
}
