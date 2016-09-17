package me.noip.chankyin.mrmrg.ui.openproject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import lombok.SneakyThrows;

import me.noip.chankyin.mrmrg.physics.Project;
import me.noip.chankyin.mrmrg.ui.project.ProjectMainPage;

public class ChooseProjectPage extends VBox implements Initializable{
	private final Stage stage;

	@FXML private Button buttonCreateNewProject;
	@FXML private Button buttonOpenProject;

	@SneakyThrows({IOException.class})
	public ChooseProjectPage(Stage stage){
		this.stage = stage;
		URL url = getClass().getClassLoader().getResource("fxml/ChooseProject.fxml");
		FXMLLoader loader = new FXMLLoader(url);
		loader.setRoot(this);
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources){
		assert buttonCreateNewProject != null && buttonOpenProject != null : "FXML fields";
		buttonCreateNewProject.setOnAction(this::createNewProject);
		buttonOpenProject.setOnAction(this::openProject);
	}

	public void createNewProject(ActionEvent e){
		Dialog<String> dialog = new TextInputDialog("Untitled");
		dialog.setTitle("New project");
		dialog.setHeaderText("Please enter project name");
		Optional<String> name = dialog.showAndWait();
		if(!name.isPresent()){
			return;
		}
		Project project = Project.createDefault(name.get());
		startProjectMainPage(project);
	}

	public void openProject(ActionEvent e){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose project file");
		fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("MrMrG project setup files", "setup"));
		File file = fileChooser.showOpenDialog(stage);
		startProjectMainPage(Project.fromFile(file));
	}

	private void startProjectMainPage(Project project){
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		ProjectMainPage page = new ProjectMainPage(stage, project);
		stage.setScene(new Scene(page));
		stage.setMaximized(true);
		stage.show();
		this.stage.hide();
	}
}
