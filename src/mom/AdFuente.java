package mom;

import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.command.ActiveMQTopic;

public class AdFuente{

	private String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private final int MILAMIN = 60*1000;
	private ConnectionFactory cf = new ActiveMQConnectionFactory(url);
	private int ackMode = Session.CLIENT_ACKNOWLEDGE;
	private boolean transacted = false;
	private Session sesion = null;
	
	public void crearTopico(String nombreTopico) throws JMSException{

		Connection conexion = cf.createConnection();
		conexion.start();

		sesion = conexion.createSession(transacted, ackMode);

		Topic topico = sesion.createTopic(nombreTopico);
		sesion.createProducer(topico);
		
		conexion.close();
	}

	public void publicar(String mensaje, String topicoUsuario, int tExpiracion) throws Exception {

		Connection conexion = cf.createConnection();
		conexion.start();

		sesion = conexion.createSession(transacted, ackMode);

		Topic topico = sesion.createTopic(topicoUsuario);

		//Verificar que el tópico al que quiere agregar mensajes exista para evitar 
		//crear topicos innecesarios.
		if(!verificarCanal(topico, conexion)){
			conexion.close();
			throw new Exception("No existe el canal "+topicoUsuario+" por favor creelo antes");
		}

		MessageProducer producer = sesion.createProducer(topico);

		TextMessage anuncio = sesion.createTextMessage();
		anuncio.setText(mensaje);//"こんにちは");

		//permite darle un tiempo determinado a los mensajes publicados
		producer.setTimeToLive(tExpiracion*MILAMIN);
		anuncio.setJMSExpiration(0);
		producer.send(anuncio);

		producer.close();
		conexion.close();

		System.out.println(anuncio.getText()+" publicado con exito en " + topicoUsuario);
	}

	public boolean verificarCanal(Topic canal, Connection conexion) throws JMSException{

		DestinationSource destinationSource = new DestinationSource(conexion);
		destinationSource.start();
		Set<ActiveMQTopic> topicList = destinationSource.getTopics();
		if(topicList.contains(canal)){
			destinationSource.stop();
			return true;
		};
		destinationSource.stop();
		return false;
	}

	public void cerrarSession() throws JMSException{
		if (sesion != null){
			sesion.close();
		}
	}
	
	public void eliminarTopic(){
		//TODO Como se elimina un topico??
	}
	
	public void traerTopicos() throws JMSException, InvalidDestinationException{

		Connection conexion = cf.createConnection();
		conexion.start();

		DestinationSource destinationSource = new DestinationSource(conexion);
		destinationSource.start();
		Set<ActiveMQTopic> topicList = destinationSource.getTopics();
		
		System.out.println("canales disponibles:");
		for (ActiveMQTopic topico : topicList) {
			System.out.println(topico.toString());
		}
		conexion.close();
	}
}
