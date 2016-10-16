package DTO;

import java.math.BigInteger;

/**
 * Created by Uzytkownik on 12.10.2016.
 */
public class Step2B {
    public Step2B(BigInteger a) {
        this.b = a;
    }

    private BigInteger b;

    public BigInteger getB() {
        return b;
    }

    public void setB(BigInteger b) {
        this.b = b;
    }
}
