package DTO;

import java.math.BigInteger;

/**
 * Created by Uzytkownik on 12.10.2016.
 */
public class Step1 {
    private BigInteger p;
    private BigInteger g;
    public Step1(){

    }

    public Step1(BigInteger p, BigInteger g) {
        this.p = p;
        this.g = g;
    }

    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getG() {
        return g;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }




}
