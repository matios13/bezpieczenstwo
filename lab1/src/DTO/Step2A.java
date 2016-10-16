package DTO;

import java.math.BigInteger;

/**
 * Created by Uzytkownik on 12.10.2016.
 */
public class Step2A {
    public Step2A(BigInteger a) {
        this.a = a;
    }

    private BigInteger a;

    public BigInteger getA() {
        return a;
    }

    public void setA(BigInteger a) {
        this.a = a;
    }
}
