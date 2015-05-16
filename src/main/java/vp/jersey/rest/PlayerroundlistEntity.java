package vp.jersey.rest;

/**
 * Entity mapping class for linking player scores and rounds.
 * Created by IntelliJ IDEA using database schema.
 */
public class PlayerroundlistEntity {
    private int playerRoundId;
    private int roundId;

    public int getPlayerRoundId() {
        return playerRoundId;
    }

    public void setPlayerRoundId(int playerRoundId) {
        this.playerRoundId = playerRoundId;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerroundlistEntity that = (PlayerroundlistEntity) o;

        if (playerRoundId != that.playerRoundId) return false;
        if (roundId != that.roundId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = playerRoundId;
        result = 31 * result + roundId;
        return result;
    }
}
