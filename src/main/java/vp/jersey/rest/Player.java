package vp.jersey.rest;

import java.sql.Timestamp;

/**
 * Class for players.
 * @author Ville Piirainen
 */
public class Player {
    /** Player ID from database. */
    private int id;
    /** Player name */
    private String name;
    /** When the player was inserted in the database */
    private Timestamp createdAt;

    public Player() {

    }

    /**
     * Creates a new player object from persisted data
     *
     * @param pe PlayerEntity object used in converting persisted entity into Player object.
     */
    public Player(PlayerEntity pe) {
        this.setId(pe.getId());
        this.setName(pe.getName());
        this.setCreatedAt(pe.getCreatedAt());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
