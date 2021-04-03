package tools;

@FunctionalInterface
public interface RunnableWithObject {
    void run(Object o, Integer i);
}