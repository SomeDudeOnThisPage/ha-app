package home.gui;

import home.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable
{
  @FXML
  private Menu serialSelectionMenu;

  @FXML
  private ScrollPane scrollPane;

  @Override
  public void initialize(URL url, ResourceBundle resources)
  {
    scrollPane.setContent(new FloorPlan());
  }

  @FXML
  public void onSerialPortSelection(ActionEvent event)
  {

  }

  @FXML
  public void menu_onLoadFloorPlan()
  {
    // simple JavaFX file chooser to load our map
    DirectoryChooser selector = new DirectoryChooser();
    selector.setTitle("Select Directory...");
    selector.setInitialDirectory(new File("resources/maps"));
    final File directory = selector.showDialog(Application.STAGE);

    if (directory != null && directory.isDirectory())
    {
      // validate folder to contain necessary map files
      for (final File file : Objects.requireNonNull(directory.listFiles()))
      {
        if (!file.isDirectory())
        {
          if (file.getName().equals("map.json"))
          {
            Application.setModel(file);
            return;
          }
        }
      }

      // seems like we didn't find a map.json in the selected directory
      // create error dialog
      Application.debug("cannot load floor plan from directory \'" + directory.getPath() + "\' - no map.json file found");

      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Unable to load Floor Plan");
      alert.setContentText("Could not load floor plan from \'" + directory.getPath() + "\' - no map.json file was found.\nPlease try again with a valid directory.");
      alert.showAndWait();
    }
  }
}
