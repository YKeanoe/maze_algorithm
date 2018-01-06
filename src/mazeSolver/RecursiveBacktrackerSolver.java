package mazeSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import maze.Cell;
import maze.Maze;

/**
 * Implements the recursive backtracking maze solving algorithm.
 */
public class RecursiveBacktrackerSolver implements MazeSolver {
  
   HashMap<String, Node> visited = new HashMap<String,Node>();
   int pathLength = 0;
   
   /**
    * solveMaze is an override function from the maze solver interface.
    * 
    * ******************************************************************************************
    * 
    *    RecursiveBacktracker maze solver is basically a DFS search algorithm. Starts on the entrance cell,
    * select a random available neighbour and move to it while tagging the node visited and add 
    * previous to the next node. Using a doubly linked list to make it easier. If there are no available 
    * cell, back up one step and search for an available node to visit. Keep looping until the current
    * node is the exit node.
    * 
    * ******************************************************************************************
    * 
    * @param maze Maze inputted maze.
    */
	@Override
	public void solveMaze(Maze maze) {
	   
      /* each list to store nodes, frontiers, and visited nodes */
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

      /* Next, set connection for each connected cells. A special case if the maze is a tunnel maze.*/
      for(Node node : nodes.values()){
         /* Creating new edge for each direction */
         for(int h = 0; h < maze.NUM_DIR; h++){ 
            if(node.getCell().neigh[h] != null){
               if(node.getCell().wall[h].present == false && node.getCell().wall[h].drawn == false){
                  Node toNode = nodes.get("[" + node.getCell().neigh[h].c + "," + node.getCell().neigh[h].r + "]");
                  if(toNode != null)
                     node.setNeighbour(toNode, h);
               }
            }
         }
         if(maze.type == 1){ // Handling tunnel.
            if(node.getCell().tunnelTo != null){
               Node tunnelDest = nodes.get("[" + node.getCell().tunnelTo.c + "," + node.getCell().tunnelTo.r + "]");
               node.setNeighbour(tunnelDest, 6);
            }
         }
      }

      // Find entrance node and start
      Node currNode = nodes.get("[" + maze.entrance.c + "," + maze.entrance.r + "]");
      Node endNode = nodes.get("[" + maze.exit.c + "," + maze.exit.r + "]");
      
      Random random = new Random();

      // Starts loop
      while(currNode != endNode){
         maze.drawFtPrt(currNode.getCell());
         
         // If Cell is a tunnel and not visited
         if(currNode.getNeighbour(6) != null && !visited.containsKey(currNode.getNeighbour(6).getKey())){
            currNode.getNeighbour(6).setPrevious(currNode);
            visited.put(currNode.getKey(), currNode);
            
            currNode = currNode.getNeighbour(6);
         }
         else{
            // Checks if there is available nodes to visit.
            boolean allVisited = true;
            for(int i = 0; i<7; i++){
               if(currNode.getNeighbour(i) != null){
                  if(!visited.containsKey(currNode.getNeighbour(i).getKey())){
                     allVisited = false;
                  }
               }
            }
            // If all nodes are visited, go back a step
            if(allVisited){
               visited.put(currNode.getKey(), currNode);
               currNode = currNode.getPrevious();
            }
            // If there are available nodes to visit.
            else{
               boolean able = false;
               // Starts a loop until available nodes to visit is visited.
               while(!able){ 
                  // Get available direction
                  ArrayList<Integer> avb = new ArrayList<Integer>();
                  for(int i = 0 ; i < maze.NUM_DIR ; i++){
                     if(currNode.neighbour[i] != null){
                        if(!visited.containsKey(currNode.neighbour[i].getKey())){
                           avb.add(i);
                        }
                     }
                  }                    
                  random = new Random();
                  int randomDir;
                  randomDir = random.nextInt(avb.size());
                  int dir = avb.get(randomDir);

                  if(currNode.getNeighbour(dir) != null){
                     if(!visited.containsKey(currNode.getNeighbour(dir).getKey())){
                        Node nextNode = nodes.get(currNode.getNeighbour(dir).getKey());
                        nextNode.setPrevious(currNode);
                        visited.put(currNode.getKey(), currNode);
                        currNode = nextNode;
                        able = true;
                     }
                  }
                  
               }
            }
         }
      }
      // Draw the exit node and add it to visited.
      maze.drawFtPrt(currNode.getCell());
      visited.put(currNode.getKey(), currNode);
      // Count the path length
      currNode.countPath();
      System.out.println("Path length of the solution is " + pathLength);
	} // end of solveMaze()


	@Override
	public boolean isSolved() {
		// The maze is solved when the solveMaze function stopped
		return true;
	} // end if isSolved()

	@Override
	public int cellsExplored() {
		// TODO Auto-generated method stub
		return visited.size();
	} // end of cellsExplored()
	
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
    */
   private class Node{
      private String key;
      private Cell cell;
      private Node[] neighbour = new Node[7];
      private Node previous;

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
      public void setNeighbour(Node node, int dir){
         neighbour[dir] = node;
      }
      public Node getNeighbour(int dir){
         return neighbour[dir];
      }
      public void setPrevious(Node node){
         this.previous = node;
      }
      public Node getPrevious(){
         return this.previous;
      }
      public void countPath(){
         if(previous != null){
            previous.countPath();
            pathLength++;
         }
      }
   }
} // end of class RecursiveBackTrackerSolver
