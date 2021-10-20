package main;

public class Entry<T> {
    T object;
    int before;
    int now;
    String name;

    public Entry(T object, String name, int before, int now) {
        this.object = object;
        this.name = name;
        this.before = before;
        this.now = now;
    }

    public T getObject() {
        return object;
    }
    public void setObject(T object) {
        this.object = object;
    }
    public int getBefore() {
        return before;
    }
    public void setBefore(int before) {
        this.before = before;
    }
    public int getNow() {
        return now;
    }
    public void setNow(int now) {
        this.now = now;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s: %d > %d", name, before, now);
    }
}
