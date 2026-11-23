import java.util.ArrayList;

/**
 * Árbol B+ de orden m.
 * - Cada nodo puede tener como máximo m hijos (m - 1 claves).
 * - Todo nodo interno (excepto la raíz) tiene al menos ceil(m/2) hijos; la raíz tiene al menos 2.
 * - Todos los datos se guardan en las hojas, que están enlazadas entre sí.
 * - Las operaciones de inserción y eliminación mantienen el árbol balanceado automáticamente.
 */
public class ArbolBMas {

    // Atributos
    private final int orden;
    private NodoArbolBMas raiz;

    // Métodos
    // Constructor
    public ArbolBMas(int orden) {
        if (orden < 3) {
            throw new IllegalArgumentException("El orden del árbol debe ser al menos 3.");
        }
        this.orden = orden;
        raiz = new NodoArbolBMas(true);
    }

    // Cantidad mínima de claves que debe tener una hoja (salvo que sea la raíz)
    private int minClavesHoja() {
        return orden / 2;
    }

    // Cantidad mínima de claves que debe tener un nodo interno (salvo la raíz): ceil(m/2) hijos -> ceil(m/2) - 1 claves
    private int minClavesInterno() {
        return (orden + 1) / 2 - 1;
    }

    // Indica si el nodo tiene menos claves de las permitidas
    private boolean tieneDeficit(NodoArbolBMas nodo) {
        int minimo = nodo.esHoja() ? minClavesHoja() : minClavesInterno();
        return nodo.getClaves().size() < minimo;
    }

    // Indica si el nodo puede prestar una clave a un hermano sin quedar en déficit
    private boolean puedePrestar(NodoArbolBMas nodo) {
        int minimo = nodo.esHoja() ? minClavesHoja() : minClavesInterno();
        return nodo.getClaves().size() > minimo;
    }

    // Retorna el índice del hijo por el que se debe descender para encontrar la clave dada
    // (las claves iguales al separador están en el hijo derecho)
    private int indiceHijo(NodoArbolBMas nodo, int clave) {
        int i = 0;
        while (i < nodo.getClaves().size() && clave >= nodo.getClaves().get(i)) {
            i++;
        }
        return i;
    }

    // Inserción de una clave (con su dato) en el árbol B+. Si la clave ya existe, se actualiza el dato.
    public void insertar(int clave, String dato) {
        // Se inserta primero de forma recursiva
        insertarRecursivo(this.raiz, clave, dato);

        // Se verifica si la raíz se desbordó
        if (this.raiz.getClaves().size() == orden) {
            NodoArbolBMas nuevaRaiz = new NodoArbolBMas(false);
            nuevaRaiz.getHijos().add(this.raiz);
            dividirHijo(nuevaRaiz, 0);
            this.raiz = nuevaRaiz;
        }
    }

    // Inserción recursiva de una clave
    // Permite que los nodos se desborden temporalmente y los divide al regresar de la recursión
    private void insertarRecursivo(NodoArbolBMas nodo, int clave, String dato) {
        if (nodo.esHoja()) {
            int i = nodo.getClaves().size() - 1;
            while (i >= 0 && clave < nodo.getClaves().get(i)) {
                i--;
            }
            if (i >= 0 && nodo.getClaves().get(i) == clave) {
                // La clave ya existe: se reemplaza el dato asociado
                nodo.getDatos().set(i, dato);
            } else {
                nodo.getClaves().add(i + 1, clave);
                nodo.getDatos().add(i + 1, dato);
            }
        } else {
            int i = indiceHijo(nodo, clave);
            NodoArbolBMas hijo = nodo.getHijos().get(i);

            // Llamada recursiva para insertar la clave primero en el hijo correspondiente
            insertarRecursivo(hijo, clave, dato);

            // Evaluación de desbordamiento al retornar de la recursividad
            if (hijo.getClaves().size() == orden) {
                dividirHijo(nodo, i);
            }
        }
    }

    // Dividir un nodo que está lleno
    private void dividirHijo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas nodoLleno = padre.getHijos().get(indice);
        NodoArbolBMas nuevoNodo = new NodoArbolBMas(nodoLleno.esHoja());
        int mitad = orden / 2;

        if (nodoLleno.esHoja()) {
            // En nodos hoja, la llave media se mantiene en el nodo derecho (y se copia al padre)
            int total = nodoLleno.getClaves().size();
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad, total)));
            nuevoNodo.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(mitad, total)));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));
            nodoLleno.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(0, mitad)));

            // Se enlaza la nueva hoja en la lista de hojas
            nuevoNodo.setSiguiente(nodoLleno.getSiguiente());
            nodoLleno.setSiguiente(nuevoNodo);

            // El padre recibe como separador la primera llave del nuevo nodo
            int llaveMedia = nuevoNodo.getClaves().get(0);
            padre.getClaves().add(indice, llaveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        } else {
            // En nodos internos, la llave media sube y se elimina del hijo
            int claveMedia = nodoLleno.getClaves().get(mitad);
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad + 1, nodoLleno.getClaves().size())));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));

            // Mover los hijos del nodo dividido
            nuevoNodo.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(mitad + 1, nodoLleno.getHijos().size())));
            nodoLleno.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(0, mitad + 1)));

            padre.getClaves().add(indice, claveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        }
    }

    // Impresión del árbol como parte de la interfaz pública del árbol
    public void imprimirArbol() {
        imprimirNodo(raiz, "", true);
    }

    // Impresión recursiva privada de los nodos a partir de uno inicial
    private void imprimirNodo(NodoArbolBMas nodo, String indentacion, boolean esUltimo) {
        System.out.println(indentacion + "+- " + (nodo.esHoja() ? "Hoja > " : "Interno > ") + nodo.getClaves());
        indentacion += esUltimo ? "   " : "|  ";
        for (int i = 0; i < nodo.getHijos().size(); i++) {
            imprimirNodo(nodo.getHijos().get(i), indentacion, i == (nodo.getHijos().size() - 1));
        }
    }

    // Retorna la hoja donde debería estar la clave dada
    private NodoArbolBMas buscarHoja(int clave) {
        NodoArbolBMas nodo = raiz;
        while (!nodo.esHoja()) {
            nodo = nodo.getHijos().get(indiceHijo(nodo, clave));
        }
        return nodo;
    }

    // Búsqueda de una clave como parte de la interfaz pública del árbol
    public boolean buscar(int clave) {
        return buscarDato(clave) != null;
    }

    // Retorna el dato asociado a una clave, o null si la clave no existe
    public String buscarDato(int clave) {
        NodoArbolBMas hoja = buscarHoja(clave);
        int pos = hoja.getClaves().indexOf(clave);
        return pos >= 0 ? hoja.getDatos().get(pos) : null;
    }

    // Indica si el árbol no contiene ninguna clave
    public boolean estaVacio() {
        return raiz.esHoja() && raiz.getClaves().isEmpty();
    }

    // Eliminación de una clave. Retorna true si la clave existía y fue eliminada.
    public boolean eliminar(int clave) {
        boolean eliminada = eliminarRecursivo(raiz, clave);

        // Si la raíz interna se quedó sin claves, su único hijo pasa a ser la nueva raíz (el árbol se achica un nivel)
        if (!raiz.esHoja() && raiz.getClaves().isEmpty()) {
            raiz = raiz.getHijos().get(0);
        }
        return eliminada;
    }

    // Eliminación recursiva. Al regresar de la recursión, rebalancea el hijo si quedó en déficit.
    private boolean eliminarRecursivo(NodoArbolBMas nodo, int clave) {
        if (nodo.esHoja()) {
            int pos = nodo.getClaves().indexOf(clave);
            if (pos < 0) {
                return false;
            }
            nodo.getClaves().remove(pos);
            nodo.getDatos().remove(pos);
            return true;
        }

        int i = indiceHijo(nodo, clave);
        NodoArbolBMas hijo = nodo.getHijos().get(i);
        boolean eliminada = eliminarRecursivo(hijo, clave);

        if (eliminada && tieneDeficit(hijo)) {
            rebalancearHijo(nodo, i);
        }
        return eliminada;
    }

    // Corrige el déficit del hijo en la posición indice: primero intenta pedir prestado
    // a un hermano y, si no es posible, fusiona con un hermano.
    private void rebalancearHijo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas hijo = padre.getHijos().get(indice);
        NodoArbolBMas izquierdo = indice > 0 ? padre.getHijos().get(indice - 1) : null;
        NodoArbolBMas derecho = indice < padre.getHijos().size() - 1 ? padre.getHijos().get(indice + 1) : null;

        if (izquierdo != null && puedePrestar(izquierdo)) {
            prestarDeIzquierdo(padre, indice, izquierdo, hijo);
        } else if (derecho != null && puedePrestar(derecho)) {
            prestarDeDerecho(padre, indice, hijo, derecho);
        } else if (izquierdo != null) {
            fusionar(padre, indice - 1);
        } else {
            fusionar(padre, indice);
        }
    }

    // El hijo toma la última clave del hermano izquierdo
    private void prestarDeIzquierdo(NodoArbolBMas padre, int indice, NodoArbolBMas izquierdo, NodoArbolBMas hijo) {
        int ultimo = izquierdo.getClaves().size() - 1;
        if (hijo.esHoja()) {
            // Se mueve la clave y su dato; el separador del padre pasa a ser la nueva primera clave del hijo
            hijo.getClaves().add(0, izquierdo.getClaves().remove(ultimo));
            hijo.getDatos().add(0, izquierdo.getDatos().remove(ultimo));
            padre.getClaves().set(indice - 1, hijo.getClaves().get(0));
        } else {
            // Rotación: el separador baja al hijo y la última clave del hermano sube al padre
            hijo.getClaves().add(0, padre.getClaves().get(indice - 1));
            padre.getClaves().set(indice - 1, izquierdo.getClaves().remove(ultimo));
            hijo.getHijos().add(0, izquierdo.getHijos().remove(izquierdo.getHijos().size() - 1));
        }
    }

    // El hijo toma la primera clave del hermano derecho
    private void prestarDeDerecho(NodoArbolBMas padre, int indice, NodoArbolBMas hijo, NodoArbolBMas derecho) {
        if (hijo.esHoja()) {
            // Se mueve la clave y su dato; el separador del padre pasa a ser la nueva primera clave del hermano
            hijo.getClaves().add(derecho.getClaves().remove(0));
            hijo.getDatos().add(derecho.getDatos().remove(0));
            padre.getClaves().set(indice, derecho.getClaves().get(0));
        } else {
            // Rotación: el separador baja al hijo y la primera clave del hermano sube al padre
            hijo.getClaves().add(padre.getClaves().get(indice));
            padre.getClaves().set(indice, derecho.getClaves().remove(0));
            hijo.getHijos().add(derecho.getHijos().remove(0));
        }
    }

    // Fusiona el hijo en indice con su hermano derecho (indice + 1), eliminando el separador del padre
    private void fusionar(NodoArbolBMas padre, int indice) {
        NodoArbolBMas izquierdo = padre.getHijos().get(indice);
        NodoArbolBMas derecho = padre.getHijos().get(indice + 1);

        if (izquierdo.esHoja()) {
            // En hojas el separador simplemente se descarta; se mantiene el enlace de hojas
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getDatos().addAll(derecho.getDatos());
            izquierdo.setSiguiente(derecho.getSiguiente());
        } else {
            // En nodos internos el separador baja al nodo fusionado
            izquierdo.getClaves().add(padre.getClaves().get(indice));
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getHijos().addAll(derecho.getHijos());
        }
        padre.getClaves().remove(indice);
        padre.getHijos().remove(indice + 1);
    }

    // Recorrido por rango: busca la primera clave mayor o igual a claveInicial e imprime
    // los siguientes n elementos (clave y dato) siguiendo el enlace entre hojas.
    // Retorna la cantidad de elementos impresos.
    public int recorrerRango(int claveInicial, int n) {
        NodoArbolBMas hoja = buscarHoja(claveInicial);
        int impresos = 0;

        // Posición de la primera clave >= claveInicial dentro de la hoja
        int pos = 0;
        while (pos < hoja.getClaves().size() && hoja.getClaves().get(pos) < claveInicial) {
            pos++;
        }

        while (hoja != null && impresos < n) {
            while (pos < hoja.getClaves().size() && impresos < n) {
                impresos++;
                System.out.println("  " + impresos + ". [" + hoja.getClaves().get(pos) + "] -> " + hoja.getDatos().get(pos));
                pos++;
            }
            hoja = hoja.getSiguiente();
            pos = 0;
        }
        return impresos;
    }

    // Verifica las invariantes del árbol (útil para pruebas): balance, límites de ocupación, orden y enlaces de hojas
    public boolean validar() {
        ArrayList<NodoArbolBMas> hojas = new ArrayList<>();
        int altura = validarNodo(raiz, true, Integer.MIN_VALUE, Integer.MAX_VALUE, hojas);
        if (altura < 0) {
            return false;
        }
        for (int i = 0; i < hojas.size(); i++) {
            NodoArbolBMas esperado = i + 1 < hojas.size() ? hojas.get(i + 1) : null;
            if (hojas.get(i).getSiguiente() != esperado) {
                return false;
            }
        }
        return true;
    }

    // Retorna la altura del subárbol o -1 si alguna invariante falla. Las claves deben estar en [min, max).
    private int validarNodo(NodoArbolBMas nodo, boolean esRaiz, int min, int max, ArrayList<NodoArbolBMas> hojas) {
        int n = nodo.getClaves().size();
        for (int i = 0; i < n; i++) {
            int c = nodo.getClaves().get(i);
            if (c < min || (max != Integer.MAX_VALUE && c >= max)) return -1;
            if (i > 0 && c <= nodo.getClaves().get(i - 1)) return -1;
        }
        if (nodo.esHoja()) {
            if (n >= orden || nodo.getDatos().size() != n || !nodo.getHijos().isEmpty()) return -1;
            if (!esRaiz && n < minClavesHoja()) return -1;
            hojas.add(nodo);
            return 0;
        }
        if (nodo.getDatos() != null || nodo.getSiguiente() != null) return -1;
        if (nodo.getHijos().size() != n + 1 || n >= orden) return -1;
        if (esRaiz ? n < 1 : n < minClavesInterno()) return -1;

        int alturaHijos = -2;
        for (int i = 0; i <= n; i++) {
            int lo = i == 0 ? min : nodo.getClaves().get(i - 1);
            int hi = i == n ? max : nodo.getClaves().get(i);
            int h = validarNodo(nodo.getHijos().get(i), false, lo, hi, hojas);
            if (h < 0) return -1;
            if (alturaHijos == -2) alturaHijos = h;
            else if (alturaHijos != h) return -1;
        }
        return alturaHijos + 1;
    }
}

