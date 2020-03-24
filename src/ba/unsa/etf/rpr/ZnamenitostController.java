package ba.unsa.etf.rpr;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javafx.scene.control.TextField;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;

public class ZnamenitostController {
    public TextField fieldNazivZnamenitosti;
    public ImageView imageViewZnamenitosti;
    private Znamenitost znamenitost;
    private Grad grad;
    private String putanja;

    public ZnamenitostController (Grad g){
        grad=g;
        putanja=" ";
    }


    public Znamenitost getZnanemitost() {
        return znamenitost;
    }

    public void spasiIzmjene(){
        if(znamenitost==null){
            znamenitost=new Znamenitost();
        }
        znamenitost.setGrad(grad);
        znamenitost.setNaziv(fieldNazivZnamenitosti.getText());
        znamenitost.setPutanja(putanja);
        Stage s= (Stage) fieldNazivZnamenitosti.getScene().getWindow();
        s.close();
    }
    public void odaberiSliku() throws IOException {
        Stage stage = new Stage();
        Parent root;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Prevod");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pretraga.fxml"),bundle);
            PretragaController pretragaController = new PretragaController();
            loader.setController(pretragaController);
            root = loader.load();
            stage.setTitle("Traži");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.show();

            stage.setOnHiding(event -> {
                putanja = pretragaController.getPutanja();
                if(putanja!=null){
                    try {
                        imageViewZnamenitosti.setImage(new Image(new FileInputStream(putanja)));
                    } catch (FileNotFoundException e) {
                        //..
                    }
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
