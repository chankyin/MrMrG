package me.noip.chankyin.mrmrg.ui.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import lombok.Cleanup;
import lombok.SneakyThrows;

import me.noip.chankyin.mrmrg.MrMrG;
import me.noip.chankyin.mrmrg.physics.Project;
import me.noip.chankyin.mrmrg.utils.io.SavedObjectOutputStream;

public class ProjectMainPage extends BorderPane implements Initializable{
	private final Stage stage;
	private final Project project;

	@FXML private Group center;
	@FXML private Tab saveTab;
	@FXML private TextField savePathField;
	@FXML private Button savePathButton;
	@FXML private Button saveButton;
	@FXML private Tab particleListTab;

	@SneakyThrows({IOException.class})
	public ProjectMainPage(Stage stage, Project project){
		this.stage = stage;
		this.project = project;
		URL url = getClass().getClassLoader().getResource("fxml/ProjectMain.fxml");
		FXMLLoader loader = new FXMLLoader(url);
		loader.setRoot(this);
		loader.setController(this);
		loader.load();

		updateTitle();
	}

	@SneakyThrows({IOException.class})
	private void updateTitle(){
		stage.setTitle(String.format("%s - [%s] - %s %s",
				project.getName(), project.getLocation() != null ? project.getLocation().getCanonicalPath() : "Not saved",
				MrMrG.ARTIFACT_NAME, MrMrG.VERSION_NAME));
	}

	@Override
	@SneakyThrows({IOException.class})
	public void initialize(URL location, ResourceBundle resources){
		assert center != null;
		assert saveTab != null;
		assert savePathField != null;
		assert savePathButton != null;
		assert saveButton != null;
		assert particleListTab != null;

		String savePath;
		if(project.getLocation() != null){
			savePath = project.getLocation().getCanonicalPath();
		}else{
			savePath = new File(MrMrG.DATA_DIR, project.getName() + ".setup").getCanonicalPath();
		}
		savePathField.setText(savePath);
		savePathButton.setOnAction(this::onSavePathButtonClick);
		saveButton.setOnAction(this::onSaveButtonClick);
	}

	@SneakyThrows({IOException.class})
	public void onSavePathButtonClick(ActionEvent e){
		File current = new File(savePathField.getText());
		File chooserDir = current.isDirectory() ? current : current.getParentFile();
		if(chooserDir == null || !chooserDir.isDirectory()){
			chooserDir = MrMrG.DATA_DIR;
		}
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(chooserDir);
		if(!current.getName().endsWith(".setup")){
			chooser.setInitialFileName(current.getName());
		}
		chooser.setTitle("Save as...");
		chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("MrMrG setup files", "setup", "dat"));

		File choosen = chooser.showSaveDialog(stage);
		savePathField.setText(choosen.getCanonicalPath());
	}

	@SneakyThrows({IOException.class})
	public void onSaveButtonClick(ActionEvent e){
		File target = new File(savePathField.getText());
		try{
			@Cleanup SavedObjectOutputStream os = new SavedObjectOutputStream(new FileOutputStream(target));
		}catch(IOException ex){
			new Alert(Alert.AlertType.ERROR, String.format("Error trying to write to %s: %s",
					target.getCanonicalPath(), ex.getMessage()), ButtonType.CANCEL);
		}
	}
}
