package view;

import java.awt.List;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

import dataBaseModel.User;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import viewControllers.ControllerInterface;
import viewControllers.SearchResultViewController;


public class BookSearchResultView {
	
	private SQLResultParser parser;
	private TablesColsNames tablesColsNames;
	private SearchResultViewController controller;
	private String ISBN;
	private double price;
	private ViewInterface prevView;
	
	public BookSearchResultView(ControllerInterface controller, ViewInterface prevView) {
		this.controller = (SearchResultViewController) controller;
		this.prevView = prevView;
	}
	
	public HBox buildBookDetails(ResultSet resultSet, SQLResultParser parser) {
		this.parser = parser;
		ISBN = parser.getResult(TablesColsNames.BOOK_ISBN, resultSet);
		price = Double.parseDouble(parser.getResult(TablesColsNames.BOOK_PRICE, resultSet));
		HBox hbox = new HBox();
		hbox.getStyleClass().add("hbox");
		
		HBox bookCover = getImageBox(parser.getResult(TablesColsNames.BOOK_COVER_IMAGE, resultSet));
		VBox detailsBox = getDetailsBox(resultSet);

		
        hbox.getChildren().add(bookCover);
        hbox.getChildren().add(detailsBox);        
        return hbox;
	}
	
	public HBox buildBookHBox(ResultSet resultSet, SQLResultParser parser) {
		HBox hbox = buildBookDetails(resultSet, parser);
		VBox cartSection = new VBox();
		cartSection.setAlignment(Pos.CENTER);
		cartSection.setSpacing(4);
		cartSection.getStyleClass().add("cart-section");
		Button cartButton = getCartButton(resultSet, hbox);
		Label msg = buildSuccessMsg();
		Label msg2 = buildErrorMsg();
		Region gap = new Region();
        hbox.setHgrow(gap, Priority.ALWAYS);
        
        hbox.getChildren().add(gap);
        cartSection.getChildren().add(cartButton);
        cartSection.getChildren().add(msg);
        cartSection.getChildren().add(msg2);
        hbox.getChildren().add(cartSection);
//        hbox.getChildren().add(cartButton);
        return hbox;
	}
	
	private Button getCartButton(ResultSet resultSet, HBox hbox) {
		Button button = new Button();
		button.getStyleClass().add("cart-button");
		button.setUserData(ISBN);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				controller.addToCart((String) button.getUserData());
			}
		});
		
		return button;
		
	}
	
	

	private Label buildSuccessMsg() {
		Label msg = new Label("Book added to your cart");
		
		if (!User.cartISBN.containsKey(ISBN)) {
			msg.setVisible(false);
		}
		msg.setAlignment(Pos.BASELINE_CENTER);
		
		msg.setId("msg" + ISBN);
		
		return msg;
	}
	private Label buildErrorMsg() {
		Label msg2 = new Label("Stock is Empty!");
		msg2.setVisible(false);
		msg2.setAlignment(Pos.BASELINE_CENTER);
		msg2.setId("msg2" + ISBN);
		return msg2;
	}

	private HBox getImageBox(String imagePath) {
		Image image = new Image("file:" + imagePath);
		ImageView iv = new ImageView();
        iv.setImage(image);
        iv.setFitWidth(100);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setCache(true);
        iv.setUserData(ISBN);
		iv.setOnMouseClicked(e-> {
        	controller.getdetails((String)iv.getUserData(), prevView);
        });
		HBox imageBox = new HBox();
		imageBox.setId("book-cover-image");
		imageBox.getChildren().add(iv);
        return imageBox;
	}
	
	private VBox getDetailsBox(ResultSet resultSet) {
		String bookName = parser.getResult(TablesColsNames.BOOK_TITLE, resultSet);
		String price = parser.getResult(TablesColsNames.BOOK_PRICE, resultSet);
		VBox vbox = new VBox();
        vbox.getStyleClass().add("vbox");
    
  	    Label title = new Label(bookName);
  	    title.setWrapText(true);
  	    title.setUserData(ISBN);
	    title.setId("title");
	    title.setOnMouseClicked(e -> {
	    	controller.getdetails((String) title.getUserData(), prevView);
	    });
        vbox.getChildren().add(title);
       
        Label priceLabel = new Label(User.decimarFormatter.format(Double.parseDouble(price)) + " $");
        priceLabel.setId("price");
        vbox.getChildren().add(priceLabel);
        
        return vbox;
	}
	
	
}
