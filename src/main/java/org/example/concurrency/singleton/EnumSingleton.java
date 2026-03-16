package org.example.concurrency.singleton;

public enum EnumSingleton {
    INSTANCE("EnumSingleton info");

    private String info;

    private EnumSingleton(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public static EnumSingleton getInstance() {
        return INSTANCE;
    }

    public String sayHello() {
        return "Hello from " + getClass().getSimpleName() + "@" + System.identityHashCode(this);
    }
}
