package org.r2.devkit.test.mod;

import java.io.Serializable;

public class People implements Cloneable, Serializable {

    private static final long serialVersionUID = 3384301847198831738L;
    public String name;
    public int age;

    public People() {
    }

    public People(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
