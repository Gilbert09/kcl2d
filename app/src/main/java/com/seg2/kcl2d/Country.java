package com.seg2.kcl2d;

/**
 * Created by Thomas on 2/12/2014.
 */
public class Country implements Comparable {

    public String id;
    public String name;

    public Country(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Object another) {
        Country other = (Country)another;
        return name.compareTo(other.getName());
    }
}
