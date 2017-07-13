package com.example.demo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Person {

    private  long id;
    private  boolean citizen;
    private  double salary;
    private  String name;
    private  Date dateOfBirth;
    private  BigDecimal age;
    private Set<Person> friends = new HashSet<>();
    private Sex sex;


    public Person(long id, boolean citizen, double salary, String name, Date dateOfBirth, BigDecimal age, Sex sex) {
        this.id = id;
        this.citizen = citizen;
        this.salary = salary;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.sex = sex;
    }

    public Person() {
    }

    public void addFriend(Person person){
        friends.add(person);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id &&
                citizen == person.citizen &&
                Double.compare(person.salary, salary) == 0 &&
                Objects.equals(name, person.name) &&
                Objects.equals(dateOfBirth, person.dateOfBirth) &&
                Objects.equals(age, person.age) &&
                Objects.equals(friends, person.friends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, citizen, salary, name, dateOfBirth, age, friends);
    }

    public long getId() {
        return id;
    }

    public boolean isCitizen() {
        return citizen;
    }

    public double getSalary() {
        return salary;
    }

    public String getName() {
        return name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public BigDecimal getAge() {
        return age;
    }

    public Set<Person> getFriends() {
        return friends;
    }

    public void setFriends(Set<Person> friends) {
        this.friends = friends;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCitizen(boolean citizen) {
        this.citizen = citizen;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAge(BigDecimal age) {
        this.age = age;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Person{");
        sb.append("id=").append(id);
        sb.append(", citizen=").append(citizen);
        sb.append(", salary=").append(salary);
        sb.append(", name='").append(name).append('\'');
        sb.append(", dateOfBirth=").append(dateOfBirth);
        sb.append(", age=").append(age);
        sb.append(", friends=").append(friends);
        sb.append(", sex=").append(sex);
        sb.append('}');
        return sb.toString();
    }
}
