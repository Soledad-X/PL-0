package com.entity;

public class Code {
    private final String function;    //功能码
    private final Integer levelDifference; //层次查
    private Integer addressOffset;

    public Code(String function, Integer levelDifference, Integer addressOffset) {
        this.function = function;
        this.levelDifference = levelDifference;
        this.addressOffset = addressOffset;
    }

    public String getFunction() {
        return function;
    }

    public Integer getLevelDifference() {
        return levelDifference;
    }

    public Integer getAddressOffset() {
        return addressOffset;
    }

    public void setAddressOffset(Integer addressOffset) {
        this.addressOffset = addressOffset;
    }

    @Override
    public String toString() {
        return "Code{" +
                "function='" + function + '\'' +
                ", levelDifference='" + levelDifference + '\'' +
                ", addressOffset='" + addressOffset + '\'' +
                '}';
    }
}
