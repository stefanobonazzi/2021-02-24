package it.polito.tdp.PremierLeague.model;

public class Event implements Comparable<Event>{

	public enum EventType {
		GOAL,
		ESPULSIONE,
		INFORTUNIO
	}
	
	private int nAzione;	
	private EventType type;

	public Event(int nAzione, EventType type) {
		this.nAzione = nAzione;
		this.type = type;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public int getnAzione() {
		return nAzione;
	}

	public void setnAzione(int nAzione) {
		this.nAzione = nAzione;
	}

	@Override
	public int compareTo(Event o) {
		return this.nAzione-o.getnAzione();
	}
	
}
