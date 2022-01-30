package dev.hcr.hcf.users.faction;

public enum Role {
    NONE(0,""),
    MEMBER(1,"*"),
    CAPTAIN(2,"**"),
    COLEADER(3,"***"),
    LEADER(4,"***");

    private final int weight;
    private final String astrix;
    Role(int weight, String astrix) {
        this.astrix = astrix;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public String getAstrix() {
        return astrix;
    }
}
