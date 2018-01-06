package mazeGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import maze.Cell;
import maze.Maze;

/**
 * KruskalGenerator generate mazes using Kruskal algorithm.
 * 
 *  @author Yohanes Keanoe - s3323595
 */
public class ModifiedPrimsGenerator implements MazeGenerator {

   /** 
    * generateMaze is an override function from the maze interface.
    * 
    * ******************************************************************************************
    * 
    *    ModifiedPrimsGenerator generateMaze follows prim's algorithm but with a minor modify. The basic idea
    * of Prim's Algorithm is that in a graph, starts on a random node and add the connected nodes as a frontier.
    * Then pick the lowest cost edge and mark the nodes as visited. Continually adding new frontier as the visited
    * nodes increase. 
    *    ModifiedPrismGenerator starts by adding all nodes into a hashmap for easier management and adding all 
    * connected edges of the node into the node class. It then pick a random nodes in the nodes list, 
    * then add the edges as the frontier as it is easier to detect from and to which nodes it picks.
    *    Next is by doing a loop, the program automatically add new frontier as more nodes are visited. The 
    * Loop will stop when the amount of visited nodes and total nodes are the same.
    * 
    * ******************************************************************************************
    * 
    * @param maze Maze inputted maze.
    */
	@Override
	public void generateMaze(Maze maze) {

	   /* each list to store nodes, frontiers, and visited nodes */
	   HashMap<String, Node> visited = new HashMap<String,Node>();
	   ArrayList<Edge> frontier = new ArrayList<Edge>();
	   HashMap<String, Node> nodes = new HashMap<String,Node>();

      /* Starts by handling different type of mazes and create nodes for each cell. */
	   if(maze.type == 2){ // Handling a hexagon maze
	      for(int i = 0; i < maze.sizeR; i++){      
	         for(int j = (i + 1) / 2; j < maze.sizeC + (i + 1) / 2; j++){
	            Node newNode = new Node(maze.map[i][j]);
	            nodes.put(newNode.getKey(), newNode);
	         }
	      }
	   }
	   else{
	      for(int i = 0; i < maze.sizeR; i++){        
	         for(int j = 0; j < maze.sizeC; j++){
               Node newNode = new Node(maze.map[i][j]);
               nodes.put(newNode.getKey(), newNode);
	         }
	      }
	   }
	     /* Next, create a new edge for each connected cells inside node class.*/
	   for(Node node : nodes.values()){
	      for(int i = 0; i < maze.NUM_DIR; i++){
            if(node.getCell().neigh[i] != null){
               if(node.getCell().neigh[i] != null){
                  Cell toCell = node.getCell().neigh[i];
                  Node toNode = nodes.get("[" + toCell.c + "," + toCell.r + "]");
                  if(toNode != null){
                     Edge newEdges = new Edge(node, toNode, i);
                     node.setNeighbour(newEdges, i);
                  }
               } 
            } 
	      }
	   }
	   
	   /* Then the program will start to pick a random starting node and mark it as visited. */
      Random random = new Random();
      int index = random.nextInt(nodes.size());  
      List<String> keys = new ArrayList<String>(nodes.keySet());
      String randomKey = keys.get( random.nextInt(keys.size()) );
      Node currNode = nodes.get(randomKey);
      visited.put(currNode.getKey(), currNode);
      for(int i = 0; i < maze.NUM_DIR; i++){
         if(currNode.getEdge(i) != null)
            frontier.add(currNode.getEdge(i));
      }
      
      /* Next is the loop to add more visited nodes and frontiers. The loop will stop 
       * when the amount of nodes visited is the same as the amount of nodes. */
      while(visited.size() != nodes.size()){
         random = new Random();
         index = random.nextInt(frontier.size());
         
         Edge randomEdge = frontier.get(index);
         Node nextNode = randomEdge.getTo();
         /* Checks if the next node is visited */
         if(!visited.containsKey(nextNode.getKey())){
            int dir = randomEdge.getDir();      
            frontier.get(index).getFrom().getCell().wall[dir].drawn = false;
            frontier.get(index).getFrom().getCell().wall[dir].present = false;
            visited.put(nextNode.getKey(), nextNode);
            frontier.remove(index);       
            for(int i = 0; i < maze.NUM_DIR; i++){
               if(nextNode.getEdge(i) != null){
                  if(nextNode.getEdge(i).getTo() != randomEdge.getFrom()){
                     frontier.add(nextNode.getEdge(i));
                  }
               }
            }
         }
         else{
            frontier.remove(index);
         }
      }
      
	} // end of generateMaze()


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
      private Edge[] neighbour = new Edge[6];
      public Node(Cell cell){
         this.cell = cell;
         this.key = "[" + cell.c + "," + cell.r + "]";
      }
      public String getKey(){
         return key;
      }
      public Cell getCell(){
         return cell;
      }
      public void setNeighbour(Edge edge, int dir){
         neighbour[dir] = edge;
      }
      public Edge getEdge(int dir){
         return neighbour[dir];
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

} // end of class ModifiedPrimsGenerator
