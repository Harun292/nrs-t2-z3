package ba.unsa.etf.rpr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PretragaController implements Initializable {
    public TextField fieldUzorak;
    public ListView<String> listViewPretraga;
    ObservableList<String> listaPutanja;
    ArrayList<String> putanje;
    private String putanja;
    private Thread thread;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        thread=new Thread();
        putanje=new ArrayList<>();
        listaPutanja=FXCollections.observableArrayList();
        listViewPretraga.setItems(listaPutanja);
    }

    public void traziAction(){
        putanje.clear();
        listaPutanja.clear();
        thread= new Thread(()-> pretrazi(fieldUzorak.getText(), new File(System.getProperty("user.home"))));
        thread.start();
    }
    private void pretrazi(String uzorak, File file){
        File[] files= file.listFiles();
        if(files!=null){
            for(File f: files){
                if(f.isDirectory()){
                    pretrazi(uzorak,f);
                }
                else {
                    if(f.getAbsolutePath().toLowerCase().contains(uzorak)){
                        Platform.runLater(()->listaPutanja.add(f.getAbsolutePath()));
                    }
                }
            }
        }
    }
    public String getPutanja(){
        return putanja;
    }
    public void izaberiFajlAction(){
        thread.interrupt();
        putanja=listViewPretraga.getSelectionModel().getSelectedItem();
        Stage s=(Stage) listViewPretraga.getScene().getWindow();
        s.close();
    }

}
