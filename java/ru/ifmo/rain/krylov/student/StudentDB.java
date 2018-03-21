package ru.ifmo.rain.krylov.student;

import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentDB implements StudentQuery {

    private List<String> listGetter(List<Student> students, Function<Student, String> field) {
        return students.stream().map(field).collect(Collectors.toList());
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return listGetter(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return listGetter(students, Student::getLastName);
    }

    @Override
    public List<String> getGroups(List<Student> students) {
        return listGetter(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return listGetter(students, student -> student.getFirstName().concat(" ").concat(student.getLastName()));
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return new TreeSet<>(getFirstNames(students));
    }

    @Override
    public String getMinStudentFirstName(List<Student> students) {
        return students.stream().min(Student::compareTo).orElse(new Student(0, "", "", "")).getFirstName();
    }

    private Comparator<Student> comparatorByName = Comparator.comparing(Student::getLastName, String::compareTo).
            thenComparing(Student::getFirstName, String::compareTo).
            thenComparingInt(Student::getId);

    private List<Student> sorter(Collection<Student> students, Comparator<Student> comparator) {
        return students.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sorter(students, Student::compareTo);
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sorter(students, comparatorByName);
    }

    private Stream<Student> filteredStreamer(Collection<Student> students, Predicate<Student> predicate) {
        return students.stream().filter(predicate);
    }

    private List<Student> finder(Collection<Student> students, Predicate<Student> predicate) {
        return filteredStreamer(students, predicate).sorted(comparatorByName).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return finder(students, student -> name.equals(student.getFirstName()));
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return finder(students, student -> name.equals(student.getLastName()));
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, String group) {
        return finder(students, student -> group.equals(student.getGroup()));
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, String group) {
        //return .collect(Collectors.toMap(Student::getLastName, Student::getFirstName, StudentDB.minString));
        return filteredStreamer(students, student -> group.equals(student.getGroup())).
                collect(Collectors.toMap(Student::getLastName, Student::getFirstName, (s1, s2) -> s1.compareTo(s2) < 0 ? s1 : s2));
    }
}
