package com.example.blocnote;




public class Model {

    private String textDoc;
    private String titleDoc;


    public String getTitleDoc() {
        return titleDoc;
    }
    public String getTextDoc() {
        return textDoc;
    }
    public void setTextDoc(String textD) {
        this.textDoc = textD;
    }
    public void setTitleDoc(String titleD) {
        this.titleDoc = titleD;
    }
    public Model( String title, String texte) {
        setTitleDoc( title );
        setTextDoc( texte );
    }



}
