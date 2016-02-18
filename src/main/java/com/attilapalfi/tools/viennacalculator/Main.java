package com.attilapalfi.tools.viennacalculator;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start( Stage primaryStage ) throws Exception {
        URL fxml = getClass().getClassLoader().getResource( "fxml/ViennaCalculator.fxml" );
        Parent root = FXMLLoader.load( fxml );
        primaryStage.setTitle( "Vienna Calculator" );
        primaryStage.setScene( new Scene( root, 860, 610 ) );
        primaryStage.setMinWidth( 870 );
        primaryStage.setMinHeight( 620 );
        primaryStage.show();

    }


    public static void main( String[] args ) {
        launch( args );
    }
}
