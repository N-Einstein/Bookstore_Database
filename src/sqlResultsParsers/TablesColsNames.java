package sqlResultsParsers;

public class TablesColsNames {
	private TablesColsNames(){}

	
	
	final public static String BOOK = "BOOK";
    final public static String BOOK_TITLE = "Title";
    final public static String BOOK_PRICE = "Price";
    final public static String BOOK_PUBLISHER = "Publisher";
    final public static String BOOK_AUTHOR = "Author";
    final public static String BOOK_CATEGORY = "Category";
    final public static String BOOK_PUBLICATION_YEAR = "PublicationYear";
    final public static String BOOK_ISBN = "ISBN";
    final public static String BOOK_COVER_IMAGE = "CoverImage";
    
    
    final public static String USER= "USER";
    final public static String USER_ID= "ID";
    final public static String USER_EMAIL= "Email";
    final public static String USER_USERNAME= "UserName";
    final public static String USER_USER_FNAME= "FName";
    final public static String  USER_USER_LNAME= "LName";
    final public static String USER_PASSWORD= "Password";
    final public static String USER_ADDRESS= "Address";
    final public static String USER_PHONE= "Telephone";
    final public static String USER_ROLE= "Role";    
    
    final public static String AUTHOR= "AUTHOR";
    final public static String AUTHOR_NAME= "AuthorName";
    final public static String AUTHOR_BOOK_ISBN= "BookISBN";
    final public static String AUTHOR_VALID= "valid";
    
    final public static String LIBRARY_ORDERS= "Library_orders";
    final public static String LIBRARY_ORDERS_ORDER_ID= "OrderId";
    final public static String LIBRARY_ORDERS_USER_ID= "UserId";
    final public static String LIBRARY_ORDERS_ISBN= "ISBN";
    final public static String LIBRARY_ORDERS_QUANTITY= "Quantity";
    final public static String LIBRARY_ORDERS_DATE= "Date";
	public static final String LIBRARY_ORDERS_VALID = "valid";


    
    final public static String STOCK= "STOCK";
    final public static String STOCK_ISBN= "ISBN";
    final public static String STOCK_THRESHOLD= "Threshold";
    final public static String STOCK_QUANTITY= "Quantity";
    final public static String STOCK_VALID= "valid";
    
    final public static String PUBLISHER= "publisher";
    final public static String PUBLISHER_NAME= "Name";
    final public static String PUBLISHER_ADDRESS= "Address";
    final public static String PUBLISHER_TELEPHONE= "Telephone";
    final public static String PUBLISHER_VALID= "valid";
    }
