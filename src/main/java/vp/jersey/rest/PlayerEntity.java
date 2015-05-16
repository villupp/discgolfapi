package vp.jersey.rest;

import org.hibernate.Session;
import org.hibernate.Transaction;
import java.sql.Timestamp;

/**
 * Entity mapping class for players.
 * Created by IntelliJ IDEA using database schema.
 */
public class PlayerEntity {
    private int id;
    private String name;
    private Timestamp createdAt;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerEntity that = (PlayerEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        return result;
    }

    public static int addPlayer(PlayerEntity newPlayer) {
        // Create new player if not in database
        Session session = DbConnection.getSession();
        Transaction tx = null;
        int playerID = -1;
        try {
                // save and commit transaction if no exceptions
            tx = session.beginTransaction();
            playerID = (Integer) session.save(newPlayer);
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return playerID;
    }
}
