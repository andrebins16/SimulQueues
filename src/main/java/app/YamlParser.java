package app;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class YamlParser {
    private String path;

    private PriorityQueue<Event> scheduler;
    private RandomGeneratorForQueue randomGenerator;
    private Queue[] queues;
    

    public YamlParser(String path) {
        this.path = path;
        this.scheduler = new PriorityQueue<>();
    }
    public  void run() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream(this.path)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Arquivo YAML não encontrado!");
            }

            // Faz o parse do arquivo YAML em um Map
            Map<String, Object> data = yaml.load(inputStream);


            // cria o scheduler
            Map<String, Object> arrivals = (Map<String, Object>) data.get("arrivals");
            for (Map.Entry<String, Object> entry : arrivals.entrySet()) {
                this.scheduler.add(new Event(EventType.CHEGADA, (double) entry.getValue(), entry.getKey(), null));
            }
            

            //cria o random generator
            Object seedObj=data.get("seed");
            Object rndNumbersPerSeedObj=data.get("rndnumbersPerSeed");
            int seed = seedObj == null ? -1 : (int)seedObj;
            int rndNumbersPerSeed = rndNumbersPerSeedObj == null ? -1 : (int)rndNumbersPerSeedObj;
            
            if(seed > 0) {
                this.randomGenerator = new RandomGeneratorForQueue(seed, rndNumbersPerSeed);
            } else {
                //Acessa os random numbers e guarda em sua variavel
                ArrayList<Double> rndNumbersAL = (ArrayList<Double>)data.get("rndnumbers");
                double[] rndNumbers = new double[rndNumbersAL.size()];
                for(int i = 0; i < rndNumbers.length; i++) {
                    rndNumbers[i] = rndNumbersAL.get(i);
                }
                this.randomGenerator = new RandomGeneratorForQueue(rndNumbers);
            }
            
            
            // cria os routings 
            Object routingObject = data.get("network");
            Map<String,ArrayList<RoutingProbability>> routings = null;
            if(routingObject != null) {
                ArrayList<Map<String, Object>> routingsYaml = (ArrayList<Map<String, Object>>) data.get("network");
                routings = new HashMap<>();
                for(Map<String, Object> entry : routingsYaml) {
                    RoutingProbability routing = new RoutingProbability((String)entry.get("target"), (double)entry.get("probability"));
                    routings.computeIfAbsent((String) entry.get("source"), k -> new ArrayList<>()).add(routing);
                }
            }


            //cria as queues
            Map<String, Object> queuesYaml = (Map<String, Object>) data.get("queues");
            ArrayList<Queue> queuesAL = new ArrayList<>();
            for(Map.Entry<String, Object> entry : queuesYaml.entrySet()) {
                Map<String, Object> queueMap = (Map<String, Object>) entry.getValue();
                ArrayList<RoutingProbability> routingsQ = null;
                
                if(routings != null) {
                    double leftProb = 1.0;
                    routingsQ = routings.get(entry.getKey());
                    if(routingsQ != null) {
                        for (RoutingProbability routing : routingsQ) {
                            leftProb -= routing.getProbability();
                        }
                        if(leftProb>0.0){
                            routingsQ.add(new RoutingProbability(null, leftProb));
                        }
                    }else{
                        if(routingsQ == null){
                            routingsQ = new ArrayList<>();
                        }
                        routingsQ.add(new RoutingProbability(null, 1.0));
                    }
                    
                }else{
                    if(routingsQ == null){
                        routingsQ = new ArrayList<>();
                    }
                    routingsQ.add(new RoutingProbability(null, 1.0));
                }
                int serverQ = (int)queueMap.get("servers");
                int capacityQ = (int)queueMap.getOrDefault("capacity",1000);
                double minArrivalQ = (double) queueMap.getOrDefault("minArrival", -1.0);
                double maxArrivalQ = (double) queueMap.getOrDefault("maxArrival", -1.0);
                double minServiceQ = (double)queueMap.get("minService");
                double maxServiceQ = (double)queueMap.get("maxService");
                Queue newQueue = new Queue(entry.getKey(),
                                            serverQ,
                                            capacityQ,
                                            minArrivalQ,
                                            maxArrivalQ,
                                            minServiceQ,
                                            maxServiceQ,
                                            routingsQ);
                queuesAL.add(newQueue);
            }
            this.queues = queuesAL.toArray(new Queue[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public PriorityQueue<Event> getScheduler() {
        return scheduler;
    }
    public RandomGeneratorForQueue getRandomGenerator() {
        return randomGenerator;
    }
    public Queue[] getQueues() {
        return queues;
    }
}