package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import it.polito.tdp.PremierLeague.model.Event.EventType;

public class Simulator {
	
	private PriorityQueue<Event> queue;
	
	private int teamA;
	private int goalA;
	private int goalB;
	private int giocatoriA;
	private int giocatoriB;
	private List<Player> players;
	private List<Player> playersA;
	private List<Player> playersB;
	
	private int n;
	private Graph<Player, DefaultWeightedEdge> graph;
	
	public Simulator(int n, Graph<Player, DefaultWeightedEdge> graph, Match match) {
		this.n = n;
		this.graph = graph;
		this.giocatoriA = 11;
		this.giocatoriB = 11;
		this.goalB = 0;
		this.goalA = 0;
		this.players = new ArrayList<>(this.graph.vertexSet());
		this.teamA = match.getTeamHomeID();
		this.playersA = new ArrayList<Player>();
		this.playersB = new ArrayList<Player>();
		
		for(Player pl: this.players) {
			if(pl.getTeam() == teamA) {
				playersA.add(pl);
			} else {
				playersB.add(pl);
			}
		}
	}
	
	public void Initialise() {
		this.queue = new PriorityQueue<>();
		
		for(int i=0; i<n; i++) {
			double p = Math.random();
			Event e;
			if(p >= 0.5) {
				e = new Event(i, EventType.GOAL);
				this.queue.add(e);
			} else if (p >= 0.3) {
				e = new Event(i, EventType.ESPULSIONE);
				this.queue.add(e);
			} else {
				e = new Event(i, EventType.INFORTUNIO);
				this.queue.add(e);
			}
		}
	}
	
	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			this.processEvent(e);
		}
	}
	
	public void processEvent(Event e) {
		switch (e.getType()) {
		case GOAL:
			if(this.giocatoriA > this.giocatoriB) {
				this.goalA++;
			} else if(this.giocatoriA < this.giocatoriB) {
				this.goalB++;
			} else {
				int team = this.bestPlayerTeam();
				if(team == teamA) {
					this.goalA++;
				} else {
					this.goalB++;
				}
			}
			break;
		case ESPULSIONE:
			double p = Math.random();
			int team = this.bestPlayerTeam();
			Player player;
			if(p >= 0.4) {
				if(team == teamA) {
					this.giocatoriA--;
					player = this.playersA.remove((int)Math.random()*playersA.size());
				} else {
					this.giocatoriB--;
					player = this.playersB.remove((int)Math.random()*playersB.size());
				}
			} else {
				if(team == teamA) {
					this.giocatoriB--;
					player = this.playersB.remove((int)Math.random()*playersB.size());
				} else {
					this.giocatoriA--;
					player = this.playersA.remove((int)Math.random()*playersA.size());
				}
			}
			this.players.remove(player);
			break;
		case INFORTUNIO:
			double pp = Math.random();
			if(pp >= 0.5) {
				for(int i=0; i<2; i++) {
					double ppp = Math.random();
					Event ev;
					if(ppp >= 0.5) {
						ev = new Event(i, EventType.GOAL);
						this.queue.add(ev);
					} else if (ppp >= 0.3) {
						ev = new Event(i, EventType.ESPULSIONE);
						this.queue.add(ev);
					} else {
						ev = new Event(i, EventType.INFORTUNIO);
						this.queue.add(ev);
					}
				}
			} else {
				for(int i=0; i<3; i++) {
					double ppp = Math.random();
					Event ev;
					if(ppp >= 0.5) {
						ev = new Event(i, EventType.GOAL);
						this.queue.add(ev);
					} else if (ppp >= 0.3) {
						ev = new Event(i, EventType.ESPULSIONE);
						this.queue.add(ev);
					} else {
						ev = new Event(i, EventType.INFORTUNIO);
						this.queue.add(ev);
					}
				}
			}
			break;
		}
	}
	
	public Integer bestPlayerTeam() {
		Player best = null;
		double max = 0.0;
		
		for(Player p: players) {
			double out = 0.0;
			double in = 0.0;
			double res = 0.0;
			
			Set<DefaultWeightedEdge> outs  = this.graph.outgoingEdgesOf(p);
			for(DefaultWeightedEdge e: outs) {
				out += this.graph.getEdgeWeight(e);
			}
			Set<DefaultWeightedEdge> ins  = this.graph.incomingEdgesOf(p);
			for(DefaultWeightedEdge e: ins) {
				in += this.graph.getEdgeWeight(e);
			}
			res = out - in;
			p.setDeltaMax(res);
		}
		
		for(Player p: players) {
			if(p.getDeltaMax() > max) {
				max = p.getDeltaMax();
				best = p;
			}
		}
		
		return best.getTeam();
	}

	public int getGoalA() {
		return goalA;
	}

	public int getGoalB() {
		return goalB;
	}

	public int getGiocatoriA() {
		return giocatoriA;
	}

	public int getGiocatoriB() {
		return giocatoriB;
	}
	
}
