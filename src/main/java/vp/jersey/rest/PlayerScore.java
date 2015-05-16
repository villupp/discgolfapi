package vp.jersey.rest;

/**
 * Holds a single players scores for a single round.
 * @author Ville Piirainen
 */
public class PlayerScore {
    /** Player object (that played the round) */
    private Player player;
    /** Scores in array */
    private int[] scores;

    public PlayerScore() {

    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int[] getScores() {
        return scores;
    }

    public void setScores(int[] scores) {
        this.scores = scores;
    }

    /**
     * Parses the scores-String with the comma-separated-values and returns integer array.
     * @param scoresCSV Player scores as csv.
     * @return Integer array of the scores.
     */

    public static int[] parsePlayerScores(String scoresCSV) {
        String[] strScores = scoresCSV.split(",");
        int[] intScores = new int[strScores.length];
        for (int i = 0; i < strScores.length; i++) {
            intScores[i] = Integer.parseInt(strScores[i]);
        }
        return intScores;
    }
}
