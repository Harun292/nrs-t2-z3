package ba.unsa.etf.rpr;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class GlavnaController {

    public TableView<Grad> tableViewGradovi;
    public TableColumn colGradId;
    public TableColumn colGradNaziv;
    public TableColumn colGradStanovnika;
    public TableColumn<Grad,String> colGradDrzava;
    private GeografijaDAO dao;
    private ObservableList<Grad> listGradovi;

    public GlavnaController() {
        dao = GeografijaDAO.getInstance();
        listGradovi = FXCollections.observableArrayList(dao.gradovi());
    }

    @FXML
    public void initialize() {
        tableViewGradovi.setItems(listGradovi);
        colGradId.setCellValueFactory(new PropertyValueFactory("id"));
        colGradNaziv.setCellValueFactory(new PropertyValueFactory("naziv"));
        colGradStanovnika.setCellValueFactory(new PropertyValueFactory("brojStanovnika"));
        colGradDrzava.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDrzava().getNaziv()));
    }

    public void actionDodajGrad(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Prevod");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"),bundle);
            GradController gradController = new GradController(null, dao.drzave());
            loader.setController(gradController);
            root = loader.load();
            stage.setTitle("Grad");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(true);
            stage.show();

            stage.setOnHiding( event -> {
                Grad grad = gradController.getGrad();
                if (grad != null) {
                    dao.dodajGrad(grad);
                    listGradovi.setAll(dao.gradovi());
                }
            } );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void actionDodajDrzavu(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Prevod");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/drzava.fxml"),bundle);
            DrzavaController drzavaController = new DrzavaController(null, dao.gradovi());
            loader.setController(drzavaController);
            root = loader.load();
            stage.setTitle("Država");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(true);
            stage.show();

            stage.setOnHiding( event -> {
                Drzava drzava = drzavaController.getDrzava();
                if (drzava != null) {
                    dao.dodajDrzavu(drzava);
                    listGradovi.setAll(dao.gradovi());
                }
            } );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionIzmijeniGrad(ActionEvent actionEvent) {
        Grad grad = tableViewGradovi.getSelectionModel().getSelectedItem();
        if (grad == null) return;

        Stage stage = new Stage();
        Parent root = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Prevod");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"),bundle);
            GradController gradController = new GradController(grad, dao.drzave());
            loader.setController(gradController);
            root = loader.load();
            stage.setTitle("Grad");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(true);
            stage.show();

            stage.setOnHiding( event -> {
                Grad noviGrad = gradController.getGrad();
                if (noviGrad != null) {
                    dao.izmijeniGrad(noviGrad);
                    listGradovi.setAll(dao.gradovi());
                }
            } );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionObrisiGrad(ActionEvent actionEvent) {
        Grad grad = tableViewGradovi.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (grad == null) return;
        if(Locale.getDefault().equals(new Locale("en","us"))){
            alert.setTitle("Deleting confirmation");
            alert.setHeaderText("Deleting city "+grad.getNaziv());
            alert.setContentText("You are about to delete city " +grad.getNaziv()+". Are you sure?");
            alert.setResizable(true);
        }
        else{
            alert.setTitle("Potvrda brisanja");
            alert.setHeaderText("Brisanje grada "+grad.getNaziv());
            alert.setContentText("Da li ste sigurni da želite obrisati grad " +grad.getNaziv()+"?");
            alert.setResizable(true);
        }
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            dao.obrisiGrad(grad);
            listGradovi.setAll(dao.gradovi());
        }
    }

    // Metoda za potrebe testova, vraća bazu u polazno stanje
    public void resetujBazu() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();
        dao = GeografijaDAO.getInstance();
    }

    public void actionStampa(){
        try {
            new GradoviReport().showReport(dao.getConn());
        } catch (JRException e) {
            e.printStackTrace();
        }

    }
    public void actionJezik() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType buttonTypeOne;
        ButtonType buttonTypeTwo;
        if(Locale.getDefault().equals(new Locale("en","US"))){
            alert.setTitle("Language");
            alert.setHeaderText("Language selection");
            alert.setContentText("Choose your option.");

            buttonTypeOne = new ButtonType("Bosnian");
            buttonTypeTwo = new ButtonType("English");

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
        }
        else {
            alert.setTitle("Jezik");
            alert.setHeaderText("Odabir jezika");
            alert.setContentText("Odaberite jezik");

            buttonTypeOne = new ButtonType("Bosanski");
            buttonTypeTwo = new ButtonType("Engleski");

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            Locale.setDefault(new Locale("bs", "BA"));
            Stage s=(Stage)tableViewGradovi.getScene().getWindow();
            s.close();
            Main m=new Main();
            try {
                m.start(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (result.get() == buttonTypeTwo) {
            Locale.setDefault(new Locale("en", "US"));
            Stage s=(Stage)tableViewGradovi.getScene().getWindow();
            s.close();
            Main m=new Main();
            try {
                m.start(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
