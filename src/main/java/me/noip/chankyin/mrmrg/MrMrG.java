package me.noip.chankyin.mrmrg;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import me.noip.chankyin.mrmrg.ui.openproject.ChooseProjectPage;

public class MrMrG extends Application{
	public final static String ARTIFACT_NAME = "MrMrG";
	public final static String VERSION_NAME = "2.0";

	public static File DATA_DIR;

	public static void main(String[] args){
		DATA_DIR = new File(".", args.length > 0 ? args[0] : "data");
		DATA_DIR.mkdirs();

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException{
		primaryStage.setScene(new Scene(new ChooseProjectPage(primaryStage)));
		primaryStage.setTitle(String.format("Welcome to %s %s", ARTIFACT_NAME, VERSION_NAME));
		primaryStage.setMaximized(true);
		primaryStage.show();
	}
}
