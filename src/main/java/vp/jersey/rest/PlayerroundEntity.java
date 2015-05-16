package vp.jersey.rest;

/**
 * Entity mapping class for player round (personal scores).
 * Created by IntelliJ IDEA using database schema.
 */
public class PlayerroundEntity {
    private int id;
    private String scores;
    private PlayerEntity player;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getScores() {
        return scores;
    }

    public void setScores(String scores) {
        this.scores = scores;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerroundEntity that = (PlayerroundEntity) o;

        if (id != that.id) return false;
        if (scores != null ? !scores.equals(that.scores) : that.scores != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (scores != null ? scores.hashCode() : 0);
        return result;
    }
}
