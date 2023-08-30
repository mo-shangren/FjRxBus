package com.self.rxdemo;

public class Test2 extends Test1 {
    private String a;
    private String text;
    private int num;

    public Test2(String a) {
        this.a = a;
    }

    @Override
    public String getA() {
        return a;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Test2{" +
                "a='" + a + '\'' +
                ", text='" + text + '\'' +
                ", num=" + num +
                '}';
    }
}
