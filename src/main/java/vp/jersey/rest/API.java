package vp.jersey.rest;

import org.hibernate.Query;
import org.hibernate.Session;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Path domain/disc/*. All API requests are in this class.
 *
 * @author Ville Piirainen
 */

@Path("/api")
public class API {
    /**
     * Prints API intro.
     *
     * @return              Hello message as HTML.
     */
    @GET
    @Produces("text/html")
    public String printAPIintro() {
        return "<b>Disc Golf API By Ville Piirainen 2015</b>";
    }

    /**
     * HTTP POST-request that adds round scores to the system. Returns a HTTP Response.
     *
     * @param scoresCSV     Player scores for the round as a comma-separated-values list in format
     *                      "playerID:[array of scores];".
     *                      For example "45:3,3,4,3,4,3,3,4,3;46:3,2,4,5,4,7,3,4,2;".
     * @param courseID      ID for the course the round was played on.
     * @return              HTTP response with appropriate status.
     */
    @Path("/addroundscore")
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addRoundScore(@FormParam("scores") String scoresCSV,
                                  @FormParam("courseID") String courseID) {
        Session session = DbConnection.getSession();
        try {
            int roundID;
            session.getTransaction().begin();
            // Creating and saving the new round
            RoundEntity re = new RoundEntity();
            Query courseQ = session.createQuery("from CourseEntity where id=:id");
            courseQ.setParameter("id", Integer.parseInt(courseID));
            if (courseQ.list().size() != 0) {
                CourseEntity ce = (CourseEntity) courseQ.list().get(0);
                re.setCourse(ce);
            } else {
                throw new Exception("No course found with that ID.");
            }
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            re.setPlayedAt(new Timestamp(now.getTime()));
            session.save(re);
            roundID = re.getId();
            // parse scores and set up playerround and playerroundList (link with round)
            for (String playerScore : scoresCSV.split(";")) {
                // playerround
                int playerID = Integer.parseInt(playerScore.split(":")[0]);
                String scores = playerScore.split(":")[1];
                System.out.println("player id: " + playerID + " , scores : " + scores);
                PlayerroundEntity pre = new PlayerroundEntity();
                Query playerQ = session.createQuery("from PlayerEntity where id=:id");
                playerQ.setParameter("id", playerID);
                if (playerQ.list().size() != 0) {
                    PlayerEntity pe = (PlayerEntity) playerQ.list().get(0);
                    pre.setPlayer(pe);
                } else {
                    throw new Exception("No player found with that ID.");
                }
                pre.setScores(scores);
                session.save(pre);
                // playerroundlist ( playerround-round -link )
                PlayerroundlistEntity prle = new PlayerroundlistEntity();
                prle.setRoundId(roundID);
                prle.setPlayerRoundId(pre.getId());
                session.save(prle);

            }
            if (!session.getTransaction().wasCommitted()){
                session.getTransaction().commit();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occured while adding scores: " + e.getMessage());
            session.getTransaction().rollback();
            return Response.serverError()
                    .status(500)
                    .entity("Error when trying to insert data.")
                    // Set the status, entity and media type of the response.
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
        finally {
            session.close();
        }
        return Response
                .ok()
                // Set the status, entity and media type of the response.
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    /**
     * HTTP POST-request that adds a Disc Caddy 2 CSV-file scorecard to the system.
     * Was used to import previous round data for testing purposes.
     *
     * @param dateStr       The date when the round was played. "yyyy-MM-dd HH:mm:ss".
     * @param courseID      ID for the course the round was played on.
     * @param scoresCsv     The scores copied from the CSV-file.
     * @return              HTTP response with appropriate status.
     */
    @Path("/add_dc_csv_score")
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addDiscCaddyCSVScoreCard(@FormParam("date") String dateStr,
                            @FormParam("scores") String scoresCsv,
                            @FormParam("courseID") String courseID) {
        // Holds score data Map<playerName, playerRound>
        Map<String, PlayerroundEntity> playerroundMap = new HashMap<String, PlayerroundEntity>();

        // Parse scores into the map
        for ( String scoreline : scoresCsv.split(" ") ) {
            String[] scoreElement = scoreline.split(",");
            //System.out.println("player name : " + scoreElement[0]);
            // Check if the first line specifying the columns is given, if is, skip to next line.
            if ( scoreElement[0].equals("player") )
                continue;
            // Adds a new player to the round if the name isn't already in playerround
            if ( playerroundMap.get(scoreElement[0]) == null ) {

                playerroundMap.put(scoreElement[0], new PlayerroundEntity());
            }
            PlayerroundEntity playerround = playerroundMap.get(scoreElement[0]);
            // Prevents the "null" in front of the scores, temporary
            if ( playerround.getScores() == null) {
                playerround.setScores("");
            }
            // Adding the score (last cell of the csv line) to playerround scores
            playerround.setScores(playerround.getScores() + scoreElement[3] + ",");
            //System.out.println("player name : " + playerround.getScores());
            //System.out.println("player name : " + scoreElement[0]);
        }
        // remove last comma from every scores-entry
        System.out.println("map size : " + playerroundMap.size());
        for (Map.Entry<String, PlayerroundEntity> entry : playerroundMap.entrySet()) {
            String scores = entry.getValue().getScores();
            System.out.println(" player in map : " + entry.getKey());
            System.out.println(" scores in map : " + scores);
            entry.getValue().setScores(scores.substring(0, scores.length() - 1));
        }
        RoundEntity round = new RoundEntity();
        // parsing the date for Round
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date result = df.parse(dateStr);
            round.setPlayedAt(new Timestamp(result.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        List<PlayerEntity> playerList = null;
        int playerroundID = -1;
        int roundID = -1;

        Session session = DbConnection.getSession();
        try {
            session.beginTransaction();

            // Get list of all players in database
            Query query = session.createQuery("from PlayerEntity");
            playerList = query.list();

            // Course for round and saving
            CourseEntity course = (CourseEntity) session.get(CourseEntity.class, Integer.parseInt(courseID));
            round.setCourse(course);

            //System.out.println(session.save(round).getClass().getName());
            session.save(round);
            roundID = round.getId();
            System.out.println("roundID : " + roundID);

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            session.getTransaction().rollback();
            return Response
                    .serverError()
                    .status(500)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } finally {
            session.close();
        }

        for (Map.Entry<String, PlayerroundEntity> entry : playerroundMap.entrySet()) {
            PlayerEntity player;
            //System.out.println(("ENTRY GETKEY (PLAYERNAME) : " + entry.getKey() + "\n--LIST:"));
            for (Object p : playerList)
                System.out.println(((PlayerEntity) p).getName());
            //System.out.println("--ENDOFLIST:");
            // create new player if not found in list
            boolean playerFoundInList = false;
            for (PlayerEntity pe : playerList) {
                if ( pe.getName().equals(entry.getKey()) ) {
                    playerFoundInList = true;
                    //break;
                }
            }
            session = DbConnection.getSession();
            try {
                if ( !playerFoundInList ) {
                    // Create new player if not in database
                    System.out.println("List does not have a player with that entry key (player name)");
                    player = new PlayerEntity();
                    player.setName(entry.getKey());
                    Date date = new Date();
                    player.setCreatedAt(new Timestamp(date.getTime()));
                    PlayerEntity.addPlayer(player);
                } else {
                    // Use existing player
                    System.out.println("List has a player with that entry key (player name)");
                    System.out.println("playerName to be queried: " + entry.getKey());

                    Query playerQueryByName = session.createQuery("from PlayerEntity where name = :playerName");
                    playerQueryByName.setParameter("playerName", entry.getKey());
                    List playerByName = playerQueryByName.list();
                    if (playerByName.get(0) != null) {
                        player = (PlayerEntity) playerByName.get(0);
                    } else {
                        throw new Exception("Error: Player found in database, but could not be queried.");
                    }

                }
                session.beginTransaction();
                entry.getValue().setPlayer(player);
                session.save(entry.getValue());
                playerroundID = entry.getValue().getId();

                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error : " + e.getMessage());
                session.getTransaction().rollback();
                return Response
                        .serverError()
                        .status(500)
                        .header("Access-Control-Allow-Origin", "*")
                        .build();
            } finally {
                session.close();
            }
            session = DbConnection.getSession();
            try {
                session.beginTransaction();
                System.out.println("playerroundID : " + playerroundID);
                System.out.println("roundID : " + roundID);
                // Set up playerround-round connection and save
                PlayerroundlistEntity prl = new PlayerroundlistEntity();
                prl.setPlayerRoundId(playerroundID);
                prl.setRoundId(roundID);
                session.save(prl);

                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
                session.getTransaction().rollback();
                return Response
                        .serverError()
                        .status(500)
                        .header("Access-Control-Allow-Origin", "*")
                        .build();
            } finally {
                session.close();
            }
        }
        return Response
                .ok("Scores added")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    /**
     * HTTP GET-request that returns round by ID as a JSON object.
     *
     * @param roundID       The requested round ID.
     * @return              HTTP response with appropriate round and status.
     */
    @Path("/round")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRound(@QueryParam("id") Integer roundID) {
        Session session = DbConnection.getSession();

        Round r = new Round();
        List<PlayerScore> playerscores = new ArrayList<PlayerScore>();
        List<Integer> pRoundIDs = new ArrayList<Integer>();

        // get round
        try {
            System.out.println("get /round?id=" + roundID);
            Query roundQByID = session.createQuery("from RoundEntity where id = :rID");
            roundQByID.setParameter("rID", roundID);
            List roundByIDres = roundQByID.list();
            if (roundByIDres.size() == 0) {
                System.out.println("no query results");
                throw new Exception("Couldn't find round with that ID.");
            }
            RoundEntity roundEntity = (RoundEntity) roundByIDres.get(0);
            Course c = new Course(roundEntity.getCourse());
            // set round details
            r.setPlayedAt(roundEntity.getPlayedAt());
            r.setCourse(c);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
            return Response
                    .serverError()
                    .status(500)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } finally {
            session.close();
        }
        // get playerround IDs
        session = DbConnection.getSession();
        try {
            Query pRoundIDsQByID = session.createQuery("from PlayerroundlistEntity where roundId = :rID");
            pRoundIDsQByID.setParameter("rID", roundID);
            List<PlayerroundlistEntity> pRoundList = pRoundIDsQByID.list();
            for (PlayerroundlistEntity prl : pRoundList) {
                pRoundIDs.add(prl.getPlayerRoundId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
            return null;
        } finally {
            session.close();
        }
        // get scores and set to playerscore
        for (int id : pRoundIDs) {
            PlayerScore pScore = new PlayerScore();
            session = DbConnection.getSession();
            try {
                Query pRoundsQByID = session.createQuery("from PlayerroundEntity where id = :prID");
                pRoundsQByID.setParameter("prID", id);
                List pRoundsByIDres = pRoundsQByID.list();
                PlayerroundEntity pr = (PlayerroundEntity)pRoundsByIDres.get(0);
                pScore.setScores(PlayerScore.parsePlayerScores(pr.getScores()));
                pScore.setPlayer(new Player(pr.getPlayer()));
                playerscores.add(pScore);
                r.setPlayers(playerscores);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error : " + e.getMessage());
                return null;
            } finally {
                session.close();
            }
        }
        System.out.println("");
        return Response
                .ok(r)
                .status(500)
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    /**
     * HTTP GET-request that returns all rounds in database as a JSON list of objects.
     *
     * @return              HTTP response with appropriate round list and status.
     */
    @Path("/rounds")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRounds() {
        Session session = DbConnection.getSession();
        List<Round> rounds = new ArrayList<Round>();
        List<Integer> roundIDs = new ArrayList<Integer>();
        // get round
        try {
            System.out.println("get /rounds");
            Query roundQ = session.createQuery("from RoundEntity");
                List<RoundEntity> roundQres = (List<RoundEntity>) roundQ.list();
            if (roundQres.size() == 0) {
                System.out.println("no query results (roundqres)");
                throw new Exception("No rounds in database");
            }
            for (RoundEntity re : roundQres) {
                roundIDs.add(re.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
            return Response
                    .serverError()
                    .status(500)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } finally {
            session.close();
        }
        for (int id : roundIDs) {
            rounds.add((Round)getRound(id).getEntity());
        }
        GenericEntity<List<Round>> roundsGen = new GenericEntity<List<Round>>(rounds) {};
        return Response
                .ok(roundsGen)
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    /**
     * HTTP GET-request that adds a player to the database.
     *
     * @param name          Name for the new player.
     * @return              HTTP response with appropriate round list and status.
     */
    @Path("/addplayer")
    @GET
    @Produces("text/html")
    public Response addPlayer(@QueryParam("name") String name) {
        Session session = DbConnection.getSession();
        try {
            Query query = session.createQuery("from PlayerEntity");

            for (PlayerEntity pe : (List < PlayerEntity >)query.list()) {
                name = name.replace("_", " ");
                if (pe.getName().equals(name)) {
                    throw new Exception("A player with the name " + name + " already exists.");
                }
            }
            session.beginTransaction();
            PlayerEntity newPlayer = new PlayerEntity();
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            newPlayer.setCreatedAt(new Timestamp(now.getTime()));
            newPlayer.setName(name);
            session.save(newPlayer);
            if (!session.getTransaction().wasCommitted()){
                session.getTransaction().commit();
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Error occured while trying to add player." + e.getMessage());
            return Response
                    .serverError()
                    .status(500)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } finally {
            session.close();
        }
        return Response
                .ok("Player added succesfully")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    /**
     * HTTP POST-request that adds a course to the database.
     *
     * @param name          Name for the new course.
     * @param location      Location for the new course.
     * @param holes         Holes for the new course. Have to be given in proper CSV format.
     *                      "par,length;par,length;par,length;par,length; ..."
     *                      for example "3,74;3,94;3,74;3,70;3,85;3,74;3,49;3,55;3,49;".
     * @return              HTTP response with appropriate round list and status.
     */
    @Path("/addcourse")
    @POST
    @Produces("text/html")
    public Response addCourse(@FormParam("name") String name,
                              @FormParam("location") String location,
                              @FormParam("holes") String holes) {
        Session session = DbConnection.getSession();
        try {
            session.beginTransaction();
            CourseEntity newCourse = new CourseEntity();
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            newCourse.setCreatedAt(new Timestamp(now.getTime()));
            newCourse.setName(name);
            newCourse.setHoles(holes);
            newCourse.setLocation(location);
            session.save(newCourse);
            if (!session.getTransaction().wasCommitted()){
                session.getTransaction().commit();
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
            System.out.println("Error occured while trying to add course." + e.getMessage());
            return Response
                    .serverError()
                    .status(500)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } finally {
            session.close();
        }
        return Response
                .ok("Course added succesfully")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    /**
     * HTTP GET-request that returns all players as a JSON list of objects.
     *
     * @return              HTTP response with appropriate status.
     */
    @Path("/players")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlayers() {
        List<Player> players = new ArrayList<Player>();
        Session session = DbConnection.getSession();
        try {
            Query query = session.createQuery("from PlayerEntity");

            for (PlayerEntity pe : (List<PlayerEntity>)query.list()) {
                players.add(new Player(pe));
            }
            GenericEntity<List<Player>> playersGen = new GenericEntity<List<Player>>(players) {};
            return Response
                    .ok(playersGen)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return Response
                    .serverError()
                    .status(500)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } finally {
            session.close();
        }
    }

    /**
     * HTTP GET-request that returns player by ID as a JSON object.
     *
     * @param playerID      Requested player's ID.
     * @return              HTTP response with appropriate player object and status.
     */
    @Path("/player")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlayer(@QueryParam("id") Integer playerID) {
        Player p;
        Session session = DbConnection.getSession();
        try {
            Query query = session.createQuery("from PlayerEntity where id=:playerID");
            query.setParameter("playerID", playerID);
            if (query.list().get(0) != null) {
                p = new Player((PlayerEntity)query.list().get(0));
                return Response
                        .ok(p)
                        .header("Access-Control-Allow-Origin", "*")
                        .build();
            } else throw new Exception("No player found with that ID.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error : " + e.getMessage());
            return Response
                    .serverError()
                    .status(500)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } finally {
            session.close();
        }
    }

    /**
     * HTTP GET-request that returns all courses as a JSON list of objects.
     *
     * @return              HTTP response with appropriate list of courses and status.
     */
    @Path("/courses")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourses() {
        List<Course> courses = new ArrayList<Course>();
        Session session = DbConnection.getSession();
        try {
            Query query = session.createQuery("from CourseEntity");
            for ( CourseEntity ce : (List<CourseEntity>)query.list()) {
                courses.add(new Course(ce));
            }
            GenericEntity<List<Course>> coursesGen = new GenericEntity<List<Course>>(courses) {};
            return Response
                    .ok(coursesGen)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return Response
                    .serverError()
                    .status(500)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } finally {
            session.close();
        }
    }

    /**
     * HTTP GET-request that returns course by ID as a JSON object.
     *
     * @param courseID      Requested course's ID.
     * @return              HTTP response with appropriate course object and status.
     */
    @Path("/course")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourse(@QueryParam("id") Integer courseID) {
        Course c;
        Session session = DbConnection.getSession();
        try {
            Query query = session.createQuery("from CourseEntity where id=:courseID");
            query.setParameter("courseID", courseID);
            if (query.list().get(0) != null) {
                c = new Course((CourseEntity) query.list().get(0));
                return Response
                        .ok(c)
                        .header("Access-Control-Allow-Origin", "*")
                        .build();
            } else throw new Exception("No course found with that id");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return Response
                    .serverError()
                    .status(500)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } finally {
            session.close();
        }
    }
}
