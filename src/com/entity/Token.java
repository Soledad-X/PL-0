package com.entity;

public class Token {
  private Integer ROW;
  private String SYM;
  private String ID;
  private Integer NUM;

  public Token(Integer ROW, String SYM) {
    this.ROW = ROW;
    this.SYM = SYM;
  }

  public Token(Integer ROW, String SYM, String ID) {
    this.ROW = ROW;
    this.SYM = SYM;
    this.ID = ID;
  }

  public Token(Integer ROW, String SYM, Integer NUM) {
    this.ROW = ROW;
    this.SYM = SYM;
    this.NUM = NUM;
  }

  public Integer getROW() {
    return ROW;
  }

  public void setROW(Integer ROW) {
    this.ROW = ROW;
  }

  public String getSYM() {
    return SYM;
  }

  public void setSYM(String SYM) {
    this.SYM = SYM;
  }

  public String getID() {
    return ID;
  }

  public void setID(String ID) {
    this.ID = ID;
  }

  public Integer getNUM() {
    return NUM;
  }

  public void setNUM(Integer NUM) {
    this.NUM = NUM;
  }

  @Override
  public String toString() {
    return "Token{" +
            "ROW=" + ROW +
            ", SYM='" + SYM + '\'' +
            ", ID='" + ID + '\'' +
            ", NUM=" + NUM +
            '}';
  }
}
