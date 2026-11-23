import java.util.Scanner;

public class Main {

    private static final Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("================================");
        System.out.println("        ARBOL B+         ");
        System.out.println("================================");

        // Se solicita el orden del árbol (m): cada nodo tiene entre m/2 y m hijos
        int orden = leerEntero("Ingrese el orden del árbol (mínimo 3, recomendado 4): ", 3, 100);
        ArbolBMas arbol = new ArbolBMas(orden);
        System.out.println("Árbol B+ de orden " + orden + " creado.");

        menu(arbol);
        System.out.println("¡Hasta luego!");
    }

    // Menú principal: se repite hasta que el usuario elige salir
    private static void menu(ArbolBMas arbol) {
        int opcion;
        do {
            System.out.println();
            System.out.println("-------------- MENU --------------");
            System.out.println(" 1. Insertar un dato");
            System.out.println(" 2. Eliminar una clave");
            System.out.println(" 3. Buscar una clave");
            System.out.println(" 4. Recorrer un rango (n elementos desde una clave)");
            System.out.println(" 5. Mostrar el árbol");
            System.out.println(" 6. Cargar datos de ejemplo");
            System.out.println(" 0. Salir");
            System.out.println("----------------------------------");
            opcion = leerEntero("Seleccione una opción: ", 0, 6);

            switch (opcion) {
                case 1 -> opcionInsertar(arbol);
                case 2 -> opcionEliminar(arbol);
                case 3 -> opcionBuscar(arbol);
                case 4 -> opcionRecorrerRango(arbol);
                case 5 -> opcionMostrar(arbol);
                case 6 -> opcionCargarEjemplo(arbol);
                default -> { }
            }
        } while (opcion != 0);
    }

    private static void opcionInsertar(ArbolBMas arbol) {
        int clave = leerEntero("Clave (número entero): ", Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.print("Dato asociado (texto): ");
        String dato = entrada.nextLine().trim();
        if (dato.isEmpty()) {
            dato = "dato-" + clave;
        }
        boolean existia = arbol.buscar(clave);
        arbol.insertar(clave, dato);
        System.out.println(existia
                ? "La clave " + clave + " ya existía; se actualizó su dato."
                : "Clave " + clave + " insertada correctamente.");
        arbol.imprimirArbol();
    }

    private static void opcionEliminar(ArbolBMas arbol) {
        if (arbol.estaVacio()) {
            System.out.println("El árbol está vacío.");
            return;
        }
        int clave = leerEntero("Clave a eliminar: ", Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (arbol.eliminar(clave)) {
            System.out.println("Clave " + clave + " eliminada. El árbol se rebalanceó si fue necesario.");
            arbol.imprimirArbol();
        } else {
            System.out.println("La clave " + clave + " no existe en el árbol.");
        }
    }

    private static void opcionBuscar(ArbolBMas arbol) {
        int clave = leerEntero("Clave a buscar: ", Integer.MIN_VALUE, Integer.MAX_VALUE);
        String dato = arbol.buscarDato(clave);
        if (dato != null) {
            System.out.println("Encontrada: [" + clave + "] -> " + dato);
        } else {
            System.out.println("La clave " + clave + " no existe en el árbol.");
        }
    }

    private static void opcionRecorrerRango(ArbolBMas arbol) {
        if (arbol.estaVacio()) {
            System.out.println("El árbol está vacío.");
            return;
        }
        int clave = leerEntero("Clave inicial: ", Integer.MIN_VALUE, Integer.MAX_VALUE);
        int n = leerEntero("Cantidad de elementos a mostrar (n): ", 1, Integer.MAX_VALUE);
        System.out.println("Elementos a partir de la clave " + clave + ":");
        int impresos = arbol.recorrerRango(clave, n);
        if (impresos == 0) {
            System.out.println("  (no hay elementos con clave mayor o igual a " + clave + ")");
        } else if (impresos < n) {
            System.out.println("  (se llegó al final del árbol: solo había " + impresos + " elementos)");
        }
    }

    private static void opcionMostrar(ArbolBMas arbol) {
        if (arbol.estaVacio()) {
            System.out.println("El árbol está vacío.");
        } else {
            arbol.imprimirArbol();
        }
    }

    private static void opcionCargarEjemplo(ArbolBMas arbol) {
        int[] claves = {10, 20, 5, 6, 15, 30, 25, 40, 45, 50};
        for (int clave : claves) {
            arbol.insertar(clave, "Registro " + clave);
        }
        System.out.println("Se insertaron " + claves.length + " claves de ejemplo.");
        arbol.imprimirArbol();
    }

    // Lee un entero validando formato y rango; repite la pregunta hasta recibir un valor válido
    private static int leerEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            System.out.print(mensaje);
            if (!entrada.hasNextLine()) {
                // Fin de la entrada
                System.out.println("\nFin de la entrada. ¡Hasta luego!");
                System.exit(0);
            }
            String linea = entrada.nextLine().trim();
            try {
                int valor = Integer.parseInt(linea);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                System.out.println("Valor fuera de rango. Intente de nuevo.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Debe ingresar un número entero.");
            }
        }
    }
}

