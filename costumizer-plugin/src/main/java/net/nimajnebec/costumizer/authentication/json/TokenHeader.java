package net.nimajnebec.costumizer.authentication.json;

public class TokenHeader {

    public TokenHeader(String algorithm) {
        this.alg = algorithm;
    }

    public String typ = "JWT";
    public String alg;
}
