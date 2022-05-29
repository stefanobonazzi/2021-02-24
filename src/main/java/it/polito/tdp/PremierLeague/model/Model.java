package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	
	private Graph<Player, DefaultWeightedEdge> graph;
	private Map<Integer, Player> players;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
	}
	
	public List<Match> getAllMatches() {
		return this.dao.listAllMatches();
	}
	
	public void creaGrafo(Match match) {
		this.graph = new SimpleDirectedWeightedGraph<Player, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		players = this.dao.listPlayers(match);
		Graphs.addAllVertices(this.graph, players.values());
		
		List<Action> actions = this.dao.listActions(match);
		
		for(Player p: players.values()) {
			for(Action a: actions) {
				if(a.getPlayerID().equals(p.getPlayerID())) {
					Double e = (double) (a.getTotalSuccessfulPassesAll()+a.getAssists())/(double) a.getTimePlayed();
					p.setE(e);
					p.setTeam(a.getTeamID());
				}
			}
		}
		
		for(Player p: players.values()) {
			for(Action a: actions) {
				if(!a.getPlayerID().equals(p.getPlayerID()) && p.getTeam()!=a.getTeamID()) {
					DefaultWeightedEdge edge = this.graph.getEdge(p, players.get(a.getPlayerID()));
					if(edge == null && this.graph.getEdge(players.get(a.getPlayerID()), p) == null) {
						double e1 = p.getE();
						double e2 = players.get(a.getPlayerID()).getE();
						double delta = 0;
						if(e1 > e2) {
							edge = this.graph.addEdge(p, players.get(a.getPlayerID()));
							delta = e1 - e2;
							List<Player> p2 = new ArrayList<Player>();
							p2.add(players.get(a.getPlayerID()));
							Graphs.addOutgoingEdges(this.graph, p, p2);
						} else {
							edge = this.graph.addEdge(players.get(a.getPlayerID()), p);
							delta = e2 - e1;
							List<Player> p1 = new ArrayList<Player>();
							p1.add(p);
							Graphs.addOutgoingEdges(this.graph, players.get(a.getPlayerID()), p1);
						}	
						this.graph.setEdgeWeight(edge, delta);
					}
				}
			}
		}
	}

	public String bestPlayer() {
		Player best = null;
		double max = 0.0;
		
		for(Player p: players.values()) {
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
		
		for(Player p: players.values()) {
			if(p.getDeltaMax() > max) {
				max = p.getDeltaMax();
				best = p;
			}
		}
		
		return best+", delta efficienza = "+max;
	}
	
	public Graph<Player, DefaultWeightedEdge> getGraph() {
		return graph;
	}

	public String simula(int n, Match match) {
		Simulator simulator = new Simulator(n, this.graph, match);
		simulator.Initialise();
		simulator.run();
		
		return "La partita è finita: "+simulator.getGoalA()+"-"+simulator.getGoalB()+"\nGiocatori espulsi rispettivamente: "+(11-simulator.getGiocatoriA())+"-"+(11-simulator.getGiocatoriB());
	}
	
}
