package app;
import java.util.PriorityQueue;
import java.util.HashMap;

public class Simulator {
    //Queues
    private Queue[] queues;

    // Objects
    private PriorityQueue<Event> scheduler;
    private RandomGeneratorForQueue randomGenerator;

    // Runtime Parameters
    private double globalTime = 0.0;

    public Simulator(Queue[] queues, RandomGeneratorForQueue randomGenerator,PriorityQueue<Event> scheduler) {
        this.queues = queues;
        this.queues[0].reset();
        this.scheduler = scheduler;
        this.randomGenerator = randomGenerator;
    }

    private Queue getQueueById(String id){
        for (Queue queue : this.queues) {
            if (queue.getId().equals(id)) {
                return queue;
            }
        }
        return null; // Retorna null se a fila com o ID não for encontrada
    }
    

    public Event nextEvent(){
        return scheduler.poll();
    }

    private double randomTimeBetween(double minTime, double maxTime) {
        return (maxTime - minTime) * randomGenerator.NextRandom() + minTime;
    }
    
    public void acumulaTempo(double eventTime){
        double delta = eventTime - globalTime;
        globalTime = eventTime;
        for (Queue queueAux : this.queues) {
            queueAux.queueTimes[queueAux.status()] += delta;
        }
    }

    public void chegada(Event event){
        Queue source = getQueueById(event.from);
        String target = source.getRoutingProbabilities().get(0).getTarget();
        acumulaTempo(event.tempo);

        if (source.status() < source.capacity()) {
            source.in();

            if (source.status() <= source.servers()) {
                scheduler.add(
                        new Event(EventType.PASSAGEM,
                                globalTime + randomTimeBetween(source.tempoServicoMin, source.tempoServicoMax),source.getId(),target));
            }
        } else {
            source.loss();
        }

        scheduler.add(new Event(EventType.CHEGADA,
                globalTime + randomTimeBetween(source.tempoChegadaMin, source.tempoChegadaMax),source.getId(),null));
    }
    
    public void saida(Event event){
        Queue source = getQueueById(event.from);
        acumulaTempo(event.tempo);

        source.out();
        if (source.status() >= source.servers()) {
            scheduler.add(new Event(EventType.SAIDA,
                    globalTime + randomTimeBetween(source.tempoServicoMin, source.tempoServicoMax),source.getId(),null));
        }
    }

    public void passagem(Event event) {
        Queue source = getQueueById(event.from);
        Queue target = getQueueById(event.to);
        acumulaTempo(event.tempo);

        source.out();
        if (source.status() >= source.servers()) {
            scheduler.add(new Event(EventType.PASSAGEM,
                    globalTime + randomTimeBetween(source.tempoServicoMin, source.tempoServicoMax),source.getId(),target.getId()));
        }

        if (target.status() < target.capacity()) {
            target.in();

            if (target.status() <= target.servers()) {
                scheduler.add(
                        new Event(EventType.SAIDA,
                                globalTime + randomTimeBetween(target.tempoServicoMin, target.tempoServicoMax),target.getId(),null));
            }
        } else {
            target.loss();
        }
    }


    public void displayResults() {
        
        System.out.println("===================================================================");
        System.out.println("                              Resultados                           ");
        System.out.println("===================================================================");

        for(Queue queue : this.queues){
            System.out.println("Fila: " + queue.getId());

            System.out.println("Tempo em cada estado da fila:");
            for (int i = 0; i < queue.queueTimes.length; i++) {
                if (queue.queueTimes[i] >0) {
                    System.out.format("\tState %d: %.2f u.t\n", i, queue.queueTimes[i]);
                }
            }
    
            System.out.println("Probabilidade de cada estado da fila");
            for (int i = 0; i < queue.queueTimes.length; i++) {
                if (queue.queueTimes[i] >0) {
                    System.out.format("\tProbabilidade do estado %d: %.3f%s \n", i, queue.queueTimes[i] / globalTime * 100, "%");
                }
            }
    
            System.out.println("Clientes perdidos: " + queue.getLossCounter());
            System.out.println("");
            System.out.println("===================================================================");
            System.out.println("");

        }
        System.out.printf("Tempo de simulação total: %.2f u.t\n\n", globalTime);
    }

    

    
}
