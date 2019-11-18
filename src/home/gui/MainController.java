package home.gui;

import com.fazecast.jSerialComm.SerialPort;
import home.io.SerialIO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable
{
  @FXML
  Menu serialSelectionMenu;

  @Override
  public void initialize(URL url, ResourceBundle resources)
  {
    // clear items and show a list of all available ports
    serialSelectionMenu.setOnAction(e -> {
      serialSelectionMenu.getItems().clear();

      SerialPort[] ports = SerialIO.getPorts();

      for (SerialPort port : ports)
      {
        MenuItem pm = new MenuItem(port.getDescriptivePortName());
        serialSelectionMenu.getItems().add(pm);
      }

      serialSelectionMenu.show();
    });
  }

  @FXML
  public void onSerialPortSelection(ActionEvent event)
  {

  }
}
