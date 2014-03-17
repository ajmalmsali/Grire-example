/*
 * Copyright (C) 2013 Lazaros Tsochatzidis
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.grirefx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.grire.Helpers.Listeners.Listened;
import org.grire.Helpers.Listeners.ProgressMadeListener;

import java.net.URL;
import java.util.ResourceBundle;


/**
 *
 * @author Lazaros
 */
public class WorklistController implements Initializable {
    MainController mainController;
    ObservableList<Listened> tasks=FXCollections.observableArrayList();
    BooleanProperty autoPick=new SimpleBooleanProperty(true);
    BooleanProperty selectedSomething=new SimpleBooleanProperty(false);
    public BooleanProperty working=new SimpleBooleanProperty(false);
    Thread t;

    @FXML
    ListView<Listened> tasksListView;

    @FXML
    public Label nameLabel,progressLabel;

    @FXML
    public ProgressBar progressBar;

    @FXML
    ToggleButton auto;

    @FXML
    Button nextButton;
    @FXML
    Button upButton;
    @FXML
    Button downButton;
    @FXML
    Button removeButton;

    public void addTask(Listened task) {
        tasks.add(task);
        if (autoPick.get()) startWorking();
    }

    public void shutDown() {
        t.stop();
    }

    private void startWorking() {
        if (!working.get() && !tasks.isEmpty()) {
            final Listened listened=tasks.remove(0);
            Task task=new Task() {

                @Override
                protected Object call() throws Exception {
                    listened.addProgressListener(new ProgressMadeListener() {
                        @Override
                        public void OnProgressMade(int progress, int total, String message) {
                            updateProgress(progress, total);
                            updateMessage(message);
                        }
                    });
                    listened.run();
                    return null;
                }
            };
            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    completed();
                }
            });
            progressBar.progressProperty().bind(task.progressProperty());
            progressLabel.textProperty().bind(task.messageProperty());
            nameLabel.setText(listened.toString());
            t=new Thread(task);
            t.start();
            working.setValue(true);

        }
    }

    private void completed() {
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
        progressLabel.textProperty().unbind();
        progressLabel.setText("");
        nameLabel.setText("");
        working.setValue(false);
        mainController.CheckContents();
        if (autoPick.get()) startWorking();
    }

    @FXML
    private void closeIssued(ActionEvent e) {
        Stage stage = (Stage) ((Button)e.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void nextIssued() {
        startWorking();
    }

   @FXML
   private void removeIssued() {
       int selectedIndex = tasksListView.getSelectionModel().getSelectedIndex();
       tasksListView.getSelectionModel().clearSelection();
       tasksListView.getItems().remove(selectedIndex);
       tasks.remove(selectedIndex);
   }

   public void setMainController(MainController mc) {
       mainController=mc;
   }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tasksListView.setItems(tasks);
        autoPick.bind(auto.selectedProperty());
        nextButton.disableProperty().bind(autoPick);
        upButton.disableProperty().bind(selectedSomething.not());
        downButton.disableProperty().bind(selectedSomething.not());
        removeButton.disableProperty().bind(selectedSomething.not());
        tasksListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Listened>() {
            @Override
            public void onChanged(Change<? extends Listened> change) {
                if (!tasksListView.getSelectionModel().isEmpty()) {
                    selectedSomething.setValue(true);
                }
            }
        });
    }
}