package ru.ifmo.rain.krylov.walk;

public class Qqq {
    public static void main(String[] args) {
        //nulls
        RecursiveWalk.main(null);

        //not enough
        String[] arg = new String[1];
        arg[0] = "in.txt";
        RecursiveWalk.main(arg);

        //no file
        String[] arg2 = new String[2];
        arg2[0] = "in.txt";
        arg2[1] = "out.txt";
        RecursiveWalk.main(arg2);

        //invalid path
        String[] arg3 = new String[2];
        arg3[0] = "in*.txt";
        arg3[1] = "ou*.txt";
        RecursiveWalk.main(arg3);
    }
}
