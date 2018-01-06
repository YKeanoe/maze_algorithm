package mazeGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import maze.Cell;
import maze.Maze;

public class RecursiveBacktrackerGenerator implements MazeGenerator {

   /** 
    * generateMaze is an override function from the maze interface.
    * 
    * ******************************************************************************************
    * 
    *    RecursiveBacktrackerGenerator generateMaze following depth first search algorithm. Starts by adding
    * all cells into a a node class and select random starting point. Then select a random available neighbouring
    * nodes. Keep selecting a random available neighbour nodes until there are no more, then backtrack it until 
    * there is an available node. The loop will stop when the size of the visited nodes and nodes are the same.
    * 
    * ******************************************************************************************
    * 
    * @param maze Maze inputted maze.
    */
   @Override
   public void generateMaze(Maze maze) {

      /* each list to store nodes, frontiers, and visited nodes */
      HashMap<String, Node> visited = new HashMap<String,Node>();
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
               Node toNode = nodes.get("[" + node.getCell().neigh[h].c + "," + node.getCell().neigh[h].r + "]");
               if(toNode != null)
                  node.setNeighbour(toNode, h);
            }
         }
         if(maze.type == 1){ // Handling tunnel.
            if(node.getCell().tunnelTo != null){
               Node tunnelDest = nodes.get("[" + node.getCell().tunnelTo.c + "," + node.getCell().tunnelTo.r + "]");
               node.setNeighbour(tunnelDest, 6);
            }
         }
      }

      // Starts at random nodes in the maze and mark it visited.
      Random random = new Random();
      List<String> keys = new ArrayList<String>(nodes.keySet());
      String randomKey = keys.get( random.nextInt(keys.size()) );
      Node currNode = nodes.get(randomKey);
      //      visited.put(currNode.getKey(), currNode);

      // Starts loop
      while(visited.size() != nodes.size()){

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

                        currNode.getCell().wall[dir].drawn = false;
                        currNode.getCell().wall[dir].present = false;
                        
                        int rev;
                        if(dir > 2)
                           rev = dir - 3;
                        else
                           rev = dir + 3;

                        nextNode.getCell().wall[rev].drawn = false;
                        nextNode.getCell().wall[rev].present = false;

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
    *
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
   }
} // end of class RecursiveBacktrackerGenerator
