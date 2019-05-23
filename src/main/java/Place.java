public class Place {
    public String Title;
    public Integer CategoryID;
    public Integer SubCategoryID;
    public Integer IsTemporary;
    public String DateStart;
    public String DateEnd;
    public Integer Price;
    public String Time;

    public Place(String title, Integer categoryID, Integer subCategoryID, Integer isTemporary, String dateStart, String dateEnd, Integer price, String time) {
        Title = title;
        CategoryID = categoryID;
        SubCategoryID = subCategoryID;
        IsTemporary = isTemporary;
        DateStart = dateStart;
        DateEnd = dateEnd;
        Price = price;
        Time = time;
    }


}
