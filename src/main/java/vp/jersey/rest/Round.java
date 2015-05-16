package vp.jersey.rest;

import java.sql.Timestamp;
import java.util.List;

/**
 * Class for round objects.
 * @author Ville Piirainen
 */
public class Round {
    /** When the round was played (scores were submitted) */
    private Timestamp playedAt;
    /** Course on which the round was played */
    private Course course;
    /** List of PlayerScore objects, one object is a single player's scores for the round */
    private List<PlayerScore> playerscores;

    public Round() {

    }

    public Timestamp getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(Timestamp playedAt) {
        this.playedAt = playedAt;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<PlayerScore> getPlayers() {
        return playerscores;
    }

    public void setPlayers(List<PlayerScore> players) {
        this.playerscores = players;
    }
}
