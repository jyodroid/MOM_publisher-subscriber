package mom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;
import java.util.Set;

import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.command.ActiveMQTopic;

public class AdCliente implements MessageListener{

	private boolean transacted = false;
	private String url = /*"tcp://192.168.104.131:61616";*/ ActiveMQConnection.DEFAULT_BROKER_URL;
	private TopicConnectionFactory connectionFactory;
	private String cedula;
	private int ackMode = Session.AUTO_ACKNOWLEDGE;//Session.CLIENT_ACKNOWLEDGE;

	public AdCliente(String cedula) throws JMSException {

		connectionFactory= new ActiveMQConnectionFactory(url);
		setCedula(cedula);
	}
	
	@Override
	public void onMessage(Message mensaje) {
		String mensajeTexto = null;
		try {
			if (mensaje instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) mensaje;
				mensajeTexto = textMessage.getText();
				
				Date tTimeStamp = new Date(textMessage.getJMSTimestamp());
				String tPublicacion = DateFormat.getDateTimeInstance().format(tTimeStamp);
				
				System.out.println(tPublicacion+" -> " + mensajeTexto.toString());
			}
		} catch (JMSException e) {
			System.out.println("Error al recibir mensaje: "+ e.getMessage());
		}
	}

	public void subcribirse(String nombreTopico) throws JMSException{

		TopicConnection tConexion = connectionFactory.createTopicConnection();
		tConexion.setClientID(getCedula());
		TopicSession tSession = tConexion.createTopicSession(transacted, ackMode);
		tConexion.start();

		Topic topico = tSession.createTopic(nombreTopico);
		tSession.createDurableSubscriber(topico, topico.getTopicName()+"_"+getCedula());

		System.out.println("suscripción: "+topico.getTopicName()+"_"+getCedula()+" realizada con exito");

		tConexion.close();
	}

	public void dejarTopico(String subscripcion) throws JMSException{
		
		TopicConnection tConexion = connectionFactory.createTopicConnection();
		tConexion.setClientID(getCedula());
		TopicSession tSession = tConexion.createTopicSession(transacted, ackMode);
		tConexion.start();
		
		try {		
			tSession.unsubscribe(subscripcion);
			System.out.println("subscripción: "+subscripcion+" cancelada");
		} catch (InvalidDestinationException e) {
			System.out.println("No existe suscripción: "+ subscripcion);
		} finally{
			tConexion.close();
		}
	}

	public void traerTopicos() throws JMSException, InvalidDestinationException{

		TopicConnection tConexion = connectionFactory.createTopicConnection();
		tConexion.start();

		DestinationSource destinationSource = new DestinationSource(tConexion);
		destinationSource.start();
		Set<ActiveMQTopic> topicList = destinationSource.getTopics();
		
		System.out.println("canales disponibles:");
		for (ActiveMQTopic topico : topicList) {
			System.out.println(topico.toString());
		}
		
		tConexion.close();
	}

	public void modoRecibir(String nombreTopico, int modo) throws Exception{

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		TopicConnection tConexion = connectionFactory.createTopicConnection();
		tConexion.setClientID(getCedula());
		TopicSession tSession = tConexion.createTopicSession(transacted, ackMode);
		tConexion.start();
		
		//Verificar que el tópico al que quiere consultar exista 
		//(para evitar que el cliente cree topicos nuevos) y permitir escuchar los mensajes.
		//DestinationSource destinationSource = new DestinationSource(connection);
		DestinationSource destinationSource = new DestinationSource(tConexion);
		destinationSource.start();
		Set<ActiveMQTopic> topicList = destinationSource.getTopics();
		
		Topic topico = tSession.createTopic(nombreTopico);
		
		if(!topicList.contains(topico)){
			tConexion.close();
			throw new Exception("No existe el canal, consulte los canales disponibles");
		};
		destinationSource.stop();
		
		TopicSubscriber ts = tSession.createDurableSubscriber(topico, topico.getTopicName()+"_"+getCedula());
		ts.setMessageListener(this);
		
		if (modo == 1){
			System.out.println("Mensajes publicados de: "+nombreTopico);
			tConexion.close();
			ts.close();
		}else{
			//salir al presionar cualquiuer tecla.
			System.out.println("Recibiendo mensajes de: "+nombreTopico+" (presione cualquier tecla para salir)");
			if (br.read()!=0){
				tConexion.close();
				ts.close();
				System.out.println("Salio del modo recibir mensajes del canal: "+nombreTopico);
			};		
		}
	}
	
	public String getCedula(){
		return this.cedula;
	}
	
	public void setCedula(String cedula){
		this.cedula = cedula;
	}
}