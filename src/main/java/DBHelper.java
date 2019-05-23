/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.sqlite.JDBC;

import java.sql.*;
import java.util.*;
 
public class DBHelper {
 

    private static final String CON_STR = "jdbc:sqlite:/Users/vladislav/IdeaProjects/WhereToGoBot/src/main/java/WhereToGoDB.db";
 

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

    public String getShowAllPlaces(){
        List<Place> places = getAllPlaces();

        String answer = "Итак , у нас есть для тебя : \n";

        for (int i = 0 ; i < places.size();i++){
            answer += places.get(i).Title + "\n";
            answer += places.get(i).DateStart + "\n";
            answer += places.get(i).DateEnd + "\n";
            answer += places.get(i).Time + "\n";
            answer += places.get(i).Price + "\n\n--------------------\n\n";
        }

        if(places.size() == 0)
            answer = "Жаль , но пока ничего нет(";
        return answer;

    }

    public List<Place> getAllPlacesBySubCategory(int subCategoryID) {

        try (Statement statement = this.connection.createStatement()) {

            List<Place> Places = new ArrayList<Place>();

            ResultSet resultSet = statement.executeQuery("SELECT Title, CategoryID, SubCategoryID , IsTemporary , DateStart , DateEnd , Price , Time  FROM places WHERE SubCategoryID == " + subCategoryID);

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

    public int getSubCategoryID(String subCategory){
        int subCategoryID = 0;
        try(Statement statement = this.connection.createStatement()){

            ResultSet resultSet = statement.executeQuery("SELECT ID FROM subCategories WHERE Title == \"" + subCategory + "\"");


            if(resultSet.next()){
                subCategoryID = resultSet.getInt("ID");
            }else{
                throw  new SQLException();
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return subCategoryID;
    }


    public String GetCategoryByID(int categoryID){

        try(Statement statement = this.connection.createStatement()){

            ResultSet resultSet = statement.executeQuery("SELECT Title FROM categories WHERE ID == " + categoryID);


            if(resultSet.next()){
                return resultSet.getString("Title");
            }else{
                throw  new SQLException();
            }

        }catch (SQLException e) {
            e.printStackTrace();

            return " ";
        }
    }

    public List<String> GetAllCategories(){
        try (Statement statement = this.connection.createStatement()) {

            List<String> categories = new ArrayList();

            ResultSet resultSet = statement.executeQuery("SELECT Title FROM categories");

            while (resultSet.next()) {
                categories.add(resultSet.getString("Title"));
            }

            return categories;

        } catch (SQLException e) {
            e.printStackTrace();

            return Collections.emptyList();
        }
    }

    public List<String> GetAllSubCategories(String mainCategory){

        int mainCategoryID = getCategoryIDbyTitle(mainCategory);


        try (Statement statement = this.connection.createStatement()) {

            List<String> subCategories = new ArrayList();

            ResultSet resultSet = statement.executeQuery("SELECT Title FROM subCategories WHERE parrentCategory == " + mainCategoryID);

            while (resultSet.next()) {
                subCategories.add(resultSet.getString("Title"));
            }

            subCategories.add("Все");
            return subCategories;

        } catch (SQLException e) {
            e.printStackTrace();

            return Collections.emptyList();
        }
    }

    public List<String> getAllSubCategories(){
        try (Statement statement = this.connection.createStatement()) {

            List<String> subCategories = new ArrayList();

            ResultSet resultSet = statement.executeQuery("SELECT Title FROM subCategories  ");

            while (resultSet.next()) {
                subCategories.add(resultSet.getString("Title"));
            }

            subCategories.add("Все");
            return subCategories;

        } catch (SQLException e) {
            e.printStackTrace();

            return Collections.emptyList();
        }
    }

    public boolean checkInSubCategory(String subcategory){
        List<String> subCategories = getAllSubCategories();
        for (int i = 0 ; i < subCategories.size() ; i++){
            if(subCategories.get(i).equals(subcategory)){
                return true;
            }
        }
        return false;
    }

    public String ShowBySubCategory(String subCategory){

        subCategory = subCategory.replace("/" , "");
        System.out.println(subCategory);
        if(checkInSubCategory(subCategory)){
            String answer = "Итак , у нас есть для тебя : \n";
            List<Place> places = getAllPlacesBySubCategory(getSubCategoryID(subCategory));
            for (int i = 0 ; i < places.size();i++){
                answer += places.get(i).Title + "\n";
                answer += places.get(i).DateStart + "\n";
                answer += places.get(i).DateEnd + "\n";
                answer += places.get(i).Time + "\n";
                answer += places.get(i).Price + "\n\n--------------------\n\n";
            }

            if(places.size() == 0)
                answer = "Жаль , но пока ничего нет(";

            return answer;
        }
        return "Request error";
    }

    public String GetPlaceByCategory(String mainCategory){
        String answer = "Итак , у нас есть для тебя : \n";

        int mainCategoryID = getCategoryIDbyTitle(mainCategory);



        try (Statement statement = this.connection.createStatement()) {

            List<Place> places = new ArrayList();
            ResultSet resultSet = statement.executeQuery("SELECT Title, CategoryID, SubCategoryID , IsTemporary , DateStart , DateEnd , Price , Time  FROM places WHERE CategoryID == " + mainCategoryID);

            while (resultSet.next()) {
                places.add(new Place(resultSet.getString("Title"),
                        resultSet.getInt("CategoryID"),
                        resultSet.getInt("SubCategoryID"),
                        resultSet.getInt("IsTemporary"),
                        resultSet.getString("DateStart"),
                        resultSet.getString("DateEnd"),
                        resultSet.getInt("Price"),
                        resultSet.getString("Time")));
            }

            for (int i = 0 ; i < places.size();i++){
                answer += places.get(i).Title + "\n";
                answer += places.get(i).DateStart + "\n";
                answer += places.get(i).DateEnd + "\n";
                answer += places.get(i).Time + "\n";
                answer += places.get(i).Price + "\n\n--------------------\n\n";
            }

            if(places.size() == 0)
                answer = "Жаль , но пока ничего нет(";
            return answer;
        } catch (SQLException e) {
            e.printStackTrace();

            return "Жаль , но пока ничего нет(";
        }
    }


    public int getCategoryIDbyTitle(String mainCategory){
        int mainCategoryID = 0;
        try(Statement statement = this.connection.createStatement()){

            ResultSet resultSet = statement.executeQuery("SELECT ID FROM categories WHERE Title == \"" + mainCategory + "\"");


            if(resultSet.next()){
                mainCategoryID = resultSet.getInt("ID");
            }else{
                throw  new SQLException();
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return mainCategoryID;
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

