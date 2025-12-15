package main;

import XMLIO.XMLAnalyser;
import metaModel.Model;
import visitor.JavaVisitor;

public class Main {
    public static void main(String[] args){
        XMLAnalyser analyser = new XMLAnalyser();

        Model model = analyser.getModelFromFilenamed("src/main/resources/Exemple3.xml");

        if (model != null) {
            JavaVisitor javaVisitor = new JavaVisitor();
            model.accept(javaVisitor);
            System.out.println(javaVisitor.getResult());
        } else {
            System.out.println("Erreur: impossible de charger le modèle");
        }
    }
}
