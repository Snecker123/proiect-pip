package com.student.tuiasi.moderation.model;

/**
 * Reprezinta un cont de parinte in sistemul de moderare.
 * Un parinte poate avea asociati mai multi copii si primeste
 * notificari prin email cand continutul copilului este blocat.
 */
public class ParentAccount {

    /** Identificatorul unic al contului. */
    private int id;

    /** Numele de utilizator unic al parintelui. */
    private String username;

    /** Adresa de email a parintelui, folosita pentru notificari. */
    private String email;

    /** Anul nasterii parintelui, folosit pentru verificarea varstei minime. */
    private int birthYear;

    /** Motivul pentru care parintele doreste sa creeze un cont. */
    private String reason;

    /** Indica daca contul a fost aprobat dupa validare. */
    private boolean approved;

    /** Motivul respingerii contului, daca e cazul. */
    private String rejectionReason;

    /**
     * Constructorul principal al contului de parinte.
     * Contul este creat implicit ca neaprobat.
     *
     * @param id identificatorul unic
     * @param username numele de utilizator
     * @param email adresa de email
     * @param birthYear anul nasterii
     * @param reason motivul inregistrarii
     */
    public ParentAccount(int id, String username, String email, int birthYear, String reason) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.birthYear = birthYear;
        this.reason = reason;
        this.approved = false;
        this.rejectionReason = null;
    }

    /**
     * Returneaza identificatorul unic al contului.
     *
     * @return id-ul contului
     */
    public int getId() { return id; }

    /**
     * Returneaza numele de utilizator al parintelui.
     *
     * @return username-ul
     */
    public String getUsername() { return username; }

    /**
     * Returneaza adresa de email a parintelui.
     *
     * @return adresa de email
     */
    public String getEmail() { return email; }

    /**
     * Returneaza anul nasterii parintelui.
     *
     * @return anul nasterii
     */
    public int getBirthYear() { return birthYear; }

    /**
     * Returneaza motivul inregistrarii.
     *
     * @return motivul
     */
    public String getReason() { return reason; }

    /**
     * Returneaza statusul de aprobare al contului.
     *
     * @return true daca contul e aprobat, false altfel
     */
    public boolean isApproved() { return approved; }

    /**
     * Returneaza motivul respingerii contului.
     *
     * @return motivul respingerii sau null daca contul e aprobat
     */
    public String getRejectionReason() { return rejectionReason; }

    /**
     * Seteaza statusul de aprobare al contului.
     *
     * @param approved true pentru aprobare, false pentru respingere
     */
    public void setApproved(boolean approved) { this.approved = approved; }

    /**
     * Seteaza motivul respingerii contului.
     *
     * @param rejectionReason motivul respingerii
     */
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}