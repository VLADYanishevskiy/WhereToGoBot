/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.sqlite.JDBC; 
import java.sql.*;
import java.util.*;
 
public class DBHelper {
 

    private static final String CON_STR = "jdbc:sqlite:/Users/vladislav/IdeaProjects/WhereToGoBot/WhereToGoDB.db";
 

    private static DBHelper instance = null;
 
    public static synchronized DBHelper getInstance() throws SQLException {
        if (instance == null)
            instance = new DBHelper();
        return instance;
    }

    private Connection connection;
 
    DBHelper() throws SQLException {
        DriverManager.registerDriver(new JDBC());

        this.connection = DriverManager.getConnection(CON_STR);
        System.out.println("Connected");
    }
 
    public List<Place> getAllPlaces() {

        try (Statement statement = this.connection.createStatement()) {

            List<Place> Places = new ArrayList<Place>();

            ResultSet resultSet = statement.executeQuery("SELECT Title, CategoryID, SubCategoryID , IsTemporary , DateStart , DateEnd , Price , Time  FROM places");

            while (resultSet.next()) {
                Places.add(new Place(resultSet.getString("Title"),
                                            resultSet.getInt("CategoryID"),
                                            resultSet.getInt("SubCategoryID"),
                                            resultSet.getInt("IsTemporary"),
                                            resultSet.getString("DateStart"),
                                            resultSet.getString("DateEnd"),
                                            resultSet.getInt("Price"),
                                            resultSet.getString("Time")));
            }

            return Places;
 
        } catch (SQLException e) {
            e.printStackTrace();

            return Collections.emptyList();
        }
    }

    public void addPlace(Place place) {

        try (PreparedStatement statement = this.connection.prepareStatement(
                        "INSERT INTO places(`Title`, `CategoryID`, `SubCategoryID` , `IsTemporary` , `DateStart`, `DateEnd`, `Price`, `Time`) " +
                         "VALUES(?, ?, ? , ? , ? , ? , ? , ?)")) {

            statement.setObject(1, place.Title);
            statement.setObject(2, place.CategoryID);
            statement.setObject(3, place.SubCategoryID);
            statement.setObject(4, place.IsTemporary);
            statement.setObject(5, place.DateStart);
            statement.setObject(6, place.DateEnd);
            statement.setObject(7, place.Price);
            statement.setObject(8, place.Time);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 
    // Удаление продукта по id
    public void deletePlace(int id) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM places WHERE id = ?")) {
            statement.setObject(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void printPlaces(){
        List<Place> Places = this.getAllPlaces();
        
        for(Place place:Places){
            System.out.println(place);
        }
            
    }
}

