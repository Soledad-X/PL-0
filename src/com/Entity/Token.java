package com.Entity;

public class Token {
  private int row;
  private String type;
  private String value;

  public Token() {
  }

  public Token(int row, String type,String value) {
    this.row = row;
    this.type = type;
    this.value = value;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getRow() {
    return row;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.format("|%-12d|%-12s|%-12s",row,type,value);
  }
}
