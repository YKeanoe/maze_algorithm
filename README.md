# Maze Algorithm

A project for my second year of Computer Science Algorithm and Analysis. The project is to implement maze generation and solver algorithm. 

The maze have 3 types of map
* Normal maze
* Hexagon maze
* Tunnel maze

The maze generation include 3 algorithm, 
* [Kruskal's Algorithm](https://en.wikipedia.org/wiki/Kruskal%27s_algorithm)
* [Prim's Algorithm](https://en.wikipedia.org/wiki/Prim%27s_algorithm)
* [Recursive Backtracking Algorithm](https://en.wikipedia.org/wiki/Backtracking)

The maze solver algorithm include 2 algorithm, 
* [Recursive Backtracking Algorithm](https://en.wikipedia.org/wiki/Backtracking) 
* [Bidirectional Breadth-First-Search Algorithm](https://en.wikipedia.org/wiki/Bidirectional_search).

### Usage
Update the example.txt in src folder first line into: 
>(normal|tunnel|hex) (modiPrim|kruskal|recurBack) (map height) (map width) (start row) (start column) (finish row) (finish column)

Example:
> hex kruskal recurBack 50 50 0 0 49 49

For tunnel type map, add new line for each tunnel into:  
> (start row) (start column) (finish row) (finish column)

Example:
> tunnel kruskal recurBack 50 50 0 0 49 49  
> 10 10 42 44  
> 25 25 0 25  
> ...

Run the MazeSolver.java with argument line:
> <example.txt location> <y/n maze visualization>

Example:
> src/example.txt y

### Showcase

***Tunnel Maze Generation***
![tunnel](https://github.com/ZankiMaru/maze_algorithm/tree/master/showcase/tunnel-maze-seq.gif)

