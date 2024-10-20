package org.example;

public class ThreadEx implements Runnable{

    @Override
    public void run(){
        try {
            Thread.sleep(10000);
            System.out.println("Thread is running");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
