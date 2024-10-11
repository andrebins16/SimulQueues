package app;
public class Event implements Comparable<Event> {
    double tempo;
    EventType tipo;
    String from; // Para saida e entrada é a fila em que ocorre o evento. Para passagem, é a origem
    String to;  //Fila destino. So para passagem

    public Event(EventType tipo, double tempo,String from,String to) {
        this.tempo = tempo;
        this.tipo = tipo;
        this.from=from;
        this.to=to;
    }

    // public Event(EventType tipo, double tempo) {
    //     this.tempo = tempo;
    //     this.tipo = tipo;
    // }

    @Override
    public int compareTo(Event outroEvento) {
        return Double.compare(this.tempo, outroEvento.tempo);
    }

    @Override
    public String toString() {
        return "Event [tempo=" + tempo + ", tipo=" + tipo + ", from=" + from + ", to=" + to + "]";
    }

    
}