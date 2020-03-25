package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

public class GradController {
    public TextField fieldNaziv;
    public TextField fieldBrojStanovnika;
    public ChoiceBox<Drzava> choiceDrzava;
    public ObservableList<Drzava> listDrzave;
    public TextField fieldPostanskiBroj;
    private Grad grad;
    private GeografijaDAO dao;
    public ListView<Znamenitost> listViewZnamenitosti;
    public ObservableList<Znamenitost> listaZnamenitosti;

    public GradController(Grad grad, ArrayList<Drzava> drzave) {
        this.grad = grad;
        listDrzave = FXCollections.observableArrayList(drzave);
        if(grad!=null) listaZnamenitosti= FXCollections.observableArrayList(grad.getZnamenitosti());
        dao=GeografijaDAO.getInstance();
    }

    @FXML
    public void initialize() {
        choiceDrzava.setItems(listDrzave);
        if (grad != null) {
            fieldNaziv.setText(grad.getNaziv());
            fieldBrojStanovnika.setText(Integer.toString(grad.getBrojStanovnika()));
            fieldPostanskiBroj.setText(Integer.toString(grad.getPostanskiBroj()));
            // choiceDrzava.getSelectionModel().select(grad.getDrzava());
            // ovo ne radi jer grad.getDrzava() nije identički jednak objekat kao član listDrzave
            for (Drzava drzava : listDrzave)
                if (drzava.getId() == grad.getDrzava().getId())
                    choiceDrzava.getSelectionModel().select(drzava);
        } else {
            choiceDrzava.getSelectionModel().selectFirst();
        }
        listViewZnamenitosti.setItems(listaZnamenitosti);
    }

    public Grad getGrad() {
        return grad;
    }

    public void clickCancel(ActionEvent actionEvent) {
        grad = null;
        Stage stage = (Stage) fieldNaziv.getScene().getWindow();
        stage.close();
    }

    public void clickOk(ActionEvent actionEvent) {
        boolean sveOk = true;

        if (fieldNaziv.getText().trim().isEmpty()) {
            fieldNaziv.getStyleClass().removeAll("poljeIspravno");
            fieldNaziv.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        } else {
            fieldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
            fieldNaziv.getStyleClass().add("poljeIspravno");
        }


        int brojStanovnika = 0;
        try {
            brojStanovnika = Integer.parseInt(fieldBrojStanovnika.getText());
        } catch (NumberFormatException e) {
            // ...
        }
        if (brojStanovnika <= 0) {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        } else {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeNijeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeIspravno");
        }
        if (fieldPostanskiBroj.getText().isEmpty()) {
            fieldPostanskiBroj.getStyleClass().removeAll("poljeIspravno");
            fieldPostanskiBroj.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        }

        if (!sveOk) return;

        Thread t=new Thread(()->{
            int pb=0;
            pb= Integer.parseInt(fieldPostanskiBroj.getText());
            URL test = null;
            try {
                test = new URL("http://c9.etf.unsa.ba/proba/postanskiBroj.php?postanskiBroj=" + pb);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            InputStreamReader inputStreamReader = null;
            try {
                inputStreamReader = new InputStreamReader(test.openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scanner scanner = new Scanner(inputStreamReader);
            String s = scanner.nextLine();

            if(s.equals("OK")){
                fieldPostanskiBroj.getStyleClass().removeAll("poljeNijeIspravno");
                fieldPostanskiBroj.getStyleClass().add("poljeIspravno");
                if (grad == null) grad = new Grad();
                grad.setNaziv(fieldNaziv.getText());
                grad.setBrojStanovnika(Integer.parseInt(fieldBrojStanovnika.getText()));
                grad.setDrzava(choiceDrzava.getValue());
                grad.setPostanskiBroj(Integer.parseInt(fieldPostanskiBroj.getText()));
                Platform.runLater(()->{
                    Stage stage = (Stage) fieldNaziv.getScene().getWindow();
                    stage.close();
                });
            }
            else {
                fieldPostanskiBroj.getStyleClass().removeAll("poljeIspravno");
                fieldPostanskiBroj.getStyleClass().add("poljeNijeIspravno");
            }
        });
        t.start();
    }
    public void dodajZnamenitost() {
        if (grad == null) return;
        Stage stage = new Stage();
        Parent root;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Prevod");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/znamenitosti.fxml"),bundle);
            ZnamenitostController znamenitostController = new ZnamenitostController(grad);
            loader.setController(znamenitostController);
            root = loader.load();
            stage.setTitle("Znamenitost");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.show();

            stage.setOnHiding(event -> {
                Znamenitost znamenitost = znamenitostController.getZnanemitost();
                if (znamenitost != null) {
                    dao.dodajZnamenitost(znamenitost);
                    grad.getZnamenitosti().add(znamenitost);
                    listaZnamenitosti.setAll(grad.getZnamenitosti());
                    listViewZnamenitosti.refresh();
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
