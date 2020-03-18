package ba.unsa.etf.rpr;

import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javafx.scene.control.TextField;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
    public void odaberiSliku(){
        TextInputDialog izbor= new TextInputDialog();
        izbor.setTitle("Izaberite sliku");
        putanja=izbor.showAndWait().get();
        System.out.println(putanja);
        try {
            imageViewZnamenitosti.setImage(new Image(new FileInputStream(putanja)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
