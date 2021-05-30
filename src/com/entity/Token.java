package com.entity;

public class Token {
  private String SYM;
  private String ID;
  private Integer NUM;

  public Token(String SYM) {
    this.SYM = SYM;
  }

  public Token(String SYM, String ID) {
    this.SYM = SYM;
    this.ID = ID;
  }

  public Token(String SYM, Integer NUM) {
    this.SYM = SYM;
    this.NUM = NUM;
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
            "SYM='" + SYM + '\'' +
            ", ID='" + ID + '\'' +
            ", NUM='" + NUM + '\'' +
            '}';
  }
}
