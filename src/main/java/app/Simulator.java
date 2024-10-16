package app;
import java.util.PriorityQueue;

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
            
            if(queueAux.queueTimes.size() <= queueAux.status()){
                queueAux.queueTimes.add(delta);
            }else{
                Double tempo = queueAux.queueTimes.get(queueAux.status());
                queueAux.queueTimes.set(queueAux.status(), tempo + delta);
            }
            
        }
    }

    public void chegada(Event event){
        Queue queue1 = getQueueById(event.to);
        String target = null;
        acumulaTempo(event.tempo);

        if (queue1.status() < queue1.capacity()) {
            queue1.in();

            if (queue1.status() <= queue1.servers()) {

                target=queue1.getNextTarget(randomGenerator);
                
                if (target == null) {
                    scheduler.add(
                        new Event(EventType.SAIDA,
                                globalTime + randomTimeBetween(queue1.tempoServicoMin, queue1.tempoServicoMax),queue1.getId(),null));
                }else{
                    scheduler.add(
                        new Event(EventType.PASSAGEM,
                                globalTime + randomTimeBetween(queue1.tempoServicoMin, queue1.tempoServicoMax),queue1.getId(),target));
                }
                
            }
        } else {
            queue1.loss();
        }
        scheduler.add(new Event(EventType.CHEGADA,
                globalTime + randomTimeBetween(queue1.tempoChegadaMin, queue1.tempoChegadaMax),null,queue1.getId()));
    }
    
    public void saida(Event event){
        Queue source = getQueueById(event.from);
        String target = null;
        acumulaTempo(event.tempo);

        source.out();

        if (source.status() >= source.servers()) {
            target = source.getNextTarget(randomGenerator);
            
            if(target == null){
                scheduler.add(new Event(EventType.SAIDA,
                    globalTime + randomTimeBetween(source.tempoServicoMin, source.tempoServicoMax),source.getId(),null));
            }else{
                scheduler.add(new Event(EventType.PASSAGEM,
                    globalTime + randomTimeBetween(source.tempoServicoMin, source.tempoServicoMax),source.getId(),target));
            }
        }
    }

    public void passagem(Event event) {
        Queue source = getQueueById(event.from);
        Queue target = getQueueById(event.to);
        String nextTarget = null;
        acumulaTempo(event.tempo);

        source.out();
        if (source.status() >= source.servers()) {
            nextTarget = source.getNextTarget(randomGenerator);
                
            if(nextTarget == null){
                scheduler.add(new Event(EventType.SAIDA,
                    globalTime + randomTimeBetween(source.tempoServicoMin, source.tempoServicoMax),source.getId(),null));
            }else{
                scheduler.add(new Event(EventType.PASSAGEM,
                    globalTime + randomTimeBetween(source.tempoServicoMin, source.tempoServicoMax),source.getId(),nextTarget));
            }
        }

        String nextTarget2 = null;
        if (target.status() < target.capacity()) {
            target.in();

            if (target.status() <= target.servers()) {
                nextTarget2= target.getNextTarget(randomGenerator);

                if(nextTarget2 == null){
                    scheduler.add(new Event(EventType.SAIDA,
                        globalTime + randomTimeBetween(target.tempoServicoMin, target.tempoServicoMax),target.getId(),null));
                }else{
                    scheduler.add(new Event(EventType.PASSAGEM,
                        globalTime + randomTimeBetween(target.tempoServicoMin, target.tempoServicoMax),target.getId(),nextTarget2));
                }
            }
        } else {
            target.loss();
        }
    }



    
    public void displayResults() {
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        System.out.println("                             Resultados                            ");
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");

        for(Queue queue : this.queues){
            if(queue.capacity() == Integer.MAX_VALUE){
                System.out.println("Fila: " + queue.getId() +" (G/G/"+queue.servers()+")");
            }else{
                System.out.println("Fila: " + queue.getId() +" (G/G/"+queue.servers()+"/"+queue.capacity()+")");
            }

            if(queue.tempoChegadaMax != -1){
                System.out.println("Chegada: " + queue.tempoChegadaMin + " ... " + queue.tempoChegadaMax );
            }
            System.out.println("Serviço: " + queue.tempoServicoMin + " ... " + queue.tempoServicoMax );
            System.out.println("-------------------------------------------------------------------");
            System.out.println(" State                     Time           Probability");


            int K = queue.queueTimes.size();
            int C = queue.servers();
            double taxaAtt = 60/((queue.tempoServicoMax+queue.tempoServicoMin)/2.0);
            double populacao=0.0;
            double vazao=0.0;
            double utilizacao=0.0;
            double tempoResposta;

            
            
            for (int i = 0; i < K; i++) {
                double queueTime = queue.queueTimes.get(i);
                double probabilityBetweenZeroAndOne = (queue.queueTimes.get(i) / globalTime);
                if (queueTime > 0) {
                    System.out.format("   %-7d %20.4f %20.2f%s \n", 
                                    i, 
                                    queueTime, 
                                    probabilityBetweenZeroAndOne * 100, 
                                    "%");
                }
                
                if(i>0){
                    double taxaAttI = taxaAtt * Math.min(i,C);
                    populacao+= i * (probabilityBetweenZeroAndOne);
                    vazao+= taxaAttI * (probabilityBetweenZeroAndOne);
                    utilizacao+= probabilityBetweenZeroAndOne * ( (Math.min(i,C))/(double)C );
                }
            }
            tempoResposta = populacao/vazao;

            System.out.println("-------------------------------------------------------------------");
            System.out.println("Clientes perdidos: " + queue.getLossCounter());
            System.out.printf("Populacao: %.4f\n", populacao);
            System.out.printf("Vazao: %.4f\n", vazao);
            System.out.printf("Utilizacao: %.2f%%\n", utilizacao*100);
            System.out.printf("Tempo de resposta: %.4f\n", tempoResposta);
            System.out.println("");
            System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
            System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");

        }
        System.out.printf("Tempo de simulação total: %.4f \n\n", globalTime);
    }

    

    
}
