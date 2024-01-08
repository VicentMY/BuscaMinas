package buscaminasPorConsola;

/*
 * Autor: Vicent Martínez Yerves
 * Version: 3
 * Última modificación: 07/01/2024
 * */

import java.util.Random;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Scanner;

public class Buscaminas {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		//Variables internas
		String[][] tableroJugador;
		String[][] tableroInterno;

		String dificultad 	= "";
		boolean dev 		= false;
		int opcion			= 0;
		int nMinas			= 0;
		int ancho			= 0;
		int alto			= 0;
		int movimiento		= 0;
		int nMovimientos	= 0;

		//Variables de juego
		String fila;
		int columna;
		LocalTime horaInicio;
		long tiempoDeJuego;


		do {
			System.out.printf("\033[36mElige un modo de juego:\n");
			System.out.printf("\033[33m1.- Fácil (10 minas)\n");
			System.out.printf("2.- Intermedio (40 minas)\n");
			System.out.printf("3.- Difícil (99 minas)\n");
			System.out.printf("4.- Personalizado (X minas)\n");
			System.out.printf("7.- Mostrar las instrucciones de buscaminas\n");
			System.out.printf("8.- Modo desarrollador\n");
			System.out.printf("9.- Salir\n\033[37m");
			opcion = sc.nextInt();

			switch (opcion) {
			case 1:
				dificultad = "FACIL";
				nMinas = 10;
				break;
			case 2:
				dificultad = "MEDIA";
				nMinas = 40;
				break;
			case 3:
				dificultad = "DIFICIL";
				nMinas = 99;
				break;
			case 4:
				dificultad = "PERSONALIZADO";
				System.out.printf("\033[33mEscribe el alto, ancho y número de minas del campo de minas (en ese orden):\n\n\033[37m");
				alto	= sc.nextInt() + 1;
				ancho	= sc.nextInt() + 1;
				nMinas	= sc.nextInt();
				break;
			case 7:
				System.out.printf("\033[32mEl juego consiste en despejar todas las casillas de una pantalla que no oculten una mina.\n");
				System.out.printf("Algunas casillas tienen un número, este número indica las minas que hay en todas las casillas circundantes.\n");
				System.out.printf("Así, si una casilla tiene el número 3, significa que de las ocho casillas que hay alrededor (si no es en una esquina o borde) hay 3 con minas y 5 sin minas.\n");
				System.out.printf("Si se descubre una casilla sin número indica que ninguna de las casillas vecinas tiene mina y estas se descubren automáticamente.\n");
				System.out.printf("Si se descubre una casilla con una mina se pierde la partida.\n");
				System.out.printf("Se puede poner una marca en las casillas que el jugador piensa que hay minas para ayudar a descubrir la que están cerca.\n\n");
				break;
			case 8:
				if (dev) {
					dev = false;
					System.out.printf("\033[31mModo desarrollador desactivado.\n\n");
				}
				else {
					dev = true;
					System.out.printf("\033[32mModo desarrollador activado.\n\n");
				}
				break;
			case 9:
				System.out.printf("\033[36m¡Hasta pronto!\n\n");
				break;
			default:
				System.out.printf("\033[33mPor favor, vuelve a intentarlo e introduce una opción válida.\n\n");
				opcion = 8;
				break;
			}

			if (opcion != 9 && opcion != 8 && opcion != 7) {

				if (dificultad.equals("PERSONALIZADO")) {

					tableroJugador = crearTablero(dificultad, ancho, alto);
					tableroInterno = crearTablero(dificultad, ancho, alto);
				}
				else {

					tableroJugador = crearTablero(dificultad);
					tableroInterno = crearTablero(dificultad);
				}

				limpiarCampo(tableroInterno);
				limpiarCampo(tableroJugador);

				oscurecerCampoJugador(tableroJugador);
				imprimirTablero(tableroJugador);

				nMovimientos = 0;

				do {

					horaInicio = LocalTime.now();

					System.out.println("\033[33mIntroduce la fila y columna de la casilla: (con el formato 'fila columna' PE: 'D 1')\033[37m");

					fila = sc.next().toUpperCase();
					columna = sc.nextInt();

					if (nMovimientos < 1) {

						introducirMinas(tableroInterno, nMinas, descifrarNumeroFila(fila), columna);

						calcularPistas(tableroInterno);
					}


					System.out.printf("\033[36mElige un tipo de movimiento:\n");
					System.out.printf("\033[33m1.- Descubrir\n");
					System.out.printf("2.- Marcar / Colocar bandera\n");
					System.out.printf("9.- Terminar la partida\n\033[37m");
					movimiento = sc.nextInt();

					switch (movimiento) {
					case 1:
						tableroJugador[descifrarNumeroFila(fila)][columna] = tableroInterno[descifrarNumeroFila(fila)][columna];

						if (tableroJugador[descifrarNumeroFila(fila)][columna].equals(" ")) {
							descubrirAdyacentesEspacios(tableroInterno, tableroJugador, descifrarNumeroFila(fila), columna);
						}

						if (tableroJugador[descifrarNumeroFila(fila)][columna].equals("*")) {

							imprimirTablero(tableroInterno);

							System.out.printf("\033[31mPartida terminada por descubrir una casilla con mina.\n\n");
							movimiento = 9;
						}
						break;
					case 2:
						tableroJugador[descifrarNumeroFila(fila)][columna] = "¶";
						break;
					case 9:
						System.out.printf("\033[35m\nPartida terminada por el usuario.\n\n");
						break;
					}

					if (movimiento != 9 && hayCasillaSinMina(tableroInterno, tableroJugador)) {

						nMovimientos ++;

						imprimirTablero(tableroJugador);

						if (dev) {

							System.out.println();
							imprimirTablero(tableroInterno);
						}
					}

				} while (movimiento != 9 && hayCasillaSinMina(tableroInterno, tableroJugador));

				tiempoDeJuego = Duration.between(horaInicio, LocalTime.now()).toSeconds();

				imprimirTablero(tableroInterno);

				if (!hayCasillaSinMina(tableroInterno, tableroJugador)) {

					System.out.printf("\033[32m\n¡Enhorabuena, has ganado!\n");
				}

				System.out.printf("\033[35m\nLa partida ha durado %d segundos y has hecho %d movimientos.\n\n", tiempoDeJuego, nMovimientos);

			}

		} while (opcion != 9);

		sc.close();
	}

	public static void imprimirTablero(String[][] tablero) {

		for (int i = 0; i < tablero.length; i++) {

			for (int j = 0; j < tablero[i].length; j++) {

				if (i == 0 || j == 0) {

					System.out.printf("\033[36m%3s\033[37m",tablero[i][j]);
				}
				else {
					System.out.printf("%3s",tablero[i][j]);
				}
			}
			System.out.println();
		}
	}

	public static String[][] crearTablero(String dificultad) {

		if (dificultad.equals("FACIL")) {

			return new String[10][10];
		}
		else if (dificultad.equals("MEDIA")) {

			return new String[17][17];
		} 
		else if (dificultad.equals("DIFICIL")) {

			return new String[17][31];
		}
		else {
			// TABLERO PREDETERMINADO
			return new String[30][30];
		}
	}

	public static String[][] crearTablero(String dificultad, int ancho, int alto) {

		return new String[alto][ancho];
	}

	public static void limpiarCampo(String[][] tablero) {

		int alto  		= tablero.length;
		int ancho 		= tablero[0].length;
		int contador 	= 1;
		int acumulador 	= 1;
		String letras 	= "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		for (int i = 0; i < alto; i++) {

			for (int j = 0; j < ancho; j++) {

				if (i == 0) {						//NUMEROS

					tablero[i][j] = Integer.toString(j);

					if (tablero[i][j].equals("0")) {

						tablero[i][j] = " ";
					}
				}
				else if (j == 0 && i > 0) {			//LETRAS

					if (i <= 26) {

						tablero[i][j] = Character.toString((char)letras.charAt(i -1));
					}
					else {

						if (contador > 26) {

							acumulador ++;
							contador = 1;
						}

						tablero[i][j] = Character.toString((char)letras.charAt(contador -1)) + acumulador;
						contador ++;
					}
				}
				else {

					tablero[i][j] = " ";
				}
			}
		}
	}

	public static int descifrarNumeroFila(String fila) {

		String letras 	= "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int posicion 	= 0;
		int i 			= 0;
		char letra;
		char numero;

		do {

			letra = letras.charAt(i);

			if (letra == fila.charAt(0)) {

				posicion = i + 1;

				if (fila.length() > 1) {

					numero = fila.charAt(1);

					posicion += (Character.getNumericValue(numero) * 26);
				}
			}

			i++;

		} while (letra != fila.charAt(0));

		return posicion;
	}

	public static void oscurecerCampoJugador(String[][] tablero) {

		for (int i = 0; i < tablero.length; i++) {

			for (int j = 0; j < tablero[i].length; j++) {

				if (i != 0 && j != 0) {

					tablero[i][j] = "■";
				}
			}
		}
	}

	public static void introducirMinas(String[][] tablero, int nMinas, int fila, int columna) {

		Random random = new Random();

		int minasPuestas = 0;
		int x;
		int y;

		while (minasPuestas < nMinas) {

			y = random.nextInt(tablero.length);
			x = random.nextInt(tablero[0].length);

			if ((x != 0 && y != 0) && (y != fila && x != columna)) {

				if (tablero[y][x] != "*") {

					tablero[y][x] = "*";
					minasPuestas ++;
				}
			}

		}
	}

	public static int hayMina(String[][] tablero, int y, int x) {

		int alto = tablero.length;
		int ancho = tablero[0].length;

		if (y >= 1 && y < alto && x >= 1 && x < ancho && tablero[y][x].equals("*")) {

			return 1;
		}
		else {
			return 0;
		}
	}

	public static int minasCerca(String[][] tablero, int y, int x) {

		int minas = 0;

		minas += hayMina(tablero, y - 1, x - 1);	//NO
		minas += hayMina(tablero, y - 1, x); 		// N
		minas += hayMina(tablero, y - 1, x + 1); 	// NE
		minas += hayMina(tablero, y, x - 1); 		// O
		minas += hayMina(tablero, y, x + 1); 		// E
		minas += hayMina(tablero, y + 1, x - 1); 	// SO
		minas += hayMina(tablero, y + 1, x); 		// S
		minas += hayMina(tablero, y + 1, x + 1); 	// SE

		if (minas > 0) {

			return minas;
		}
		else {

			return 0;
		}
	}

	public static void calcularPistas(String[][] tablero) {

		for (int i = 0; i < tablero.length; i++) {

			for (int j = 0; j < tablero[i].length; j++) {

				if (i != 0 && j != 0) {

					if (tablero[i][j] != "*") {

						if (minasCerca(tablero, i, j) == 0) {

							tablero[i][j] = " ";
						}
						else {

							tablero[i][j] = Integer.toString(minasCerca(tablero, i, j));
						}
					}
				}
			}
		}

	}

	public static void descubrirAdyacentesEspacios(String[][] tableroInterno, String[][] tableroJugador, int fila, int columna) {

		int alto = tableroInterno.length;
		int ancho = tableroInterno.length;

		if (fila >= 1 && fila < (alto - 1) && columna >= 1 && columna < (ancho - 1) && tableroInterno[fila][columna].equals(" ")) {


			// NO
			tableroJugador[fila - 1][columna - 1] = tableroInterno[fila - 1][columna - 1];

			// N
			tableroJugador[fila - 1][columna] = tableroInterno[fila - 1][columna];

			// NE
			tableroJugador[fila - 1][columna + 1] = tableroInterno[fila - 1][columna + 1];

			// O
			tableroJugador[fila][columna - 1] = tableroInterno[fila][columna - 1];

			// E
			tableroJugador[fila][columna + 1] = tableroInterno[fila][columna + 1];

			// SO
			tableroJugador[fila + 1][columna - 1] = tableroInterno[fila + 1][columna - 1];

			// S
			tableroJugador[fila + 1][columna] = tableroInterno[fila + 1][columna];

			// SE
			tableroJugador[fila + 1][columna + 1] = tableroInterno[fila + 1][columna + 1];
		}
	}

	public static boolean hayCasillaSinMina(String[][] tableroInterno, String[][] tableroJugador) {


		for (int i = 0; i < tableroJugador.length; i++) {

			for (int j = 0; j < tableroJugador[0].length; j++) {

				if (tableroJugador[i][j].equals("■") && !tableroInterno[i][j].equals("*")) {

					return true;
				}
			}
		}

		return false;
	}

}
