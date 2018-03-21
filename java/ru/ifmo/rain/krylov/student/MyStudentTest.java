package ru.ifmo.rain.krylov.student;

import info.kgeorgiy.java.advanced.student.Student;

import java.util.ArrayList;

public class MyStudentTest {

    public static void main(String[] args) {

        Student student1 = new Student(0, "first", "first", "666");
        Student student2 = new Student(0, "second", "second", "666");
        Student student3 = new Student(0, "third", "third", "666");
        Student student4 = new Student(0, "fourth", "fourth", "666");

        ArrayList<Student> sl = new ArrayList<>();

        sl.add(student1);
        sl.add(student2);
        sl.add(null);
        sl.add(student3);
        sl.add(student4);

        StudentDB db = new StudentDB();
        System.out.println(db.getMinStudentFirstName(sl));
    }
}
