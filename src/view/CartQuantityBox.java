package view;

import java.sql.ResultSet;

import javax.crypto.CipherInputStream;

import dataBaseModel.User;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import viewControllers.CartViewController;

public class CartQuantityBox {
	
	int quantityInt;
	String ISBN;
	CartViewController controller;
	SQLResultParser parser;
	
	public CartQuantityBox(CartViewController controller) {
		this.controller = controller;
		this.parser = controller.getParser();
	}
	

	public HBox getControlBox(ResultSet resultSet) {
		ISBN = parser.getResult(TablesColsNames.BOOK_ISBN, resultSet);
		HBox hBox = new HBox();
		VBox vBox = new VBox();
		vBox.setAlignment(Pos.CENTER);
		vBox.setSpacing(3);
		HBox quantityBox = buildQuantitybox(ISBN);
		HBox price = buildPriceBox(resultSet);
		
		vBox.getChildren().add(quantityBox);
		vBox.getChildren().add(price);
		
		hBox.getChildren().add(vBox);
		return hBox;
		
	}


	private HBox buildPriceBox(ResultSet resultSet) {
		HBox price = new HBox();
		price.setAlignment(Pos.CENTER);
		String priceStr = parser.getResult(TablesColsNames.BOOK_PRICE, resultSet);
		Label label = new Label(User.decimarFormatter.format(quantityInt * Double.parseDouble(priceStr)) + "$");
		label.setId("p"+ISBN);
		price.getChildren().add(label);
		
		return price;
	}


	private HBox buildQuantitybox(String ISBN) {
		HBox quantityBox = new HBox();
		quantityBox.setSpacing(8);
		Button minus = new Button();
		minus.getStyleClass().add("minus");
		minus.setUserData(ISBN);
		minus.setOnMouseClicked(e-> {
			controller.updateQuantity(-1, (String) minus.getUserData());
		});
		quantityInt = (User.getInstance().cartISBN.get(ISBN));
		Label quantity = new Label(Integer.toString(quantityInt));
		quantity.setId("q" + ISBN);
		quantity.getStyleClass().add("quantity");
		Button plus = new Button();
		plus.getStyleClass().add("plus");
		plus.setOnMouseClicked(e-> {
			controller.updateQuantity(1, (String) minus.getUserData());
		});
		quantityBox.getChildren().add(minus);
		quantityBox.getChildren().add(quantity);
		quantityBox.getChildren().add(plus);
		return quantityBox;
	}

}
