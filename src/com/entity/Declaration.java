package com.entity;

public class Declaration {
    String name;    //常量名/变量名/过程名
    String kind;    //类型：CONSTANT/VARIABLE/PROCEDURE
    Integer value;   //常量值
    Integer level;      //所在层次
    Integer address;    //相对存贮位置——每个过程中变量的相对起始位置在BLOCK内置DX := 3
    Integer size;       //记录该过程所需的数据空间

    public Declaration() {
    }

    public Declaration(Integer address) {
        this.address = address;
    }

    public Declaration(String name, String kind) {
        this.name = name;
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                ", kind='" + kind + '\'' +
                ", value='" + value + '\'' +
                ", level=" + level +
                ", address=" + address +
                ", size=" + size +
                '}';
    }
}
