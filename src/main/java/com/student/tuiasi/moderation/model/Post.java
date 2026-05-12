package com.student.tuiasi.moderation.model;

import java.time.LocalDateTime;

/**
 * Reprezinta o postare facuta de un copil in sistemul de moderare.
 * Fiecare postare este analizata automat si primeste un status
 * in functie de rezultatul moderarii.
 */
public class Post {

    /** Identificatorul unic al postarii. */
    private int id;

    /** Identificatorul copilului care a facut postarea. */
    private int childId;

    /** Continutul postarii. */
    private String content;

    /** Tipul postarii — in prezent doar TEXT este suportat. */
    private String type;

    /**
     * Statusul postarii dupa moderare.
     * Valori posibile: POSTED, BLOCKED.
     */
    private String status;

    /** Data si ora la care a fost creata postarea. */
    private LocalDateTime createdAt;

    /**
     * Constructorul principal al postarii.
     * Data crearii este setata automat la momentul instantierii.
     *
     * @param id identificatorul unic
     * @param childId identificatorul copilului care posteaza
     * @param content continutul postarii
     * @param type tipul postarii
     * @param status statusul initial al postarii
     */
    public Post(int id, int childId, String content, String type, String status) {
        this.id = id;
        this.childId = childId;
        this.content = content;
        this.type = type;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Returneaza identificatorul unic al postarii.
     *
     * @return id-ul postarii
     */
    public int getId() { return id; }

    /**
     * Returneaza identificatorul copilului care a facut postarea.
     *
     * @return id-ul copilului
     */
    public int getChildId() { return childId; }

    /**
     * Returneaza continutul postarii.
     *
     * @return continutul
     */
    public String getContent() { return content; }

    /**
     * Returneaza tipul postarii.
     *
     * @return tipul postarii
     */
    public String getType() { return type; }

    /**
     * Returneaza statusul postarii dupa moderare.
     *
     * @return statusul postarii
     */
    public String getStatus() { return status; }

    /**
     * Returneaza data si ora crearii postarii.
     *
     * @return timestamp-ul crearii
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Actualizeaza statusul postarii dupa moderare.
     *
     * @param status noul status al postarii
     */
    public void setStatus(String status) { this.status = status; }
}