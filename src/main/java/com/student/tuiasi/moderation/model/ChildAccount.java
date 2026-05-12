package com.student.tuiasi.moderation.model;

/**
 * Reprezinta un cont de copil in sistemul de moderare.
 * Fiecare cont de copil este asociat unui parinte prin parentId.
 * Continutul postat de copil este moderat automat.
 */
public class ChildAccount {

    /** Identificatorul unic al contului. */
    private int id;

    /** Numele de utilizator unic al copilului. */
    private String username;

    /** Varsta copilului, trebuie sa fie intre 5 si 17 ani. */
    private int age;

    /** Identificatorul parintelui asociat acestui cont. */
    private int parentId;

    /**
     * Constructorul principal al contului de copil.
     *
     * @param id identificatorul unic
     * @param username numele de utilizator
     * @param age varsta copilului
     * @param parentId identificatorul parintelui asociat
     */
    public ChildAccount(int id, String username, int age, int parentId) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.parentId = parentId;
    }

    /**
     * Returneaza identificatorul unic al contului.
     *
     * @return id-ul contului
     */
    public int getId() { return id; }

    /**
     * Returneaza numele de utilizator al copilului.
     *
     * @return username-ul
     */
    public String getUsername() { return username; }

    /**
     * Returneaza varsta copilului.
     *
     * @return varsta
     */
    public int getAge() { return age; }

    /**
     * Returneaza identificatorul parintelui asociat.
     *
     * @return id-ul parintelui
     */
    public int getParentId() { return parentId; }

    /**
     * Actualizeaza numele de utilizator al copilului.
     *
     * @param username noul nume de utilizator
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Actualizeaza varsta copilului.
     *
     * @param age noua varsta
     */
    public void setAge(int age) { this.age = age; }
}