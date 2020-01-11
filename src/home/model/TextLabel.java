package home.model;

import home.Application;
import home.gui.DialogManager;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Font;

public class TextLabel extends TextField
{
  private double[] position;
  private double size;

  public double[] getPosition()
  {
    return position;
  }

  public double getSize()
  {
    return this.size;
  }

  public TextLabel(double x, double y, double size, double rotation, String text)
  {
    this.setText(text);
    this.position = new double[] {x, y};
    this.size = size;

    this.setEditable(false);
    this.setCursor(Cursor.DEFAULT);
    this.setAlignment(Pos.CENTER);

    this.getStyleClass().clear();
    this.getStyleClass().add("view-label");

    this.setTranslateX(100.0);
    this.setTranslateY(100.0);

    // rotation of label is explicitly saved in its' rotational value, as it does not depend on the zoom or scroll pane panning
    this.setRotate(rotation);

    ContextMenu cmenu = new ContextMenu();
    MenuItem delete = new MenuItem("Delete");
    delete.setOnAction(e -> {
      // remove self. the java equivalent of suicide.
      if (DialogManager.confirm("Are you sure?", "Do you want to remove the Label \'" + this.getText() + "\'? This cannot be undone!"))
      {
        Application.getModel().getLabels().remove(this);
        Application.canvas().removeInteractable(this);
      }
    });

    MenuItem edit = new MenuItem("Edit Text");
    edit.setOnAction(e -> {
      this.setEditable(true);
      this.setFocusTraversable(true);
      this.setCursor(Cursor.TEXT);

      this.setStyle("-fx-text-fill: red;");

      Application.status("Change the labels' text to whatever you like. Press [ENTER] to save.");
    });

    cmenu.getItems().addAll(edit, delete);

    this.setOnMouseClicked(e -> {
      if (e.isShiftDown() && e.getButton() == MouseButton.SECONDARY && !Application.canvas().isDrawing())
      {
        cmenu.show(this, e.getScreenX(), e.getScreenY());
      }
    });

    double[] t = new double[2];
    this.setOnMousePressed(e -> {
      if (e.isShiftDown() && e.getButton() == MouseButton.PRIMARY && !Application.canvas().isDrawing())
      {
        t[0] = this.getTranslateX() - e.getSceneX();
        t[1] = this.getTranslateY() - e.getSceneY();
        this.setCursor(Cursor.MOVE);
      }
    });

    this.setOnMouseReleased(e -> this.setCursor(Cursor.DEFAULT));

    this.setOnMouseDragged(e -> {
      if (!this.isEditable())
      {
        this.deselect();
      }

      if (e.isShiftDown() && e.getButton() == MouseButton.PRIMARY && !Application.canvas().isDrawing())
      {
        this.setTranslateX(e.getSceneX() + t[0]);
        this.setTranslateY(e.getSceneY() + t[1]);
        this.position[0] = (e.getSceneX() + t[0]) / Application.canvas().getView().getScale();
        this.position[1] = (e.getSceneY() + t[1]) / Application.canvas().getView().getScale();
      }
    });

    // ok so this is a hack: Use ScrollEvent.SCROLL here and ScrollEvent.ANY to control zoom of canvas (elsewhere)
    // because SCROLL is more specific than ANY, scroll will be executed first!!!
    // consume the event to then stop propagation to the canvas zoom subroutine
    this.addEventFilter(ScrollEvent.SCROLL, event -> {
      if (event.isShiftDown() && !Application.canvas().isDrawing())
      {
        // we holding shift, so use deltaX!!!
        if (event.getDeltaX() > 0)
        {
          this.setRotate(this.getRotate() + 15);
        }
        else if (event.getDeltaX() < 0)
        {
          this.setRotate(this.getRotate() - 15);
        }
        event.consume();
      }
    });

    this.setOnContextMenuRequested(Event::consume);

    this.setOnAction(e -> {
      this.setEditable(false);
      this.setFocusTraversable(false);
      this.setCursor(Cursor.DEFAULT);

      this.setStyle("-fx-text-fill: black;");

      Application.status("Label changed to \'" + this.getText() + "\'");
    });
  }
}
