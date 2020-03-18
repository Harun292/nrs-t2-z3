package ba.unsa.etf.rpr;

public class Znamenitost {
    private int id;
    private String putanja;
    private String naziv;
    private Grad grad;

    public Znamenitost(int id, String naziv, String putanja, Grad grad) {
        this.id = id;
        this.putanja = putanja;
        this.naziv = naziv;
        this.grad = grad;
    }

    public Znamenitost() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPutanja() {
        return putanja;
    }

    public void setPutanja(String putanja) {
        this.putanja = putanja;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Grad getGrad() {
        return grad;
    }

    public void setGrad(Grad grad) {
        this.grad = grad;
    }

    @Override
    public String toString() {
        return this.naziv;
    }
}
