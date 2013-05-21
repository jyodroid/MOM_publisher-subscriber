package mom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnection;


public class GuiFuente {

	private static AdFuente af = null;
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) {

		String url;
		if(args.length==0){
			url = ActiveMQConnection.DEFAULT_BROKER_URL;
		}else{
			url = args[0];
		}
		try {
			af = new AdFuente(url);
		} catch (JMSException e) {	
			System.out.println("Error de jms :"+e.getMessage());
		}
		int opcion = 0;
		GuiFuente guif = new GuiFuente();
		do{
			guif.menu();
			try {
				opcion = Integer.valueOf(br.readLine());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}while(guif.seleccion(opcion));
	}

	public void menu(){
		System.out.println("\nPrueva de aplicaciones utilizando MOM (Messagge Oriented Middleware)\n" +
				"Seleccione una opción de las siguente:\n" +
				"1. Crear un canal para publicar mensajes.\n" +
				"2. Publicar mensajes por un canal existente.\n" +
				"3. Traer lista de canales.\n"+
				"4. Publicar un mensaje por un tiempo determinado.\n" +
				"5. Eliminar un canal.\n" +
				"6. Salir\n");
	}

	public boolean seleccion(int opcion){
		
		String topico;
		String mensaje;
		int tiempo;
		
		switch (opcion) {
		
		case 1:
			System.out.println("Ingrese el nombre del canal");
			try {
				topico = br.readLine();
				af.crearTopico(topico);
				System.out.println("topico "+topico+" creado con exito");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case 2:
			System.out.println("Ingrese el nombre del canal donde va a publicar el mensaje");
			try {
				topico = br.readLine();
				System.out.println("Ingrese el mensaje");
				mensaje = br.readLine();
				af.publicar(mensaje, topico, 0);
				System.out.println("mensaje: "+mensaje+" pubicado con exito en: "+topico);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case 3:
			try {
				af.traerTopicos();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case 4:
			System.out.println("Ingrese el nombre del canal donde va a publicar el mensaje");
			try {
				topico = br.readLine();
				System.out.println("Ingrese el mensaje");
				mensaje = br.readLine();
				System.out.println("Ingrese el tiempo en minutos que estará disponible el mensaje");
				tiempo = Integer.valueOf(br.readLine());
				af.publicar(mensaje, topico, tiempo);
				System.out.println("mensaje: "+mensaje+"pubicado con exito en: "+topico+"por: "+tiempo+" minutos");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case 5:
			System.out.println("Opcion no implementada, intente otra");
			break;
		case 6:
			System.out.println("Fin del programa");
			try {
				af.cerrarSession();
			} catch (JMSException e) {
				System.out.println(e.getMessage());
			}
			return false;
			
		default:
			System.out.println("Opción no disponible, intente otra");
			break;
		}

		return true;
	}
}
