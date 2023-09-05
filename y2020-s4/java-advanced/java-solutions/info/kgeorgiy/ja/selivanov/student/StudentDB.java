package info.kgeorgiy.ja.selivanov.student;

import info.kgeorgiy.java.advanced.student.Group;
import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.GroupQuery;
import info.kgeorgiy.java.advanced.student.Student;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

// :NOTE: Student::getId can be replaced with natural comparator
public class StudentDB implements GroupQuery {

    private final Comparator<Student> comparatorByName = Comparator
            .comparing(Student::getLastName).thenComparing(Student::getFirstName)
            .reversed().thenComparing(Student::getId);

    private List<Group> getGroups(Collection<Student> students, Function<List<Student>, List<Student>> by) {
        return students.stream().sorted()
                .collect(Collectors.groupingBy(Student::getGroup)).entrySet().stream()
                .map(x -> new Group(x.getKey(), by.apply(x.getValue())))
                .sorted(Comparator.comparing(Group::getName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Group> getGroupsByName(Collection<Student> students) {
        return getGroups(students, x -> x.stream().sorted(comparatorByName).collect(Collectors.toList()));
    }

    @Override
    public List<Group> getGroupsById(Collection<Student> students) {
        return getGroups(students, x -> x);
    }

    private GroupName getLargestGroup(Collection<Student> students, Function<List<Student>, Integer> by, boolean reversedKeys) {
        return students.stream()
                .collect(Collectors.groupingBy(Student::getGroup)).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, x -> by.apply(x.getValue()))).entrySet().stream()
                .max(Map.Entry.<GroupName, Integer>comparingByValue().
                        thenComparing(Map.Entry.comparingByKey(reversedKeys ? Comparator.<GroupName>reverseOrder() : Comparator.<GroupName>naturalOrder())))
                .map(Map.Entry::getKey).orElse(null);
    }

    @Override
    public GroupName getLargestGroup(Collection<Student> students) {
        return getLargestGroup(students, List::size, false);
    }

    @Override
    public GroupName getLargestGroupFirstName(Collection<Student> students) {
        return getLargestGroup(students, x -> getDistinctFirstNames(x).size(), true);
    }

    private <E, T extends Collection<E>> T map(
            Collection<Student> students,
            Function<Student, E> fun,
            Collector<? super E, ?, T> collector
    ) {
        return students.stream().map(fun).collect(collector);
    }

    private <E> List<E> mapToList(Collection<Student> students, Function<Student, E> fun) {
        return map(students, fun, Collectors.toList());
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return mapToList(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return mapToList(students, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return mapToList(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return mapToList(students, x -> x.getFirstName() + " " + x.getLastName());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return map(students, Student::getFirstName, Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream().max(Comparator.comparingInt(Student::getId)).map(Student::getFirstName).orElse("");
    }

    private List<Student> sort(Collection<Student> students, Comparator<Student> comparator) {
        return students.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sort(students, Comparator.comparing(Student::getId));
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sort(students, comparatorByName);
    }

    private <E> List<Student> find(Collection<Student> students, Function<Student, E> map, E obj) {
        return students.stream().filter(x -> map.apply(x).equals(obj)).sorted(comparatorByName).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return find(students, Student::getFirstName, name);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return find(students, Student::getLastName, name);
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return find(students, Student::getGroup, group);
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return findStudentsByGroup(students, group).stream().collect(Collectors.toMap(
                Student::getLastName, Student::getFirstName, BinaryOperator.minBy(Comparator.naturalOrder())
        ));
    }
}
