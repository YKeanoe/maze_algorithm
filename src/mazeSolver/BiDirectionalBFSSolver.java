package mazeSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import maze.Cell;
import maze.Maze;

/**
 * KruskalGenerator generate mazes using Kruskal algorithm.
 * 
 *  @author Yohanes Keanoe - s3323595
 *
 */
public class BiDirectionalBFSSolver implements MazeSolver {
   // The list of needed variables is initialized
   Queue<Node> queue = new LinkedList<Node>();
   HashMap<String,Node> explored = new HashMap<String,Node>();
   HashMap<String,Node> nodes = new HashMap<String,Node>();
   Node headNode, endNode;
   // pathLength is the variable to keep count of the path taken to finish the maze
   int pathLength = 0;
   
   /**
    * solveMaze is an override function from the maze solver interface.
    * 
    * ******************************************************************************************
    * 
    *    BidirectionalBFSSolver is a BFS algorithm that has both start and end node as starting node.
    * The BidirectionalBFSSolve will first create a Node class for all the maze cells. Then it will
    * starts by adding both entrance and exit node to the explored nodes list. Then starts the queue
    * with both entrance and exit node.
    *    The program will then go into loop that will go through the queue and for each node in the queue,
    * it will check if the node is in the startNodeFrontier or endNodeFrontier. 
    *    If it is, then it means that the maze has been solved and it will call the current
    * node and the next node, depending whether it's startNodeFrontier or endNodeFrontier, 
    * to go on recursive call of print() function and it will count how many steps taken 
    * from the entrance to exit. 
    *    If it is not, then it means that the maze has not been solved and the loop continue. It will
    * keep adding the next node of the current node to the queue and add the current node into the frontier.
    * 
    * ******************************************************************************************
    * 
    * @param maze Maze inputted maze.
    */
	@Override
	public void solveMaze(Maze maze) {
      if(maze.type == 2){ // Handling a hexagon maze
         for(int i = 0; i < maze.sizeR; i++){      
            for(int j = (i + 1) / 2; j < maze.sizeC + (i + 1) / 2; j++){
               nodes.put("[" + maze.map[i][j].c + "," + maze.map[i][j].r + "]",new Node(maze.map[i][j]));
            }
         }
      }
      else{
         for(int i = 0; i < maze.sizeR; i++){        
            for(int j = 0; j < maze.sizeC; j++){
               nodes.put("[" + maze.map[i][j].c + "," + maze.map[i][j].r + "]",new Node(maze.map[i][j]));
            }
         }
      }
	   
      // initialize both headNode and endNode and add the nodes into the queue and mark it explored.
      headNode = nodes.get("[" + maze.entrance.c + "," + maze.entrance.r + "]");
      headNode.type = 0;
      endNode = nodes.get("[" + maze.exit.c + "," + maze.exit.r + "]");
      endNode.type = 1;
	   queue.add(headNode);
	   queue.add(endNode);
      explored.put(headNode.getKey(), headNode);
      explored.put(endNode.getKey(), endNode);
      
      // Initialize startFrontier and endFrontier and add both headNode and endNode to it.
      HashMap<String,Node> startFrontier = new HashMap<String,Node>();
      HashMap<String,Node> endFrontier = new HashMap<String,Node>();
      startFrontier.put(headNode.getKey(), headNode);
      endFrontier.put(endNode.getKey(), endNode);
      
      // Starts the loop until queue is empty
	   while(!queue.isEmpty()){
	      Node currNode = queue.remove();
	      maze.drawFtPrt(currNode.getCell());
    
	      if(currNode.getType() == 0) {
            if(endFrontier.containsKey(currNode.getKey())){
               Node tempN = null;
               for(int i = 0; i < 6; i++){
                  if(tempN == null){
                     if(currNode.getCell().neigh[i] != null && !currNode.getCell().wall[i].present){
                        tempN = endFrontier.get("[" + currNode.getCell().neigh[i].c + "," + currNode.getCell().neigh[i].r + "]");                 
                     }
                  }
               }
               if(tempN != null){
                  tempN.print();
               }
               currNode.print();
               System.out.println("Path length of the solution is " + pathLength);
               break;
            }
         }
         if(currNode.getType() == 1) {
            if(startFrontier.containsKey(currNode.getKey())){
               Node tempN = null;
               for(int i = 0; i < 6; i++){
                  if(tempN == null){
                     if(currNode.getCell().neigh[i] != null && !currNode.getCell().wall[i].present){
                        tempN = startFrontier.get("[" + currNode.getCell().neigh[i].c + "," + currNode.getCell().neigh[i].r + "]");                 
                     }
                  }
               }
               if(tempN != null){
                  tempN.print();
               }
               currNode.print();
               System.out.println("Path length of the solution is " + pathLength);
               break;
            }
         }
         
         /* Handle if the current node is the tunnel, then add the destined tunnel to the queue */
         if(currNode.getCell().tunnelTo != null){
            Cell destCell = currNode.getCell().tunnelTo;
            if(!explored.containsKey("[" + destCell.c + "," + destCell.r + "]")){
                  Node nextNode = nodes.get("[" + currNode.getCell().tunnelTo.c + "," + currNode.getCell().tunnelTo.r + "]");
             
                  if(currNode.getType() == 0){
                     nextNode.setType(0);
                     nextNode.addLink(nextNode, 6);
                     nextNode.addPrev(currNode);
                     
                     startFrontier.put(nextNode.getKey(), nextNode);
                  }
                  if(currNode.getType() == 1) {
                     nextNode.setType(1);
                     currNode.addLink(nextNode, 6);
                     nextNode.addPrev(currNode);

                     endFrontier.put(nextNode.getKey(), nextNode);
                  }
                  queue.add(nextNode);
            }
         }
         
         /* Loop to handle each direction and add available direction to the queue */
         for(int i = 0; i < maze.NUM_DIR; i++){
            if(currNode.getCell().neigh[i] != null){
               Cell neighCell = currNode.getCell().neigh[i];
               if(!explored.containsKey("[" + neighCell.c + "," + neighCell.r + "]")){
                  if(!currNode.getCell().wall[i].present){
                     Node nextNode = nodes.get("[" + currNode.getCell().neigh[i].c + "," + currNode.getCell().neigh[i].r + "]");
                     
                     if(currNode.getType() == 0){
                        nextNode.setType(0);
                        nextNode.addLink(nextNode, i);
                        nextNode.addPrev(currNode);

                        startFrontier.put(nextNode.getKey(), nextNode);
                     }
                    
                     if(currNode.getType() == 1) {
                        int rev;
                        if(i > 2)
                           rev = i - 3;
                        else
                           rev = i + 3;

                        nextNode.setType(1);
                        currNode.addLink(nextNode, rev);
                        nextNode.addPrev(currNode);
                        endFrontier.put(nextNode.getKey(), nextNode);
                     }
                     queue.add(nextNode);
                  }
               }
            }         
         }
         explored.put(currNode.getKey(), currNode);
	   }
	} // end of solveMaze()


	@Override
	public boolean isSolved() {
	   /* Maze is solved whenever the solveMaze function stop. */
	   return true;
	} // end of isSolved()


	@Override
	public int cellsExplored() {
		return explored.size();
	} // end of cellsExplored()
	
   /** 
    * Node is a private class that acted as a Tree for ease of use.
    * 
    * @param cell Cell.
    * 
    */ 
	private class Node{
	   String key;
	   Cell cell;
	   Node prev;
	   Node[] next = new Node[7];
	   int type;
	   
	   public Node(Cell cell){
	      this.cell = cell;
	      key = "[" + cell.c + "," + cell.r + "]";
	   }
	   public Cell getCell(){
	      return cell;
	   }
	   public void print(){
	      if(type == 1){
//	         System.out.println("cell [" + cell.c + "," + cell.r + "]");
   	      if(prev != null)
   	         prev.print();
   	      pathLength++;
	      }
	      else{
	           if(prev != null)
	               prev.print();
//	         System.out.println("cell [" + cell.c + "," + cell.r + "]");
            pathLength++;
	      }
	   }
	   public void addLink(Node node, int dir){
	      next[dir] = node;
	   }
	   public void addPrev(Node node){
         prev = node;
      }
	   public void setType(int i){
	      this.type = i;
	   }
	   public int getType(){
	      return type;
	   }
	   public String getKey(){
	      return key;
	   }
	}
	
} // end of class BiDirectionalBFSSolver

