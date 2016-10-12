package DTO;

/**
 * Created by Uzytkownik on 12.10.2016.
 */
public class Step1 {
    public Step1(){

    }
    public Step1(int p,int g){
        this.p=p;
        this.g=g;
    }
    private int p;
    private int g;

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }
}
