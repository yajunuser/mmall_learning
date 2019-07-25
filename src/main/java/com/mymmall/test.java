package com.mymmall;

public abstract class test {
    public abstract void run();
    public abstract void fly();
    public abstract void walk();
    public abstract void sleep();

}

abstract class  TestToo extends test{
    @Override
    public void run() {
        System.out.println("跑的很快");
    }
}
class testtt {
    public static void main(String[] args) {

    }
}