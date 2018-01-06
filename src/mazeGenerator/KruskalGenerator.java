package mazeGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import maze.*;

/**
 * KruskalGenerator generate mazes using Kruskal algorithm.
 * 
 *  @author Yohanes Keanoe - s3323595
 */
public class KruskalGenerator implements MazeGenerator {
   
   /* each arraylist to store nodes, edges, and connected nodes */
   ArrayList<Edge> edges = new ArrayList<Edge>();
   ArrayList<Node> nodes = new ArrayList<Node>();
   ArrayList<nodeSet> connectedNodes = new ArrayList<nodeSet>();
   
   /** 
    * generateMaze is an override function from the maze interface.
    * 
    * ******************************************************************************************
    * 
    *    KruskalGenerator generateMaze follows kruskal algorithm. Starts by creating a node for each of
    * the cells and handling different type of cells. i.e., Hexagon maze has a different cell numbering than Normal or Tunnel maze.
    *    Then creating edges for each node and add connection for each node to the neighbouring node. As the maze started with 
    * edges connecting with the neighbour, I stored it in as an edge class in node class for easier management. Tunnel maze
    * is a special case, as it start connected with the tunnel destination, so it will store both node and destinated node
    * in connectedNodes arraylist.
    *    Next, while the edge list is not empty, the program will go on a loop and select random edges to connect. The program
    * will check if the connected node is already connected or not using the connectedNodes list. Depend on if it is a Disjoint-set,
    * it will either merge or add a new nodeSet.
    * 
    * ******************************************************************************************
    * 
    * @param maze Maze inputted maze.
    */
   @Override
   public void generateMaze(Maze maze){
      
      /* Starts by handling different type of mazes and create nodes for each cell. */
	   if(maze.type == 2){ // Handling a hexagon maze
	      for(int i = 0; i < maze.sizeR; i++){      
	         for(int j = (i + 1) / 2; j < maze.sizeC + (i + 1) / 2; j++){
	            nodes.add(new Node(maze.map[i][j], 2));
	         }
	      }
	   }
	   else{
	      for(int i = 0; i < maze.sizeR; i++){	     
	         for(int j = 0; j < maze.sizeC; j++){
	            if(maze.type == 1) // Handling a tunnel maze
	               nodes.add(new Node(maze.map[i][j], 1));
	            else // Handling a normal maze
	               nodes.add(new Node(maze.map[i][j], 0));
	         }
	      }
	   }

	   /* Next, create a new edge for each connected cells. A special case if the maze is a tunnel maze.*/
	   for(Node node : nodes){
	      /* Creating new edge for each direction */
	      for(int h = 0; h < maze.NUM_DIR; h++){ 
	         if(node.getCell().neigh[h] != null){
	            Cell toCell = node.getCell().neigh[h];
	            Node toNode = null;
	            for(Node sNode : nodes){ // Searching for the destinated node to store in edge class
	               if(sNode.getCell() == toCell){
	                  toNode = sNode;
	                  break;
	               }
	            }
	            if(toNode != null){
	               Edge newEdges = new Edge(node, toNode, h);
	               edges.add(newEdges);
	               node.addEdge(newEdges, h);
	            }
	         } 
	      }
	      if(maze.type == 1){ // Handling tunnel.
	         if(node.getCell().tunnelTo != null){
	            Node tunnelDest = null;
	            for(Node sNode : nodes){ // Searching for destinated node from the tunnel.
	               if(sNode.getCell() == node.getCell().tunnelTo){
	                  tunnelDest = sNode;
	                  break;
	               }
	            }
	            node.setTunnel(tunnelDest);
	            /* checkConnection function is called so that no duplicated connection is made.
	             * If not, then it will create a new connection. */
               if(!checkConnection(node, tunnelDest)){   
   	            nodeSet tunnelNodeSet = new nodeSet();
                  tunnelNodeSet.connectedNodes.put(node.getKey(), node);
                  tunnelNodeSet.connectedNodes.put(tunnelDest.getKey(), tunnelDest);
                  connectedNodes.add(tunnelNodeSet);
               }
	         }
	      }
      }

	   /* Next, do a loop for all the listed edges in random order and depending if the cell is already connected or not, create a new connection. */
	   while(!edges.isEmpty()){	   
	      Random random = new Random();
	      int index = random.nextInt(edges.size());
         int dir = edges.get(index).getDir();  
         
         /* checkConnection returns true if there at least one connection found. */
         if(!checkConnection(edges.get(index).getFrom(), edges.get(index).getTo())){
            /* Add connectedEdge for both of the nodes. */
            edges.get(index).getFrom().addConnectedEdge(dir);
            int rev;
            if(dir > 2)
               rev = dir - 3;
            else
               rev = dir + 3;
            edges.get(index).getTo().addConnectedEdge(rev);
            edges.get(index).getFrom().getCell().wall[dir].drawn = false;
            edges.get(index).getFrom().getCell().wall[dir].present = false;
            
            nodeSet fromSet = null;
            nodeSet toSet = null;
            /* Search nodeSet for the current node to the destinated node. */
            for(nodeSet nodeSet : connectedNodes){ 
               if(nodeSet.searchNode(edges.get(index).getFrom()))
                  fromSet = nodeSet;
               if(nodeSet.searchNode(edges.get(index).getTo()))
                  toSet = nodeSet;
            }
            /* If they both don't have any connection, create a new nodeSet and add both node to the connectedNodes list. */
            if(fromSet == null && toSet == null){
               nodeSet newNodeSet = new nodeSet();
               newNodeSet.connectedNodes.put(edges.get(index).getFrom().getKey(), edges.get(index).getFrom());
               newNodeSet.connectedNodes.put(edges.get(index).getTo().getKey(), edges.get(index).getTo());
               connectedNodes.add(newNodeSet);
            }
            /* If only one of them have connection, merge it. */
            else if( fromSet == null)
               toSet.connectedNodes.put(edges.get(index).getFrom().getKey(), edges.get(index).getFrom());
            else
               fromSet.connectedNodes.put(edges.get(index).getTo().getKey(), edges.get(index).getTo());
         }
         /* If both nodes have connection, check for a loop by calling isLoop function. */
         else{
            if(!isLoop(edges.get(index).getFrom(), edges.get(index).getTo())){
               /* Add connectedEdge for both of the nodes. */
               edges.get(index).getFrom().addConnectedEdge(dir);
               int rev;
               if(dir > 2)
                  rev = dir - 3;
               else
                  rev = dir + 3;
               edges.get(index).getTo().addConnectedEdge(rev);
               
               edges.get(index).getFrom().getCell().wall[dir].drawn = false;
               edges.get(index).getFrom().getCell().wall[dir].present = false;

               nodeSet fromSet = null;
               nodeSet toSet = null;
               for(nodeSet nodeSet : connectedNodes){
                  /* Search nodeSet for the current node to the destinated node. */
                  if(nodeSet.searchNode(edges.get(index).getFrom()))
                     fromSet = nodeSet;
                  if(nodeSet.searchNode(edges.get(index).getTo()))
                     toSet = nodeSet;
               }                 
               /* Create a new nodeSet and merge both nodeSet of the current node and destinated node,
                * then add it to the connectedNodes list while removing the old nodeSet. */
               nodeSet newNodeSet = new nodeSet();
               newNodeSet.connectedNodes.putAll(fromSet.connectedNodes);
               newNodeSet.connectedNodes.putAll(toSet.connectedNodes);
               connectedNodes.remove(fromSet);
               connectedNodes.remove(toSet);
               connectedNodes.add(newNodeSet);
            }
         }
         edges.remove(index);  
	   }
	} // end of generateMaze()
	
   /** 
    * checkConnection is a simple method that will return true if both node have connections.
    * 
    * @param from node.
    * @param to node
    */ 
   public boolean checkConnection(Node from, Node to){
      nodeSet fromSet = null;
      nodeSet toSet = null;
      if(connectedNodes.size() == 0)
         return false;
      for(nodeSet nodeSet : connectedNodes){
         if(nodeSet.searchNode(from))
            fromSet = nodeSet;
         if(nodeSet.searchNode(to))
            toSet = nodeSet;
      }
      if(fromSet == null || toSet == null)
         return false;
      else
         return true;
   }
   
   /** 
    * isLoop function will search through the connectedNodes list for a loop.
    * 
    * ******************************************************************************************
    * 
    * Using the connectedNodes list, isLoop function will search for currect node in destinated
    * node's connection. It will return true if there is a loop, and false if there isn't
    * 
    * ******************************************************************************************
    * 
    * @param node from.
    * @param node to.
    * @returns boolean. True if there's a loop, False if else.
    */
   public boolean isLoop(Node from, Node to){
      nodeSet toSet = null;
      for(nodeSet nodeSet : connectedNodes){
         if(nodeSet.searchNode(to))
            toSet = nodeSet;
      }
      if(toSet.connectedNodes.containsValue(from))
         return true;
      return false;
     }
	  
   /** 
    * nodeSet is a private class to store a hashmap of connected nodes.
    * 
    * ******************************************************************************************
    * 
    * nodeSet class is created to make it easier to store a set of connected nodes.
    * 
    * ******************************************************************************************
    */
	private class nodeSet{
      private HashMap<String, Node> connectedNodes = new HashMap<String, Node>();
      public boolean searchNode(Node node){
         if(connectedNodes.containsValue(node))
            return true;
         else
            return false;
      }
	}
	
   /** 
    * Node is a private class to store cells.
    * 
    * ******************************************************************************************
    * 
    * Node class is created to makes it easier to access and storing informations about the cells.
    * It also makes the program cleaner as it makes the class less dependent on maze class.
    * 
    * ******************************************************************************************
    * 
    * @param Cell cell.
    * @param int type. Type referring to the type of maze.
    */
	private class Node{
	   private String key;
	   private Cell cell;
	   private int type;
	   private Node tunnel;
	   private Edge[] edges = new Edge[6];
	   private Edge[] connectedEdges = new Edge[6];
	   
	   public Node(Cell cell, int type){
	      this.cell = cell;
	      this.type = type;
	      key = "[" + cell.c + "," + cell.r + "]";
	   }
	   public String getKey(){
	      return key;
	   }
	   public void addEdge(Edge edge, int dir){
	      edges[dir] = edge;
	   }
	   public void addConnectedEdge(int dir){
	      connectedEdges[dir] = edges[dir];
	   }
	   public Node getTunnel(){
	      return tunnel;
	   }
	   public void setTunnel(Node node){
	      tunnel = node;
	   }
	   public Cell getCell(){
	      return cell;
	   }
	}
	
   /** 
    * Edge is a private class to store edge.
    * 
    * ******************************************************************************************
    * 
    * edge class is created to makes it easier to access and storing informations about the cell's edges.
    * 
    * ******************************************************************************************
    * 
    * @param node from.
    * @param node to.
    * @param int direction.
    */
	private class Edge{
	   private Node from;
	   private Node to;
	   private int direction;
	   
	   public Edge(Node from, Node to, int direction){
	      this.from = from;
	      this.to = to;
	      this.direction = direction;
	   }	   
	   public int getDir(){
	      return direction;
      }
	   public Node getFrom(){
         return from;
      }
	   public Node getTo(){
         return to;
      }
	}
} // end of class KruskalGenerator
