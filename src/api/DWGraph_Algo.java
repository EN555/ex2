package api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

import com.google.gson.*;

import gameClient.util.Point3D;

public class DWGraph_Algo implements dw_graph_algorithms, JsonDeserializer<DWGraph_DS> {

	private directed_weighted_graph g;
	private int time = 0; //helper for dfs algo
	private directed_weighted_graph rec_graph;
	public DWGraph_Algo() {
		this.g = new DWGraph_DS();
	}

	@Override
	public void init(directed_weighted_graph g) {
		this.g = g;
	}

	@Override
	public directed_weighted_graph getGraph() {
		return this.g;
	}

	/**
	 * @return deep copy of the this graph
	 */
	@Override
	public directed_weighted_graph copy() {

		if (this.g == null) // check if the load graph is null
			return null;

		directed_weighted_graph h = new DWGraph_DS(); // create new graph

		// create the new nodes of the graph and insert them to the new graph

		Iterator<node_data> iterNodeCreator = this.g.getV().iterator();

		while (iterNodeCreator.hasNext()) {
			Node temp = (Node) iterNodeCreator.next();
			h.addNode(new Node(temp.getKey(), temp.getWeight(), temp.getInfo(), temp.getLocation()));
		}

		// create the new edges of the new graph and insert them to the graph

		for (node_data node : h.getV()) { // move on all the nodes
			Iterator<edge_data> iterEdge = this.getGraph().getE(node.getKey()).iterator(); // take all the nodes that
																							// start from this integer
			while (iterEdge.hasNext()) {
				edge_data edge = iterEdge.next();
				h.connect(edge.getSrc(), edge.getDest(), edge.getWeight());
			}
		}

		return h;
	}

	/**
	 * this method implement dijkstra algorithm for calculate the minimum path to
	 * move from one node to another
	 * 
	 * @param src
	 * @param dest
	 * @return map with all the path from the src to dest if have path like that
	 */
	private HashMap<Integer, node_data> dijkstra(int src, int dest) {

		if (this.g == null) // check if the graph is null
			return null;

		if (this.g.getNode(src) == null || this.g.getNode(dest) == null) // check if the src exist
			return null;

		for (node_data paintNode : this.g.getV()) { // sign all the nodes as white
			paintNode.setInfo(Colors.WHITE.toString());
			paintNode.setWeight(-1); // initial all the nodes to -1
		}

		PriorityQueue<node_data> minPriotiy = new PriorityQueue<node_data>(); // create minimum priority queue
		HashMap<Integer, node_data> parents = new HashMap<Integer, node_data>(); // map to find the path from src to
																					// dest

		this.g.getNode(src).setWeight(0);
		this.g.getNode(src).setInfo(Colors.BLACK.toString());
		minPriotiy.add(this.g.getNode(src)); // add the src to the priority

		while (!minPriotiy.isEmpty()) {

			node_data temp = minPriotiy.poll(); // pull the next check neighbors nodes

			for (edge_data neighborsEdges : this.g.getE(temp.getKey())) { // move on all the neighbors
				node_data neighbor = this.g.getNode(neighborsEdges.getDest());

				if (neighbor.getInfo().equals(Colors.WHITE.toString())) { // no one visit there before

					neighbor.setWeight(temp.getWeight() + neighborsEdges.getWeight());
					neighbor.setInfo(Colors.GREY.toString());
					parents.put(neighbor.getKey(), temp);
					minPriotiy.add(neighbor);
				}

				else if (neighbor.getInfo().equals(Colors.GREY.toString())) { // visit there but not in all his
																				// neighbors

					if ((temp.getWeight() + neighborsEdges.getWeight()) < neighbor.getWeight()) { // check if have
																									// another option

						neighbor.setWeight(temp.getWeight() + neighborsEdges.getWeight());
						parents.put(neighbor.getKey(), temp);
					}
				}

			}
			temp.setInfo(Colors.BLACK.toString()); // finished to move on all the neighbors of this node
		}

		return parents;
	}

	private void dfs(node_data node, directed_weighted_graph graph) {

		node.setInfo(Colors.GREY.toString()); // mark the current node as grey, and add one to its tag
		node.setTag(node.getTag() + 1);

		// for each of the current node neighbors, if it is not yet visited, visit it
		for (edge_data e : graph.getE(node.getKey())) { // go though the neighbors
			node_data neighbor = graph.getNode(e.getDest());
			if (neighbor.getInfo().equals(Colors.WHITE.toString())) { // if not visited
				dfs(neighbor, graph); // visit
			}
		}
		node.setInfo(Colors.BLACK.toString()); // mark the current node as black (done)
	}

	/**
	 * @return true iff the graph is strongly connected
	 */
	@Override
	public boolean isConnected() {

		if (this.g == null) {
			return false;
		} // check if it's null graph
		if (this.g.nodeSize() == 0) {
			return true;
		} // if the graph is empty, return true

		// initiate all the nodes
		for (node_data n : this.g.getV()) {
			n.setTag(0);
			n.setInfo(Colors.WHITE.toString());
		}

		// pick a random node in the graph
		Node randomNode = (Node) this.g.getV().iterator().next();

		// call dfs
		dfs(randomNode, this.g);

		// reverse the graph
		directed_weighted_graph new_graph = new DWGraph_DS();
		for (node_data n : this.g.getV()) { // add all the nodes
			node_data new_node = new Node(n.getKey());
			new_node.setTag(0);
			new_node.setInfo(Colors.WHITE.toString());
			new_graph.addNode(new_node);
		}
		for (node_data n : this.g.getV()) { // connect all edges reversed
			for (edge_data e : this.g.getE(n.getKey())) {
				new_graph.connect(e.getDest(), e.getSrc(), e.getWeight());
			}
		}

		// call dfs on new_garph
		dfs(new_graph.getNode(randomNode.getKey()), new_graph);

		// check if all nodes are connected to the random node in both directions
		for (node_data n : this.g.getV()) {
			if (n.getTag() != 1 || new_graph.getNode(n.getKey()).getTag() != 1) {
				return false;
			}
		}

		return true;
	}

		///
	
    public LinkedList<LinkedList<Integer>> dfs_alg(directed_weighted_graph graph, LinkedList<node_data> list){
    	LinkedList<LinkedList<Integer>> res = new LinkedList<LinkedList<Integer>>();
    	for (node_data node: graph.getV()) {node.setTag(0); node.setInfo(Colors.WHITE.toString()); node.setWeight(0);}
    	this.time =0;
    	Iterator<node_data> iter_arr = list.iterator();
    	while(iter_arr.hasNext()){
    		node_data node = iter_arr.next();
    		if(node.getInfo().equals(Colors.WHITE.toString())) {node.setTag(this.time);node.setInfo(Colors.GREY.toString()); res.add(rec_dfs(graph, node,new LinkedList<Integer>()));}
    	} 
    	return res;
    }
    
    public node_data sighnNode(node_data nei, directed_weighted_graph graph) {	//return node_data if found either return null

    	Iterator<edge_data> iter = graph.getE(nei.getKey()).iterator();	
    	while(iter.hasNext()) {
			node_data tp = graph.getNode(iter.next().getDest());
			if(tp.getInfo().equals(Colors.WHITE.toString())) {
				tp.setInfo(Colors.GREY.toString());
				this.time+=1;
				tp.setTag(this.time);
				return tp;
			}   	
    	}
    	return null;
    }

    public LinkedList<Integer> rec_dfs(directed_weighted_graph graph, node_data node, LinkedList<Integer> list){
    	
    	list.add(node.getKey());
    	
    	Stack<node_data> q = new Stack<node_data>();
    	q.add(node);
    	
    	while(!q.isEmpty()) { 
 
    		node_data tp = sighnNode(node, graph);
    		if(tp != null) {
    			list.add(tp.getKey());
    			q.add(tp);
    		}
    		while(tp != null) {
    		tp = sighnNode(tp, graph);
    		if(tp != null) {
    		q.add(tp);
    		list.add(tp.getKey());
    		}
    		}

    		tp = q.pop();

    		if(tp != null) {
        		tp.setInfo(Colors.BLACK.toString());
        		this.time+=1;
        		tp.setWeight(this.time);
        		}
    		if(!q.isEmpty()) {
    		node = q.peek();
    		}
    		}
    	
    	return list;
    }
	
    public directed_weighted_graph reverse(directed_weighted_graph graph) {
    	directed_weighted_graph new_graph = new DWGraph_DS();
		for (node_data n : graph.getV()) { // add all the nodes
			node_data new_node = new Node(n.getKey());
			new_graph.addNode(new_node);
			new_node.setWeight(n.getWeight());
		}
		for (node_data n : graph.getV()) { // connect all edges reversed
			for (edge_data e : graph.getE(n.getKey())) {
				new_graph.connect(e.getDest(), e.getSrc(), 1);
			}
		}
			return new_graph;
    }
    
    public LinkedList<LinkedList<Integer>> connected_components(){
    	if(this.g == null)
    		return null;
    	LinkedList<LinkedList<Integer>> res = new LinkedList<LinkedList<Integer>>();
    	dfs_alg(this.g, new LinkedList<node_data>(this.g.getV()));
    	directed_weighted_graph rev_graph = reverse(this.g);
    	this.rec_graph = rev_graph;
    	for(node_data np : this.g.getV())
    		this.rec_graph.getNode(np.getKey()).setWeight(np.getWeight());
        List<node_data> ord = new LinkedList<node_data>(this.rec_graph.getV());
    	Collections.sort(ord, Collections.reverseOrder(new Comparator<node_data>() {
    		@Override 
    		public int compare(node_data s1, node_data s2) {return (int)(s1.getWeight() - s2.getWeight()); }
    	}));
    	
    	res = dfs_alg(this.rec_graph, new LinkedList<node_data>(ord));
    	return res;
    }
       
    public LinkedList<Integer> connected_component(int id1) {
    	if(this.g.getNode(id1)== null)
    		return null;
    	for(LinkedList<Integer> list : this.connected_components()) {
    		if(list.contains(this.g.getNode(id1).getKey()))
    			return list;
    	}
    	return null;
    }
	/**
	 * @return the minimum path from node to node if havn't path return -1
	 */
	@Override
	public double shortestPathDist(int src, int dest) {

		if (dijkstra(src, dest) == null)
			return -1;

		return this.g.getNode(dest).getWeight();
	}

	/**
	 * @return the shortest path from src to dest if hasn't path it's return null
	 */
	@Override
	public List<node_data> shortestPath(int src, int dest) {
		HashMap<Integer, node_data> map = dijkstra(src, dest);

		if (map == null) // check if map is null (e.g. src not exist or doesn't isn't exist)
			return null;
		LinkedList<node_data> path = new LinkedList<node_data>();

		if (this.g.getNode(dest).getInfo().equals(Colors.WHITE.toString())) // if the color is white so the node can't
																			// reachable from src
			return null;

		node_data temp = this.g.getNode(dest);

		while (temp != this.g.getNode(src)) {

			path.addFirst(temp); // go from the dest to the src
			temp = map.get(temp.getKey());
		}

		path.addFirst(this.g.getNode(src)); // insert the src to the list
		return path;
	}

	/**
	 * save the input of the current graph in json file
	 */
	@Override
	public boolean save(String file) {

		if (this.g == null) // check if it's null graph
			return false;

		/*
		 * //if empty graph if(this.g.nodeSize() == 0) { try {
		 * 
		 * PrintWriter pw = new PrintWriter(new File(file));
		 * pw.write("{\"Nods\":[],\"Edges\":[]}"); pw.close();
		 * 
		 * } catch(Exception e) { return false; //e.printStackTrace(); } return true; }
		 */

		StringBuilder jb = new StringBuilder("{\"Edges\":[");

		// add all the edges
		for (node_data node : this.g.getV()) {
			for (edge_data edge : this.g.getE(node.getKey())) {
				jb.append("{\"src\":");
				jb.append(edge.getSrc());
				jb.append(",\"w\":");
				jb.append(edge.getWeight());
				jb.append(",\"dest\":");
				jb.append(edge.getDest());
				jb.append("},");
			}
		}

		if (this.g.edgeSize() != 0)
			jb.deleteCharAt(jb.length() - 1); // remove the last comma

		// add all the nodes
		jb.append("],\"Nodes\":[");
		for (node_data node : this.g.getV()) {
			jb.append("{\"pos\":\"");
			jb.append(node.getLocation().toString());
			jb.append("\"");
			jb.append(",\"id\":");
			jb.append(node.getKey());
			jb.append("},");
		}

		if (this.g.nodeSize() != 0)
			jb.deleteCharAt(jb.length() - 1); // remove the last comma

		jb.append("]}");

		// write json to file

		try {

			PrintWriter pw = new PrintWriter(new File(file));
			pw.write(jb.toString());
			pw.close();

		} catch (Exception e) {
			return false;
			// e.printStackTrace();
		}

		return true;
	}

	/**
	 * this method use for read graph from json
	 * 
	 */
	@Override
	public DWGraph_DS deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {

		DWGraph_DS graph = new DWGraph_DS(); // create new graph to put json graph in

		JsonObject jsonObject = json.getAsJsonObject();

		JsonArray NodeArray = jsonObject.get("Nodes").getAsJsonArray(); // get the list of nodes
		JsonArray EdgeArray = jsonObject.get("Edges").getAsJsonArray(); // get the list of edges

		// add all the nodes
		for (JsonElement n : NodeArray) {

			JsonObject no = n.getAsJsonObject();

			// create the node
			node_data node = new Node(no.get("id").getAsInt(), 0, "", new Point3D(no.get("pos").getAsString()));

			graph.addNode(node); // add the current node to the graph
		}

		// add all the edges
		for (JsonElement e : EdgeArray) {

			JsonObject eo = e.getAsJsonObject();

			// connect the given edge
			graph.connect(eo.get("src").getAsInt(), eo.get("dest").getAsInt(), eo.get("w").getAsDouble());
		}

		return graph;
	}

	/**
	 * load the current class with graph from Json
	 */
	@Override
	public boolean load(String file) {

		GsonBuilder builder = new GsonBuilder(); // create jason object
		builder.registerTypeAdapter(DWGraph_DS.class, new DWGraph_Algo()); // adapt between the object class to the way
																			// to read json that create from this class
		Gson gson = builder.create();

		try {

			// read from json

			FileReader read = new FileReader(file);
			this.g = gson.fromJson(read, DWGraph_DS.class); // update the current graph as json graph

			if (this.g == null) // if it's empty file return empty graph
				this.g = new DWGraph_DS();

		} catch (FileNotFoundException e) {

			// e.printStackTrace();
			return false; // didn't succeed to read from file

		}

		return true;
	}

	// enum used by algorithms
	private enum Colors {
		BLACK, WHITE, GREY;
	}

	public static void main(String [] args) {
		
		directed_weighted_graph graph = new DWGraph_DS();
		Node a0 = new Node(0);
		Node a1 = new Node(1);
		Node a2 = new Node(2);
		Node a3 = new Node(3);
//		Node a4 = new Node(4);
//		Node a5 = new Node(5);
//		Node a6 = new Node(6);
//		Node a7 = new Node(7);
//		Node a8 = new Node(8);
//		Node a9 = new Node(9);
		graph.addNode(a0);
		graph.addNode(a1);
		graph.addNode(a2);
		graph.addNode(a3);
//		graph.addNode(a4);
//		graph.addNode(a5);
//		graph.addNode(a6);
//		graph.addNode(a7);
//		graph.addNode(a8);
//		graph.addNode(a9);
		graph.connect(0, 2, 1);
		graph.connect(2, 0, 2);
		graph.connect(1, 2, 3);
		graph.connect(2, 3, 4);
//		graph.connect(3, 9, 4);

//		graph.connect(3, 4, 4);
//		graph.connect(4, 6, 4);
//		graph.connect(6, 4, 4);
//		graph.connect(5, 6, 4);
//		graph.connect(4, 5, 4);
//
//		
//		graph.connect(9, 6, 4);
//		graph.connect(6, 9, 4);
//
//		graph.connect(2, 7, 4);
//		graph.connect(2, 8, 4);
//
//		graph.connect(7, 8, 4);
//		graph.connect(8, 7, 4);

		DWGraph_Algo alg= new DWGraph_Algo();
		alg.init(graph);
		System.out.println(alg.connected_components());
		System.out.println(alg.connected_component(1));

	}
}
