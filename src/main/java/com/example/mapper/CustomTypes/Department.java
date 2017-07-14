package com.example.mapper.CustomTypes;

public class Department {
    String name = "";
    String boss = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoss() {
        return boss;
    }

    public void setBoss(String boss) {
        this.boss = boss;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Department that = (Department) o;

        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        return getBoss() != null ? getBoss().equals(that.getBoss()) : that.getBoss() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getBoss() != null ? getBoss().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Department{" +
                "name='" + name + '\'' +
                ", boss='" + boss + '\'' +
                '}';
    }
}
