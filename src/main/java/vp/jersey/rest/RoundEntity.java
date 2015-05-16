package vp.jersey.rest;

import java.sql.Timestamp;

/**
 * Entity mapping class for rounds.
 * Created by IntelliJ IDEA using database schema.
 */
public class RoundEntity {
    private int id;
    private Timestamp playedAt;
    private CourseEntity course;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(Timestamp playedAt) {
        this.playedAt = playedAt;
    }

    public CourseEntity getCourse() {
        return course;
    }

    public void setCourse(CourseEntity course) {
        this.course = course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoundEntity that = (RoundEntity) o;

        if (id != that.id) return false;
        if (playedAt != null ? !playedAt.equals(that.playedAt) : that.playedAt != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (playedAt != null ? playedAt.hashCode() : 0);
        return result;
    }

}
