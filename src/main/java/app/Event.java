package app;
public class Event implements Comparable<Event> {
    double tempo;
    EventType tipo;
    String from;  //Fila origem. Usado na saida e na passagem
    String to;  //Fila destino. Usado na chegada e na passagem

    public Event(EventType tipo, double tempo,String from,String to) {
        this.tempo = tempo;
        this.tipo = tipo;
        this.from=from;
        this.to=to;
    }

    @Override
    public int compareTo(Event outroEvento) {
        return Double.compare(this.tempo, outroEvento.tempo);
    }

    @Override
    public String toString() {
        return "Event [tempo=" + tempo + ", tipo=" + tipo + ", from=" + from + ", to=" + to + "]";
    }

    
}