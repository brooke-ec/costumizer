package net.nimajnebec.costumizer.api.json;

public class TokenHeader {

    public TokenHeader(String algorithm) {
        this.alg = algorithm;
    }

    public String typ = "JWT";
    public String alg;
}
