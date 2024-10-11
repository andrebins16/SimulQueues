package app;

import java.util.Arrays;

public class RandomGeneratorForQueue {
    //Max de numeros a serem gerados
    private int maxNumbers;
    //Contador de quantos numeros foram gerados
    private int count=0;
    
    //Para gerar numeros pseudoaleatorios 
    private static final long a = 1664525;
    private static final long c = 1013904223;
    private static final long M = 4294967296L; // 2^32
    private long previous; // Semente inicial

    //Para usar um array de numeros pre estabelecidos
    private double[] arrMock;
    

    public RandomGeneratorForQueue(double[] arrMock){
        this.previous = 0;
        this.arrMock=arrMock;
        this.maxNumbers = arrMock.length;
    }

    public RandomGeneratorForQueue(long seed,int maxNumbers){
        this.previous =seed;
        this.arrMock=null;
        this.maxNumbers=maxNumbers;
    }

    public double NextRandom() {
        if(!hasNumbers()){
            return -1;
        }
        if(arrMock!=null){
            return arrMock[count++];
        }else{
            previous = (a * previous + c) % M;
            this.count++;
            return (double) previous / M;
        }
    }

    public boolean hasNumbers(){
        if(arrMock!=null){
            return this.count < this.arrMock.length;
        }else{
            return this.count < this.maxNumbers;
        }
    }

    @Override
    public String toString() {
        return "RandomGeneratorForQueue [previous=" + previous + ", count=" + count + ", maxNumbers=" + maxNumbers
                + ", arrMock=" + Arrays.toString(arrMock) + "]";
    }

    
}