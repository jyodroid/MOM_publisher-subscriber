package mom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import mom.AdCliente;

public class GuiCliente {

	private static AdCliente ac = null;
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) {

		int opcion = 0;
		System.out.println("Ingrese su número de cédula");
		String cedula;
		try {
			cedula = br.readLine();
			ac = new AdCliente(cedula);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		GuiCliente guic = new GuiCliente();
		do{
			guic.menu();
			try {
				opcion = Integer.valueOf(br.readLine());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}while(guic.seleccion(opcion));
	}

	public void menu(){
		System.out.println("\nPrueva de aplicaciones utilizando MOM (Messagge Oriented Middleware)\n" +
				"Seleccione una opción de las siguente:\n" +
				"1. Suscribirse a un canal de mensajes.\n" +
				"2. Cancelar suscripción.\n" +
				"3. Traer lista de canales.\n"+
				"4. Recibir mensajes pendientes por recibir.\n" +
				"5. Recibir mensajes pendientes por recibir y quedarse recibiendo mensajes.\n" +
				"6. Salir\n");
	}

	public boolean seleccion(int opcion){

		String topico;

		switch (opcion) {

		case 1:
			System.out.println("Ingrese el nombre del canal que desea susbcribirse");
			try {
				topico = br.readLine();
				ac.subcribirse(topico);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case 2:
			System.out.println("Ingrese el nombre del canal que va cancelar subscripcion");
			try {
				topico = br.readLine();
				ac.dejarTopico(topico+"_"+ac.getCedula());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case 3:
			try {
				ac.traerTopicos();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case 4:
			System.out.println("Ingrese el nombre del canal del que desea recibir los mensajes");
			try {
				topico = br.readLine();
				ac.modoRecibir(topico, 1);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case 5:
			System.out.println("Ingrese el nombre del canal del que desea recibir los mensajes");
			try {
				topico = br.readLine();
				ac.modoRecibir(topico, 0);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case 6:
			System.out.println("Fin del programa");
			return false;

		default:
			System.out.println("Opción no disponible, intente otra");
			break;
		}

		return true;
	}
}
