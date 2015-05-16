package vp.jersey.rest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for course objects.
 * @author Ville Piirainen
 */
public class Course {
    /** Course ID (from Hibernate mapping) */
    private int id;
    /** Holes in the course as a list of Hole-objects */
    private List<Hole> holes;
    /** Timestamp from the time the course was added to the system. */
    private Timestamp createdAt;
    /** Course name */
    private String name;
    /** Course location */
    private String location;

    public Course() {

    }

    /**
     * creates a new course object using CourseEntity-object data
     * @param c CourseEntity hibernate entity mapping object to be converted to Course object.
     */
    public Course(CourseEntity c) {
        this.setId(c.getId());
        this.setCreatedAt(c.getCreatedAt());
        this.setName(c.getName());
        this.setLocation(c.getLocation());
        List<Hole> holes = new ArrayList<Hole>();
        for (String s : c.getHoles().split(";")) {
            int par = Integer.parseInt(s.split(",")[0]);
            int lengthm = Integer.parseInt(s.split(",")[1]);
            holes.add(new Hole(par, lengthm));
        }
        this.setHoles(holes);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Hole> getHoles() {
        return holes;
    }

    public void setHoles(List<Hole> holes) {
        this.holes = holes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
