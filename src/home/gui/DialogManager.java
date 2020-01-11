package home.gui;

import home.Application;
import home.model.House;
import home.model.Room;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Optional;

/**
 * All this class does is provide a collection of dialogs used throughout the app.
 * So boring I didn't even bother to comment it. Arrest me.
 * Also the code is inefficient af.
 */
public class DialogManager
{
  public static void error(String title, String content)
  {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Information");
    alert.setHeaderText(title);
    alert.setContentText(content);
    alert.showAndWait();
  }

  public static void info(String title, String content)
  {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText(title);
    alert.setContentText(content);
    alert.show();
  }

  public static boolean confirm(String title, String content)
  {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirm");
    alert.setHeaderText(title);
    alert.setContentText(content);

    Optional<ButtonType> result = alert.showAndWait();

    return result.filter(buttonType -> buttonType == ButtonType.OK).isPresent();
  }

  public static Pair<String, Integer> mapificator()
  {
    Dialog<Pair<String, Integer>> dialog = new Dialog<>();
    dialog.setTitle("New Map");
    dialog.setContentText("Create a new Map:");

    ButtonType confirm = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(confirm, ButtonType.CANCEL);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField name = new TextField();
    name.setPromptText("map name");

    Spinner<Integer> size = new Spinner<>();
    SpinnerValueFactory.IntegerSpinnerValueFactory ifac = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 50, 20);
    ifac.setAmountToStepBy(5);
    size.setValueFactory(ifac);

    grid.add(new Label("Map Name:"), 0, 0);
    grid.add(name, 1, 0);
    grid.add(new Label("Map Size:"), 0, 1);
    grid.add(size, 1, 1);

    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == confirm) {
        return new Pair<>(name.getText(), size.getValue());
      }
      return null;
    });

    Optional<Pair<String, Integer>> result = dialog.showAndWait();

    return result.orElse(null);
  }

  public static int temperatureficator(House model)
  {
    Dialog<Integer> dialog = new Dialog<>();
    dialog.setTitle("New Temperature");
    dialog.setContentText("Select a room to create a temperature display from:");

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    ButtonType confirm = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(confirm, ButtonType.CANCEL);

    ArrayList<String> choices = new ArrayList<>();

    for (Room room : model.getRooms())
    {
      if (room.isManaged())
      {
        choices.add(room.getName());
      }
    }

    ChoiceBox<String> rooms = new ChoiceBox<>();
    rooms.getItems().addAll(choices);

    grid.add(new Label("Room:"), 0, 0);
    grid.add(rooms, 1, 0);

    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == confirm)
      {
        for (Room room : model.getRooms())
        {
          if (room.getName().equals(rooms.getValue()))
          {
            return room.id();
          }
        }
      }
      return null;
    });

    Optional<Integer> result = dialog.showAndWait();

    return result.orElse(-1);
  }

  public static int[] lightificator(House model)
  {
    Dialog<Pair<String, Integer>> dialog = new Dialog<>();
    dialog.setTitle("New Light");
    dialog.setContentText("Select a room to add a light to:");

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    ButtonType confirm = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(confirm, ButtonType.CANCEL);

    ArrayList<String> choices = new ArrayList<>();

    for (Room room : model.getRooms())
    {
      if (room.isManaged())
      {
        choices.add(room.getName());
      }
    }

    ChoiceBox<String> rooms = new ChoiceBox<>();
    rooms.getItems().addAll(choices);

    Spinner<Integer> id = new Spinner<>();
    id.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 256, 0));

    grid.add(new Label("Room:"), 0, 0);
    grid.add(rooms, 1, 0);
    grid.add(new Label("Light ID:"), 0, 1);
    grid.add(id, 1, 1);

    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == confirm) {
        return new Pair<>(rooms.getValue(), id.getValue());
      }
      return null;
    });

    Optional<Pair<String, Integer>> result = dialog.showAndWait();

    if (result.isPresent())
    {
      for (Room room : model.getRooms())
      {
        if (room.getName().equals(result.get().getKey()))
        {
          return new int[]{ room.id(), result.get().getValue() };
        }
      }
    }

    return null;
  }

  public static Pair<String, Pair<Integer, Boolean>> roomificator() throws Exception
  {
    Dialog<Pair<String, Pair<Integer, Boolean>>> dialog = new Dialog<>();
    dialog.setTitle("New Room");

    ButtonType confirm = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(confirm, ButtonType.CANCEL);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField name = new TextField();
    name.setPromptText("room name");

    Spinner<Integer> id = new Spinner<>();
    id.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 256, 0));

    CheckBox managed = new CheckBox();
    managed.setSelected(true);

    // disable spinner when checkbox is checked as non-managed rooms don't need IDs
    managed.selectedProperty().addListener((observable, oldValue, newValue) -> id.setDisable(!newValue));

    grid.add(new Label("Room Name:"), 0, 0);
    grid.add(name, 1, 0);
    grid.add(new Label("Room ID:"), 0, 1);
    grid.add(id, 1, 1);
    grid.add(new Label("Managed:"), 0, 2);
    grid.add(managed, 1, 2);

    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == confirm) {
        return new Pair<>(name.getText(), new Pair<>(id.getValue(), managed.isSelected()));
      }
      return null;
    });

    Optional<Pair<String, Pair<Integer, Boolean>>> result = dialog.showAndWait();

    // if our ID already exists throw an error
    if (result.isPresent())
    {
      for (Room room : Application.getModel().getRooms())
      {
        boolean currentManaged = result.get().getValue().getValue();
        // we only care about IDs of managed rooms obviously
        if (currentManaged && room.isManaged() && room.id() == result.get().getValue().getKey())
        {
          throw new Exception("The selected ID already exists.");
        }
      }

      // if we fine return our data
      return result.get();
    }

    // if we have no values at all throw an error
    // this is actually intended behavior, don't error
    // throw new Exception("No valid values found.");
    return null;
  }
}