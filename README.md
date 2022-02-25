# Presentation

This project is an implementation of a vocal server.

# Download

First you need to download this project on your computer. To do so, you can use the following command line :

```git
git clone -b 1.0-SNAPSHOT https://github.com/Pierre-Emmanuel41/vocal-server.git --recursive
```

and then double click on the deploy.bat file. This will deploy this project and all its dependencies on your computer. Which means it generates the folder associated to this project and its dependencies in your .m2 folder. Once this has been done, you can add the project as maven dependency on your maven project :

```xml
<dependency>
	<groupId>fr.pederobien</groupId>
	<artifactId>vocal-server</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```

# Tutorial

A vocal server is defined by the interface [IVocalServer](https://github.com/Pierre-Emmanuel41/vocal-server/blob/1.0-SNAPSHOT/src/main/java/fr/pederobien/vocal/server/interfaces/IVocalServer.java) whose the implementation is <code>VocalServer</code>. The communication between a client and the server is done with the UDP protocol. When a request is received from a client, a <code>PlayerSpeakEvent</code> is thrown. When caught by an external application, it is possible to specify to which players the transmitter will speak. By default, a player cannot speak to other players.

```java
// Server name
String name = "Vocal Server";

// Server port
int port = 25000;

// Instantiating a vocal server
IVocalServer server = new VocalServer(name, port);
server.open();

EventListener listener = new EventListener(server);
EventManager.registerListener(listener);



private class EventListener implements IEventListener {
	private IVocalServer server;
	
	public EventListener(IVocalServer server) {
		this.server = server;
	}
	
	@EventHandler
	private void onPlayerSpeak(PlayerSpeakEvent event) {
		if (!event.getServer().equals(server))
			return;
		
		// Specific code to specify the receivers
		event.getVolumes().put(event.getPlayers().get("Player 1"), new VolumeResult(0.75));
		event.getVolumes().put(event.getPlayers().get("Player 2"), new VolumeResult(1.25));
	}
}
```