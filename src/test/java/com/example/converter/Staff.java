package com.example.converter;

import java.math.BigDecimal;
import java.util.List;

public class Staff {

    private String name;
    private int age;
    private String position;
    private BigDecimal salary;
    private List<String> skills;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Staff staff = (Staff) o;

        if (getAge() != staff.getAge()) return false;
        if (getName() != null ? !getName().equals(staff.getName()) : staff.getName() != null) return false;
        if (getPosition() != null ? !getPosition().equals(staff.getPosition()) : staff.getPosition() != null)
            return false;
        if (getSalary() != null ? !getSalary().equals(staff.getSalary()) : staff.getSalary() != null) return false;
        return getSkills() != null ? getSkills().equals(staff.getSkills()) : staff.getSkills() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + getAge();
        result = 31 * result + (getPosition() != null ? getPosition().hashCode() : 0);
        result = 31 * result + (getSalary() != null ? getSalary().hashCode() : 0);
        result = 31 * result + (getSkills() != null ? getSkills().hashCode() : 0);
        return result;
    }

    @Override

    public String toString() {
        return "Staff{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", position='" + position + '\'' +
                ", salary=" + salary +
                ", skills=" + skills +
                '}';
    }
}