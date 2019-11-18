package home.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable
{
  @FXML
  Menu serialSelectionMenu;

  @FXML
  ScrollPane scrollPane;

  @Override
  public void initialize(URL url, ResourceBundle resources)
  {
    scrollPane.setContent(new FloorPlan());
  }

  @FXML
  public void onSerialPortSelection(ActionEvent event)
  {

  }
}
