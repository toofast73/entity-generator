package com.example.data.converter;

import com.example.mapper.CustomTypes.Department;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Staff {

    private String id;
    private String name;
    private Integer age;
    private String position;
    private BigDecimal salary;
    private Department department;
    private List<String> skills;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", position='" + position + '\'' +
                ", salary=" + salary +
                ", department=" + department +
                ", skills=" + skills +
                '}';
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
        if (getDepartment() != null ? !getDepartment().equals(staff.getDepartment()) : staff.getDepartment() != null)
            return false;
        return getSkills() != null ? getSkills().equals(staff.getSkills()) : staff.getSkills() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + getAge();
        result = 31 * result + (getPosition() != null ? getPosition().hashCode() : 0);
        result = 31 * result + (getSalary() != null ? getSalary().hashCode() : 0);
        result = 31 * result + (getDepartment() != null ? getDepartment().hashCode() : 0);
        result = 31 * result + (getSkills() != null ? getSkills().hashCode() : 0);
        return result;
    }

    public static Staff createDummyObject() {
        Staff staff = new Staff();
        staff.setId(UUID.randomUUID().toString());
        staff.setDepartment(new Department());
        staff.setName("example");
        staff.setAge(33);
        staff.setPosition("Developer");
        staff.setSalary(new BigDecimal("7500"));
        List<String> skills = new ArrayList<>();
        skills.add("java");
        skills.add("python");
        staff.setSkills(skills);
        return staff;
    }

}