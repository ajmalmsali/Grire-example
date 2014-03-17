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

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.grire.BagOfVisualWords.BOVWSystem;
import org.grire.BagOfVisualWords.Functions.*;
import org.grire.BagOfVisualWords.Interfaces.VisualWordDescriptor;
import org.grire.BagOfVisualWords.Interfaces.WeightingScheme;
import org.grire.BagOfVisualWords.Structures.*;
import org.grire.GeneralUtilities.Data.GeneralData;
import org.grire.GeneralUtilities.Data.GeneralMap;
import org.grire.GeneralUtilities.Data.Histogram;
import org.grire.GeneralUtilities.GeneralStorers.GeneralMapDBStorer;
import org.grire.GeneralUtilities.GeneralStorers.GeneralStorer;
import org.grire.GeneralUtilities.Interfaces.ClusteringAlgorithm;
import org.grire.GeneralUtilities.Interfaces.FeatureExtractor;
import org.grire.GeneralUtilities.Interfaces.SimilarityMeasure;
import org.grire.Helpers.FileBlacklists.Blacklist;
import org.grire.Helpers.FileBlacklists.ExactFileBlacklist;
import org.grire.Helpers.Plugins.GRirePlugin;
import org.grire.Helpers.Plugins.PluginLoader;
import org.grirefx.Dialog.Dialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Lazaros
 */
public class MainController implements Initializable {
    
    GeneralStorer storer=null;
    String newTextField;
    ImagePool collection;
    PoolFeatures features;
    Codebook codebook;
    Index index;
    Histogram histogram;
    GeneralMap generalMap;
    List<PluginWrapper> plugins;
    boolean canceled;
    WorklistController worklistController;
    Stage worklist;
    BOVWSystem system;
    QueryPerformer performer;

    @FXML
    AnchorPane everything;
    @FXML
    AnchorPane collectionPane;
    @FXML
    AnchorPane codebookPane;
    @FXML
    AnchorPane featuresPane;
    @FXML
    AnchorPane indexPane;
    @FXML
    AnchorPane currentlyWorkingPane;
    @FXML
    AnchorPane histogramPane;
    @FXML
    AnchorPane generalMapsPane;
    @FXML
    AnchorPane systemConfigurationPane;
    @FXML
    AnchorPane searchPane;
    @FXML
    AnchorPane queryConfigurationPane;

    @FXML
    Button newCollectionButton;
    @FXML
    Button newFeaturesButton;
    @FXML
    Button newCodebookButton;
    @FXML
    Button newIndexButton;
    @FXML
    Button collectionDeleteImageButton;
    @FXML
    Button featuresDeleteIdButton;
    @FXML
    Button indexDeleteIdButton;
    
    @FXML
    ListView<String> collectionsListView;
    @FXML
    ListView<String> featuresListView;
    @FXML
    ListView<String> codebooksListView;
    @FXML
    ListView<String> indicesListView;
    @FXML
    ListView<String> collectionNamesListView;
    @FXML
    ListView<String> featuresIdListView;
    @FXML
    ListView<String> featuresFeaturesListView;
    @FXML
    ListView<String> codebookWordsListView;
    @FXML
    ListView<String> indexIdListView;
    @FXML
    ListView<String> indexHistogramListView;
    @FXML
    ListView<String> histogramXListView;
    @FXML
    ListView generalMapValueListView;
    @FXML
    ListView queryResultList;
    @FXML
    ListView queryTRECFilesListView;

    @FXML
    ImageView collectionImageView;
    @FXML
    ImageView queryImagePreview;


    @FXML
    Hyperlink featuresExtractedFromCollection;
    @FXML
    Label extractorExtractedFromCollection;
    @FXML
    Hyperlink codebookCreatedFromLabel;
    @FXML
    Label codebookMethodLabel;
    @FXML
    Hyperlink indexCreatedFeaturesLabel;
    @FXML
    Label indexDescriptorLabel;
    @FXML
    Label currentTaskLabel;
    @FXML
    Label progressMessageLabel;
    @FXML
    Hyperlink systemImageCollectionHyperlink;
    @FXML
    Hyperlink systemCollectionFeaturesHyperlink;
    @FXML
    Label systemDescriptorLabel;
    @FXML
    Hyperlink systemIndexHyperlink;
    @FXML
    Hyperlink queryConfigWeightingLabel;
    @FXML
    Hyperlink queryConfigSimilarityLabel;


    @FXML
    ProgressBar progressBar;
    @FXML
    ProgressIndicator imageCollectionIndicator,featuresIndicator,codebookIndicator,indexIndicator;

    @FXML
    TreeView generalDataTreeView;

    @FXML
    TextField histogramYField;
    @FXML
    TextField queryImageTextField;
    @FXML
    TextField queryTRECOutputTextField;
    @FXML
    TextField TRECExperimentNameTextField;
    @FXML
    TextField TRECResultsPerQueryTextField;
    @FXML
    TextField newNameTextField;
    @FXML
    TextArea generalMapValueTextArea;

    @FXML
    ToggleButton searchToggleButton;

    ObservableList<String> collectionsListViewStrings;
    ObservableList<String> featuresListViewStrings;
    ObservableList<String> codebooksListViewStrings;
    ObservableList<String> indicesListViewStrings;

    @FXML
    TitledPane CollectionTitledPane;

    @FXML
    private void reloadPluginsIssued(ActionEvent a) {
        try {
            List<GRirePlugin> plugins1 =PluginLoader.LoadPluginsFromDefaultFile();
            plugins=new ArrayList<>();
            for (GRirePlugin p:plugins1)
                plugins.add(new PluginWrapper(p));
        } catch (FileNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | ClassNotFoundException | InvocationTargetException | MalformedURLException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @FXML
    private void CreateTRECIssued(ActionEvent a) {
        File file = new File(queryTRECOutputTextField.getText());
        try {
            FileOutputStream outputStream=new FileOutputStream(file);
            worklistController.addTask(performer.NewTRECQuery(TRECExperimentNameTextField.getText(),queryTRECFilesListView.getItems(),outputStream,Integer.parseInt(TRECResultsPerQueryTextField.getText())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @FXML
    private void queryTRECClearIssued(ActionEvent a) {
        queryTRECFilesListView.getSelectionModel().clearSelection();
        queryTRECFilesListView.getItems().clear();
    }

    @FXML
    private void queryTRECRemoveIssued(ActionEvent a) {
        if (!queryTRECFilesListView.getSelectionModel().isEmpty()) {
            queryTRECFilesListView.getItems().remove(queryTRECFilesListView.getSelectionModel().getSelectedIndices());
        }
    }

    @FXML
    private void addFromFileTRECIssued(ActionEvent a) {
        FileChooser fc=new FileChooser();
        File file = fc.showOpenDialog(null);
        if (file!=null) {
            try {
                Scanner sc=new Scanner(file);
                while (sc.hasNextLine() ) {
                    String line=sc.nextLine();
                    if (!line.equals("")) {
                        queryTRECFilesListView.getItems().add(line);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    @FXML
    private void addFileTRECIssued(ActionEvent a) {
        FileChooser fc=new FileChooser();
        List<File> files = fc.showOpenMultipleDialog(null);
        for (File f:files)
            queryTRECFilesListView.getItems().add(f.getAbsolutePath());
    }

    @FXML
    private void querySearchIssued(ActionEvent a) {
        TreeSet<QueryPerformer.setItem> set = performer.NewQuery(new File(queryImageTextField.getText()));
        ObservableList objects = FXCollections.observableArrayList();
        for (QueryPerformer.setItem e:set) {
            try {
                objects.add(system.getCollection().getImage(e.id));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        queryResultList.setItems(objects);
    }

    @FXML
    private void queryBrowseTRECOutputIssued(ActionEvent a) {
        FileChooser fc=new FileChooser();
        File file = fc.showSaveDialog(null);
        if (file!=null) {
            queryTRECOutputTextField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void queryBrowseImageIssued (ActionEvent a) {
        FileChooser fc=new FileChooser();
        File file = fc.showOpenDialog(null);
        if (file!=null)
            queryImageTextField.setText(file.getAbsolutePath());
    }

    @FXML
    private void goToIndex(ActionEvent e) {
        searchToggleButton.setSelected(false);
        indicesListView.getSelectionModel().clearSelection();
        indicesListView.getSelectionModel().select(((Hyperlink)e.getSource()).getText());
    }

    @FXML
    private void goToCollection(ActionEvent e) {
        searchToggleButton.setSelected(false);
        collectionsListView.getSelectionModel().clearSelection();
        collectionsListView.getSelectionModel().select(((Hyperlink)e.getSource()).getText());
    }

    @FXML
    private void goToFeatures(ActionEvent e) {
        searchToggleButton.setSelected(false);
        featuresListView.getSelectionModel().clearSelection();
        featuresListView.getSelectionModel().select(((Hyperlink)e.getSource()).getText());
    }

    @FXML
    private void showWorklistMenuIssued(ActionEvent e) {
        if (!worklist.isShowing()) worklist.show();
    }

    @FXML
    private void newIndexIssued() throws Exception {
        String name=newNameTextField.getText();
        if (!Index.getIndices(storer).contains(name)){
            ArrayList<Parameter> parameters = new ArrayList<>();
            parameters.add(new Parameter("Feature Collection to use","org.grire.BagOfVisualWords.Structures.PoolFeatures","PoolFeatures"));
            parameters.add(new Parameter("VisualWordDescriptor to use","org.grire.BagOfVisualWords.Interfaces.VisualWordDescriptor","VisualWordDescriptor"));
            parameters.add(new Parameter("Enable multi-threaded descriptor creation",Boolean.class.getName(),Boolean.class.getSimpleName()));
            TreeView<Parameter> treeView = showParamatersConfigurationWindow("Indexing Configuration", "Index Factory", parameters);
            if (!canceled) {
                VisualWordDescriptor descriptor = (VisualWordDescriptor) createObjectReccursion(treeView.getRoot().getChildren().get(1));
                PoolFeatures cf = (PoolFeatures)createObjectReccursion(treeView.getRoot().getChildren().get(0));
                Boolean multithreaded=(Boolean)createObjectReccursion(treeView.getRoot().getChildren().get(2));
                IndexFactory ifac=new IndexFactory(cf,descriptor,new Index(storer,newTextField,true),multithreaded);
                worklistController.addTask(ifac);
            }
        }else
            Dialog.showError("Error!","Index already exists");
    }

    @FXML
    private void newCodebookIssued() throws Exception {
        String name=newNameTextField.getText();
        if (!Codebook.getCodebooks(storer).contains(name)) {
            ArrayList<Parameter> parameters = new ArrayList<>();
            parameters.add(new Parameter("Feature Collection to use","org.grire.BagOfVisualWords.Structures.PoolFeatures","PoolFeatures"));
            parameters.add(new Parameter("Classifier to use","org.grire.GeneralUtilities.Interfaces.ClusteringAlgorithm","ClusteringAlgorithm"));
            parameters.add(new Parameter("Training subset of data %",Float.class.getName(),Float.class.getSimpleName(),"30"));
            TreeView<Parameter> treeView = showParamatersConfigurationWindow("Codebook Configuration", "Clustering Codebook Factory", parameters);
            if (!canceled) {
                ClusteringAlgorithm cluster = (ClusteringAlgorithm) createObjectReccursion(treeView.getRoot().getChildren().get(1));
                PoolFeatures cf = (PoolFeatures)createObjectReccursion(treeView.getRoot().getChildren().get(0));
                ClusteringCodebookFactory fac=new ClusteringCodebookFactory(cf,cluster,new Codebook(storer,this.newTextField,true));
                fac.setPercentageOfData(((Float)createObjectReccursion(treeView.getRoot().getChildren().get(2))).floatValue()/100f);
                worklistController.addTask(fac);
            }
        } else
            Dialog.showError("Error!","Codebook already exists");
    }

    @FXML
    private void newFeaturesIssued() throws Exception {
        String name=newNameTextField.getText();
        if (!PoolFeatures.getFeatures(storer).contains(name)) {
            ArrayList<Parameter> parameters = new ArrayList<>();
            parameters.add(new Parameter("Collection to parse from","org.grire.BagOfVisualWords.Structures.ImagePool","ImagePool"));
            parameters.add(new Parameter("FeatureExtractor to use","org.grire.GeneralUtilities.Interfaces.FeatureExtractor","FeatureExtractor"));
            parameters.add(new Parameter("Enable multi-threaded extraction",Boolean.class.getName(),Boolean.class.getSimpleName()));
            TreeView<Parameter> treeView = showParamatersConfigurationWindow("Parsing Configuration", "Collection FeatureExtractor", parameters);
            if (!canceled) {
                FeatureExtractor parser=(FeatureExtractor)createObjectReccursion(treeView.getRoot().getChildren().get(1));
                ImagePool ic = (ImagePool)createObjectReccursion(treeView.getRoot().getChildren().get(0));
                Boolean multithreaded=(Boolean)createObjectReccursion(treeView.getRoot().getChildren().get(2));
                PoolFeatureExtractor cp=new PoolFeatureExtractor(ic,parser,new PoolFeatures(storer,this.newTextField,true),multithreaded);
                worklistController.addTask(cp);
            }
        }else
            Dialog.showError("Error!","Features already exist");
    }

    private Object createObjectReccursion(TreeItem<Parameter> curr) throws Exception {
        Parameter param = curr.getValue();
        Class cls=Class.forName(param.getInterface());
        Class cfloat=Float.class;
        Class cstring=String.class;
        Class cbool=Boolean.class;
        Class dataComp=Structure.class;
        Class generalData=GeneralData.class;
        Class cplugin=GRirePlugin.class;
        String paramValue = param.getValue();
        if (cls.equals(cfloat)) {
            return new Float(Float.parseFloat(paramValue));
        } else if (cls.equals(cstring)) {
            return new String(paramValue);
        } else if (cls.equals(cbool)) {
            return new Boolean(paramValue);
        } else if (dataComp.isAssignableFrom(cls)){
            switch(cls.getSimpleName()) {
                case "ImagePool":
                    return new ImagePool(storer, paramValue);
                case "PoolFeatures":
                    return new PoolFeatures(storer, paramValue);
                case "Codebook":
                    return new Codebook(storer, paramValue);
                case "Index":
                    return new Index(storer, paramValue);
            }
        }else if (generalData.isAssignableFrom(cls)){
            switch (cls.getSimpleName()) {
                case "Histogram":
                    return (Histogram.getHistograms(storer).contains(paramValue)?new Histogram<>(storer,paramValue):new Histogram<>(storer,paramValue,true));
                case "GeneralMap":
                    return (GeneralMap.getGeneralMaps(storer).contains(paramValue)?new GeneralMap<>(storer,paramValue):new GeneralMap<>(storer,paramValue,true));
            }
        } else if (cplugin.isAssignableFrom(cls)) {
            if (curr.getChildren().isEmpty()) {
                return getPluginClassOfName(curr.getValue().getValue()).newInstance();
            } else {
                Object[] params=new Object[curr.getChildren().size()];
                Class[] cparams=new Class[params.length];
                int i=0;
                for (TreeItem<Parameter> p : curr.getChildren()) {
                    cparams[i]=Class.forName(p.getValue().getInterface());
                    params[i++]=createObjectReccursion(p);
                }
                return getPluginClassOfName(curr.getValue().getValue()).getConstructor(cparams).newInstance(params);
            }
        }
        throw new Exception("Class: "+ paramValue +" of type : "+param.getInterface()+" not recognized!" );
    }

    private TreeView showParamatersConfigurationWindow(String titleString,String rootString,ArrayList<Parameter> params) {
        TreeView<Parameter> tree=new TreeView<>();
        TreeItem<Parameter> root=new TreeItem(new Parameter(rootString,"",""));
        root.setExpanded(true);
        tree.setRoot(root);
        tree.setEditable(true);
        for (Parameter p:params) root.getChildren().add(new TreeItem(p));
        tree.setCellFactory(new Callback<TreeView<Parameter>, TreeCell<Parameter>>() {
            @Override
            public TreeCell<Parameter> call(TreeView<Parameter> parameterTreeView) {
                return new ParameterCell();
            }
        });
        Dialog.showTree(titleString,"The action you requested requires you to provide the necessary parameters. Please configure them in the following tree\n\n",tree,new CancelHandler());
        return tree;
    }

    private Class getPluginClassOfName(String name) throws Exception {
        for (PluginWrapper pw:plugins) {
            if (pw.getPlugin().getClass().getName().equals(name)) return pw.getPlugin().getClass();
        }
        throw new Exception ("Missing plugin of name: "+name);
    }

    private List<GRirePlugin> getPluginsOfInterface(Class cls) {
        List<GRirePlugin> ret=new ArrayList<>();
        for (PluginWrapper pw:plugins) {
            if (pw.getPlugin().getComponentInterface().equals(cls)) ret.add(pw.getPlugin());
        }
        return ret;
    }

    @FXML
    private void newCollectionIssued() {
        String name=newNameTextField.getText();
        if (!ImagePool.getPools(storer).contains(name))
            try {
                ImagePool ic=new ImagePool(storer,newTextField,true);
                CheckContents();
                collectionsListView.getSelectionModel().select(name);
                CollectionTitledPane.setExpanded(true);
            } catch (Exception e) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
            }
        else
            Dialog.showError("Error!","Collection already exists");
    }

    private void SetUpIssued(GRirePlugin plugin) {
        TreeView treeView = showParamatersConfigurationWindow("SetUp Configuration", "The setup requires the following parameters", (new PluginWrapper(plugin)).getSetUpParameters());
        if (!canceled) {
            ObservableList<TreeItem> children = treeView.getRoot().getChildren();
            Object[] args=new Object[children.size()];
            try {
                for (int i=0;i<args.length;i++) {
                        args[i]=createObjectReccursion(children.get(i));
                }
                worklistController.addTask(plugin.setUp(args));
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    @FXML
    private void pluginsMenuIssued(ActionEvent a) {
        if (plugins!=null) {
            TableView PluginsTable=new TableView();
            Callback<TableColumn<PluginWrapper, GRirePlugin>, TableCell<PluginWrapper, GRirePlugin>> buttonCellFactory =
                    new Callback<TableColumn<PluginWrapper, GRirePlugin>, TableCell<PluginWrapper, GRirePlugin>>() {

                        @Override
                        public TableCell call(final TableColumn param) {
                            final TableCell cell = new TableCell() {

                                @Override
                                public void updateItem(final Object item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty) {
                                        setText(null);
                                        setGraphic(null);
                                    } else {
                                        final Button btnPrint = new Button("Set Up");
                                        btnPrint.setOnAction(new EventHandler<ActionEvent>() {

                                            @Override
                                            public void handle(ActionEvent event) {
                                                SetUpIssued((GRirePlugin)item);
                                            }
                                        });
                                        btnPrint.setDisable(!((GRirePlugin)item).requiresSetUp());
                                        setGraphic(btnPrint);
                                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                                    }
                                }
                            };
                            return cell;
                       }
                    };

            TableColumn<PluginWrapper,String> nameCol = new TableColumn<>();
            nameCol.setText("Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<PluginWrapper, String>("name"));

            TableColumn<PluginWrapper,String> typeCol = new TableColumn<>();
            typeCol.setText("Interface");
            typeCol.setCellValueFactory(new PropertyValueFactory<PluginWrapper, String>("interface"));

            TableColumn<PluginWrapper,String> paramsCol = new TableColumn<>();
            paramsCol.setText("Parameters");
            paramsCol.setCellValueFactory(new PropertyValueFactory<PluginWrapper, String>("parameters"));

            TableColumn<PluginWrapper,GRirePlugin> setUpCol = new TableColumn<>();
            setUpCol.setText("SetUp");
            setUpCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PluginWrapper, GRirePlugin>, ObservableValue<GRirePlugin>>() {
                @Override
                public ObservableValue<GRirePlugin> call(final TableColumn.CellDataFeatures<PluginWrapper, GRirePlugin> pluginWrapperPluginCellDataFeatures) {
                    return new ObservableValue<GRirePlugin>() {
                        @Override
                        public void addListener(ChangeListener<? super GRirePlugin> changeListener) {
                        }

                        @Override
                        public void removeListener(ChangeListener<? super GRirePlugin> changeListener) {
                        }

                        @Override
                        public GRirePlugin getValue() {
                            return pluginWrapperPluginCellDataFeatures.getValue().getPlugin();
                        }

                        @Override
                        public void addListener(InvalidationListener invalidationListener) {
                        }

                        @Override
                        public void removeListener(InvalidationListener invalidationListener) {
                        }
                    };
                }
            });
            setUpCol.setCellFactory(buttonCellFactory);
            PluginsTable.getColumns().addAll(nameCol,typeCol,paramsCol,setUpCol);
            ObservableList<PluginWrapper> wrappers = FXCollections.observableArrayList(plugins);
            PluginsTable.setItems(wrappers);
            Dialog.showTableNoWait("Plugins", "Below there is a table with every detected plugin and its parameters.                                       \n\n", PluginsTable, new CancelHandler());
        }
    }

    @FXML
    private void ConfigureSimilarity(ActionEvent e) {
        ArrayList<Parameter> params=new ArrayList<>();
        params.add(new Parameter("SimilarityMeasure",SimilarityMeasure.class.getName(),SimilarityMeasure.class.getSimpleName()));
        ObservableList<TreeItem> children = showParamatersConfigurationWindow("SimilarityMeasure Configuration", "QueryPerformer", params).getRoot().getChildren();
        SimilarityMeasure similarity;
        try {
            similarity = (SimilarityMeasure) createObjectReccursion(children.get(0));
            performer=new QueryPerformer(system,performer.getScheme(),similarity);
            queryConfigSimilarityLabel.setText(similarity.toString());
        } catch (Exception e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @FXML
     private void ConfigureModel(ActionEvent e) {
        ArrayList<Parameter> params=new ArrayList<>();
        params.add(new Parameter("Weighting Scheme",WeightingScheme.class.getName(),WeightingScheme.class.getSimpleName()));
        ObservableList<TreeItem> children = showParamatersConfigurationWindow("WeightingScheme Configuration", "QueryPerformer", params).getRoot().getChildren();
        WeightingScheme model;
        try {
            model = (WeightingScheme) createObjectReccursion(children.get(0));
            performer=new QueryPerformer(system,model,performer.getSimilarity());
            queryConfigWeightingLabel.setText(model.toString());
        } catch (Exception e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private boolean ConfigureQuery(ActionEvent a) {
        queryConfigurationPane.setVisible(true);
        ArrayList<Parameter> params=new ArrayList<>();
        params.add(new Parameter("Weighting Scheme", WeightingScheme.class.getName(), WeightingScheme.class.getSimpleName()));
        params.add(new Parameter("SimilarityMeasure", SimilarityMeasure.class.getName(), WeightingScheme.class.getSimpleName()));
        ObservableList<TreeItem> children = showParamatersConfigurationWindow("Quering Configuration", "QueryPerformer", params).getRoot().getChildren();

        try{
            WeightingScheme model=(WeightingScheme)createObjectReccursion(children.get(0));
            SimilarityMeasure similarity=(SimilarityMeasure)createObjectReccursion(children.get(1));

            performer=new QueryPerformer(system,model,similarity);

            queryConfigWeightingLabel.setText(model.toString());
            queryConfigSimilarityLabel.setText(similarity.toString());
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    @FXML
    private boolean ConfigureSystem(ActionEvent a) {
        systemConfigurationPane.setVisible(true);
        ArrayList<Parameter> params=new ArrayList<>();
        if (system==null) {
            params.add(new Parameter("Image Collection",ImagePool.class.getName(),ImagePool.class.getSimpleName()));
            params.add(new Parameter("VisualWordDescriptor",VisualWordDescriptor.class.getName(),VisualWordDescriptor.class.getSimpleName()));
            params.add(new Parameter("Index",Index.class.getName(),Index.class.getSimpleName()));

        }else {
            params.add(new Parameter("Image Collection",ImagePool.class.getName(),ImagePool.class.getSimpleName(),system.getCollection().getName()));
            params.add(new Parameter("VisualWordDescriptor",VisualWordDescriptor.class.getName(),VisualWordDescriptor.class.getSimpleName()));
            params.add(new Parameter("Index",Index.class.getName(),Index.class.getSimpleName(),system.getIndex().getName()));
        }
        ObservableList<TreeItem> children = showParamatersConfigurationWindow("Search Configuration", "BOVWSystem", params).getRoot().getChildren();

        if (!canceled) {

            ImagePool ic;
            VisualWordDescriptor desc;
            Index in;

            try {
                ic = (ImagePool) createObjectReccursion(children.get(0));
                desc = (VisualWordDescriptor) createObjectReccursion(children.get(1));
                in = (Index) createObjectReccursion(children.get(2));

                if (!in.getCreatedFrom().contains(desc.toString())) {
                    Dialog.showConfirmation("Missmatch Detected!","The index you selected does not seem to be created with this descriptor. Are you sure you want to continue?",new CancelHandler());
                    if (canceled) return false;
                }
                system=new BOVWSystem(ic,desc,in);

                systemImageCollectionHyperlink.setText(ic.getName());
                systemDescriptorLabel.setText(desc.toString());
                systemIndexHyperlink.setText(in.getName());

                if (performer!=null) performer=new QueryPerformer(system,performer.getScheme(),performer.getSimilarity());

            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return false;
            }
            return true;
        }
        return false;
    }



    @FXML
    private void searchButtonToggled(ActionEvent a) {
        HideAllPanes();
        if (((ToggleButton)a.getSource()).isSelected()) {
            if (system==null) {
                if (!ConfigureSystem(null) || !ConfigureQuery(null)) return;
            }
            systemConfigurationPane.setVisible(true);
            queryConfigurationPane.setVisible(true);
            searchPane.setVisible(true);
        }

    }

    @FXML
    private void histogramDeleteIssued(ActionEvent e) {
        try {
            Dialog.showConfirmation("Confirmation needed","Are you sure you want to delete this item?",new CancelHandler());
            if (!canceled) {
                histogram.Delete();
                CheckContents();
                histogramPane.setVisible(false);
            }
        } catch (Exception a) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, a);
        }

    }

    @FXML
    private void indexGotoIssued(ActionEvent e) {
        String id = ((TextField) e.getSource()).getText();
        try {
            indexIdListView.getSelectionModel().select(id);
        } catch (Exception e1) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e1);
        }

    }

    @FXML
    private void indexDeleteIssued(ActionEvent a) {
        try {
            Dialog.showConfirmation("Confirmation needed","Are you sure you want to delete this item?",new CancelHandler());
            if (!canceled) {
                index.delete();
                CheckContents();
                indexPane.setVisible(false);
            }
        } catch (Exception e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    @FXML
    private void indexDeleteIdIssued(ActionEvent a) {
        try {
            Dialog.showConfirmation("Confirmation needed","Are you sure you want to delete this item?",new CancelHandler());
            if (!canceled) {
                index.deleteId(Long.parseLong(indexIdListView.getSelectionModel().getSelectedItem().toString()));
                indicesListView.getSelectionModel().select(indicesListView.getSelectionModel().getSelectedIndex());
            }
        } catch (Exception e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @FXML
    private void deleteCodebookIssued(ActionEvent a) {
        Set usedToCreate = codebook.getUsedToCreate();
        ObservableList<ComponentInTable> list = FXCollections.observableArrayList();
        for (Object o:usedToCreate) {
            String[] s=((String)o).split("\\.",2);
            list.add(new ComponentInTable(true,s[0],s[1]));
        }
        TableView table=new TableView();
        TableColumn<ComponentInTable, Boolean> checkedCol=new TableColumn<>();
        checkedCol.setText("delete From");
        checkedCol.setMinWidth(150);
        checkedCol.setCellValueFactory(new PropertyValueFactory<ComponentInTable, Boolean>("checked"));
        checkedCol.setCellFactory(new Callback<TableColumn<ComponentInTable, Boolean>, TableCell<ComponentInTable, Boolean>>() {
            public TableCell<ComponentInTable, Boolean> call(TableColumn<ComponentInTable, Boolean> p) {
                return new CheckBoxTableCell<ComponentInTable, Boolean>();
            }
        });

        TableColumn typeCol = new TableColumn();
        typeCol.setText("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory("type"));

        TableColumn nameCol = new TableColumn();
        nameCol.setText("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));

        table.getColumns().addAll(checkedCol, typeCol, nameCol);
        table.setEditable(true);
        table.setItems(list);
        TableView tableView = Dialog.showTable("Dependencies Detected", "The item you are trying to delete is found to be depended on by other components. Check the components that you want to delete  too.\n\n", table, new CancelHandler());
        try {
            if (!canceled) {
                codebook.delete();
                ObservableList items = tableView.getItems();
                for (Object o:items) {
                    ComponentInTable co=(ComponentInTable)o;
                    if (co.checkedProperty().get())
                        (new Index(storer,co.nameProperty().get())).delete();
                }
                codebookPane.setVisible(false);
                CheckContents();
            }
        }catch (Exception e) {

        }
    }

    @FXML
    private void featuresGotoIssued(ActionEvent a) {
        String id = ((TextField) a.getSource()).getText();
        try {
            featuresIdListView.getSelectionModel().select(id);
        } catch (Exception e1) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e1);
        }

    }

    @FXML
    private void featuresDeleteIssued(ActionEvent a) {
        try {
            TableView table = createTableFromFeaturesDeps(features);
            TableView tableView = Dialog.showTable("Dependencies Detected", "The item you are trying to delete is found to be depended on by other components. Check the components that you want to delete  too.\n\n", table, new CancelHandler());
            if (!canceled) {
                features.delete();
                ObservableList items = tableView.getItems();
                for (Object o:items) {
                    ComponentInTable co=(ComponentInTable)o;
                    if (co.checkedProperty().get())
                        (new Index(storer,co.nameProperty().get())).delete();
                }
                featuresPane.setVisible(false);
                CheckContents();
            }
        }catch (Exception e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @FXML
    private void featuresDeleteIdIssued(ActionEvent a) {
        String selectedItem = featuresIdListView.getSelectionModel().getSelectedItem();
        long id=Long.parseLong(selectedItem.split(":",2)[0]);
        try {
            TableView table = createTableFromFeaturesDeps(features);
            TableView tableView = Dialog.showTable("Dependencies Detected", "The item you are trying to delete is found to be depended on by other components. Check the components that you want to delete too. (Only the Id Selected)\n\n", table, new CancelHandler());
            if (!canceled) {
                features.deleteId(id);
                ObservableList items = tableView.getItems();
                for (Object o:items) {
                    ComponentInTable co=(ComponentInTable)o;
                    if (co.checkedProperty().get())
                        (new Index(storer,co.nameProperty().get())).deleteId(id);
                }
                featuresListView.getSelectionModel().select(featuresListView.getSelectionModel().getSelectedIndex());
            }
        }catch (Exception e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private TableView createTableFromFeaturesDeps( PoolFeatures features) {
        Set usedToCreate = features.getUsedToCreate();
        ObservableList<ComponentInTable> list = FXCollections.observableArrayList();
        for (Object o:usedToCreate) {
            String[] s=((String)o).split("\\.",2);
            if (!s[0].equals("codebook"))
                list.add(new ComponentInTable(true,s[0],s[1]));
        }
        TableView table=new TableView();
        TableColumn<ComponentInTable, Boolean> checkedCol=new TableColumn<>();
        checkedCol.setText("delete From");
        checkedCol.setMinWidth(150);
        checkedCol.setCellValueFactory(new PropertyValueFactory<ComponentInTable, Boolean>("checked"));
        checkedCol.setCellFactory(new Callback<TableColumn<ComponentInTable, Boolean>, TableCell<ComponentInTable, Boolean>>() {
            public TableCell<ComponentInTable, Boolean> call(TableColumn<ComponentInTable, Boolean> p) {
                return new CheckBoxTableCell<ComponentInTable, Boolean>();
            }
        });

        TableColumn typeCol = new TableColumn();
        typeCol.setText("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory("type"));

        TableColumn nameCol = new TableColumn();
        nameCol.setText("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));

        table.getColumns().addAll(checkedCol, typeCol, nameCol);
        table.setEditable(true);
        table.setItems(list);
        return table;
    }

    @FXML
    private void collectionGotoIssued(ActionEvent e) {
        String id = ((TextField) e.getSource()).getText();
        try {
            String image = collection.getImage(Long.parseLong(id));
            collectionNamesListView.getSelectionModel().select(id+":"+image);
        } catch (Exception e1) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e1);
        }
    }

    @FXML
    private void collectionDeleteIssued(ActionEvent e) {
        try {
            TableView table = createTableFromCollectionDeps(collection);
            TableView tableView = Dialog.showTable("Dependencies Detected", "The item you are trying to delete is found to be depended on by other components. Check the components that you want to delete too.\n\n", table, new CancelHandler());
            if (!canceled) {
                collection.delete();
                ObservableList items = tableView.getItems();
                for (Object o:items) {
                    ComponentInTable co=(ComponentInTable)o;
                    if (co.checkedProperty().get())
                        switch (co.typeProperty().get()) {
                            case "features":
                                (new PoolFeatures(storer,co.nameProperty().get())).delete();
                                break;
                            case "index":
                                (new Index(storer,co.nameProperty().get())).delete();
                                break;
                        }
                }
                CheckContents();
                collectionPane.setVisible(false);
            }
        } catch (Exception e1) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e1);
        }
        collectionPane.setVisible(false);
    }

    @FXML
    private void collectionDeleteImageIssued(ActionEvent e) {
        String selectedItem = collectionNamesListView.getSelectionModel().getSelectedItem();
        long id=Long.parseLong(selectedItem.split(":",2)[0]);
        try {
            TableView table = createTableFromCollectionDeps(collection);
            TableView tableView = Dialog.showTable("Dependencies Detected", "The item you are trying to delete is found to be depended on by other components. Check the components that you want to delete too. (Only the id selected)\n\n", table, new CancelHandler());
            if (!canceled) {
                collection.deleteImage(id);
                ObservableList items = tableView.getItems();
                for (Object o:items) {
                    ComponentInTable co=(ComponentInTable)o;
                    if (co.checkedProperty().get())
                        switch (co.typeProperty().get()) {
                            case "features":
                                (new PoolFeatures(storer,co.nameProperty().get())).deleteId(id);
                                break;
                            case "index":
                                (new Index(storer,co.nameProperty().get())).deleteId(id);
                                break;
                        }
                }
                collectionsListView.getSelectionModel().select(collectionsListView.getSelectionModel().getSelectedIndex());
            }
        } catch (Exception e1) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e1);
        }
    }

    private TableView createTableFromCollectionDeps(ImagePool collection) throws Exception{
        Set usedToCreate = collection.getUsedToCreate();
        ObservableList<ComponentInTable> list = FXCollections.observableArrayList();
        PoolFeatures features;
        for (Object o:usedToCreate)           {
            String[] s = ((String)o).split("\\.",2);
            list.add(new ComponentInTable(true,s[0],s[1]));
            features=new PoolFeatures(storer,s[1]);
            Set usedToCreate1 = features.getUsedToCreate();
            for (Object o1: usedToCreate1) {
                String[] s1=((String)o1).split("\\.",2);
                if (!s1[0].equals("codebook")) {
                    list.add(new ComponentInTable(true,s1[0],s1[1]));
                }
            }
        }
        TableView table=new TableView();
        TableColumn<ComponentInTable, Boolean> checkedCol=new TableColumn<>();
        checkedCol.setText("delete From");
        checkedCol.setMinWidth(150);
        checkedCol.setCellValueFactory(new PropertyValueFactory<ComponentInTable, Boolean>("checked"));
        checkedCol.setCellFactory(new Callback<TableColumn<ComponentInTable, Boolean>, TableCell<ComponentInTable, Boolean>>() {
            public TableCell<ComponentInTable, Boolean> call(TableColumn<ComponentInTable, Boolean> p) {
                return new CheckBoxTableCell<ComponentInTable, Boolean>();
            }
        });

        TableColumn typeCol = new TableColumn();
        typeCol.setText("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory("type"));

        TableColumn nameCol = new TableColumn();
        nameCol.setText("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));

        table.getColumns().addAll(checkedCol, typeCol, nameCol);
        table.setEditable(true);
        table.setItems(list);
        return table;
    }

    @FXML
    private void collectionAddFolderIssued(ActionEvent e) {
        DirectoryChooser dc=new DirectoryChooser();
        final File file = dc.showDialog(null);
        imageCollectionIndicator.setVisible(true);
        if (file!=null) {
            Task task=new Task() {
                @Override
                protected Object call() throws Exception {
                    Blacklist[] bs=new Blacklist[0];
                    if(System.getProperty("os.name").startsWith("Windows")){
                        bs=new Blacklist[]{new ExactFileBlacklist("Thumbs.db")};
                    }
                    Importer im=new Importer(new String[] {file.getAbsolutePath()},collection,bs);
                    im.run();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            collectionsListView.getSelectionModel().select(collectionsListView.getSelectionModel().getSelectedIndex());
                        }
                    });
                    return null;
                }
            };
            new Thread(task).start();
        }
    }

    @FXML
    private void collectionAddImageIssued(ActionEvent e) {
        FileChooser fc=new FileChooser();
        File file = fc.showOpenDialog(null);
        if (file!=null) {
            try {
                collection.addImage(file.getAbsolutePath());
                collectionsListView.getSelectionModel().select(collectionsListView.getSelectionModel().getSelectedIndex());
            } catch (Exception e1) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e1);
            }
        }
    }

    @FXML
    private void newDatabaseMenuIssued(ActionEvent a){
        FileChooser fc=new FileChooser();
        File db=fc.showSaveDialog(null);
        if (db!=null) {
            storer=new GeneralMapDBStorer(db.getAbsolutePath());
            CheckContents();
        }
    }

    @FXML
    private void openDatabaseMenuIssued(ActionEvent a) {
        FileChooser fc=new FileChooser();
        File db=fc.showOpenDialog(null);
        if (db!=null) {
            storer=new GeneralMapDBStorer(db.getAbsolutePath());
            CheckContents();
        }
    }

    public void CheckContents() {
        collectionsListView.getSelectionModel().clearSelection();
        featuresListView.getSelectionModel().clearSelection();
        codebooksListView.getSelectionModel().clearSelection();
        indicesListView.getSelectionModel().clearSelection();
        generalDataTreeView.getSelectionModel().clearSelection();

        collectionsListViewStrings=FXCollections.observableArrayList();
        featuresListViewStrings=FXCollections.observableArrayList();
        codebooksListViewStrings=FXCollections.observableArrayList();
        indicesListViewStrings=FXCollections.observableArrayList();
        TreeItem root = new TreeItem("General Data");
        root.setExpanded(true);
        generalDataTreeView.setRoot(root);
        TreeItem histograms = new TreeItem("Histograms");
        TreeItem generalmaps = new TreeItem("General Maps");
        histograms.setExpanded(true);
        generalDataTreeView.getRoot().getChildren().add(histograms);
        generalDataTreeView.getRoot().getChildren().add(generalmaps);

        collectionsListView.setItems(collectionsListViewStrings);
        featuresListView.setItems(featuresListViewStrings);
        codebooksListView.setItems(codebooksListViewStrings);
        indicesListView.setItems(indicesListViewStrings);

        for (Object o: ImagePool.getPools(storer))
            collectionsListViewStrings.add((String)o);
        for (Object o: PoolFeatures.getFeatures(storer))
            featuresListViewStrings.add((String)o);
        for (Object o: Codebook.getCodebooks(storer))
            codebooksListViewStrings.add((String)o);
        for (Object o: Index.getIndices(storer))
            indicesListViewStrings.add((String)o);
        for (Object o: Histogram.getHistograms(storer))
            histograms.getChildren().add(new TreeItem(o));
        for (Object o: GeneralMap.getGeneralMaps(storer))
            generalmaps.getChildren().add(new TreeItem(o));

        imageCollectionIndicator.setVisible(false);
        featuresIndicator.setVisible(false);
        codebookIndicator.setVisible(false);
        indexIndicator.setVisible(false);
    }
    
    @FXML
    private void nameTextFieldTypedIn(ActionEvent a){
        String src=((TextField)a.getSource()).getText();
        newTextField=src;
        if (!((TextField)a.getSource()).getText().isEmpty() && storer!=null) {
            if (!collectionsListViewStrings.contains(src)) newCollectionButton.setDisable(false);
            else  newCollectionButton.setDisable(true);
            if (!featuresListViewStrings.contains(src)) newFeaturesButton.setDisable(false);
            else newFeaturesButton.setDisable(true);
            if (!codebooksListViewStrings.contains(src)) newCodebookButton.setDisable(false);
            else newCodebookButton.setDisable(true);
            if (!indicesListViewStrings.contains(src)) newIndexButton.setDisable(false);
            else newIndexButton.setDisable(true);
        }else{
            newCollectionButton.setDisable(true);
            newFeaturesButton.setDisable(true);
            newCodebookButton.setDisable(true);
            newIndexButton.setDisable(true);
        }
    }
    
    @FXML
    private void closedMenuIssued(ActionEvent a) {
        if (storer!=null) storer.close();
        System.exit(0);
    }

    private void HideAllPanes() {
        collectionPane.setVisible(false);
        codebookPane.setVisible(false);
        featuresPane.setVisible(false);
        indexPane.setVisible(false);
        histogramPane.setVisible(false);
        generalMapsPane.setVisible(false);
        searchPane.setVisible(false);
        systemConfigurationPane.setVisible(false);
        queryConfigurationPane.setVisible(false);
    }

    @FXML
    private void about(ActionEvent actionEvent) {
        try {
            Parent root=FXMLLoader.load(getClass().getResource("About.fxml"));
            Scene scene=new Scene(root);
            Stage window=new Stage();
            window.setScene(scene);
            window.show();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        collectionsListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                imageCollectionIndicator.setVisible(true);
                HideAllPanes();
                collectionPane.setVisible(true);
                Task task=new Task(){

                    @Override
                    protected Object call() throws Exception {
                        try {
                            if (collectionsListView.getSelectionModel().getSelectedItems().isEmpty()) return null;
                            searchToggleButton.setSelected(false);
                            String selectedItem = collectionsListView.getSelectionModel().getSelectedItem();
                            if (selectedItem != null) {
                                collection = new ImagePool(storer, selectedItem);
                                final ObservableList<String> temp = FXCollections.observableArrayList();
                                for (Object o : collection) {
                                    temp.add(o.toString() + ":" + collection.getImage((Long) o));
                                }
                                collectionImageView.setImage(null);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        collectionNamesListView.setItems(temp);
                                        imageCollectionIndicator.setVisible(false);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
                            e.printStackTrace();
                        }

                        return null;
                    }
                };
                new Thread(task).start();

            }
        });
        featuresListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                HideAllPanes();
                featuresPane.setVisible(true);
                featuresIndicator.setVisible(true);
                Task task=new Task() {
                    @Override
                    protected Object call() throws Exception {
                        try {
                            if (featuresListView.getSelectionModel().getSelectedItems().isEmpty()) return null;
                            searchToggleButton.setSelected(false);
                            features = new PoolFeatures(storer, featuresListView.getSelectionModel().getSelectedItem());
                            final ObservableList<String> temp = FXCollections.observableArrayList();
                            for (Object o : features) temp.add(o.toString());
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    for (Object o : features.getCreatedFrom()) {
                                        String s = (String) o;
                                        if (s.split("\\.")[0].equals("collection"))
                                            featuresExtractedFromCollection.setText(s.split("\\.", 2)[1]);
                                        else
                                            extractorExtractedFromCollection.setText(s);
                                    }
                                    featuresIdListView.setItems(temp);
                                    featuresIndicator.setVisible(false);
                                    if (!temp.isEmpty()) featuresIdListView.getSelectionModel().selectFirst();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };

                new Thread(task).start();
            }
        });
        codebooksListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                HideAllPanes();
                codebookPane.setVisible(true);
                codebookIndicator.setVisible(true);
                Task task=new Task() {
                    @Override
                    protected Object call() throws Exception {
                        try {
                            if (codebooksListView.getSelectionModel().getSelectedItems().isEmpty()) return null;
                            searchToggleButton.setSelected(false);
                            codebook = new Codebook(storer, codebooksListView.getSelectionModel().getSelectedItem().toString());
                            final ObservableList<String> temp = FXCollections.observableArrayList();
                            for (Object o : codebook) {
                                float[] word = codebook.getCodebookWord((Integer) o);
                                String vector = o + ": (";
                                for (float w : word)
                                    vector += w + ",";
                                vector = vector.substring(0, vector.length() - 1);
                                temp.add(vector);
                            }
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    codebookWordsListView.setItems(temp);
                                    for (Object o : codebook.getCreatedFrom())
                                        if (((String) o).split("\\.", 2)[0].equals("features"))
                                            codebookCreatedFromLabel.setText(((String) o).split("\\.", 2)[1]);
                                        else
                                            codebookMethodLabel.setText((String) o);
                                    codebookIndicator.setVisible(false);
                                }
                            });
                        } catch (Exception e) {
                            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
                        }
                        return null;
                    }
                };
                new Thread(task).start();
            }
        });
        indicesListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                HideAllPanes();
                indexPane.setVisible(true);
                indexIndicator.setVisible(true);
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        try {
                            if (indicesListView.getSelectionModel().getSelectedItems().isEmpty()) return null;
                            searchToggleButton.setSelected(false);
                            index = new Index(storer, indicesListView.getSelectionModel().getSelectedItem());
                            final ObservableList<String> temp = FXCollections.observableArrayList();
                            for (Object o : index) {
                                temp.add(o.toString());
                            }
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    indexIdListView.setItems(temp);
                                    if (!temp.isEmpty()) indexIdListView.getSelectionModel().selectFirst();
                                    for (Object o : index.getCreatedFrom())
                                        if (((String) o).split("\\.", 2)[0].equals("features"))
                                            indexCreatedFeaturesLabel.setText(((String) o).split("\\.", 2)[1]);
                                        else
                                            indexDescriptorLabel.setText((String) o);
                                    indexIndicator.setVisible(false);
                                }
                            });
                        } catch (Exception e) {
                            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
                        }
                        return null;
                    }
                };
                new Thread(task).start();
            }
        });
        generalDataTreeView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                HideAllPanes();
                if (generalDataTreeView.getSelectionModel().getSelectedItems().isEmpty()) return;
                TreeItem selected = (TreeItem) change.getList().get(0);
                if (selected.getParent().getValue().equals("Histograms")) {
                    try {
                        histogramPane.setVisible(true);
                        histogram = new Histogram(storer, selected.getValue().toString());
                        ObservableList temp = FXCollections.observableArrayList();
                        for (Object o : histogram) {
                            temp.add(o);
                        }
                        histogramXListView.setItems(temp);
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                } else if (selected.getParent().getValue().equals("General Maps")) {
                    try {
                        generalMapsPane.setVisible(true);
                        generalMap = new GeneralMap(storer, selected.getValue().toString());
                        ObservableList temp = FXCollections.observableArrayList();
                        for (Object o : generalMap) {
                            temp.add(o);
                        }
                        generalMapValueListView.setItems(temp);
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                }
            }
        });
        collectionNamesListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                String whole = collectionNamesListView.getSelectionModel().getSelectedItem();
                String path = whole.split(":", 2)[1];
                collectionImageView.setImage(new Image((new File(path)).toURI().toString()));
                collectionDeleteImageButton.setDisable(false);
            }
        });
        featuresIdListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                featuresIndicator.setVisible(true);
                Task task=new Task() {
                    @Override
                    protected Object call() throws Exception {
                        final ObservableList<String> temp = FXCollections.observableArrayList();
                        Object[] feats = features.getImageFeatures(Long.parseLong(featuresIdListView.getSelectionModel().getSelectedItem().toString()));
                        for (Object f : feats) {
                            String vector = "(";
                            for (float df : (float[]) f) {
                                vector += df + ",";
                            }
                            vector = vector.substring(0, vector.length() - 1) + ")";
                            temp.add(vector);
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                featuresFeaturesListView.setItems(temp);
                                featuresDeleteIdButton.setDisable(false);
                                featuresIndicator.setVisible(false);
                            }
                        });
                        return null;
                    }
                };
                new Thread(task).start();
            }
        });
        indexIdListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                indexIndicator.setVisible(true);
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        final ObservableList<String> temp=FXCollections.observableArrayList();
                        float[] desc=index.getImageDescriptor(Long.parseLong(indexIdListView.getSelectionModel().getSelectedItem()));
                        for (float d:desc){
                            temp.add(Float.toString(d));
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                indexHistogramListView.setItems(temp);
                                indexDeleteIdButton.setDisable(false);
                                indexIndicator.setVisible(false);
                            }
                        });
                        return null;
                    }
                };
                new Thread(task).start();
            }
        });
        histogramXListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change change) {
                Float s = (Float) change.getList().get(0);
                histogramYField.setText(String.valueOf(histogram.getValueAt(s)));
            }
        });
        generalMapValueListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                Object o = change.getList().get(0);
                generalMapValueTextArea.setText(generalMap.get(o).toString());
            }
        });
        queryResultList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                queryImagePreview.setImage(new Image(new File(change.getList().get(0).toString()).toURI().toString()));
            }
        });

        try {
            List<GRirePlugin> plugins1 =PluginLoader.LoadPluginsFromDefaultFile();
            plugins=new ArrayList<>();
            for (GRirePlugin p:plugins1)
                plugins.add(new PluginWrapper(p));
        } catch (FileNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | ClassNotFoundException | InvocationTargetException | MalformedURLException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }

        FXMLLoader loader=new FXMLLoader(getClass().getResource("Worklist.fxml"));
        Parent root = null;
        try {
            root = (Parent) loader.load();
            worklistController=loader.getController();
            Scene scene = new Scene(root);
            worklist=new Stage();
            worklist.setScene(scene);
            worklist.setTitle("GRire - Worklist");
            worklistController.setMainController(this);
            progressBar.progressProperty().bind(worklistController.progressBar.progressProperty());
            progressMessageLabel.textProperty().bind(worklistController.progressLabel.textProperty());
            currentTaskLabel.textProperty().bind(worklistController.nameLabel.textProperty());
        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Platform.setImplicitExit(true);
        currentlyWorkingPane.visibleProperty().bind(worklistController.working);
    }

    public void insertShutdownPrevention() {
        collectionDeleteImageButton.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(final WindowEvent windowEvent) {
                if (worklistController.working.get()) {
                    Dialog.showConfirmation("Close Confirmation","There are tasks still running. Are you sure you want to close GRire?",new CancelHandler() {
                        @Override
                        public void handle(Event event) {
                            super.handle(event);
                            windowEvent.consume();
                        }
                    });
                    System.exit(0);
                    //if (!canceled) worklistController.shutDown();
                }
            }
        });
    }

    class CancelHandler implements EventHandler {

        CancelHandler() {
            canceled=false;
        }

        @Override
        public void handle(Event event) {
            canceled=true;
        }
    }

    public class ComponentInTable {

        private BooleanProperty checked;
        private StringProperty type;
        private StringProperty name;

        ComponentInTable(boolean checked,String type,String name) {
            this.checked= new SimpleBooleanProperty(checked);
            this.name= new SimpleStringProperty(name);
            this.type= new SimpleStringProperty(type);
        }

        public BooleanProperty checkedProperty() { return checked; }
        public StringProperty typeProperty() { return  type; }
        public StringProperty nameProperty() { return  name; }
    }

    public class PluginWrapper {

        public PluginWrapper(GRirePlugin plugin) {
            this.plugin = plugin;
        }

        private GRirePlugin plugin;

        public StringProperty nameProperty() {return new SimpleStringProperty(plugin.getClass().getSimpleName());}
        public StringProperty interfaceProperty() {return new SimpleStringProperty(plugin.getComponentInterface().getSimpleName());}
        public StringProperty parametersProperty() {
            String ret="";
            Class[] types = plugin.getParameterTypes();
            if (types.length>0) {
                for (Class c: types)
                    ret+=c.getSimpleName()+",";
                ret=ret.substring(0,ret.length()-1);
            }
            return new SimpleStringProperty(ret);
        }
        public StringProperty setUpParametersProperty() {
            String ret="";
            Class[] types = plugin.getSetUpParameterTypes();
            if (types.length>0) {
                for (Class c: types)
                    ret+=c.getSimpleName()+",";
                ret=ret.substring(0,ret.length()-1);
            }
            return new SimpleStringProperty(ret);
        }
        public BooleanProperty requiresSetUpProperty() {return new SimpleBooleanProperty(plugin.requiresSetUp());}

        public GRirePlugin getPlugin() {return plugin;}

        public ArrayList<Parameter> getParameters() {
            Class[] parameterTypes = plugin.getParameterTypes();
            String[] parameterNames = plugin.getParameterNames();
            String[] parameterValues=plugin.getDefaultParameterValues();
            ArrayList<Parameter> ret=new ArrayList<>();
            for (int i=0;i<parameterNames.length;i++) {
                ret.add(new Parameter(parameterNames[i],parameterTypes[i].getName(),parameterTypes[i].getSimpleName(),parameterValues[i]));
            }
            return ret;
        }

        public ArrayList<Parameter> getSetUpParameters() {
            Class[] parameterTypes = plugin.getSetUpParameterTypes();
            String[] parameterNames = plugin.getSetUpParameterNames();
            String[] parameterValues=plugin.getDefaultSetUpParameterValues();
            ArrayList<Parameter> ret=new ArrayList<>();
            for (int i=0;i<parameterNames.length;i++) {
                ret.add(new Parameter(parameterNames[i],parameterTypes[i].getName(),parameterTypes[i].getSimpleName(),parameterValues[i]));
            }
            return ret;
        }

        @Override
        public String toString() {
            return nameProperty().getValue();
        }
    }

    public class Parameter {
        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getInterface() {
            return Interface;
        }

        public void setInterface(String anInterface) {
            Interface = anInterface;
        }

        private String Name;
        private String Interface;
        private String SimpleInterface;

        public String getValue() {
            return Value;
        }

        public void setValue(String value) {
            Value = value;
        }

        private String Value="";

        public Parameter(String name,String ifc, String sifc) {
            Name=name;
            Interface=ifc;
            SimpleInterface=sifc;
        }

        public Parameter(String name,String ifc, String sifc,String value) {
            Name=name;
            Interface=ifc;
            SimpleInterface=sifc;
            Value=value;
        }

        @Override
        public String toString() {
            String ret=Name+"("+SimpleInterface+"): ";
            if (!Value.equals("")){
                if (SimpleInterface.equals("Float")) ret+=Value;
                else ret+=Value.substring(Value.lastIndexOf('.')+1);
            }
            return ret;
        }
    }

    final class ParameterCell extends TreeCell<Parameter>  {

        @Override
        public void startEdit() {
            super.startEdit();
            try {
                Class cls=Class.forName(getItem().getInterface());
                Class plugin=GRirePlugin.class;
                Class dataComp=Structure.class;
                if (plugin.isAssignableFrom(cls)) {
                    final ComboBox<GRirePlugin> combo=new ComboBox<>();
                    combo.setItems(FXCollections.observableArrayList(getPluginsOfInterface(cls)));
                    combo.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            GRirePlugin plugin = ((ComboBox<GRirePlugin>) actionEvent.getSource()).getSelectionModel().getSelectedItem();
                            getItem().setValue(plugin.getClass().getName());
                            commitEdit(getItem());
                            setGraphic(null);
                            ArrayList<Parameter> parameters = (new PluginWrapper(plugin)).getParameters();
                            ObservableList<TreeItem<Parameter>> children = getTreeItem().getChildren();
                            children.clear();
                            if (!parameters.isEmpty()) {
                                children = getTreeItem().getChildren();
                                children.clear();
                                for (Parameter p : parameters) {
                                    children.add(new TreeItem<>(p));
                                }
                                getTreeItem().setExpanded(true);
                            }
                        }
                    });
                    setGraphic(combo);
                }else if (dataComp.isAssignableFrom(cls)){
                    ComboBox<String> combo=new ComboBox<>();
                    switch(cls.getSimpleName()) {
                        case "ImagePool":
                            combo.setItems(FXCollections.observableArrayList(ImagePool.getPools(storer)));
                            break;
                        case "PoolFeatures":
                            combo.setItems(FXCollections.observableArrayList(PoolFeatures.getFeatures(storer)));
                            break;
                        case "Codebook":
                            combo.setItems(FXCollections.observableArrayList(Codebook.getCodebooks(storer)));
                            break;
                        case "Index":
                            combo.setItems(FXCollections.observableArrayList(Index.getIndices(storer)));
                            break;
                    }
                    combo.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            getItem().setValue(((ComboBox<String>)actionEvent.getSource()).getSelectionModel().getSelectedItem());
                            commitEdit(getItem());
                            setGraphic(null);
                        }
                    });
                    setGraphic(combo);
                }else if (GeneralData.class.isAssignableFrom(cls)) {
                    ComboBox<String> combo=new ComboBox<>();
                    combo.setEditable(true);
                    combo.setMinWidth(50);
                    ObservableList observableList=null;
                    switch(cls.getSimpleName()){
                        case "Histogram":
                            observableList = FXCollections.observableArrayList(Histogram.getHistograms(storer));
                            break;
                        case "GeneralMap":
                            observableList = FXCollections.observableArrayList(GeneralMap.getGeneralMaps(storer));
                            break;
                    };
                    combo.setItems(observableList);
                    combo.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            String value = ((ComboBox<String>) actionEvent.getSource()).getValue();
                            String selection = ((ComboBox<String>) actionEvent.getSource()).getSelectionModel().getSelectedItem();
                            String selectedItem = (value.isEmpty()?selection:value);
                            getItem().setValue(selectedItem);
                            System.out.println(actionEvent.getEventType());
                            System.out.println(selectedItem);
                            commitEdit(getItem());
                            setGraphic(null);
                        }
                    });

                    setGraphic(combo);
                }else if (cls.equals(Boolean.class)) {
                    ComboBox<String> combo=new ComboBox<>();
                    combo.setItems(FXCollections.observableArrayList("True","False"));
                    combo.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                        getItem().setValue(((ComboBox<String>) actionEvent.getSource()).getSelectionModel().getSelectedItem());
                        commitEdit(getItem());
                        setGraphic(null);
                        }
                    });
                    setGraphic(combo);
                } else {
                    TextField field=new TextField();
                    field.setOnKeyReleased(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent t) {
                            if (t.getCode() == KeyCode.ENTER) {
                                getItem().setValue(((TextField)t.getSource()).getText());
                                commitEdit(getItem());
                                setGraphic(null);
                            } else if (t.getCode() == KeyCode.ESCAPE) {
                                cancelEdit();
                                setGraphic(null);
                            }
                        }
                    });
                    setGraphic(field);
                    field.requestFocus();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void updateItem(Parameter parameter, boolean b) {
            super.updateItem(parameter, b);
            if (!b) {
                setText(parameter.toString());
                if (getTreeView().getRoot().equals(getTreeItem())) { setTextFill(Color.BLACK); return; }
                if (parameter.getValue().equals("")) setTextFill(Color.RED);
                else setTextFill(Color.GREEN);
            }
        }
}
}