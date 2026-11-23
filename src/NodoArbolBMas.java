import java.util.ArrayList;
import java.util.List;

 // Nodo de un árbol B+.
 // Nodo interno: solo contiene claves (separadores) e hijos. datos y siguiente son null.
 // Nodo hoja: contiene las claves y los datos asociados (en la misma posición), no tiene
 // hijos y se enlaza con la hoja siguiente para permitir recorridos secuenciales.

public class NodoArbolBMas {

    // Atributos
    private boolean esHoja;
    private List<Integer> claves;
    private List<NodoArbolBMas> hijos;   // Vacía si es una hoja
    private List<String> datos;          // null si es un nodo interno; datos.get(i) corresponde a claves.get(i)
    private NodoArbolBMas siguiente;     // null si es un nodo interno (o la última hoja)

    // Métodos
    // Constructor: las hojas inicializan su lista de datos; los nodos internos la dejan en null
    public NodoArbolBMas(boolean esHoja) {
        this.esHoja = esHoja;
        this.claves = new ArrayList<>();
        this.hijos = new ArrayList<>();
        this.datos = esHoja ? new ArrayList<>() : null;
        this.siguiente = null;
    }

    // Getters
    public boolean esHoja() {
        return esHoja;
    }

    public List<Integer> getClaves() {
        return claves;
    }

    public List<NodoArbolBMas> getHijos() {
        return hijos;
    }

    public List<String> getDatos() {
        return datos;
    }

    public NodoArbolBMas getSiguiente() {
        return siguiente;
    }

    // Setters
    public void setClaves(List<Integer> claves) {
        this.claves = claves;
    }

    public void setHijos(List<NodoArbolBMas> hijos) {
        this.hijos = hijos;
    }

    public void setDatos(List<String> datos) {
        this.datos = datos;
    }

    public void setSiguiente(NodoArbolBMas siguiente) {
        this.siguiente = siguiente;
    }
}

