package in.zeko.stepcount;

import org.postgresql.util.PSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import in.zeko.Database;

// The Java class will be hosted at the URI path "/helloworld"
@Path("/stepCount")
public class StepCount {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getFoo() {
    return ("alive-current");
  }

  @GET
  @Path("/single")
  @Produces(MediaType.TEXT_PLAIN)
  public String getSingleDayStepCount(@QueryParam("userId") int userId, @QueryParam("day") int day) {

    System.out.println("GET:Single endpoint hit");

    try (Connection conn = Database.getConnection();
         PreparedStatement st = conn.prepareStatement(
                 "SELECT SUM(stepcount) from step_counts where userid = ? AND day=?")) {

      st.setInt(1, userId);
      st.setInt(2, day);

      ResultSet rs = st.executeQuery();

      rs.next();
      String res = rs.getString(1);
      rs.close();

      return res;

    } catch (Exception e) {
      System.out.println("Single Endpoint exception! : " + e);
      return e.toString();
    }
  }

  @GET
  @Path("/range")
  @Produces(MediaType.TEXT_PLAIN)
  public String getStepCountInRange(@QueryParam("userId") int userId, @QueryParam("startDay") int startDay, @QueryParam("numDays") int numDays) {

    System.out.println("GET:range endpoint hit");

    try {

      Connection conn = Database.getConnection();

      // process
      PreparedStatement st = conn.prepareStatement(
              "SELECT userrecords.day, SUM(userrecords.stepcount) AS totalcount FROM (SELECT * FROM step_counts WHERE userid=? AND day >= ?) AS userrecords GROUP BY userrecords.day ORDER BY userrecords.day LIMIT ? ");


      st.setInt(1, userId);
      st.setInt(2, startDay);
      st.setInt(3, numDays);

      ResultSet rs = st.executeQuery();

      String res = "";

      while (rs.next()) {
        res += rs.getString("totalCount") + " ";
      }

      res = res.trim();

      rs.close();
      st.close();
      conn.close();

      return res;

    } catch (ClassNotFoundException e) {
      System.out.println(e);
    } catch (PSQLException e) {
      System.out.println(e);
    } catch (SQLException e) {
      System.out.println(e);
    }
    return "request-failed";
  }

  @GET
  @Path("/current")
  @Produces(MediaType.TEXT_PLAIN)
  public String getCurrentStepCount(@QueryParam("userId") int userId) {

    System.out.println("GET:Current endpoint hit");


    try (Connection conn = Database.getConnection();
         PreparedStatement st = conn.prepareStatement(
                 "SELECT SUM(stepcount) FROM step_counts WHERE day = (SELECT MAX(day) from step_counts WHERE userid=?) AND userid=?;")) {

      st.setInt(1, userId);
      st.setInt(2, userId);

      ResultSet rs = st.executeQuery();

      rs.next();
      String res = rs.getString(1);
      rs.close();
      return res;

    } catch (Exception e) {
      System.out.println("exception in current! : " + e);
      return e.toString();
    }

  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public String postText(MultivaluedMap<String, String> formParams) {

    System.out.println("POST endpoint hit");

    int userId = Integer.parseInt(formParams.get("userId").get(0));
    int day = Integer.parseInt(formParams.get("day").get(0));
    int timeInterval = Integer.parseInt(formParams.get("timeInterval").get(0));
    int stepCount = Integer.parseInt(formParams.get("stepCount").get(0));

    try (Connection conn = Database.getConnection();
            // process
         PreparedStatement st =
                 conn.prepareStatement(
                         "INSERT INTO step_counts (userid, day, timeinterval, stepcount) " +
                                 "VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING")) {


      st.setInt(1, userId);
      st.setInt(2, day);
      st.setInt(3, timeInterval);
      st.setInt(4, stepCount);
      st.executeUpdate();

      return "success";

    } catch (Exception e) {
      System.out.println(e);
      return e.toString();
    }
  }
}