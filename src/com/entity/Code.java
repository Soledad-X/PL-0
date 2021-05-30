package com.entity;

public class Code {
    private String function;    //功能码
    private Integer levelDifference; //层次查
    private Integer addressOffset;

    public Code(String function, Integer levelDifference, Integer addressOffset) {
        this.function = function;
        this.levelDifference = levelDifference;
        this.addressOffset = addressOffset;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public Integer getLevelDifference() {
        return levelDifference;
    }

    public void setLevelDifference(Integer levelDifference) {
        this.levelDifference = levelDifference;
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
