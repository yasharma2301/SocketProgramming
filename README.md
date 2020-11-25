# Socket Programming and directed graph gui

This project uses TCP sockets to connect a client with server. The client sends server 4 values, for example X, n, B, C where,  X is the adjacency matrix of a directed graph with 5 nodes A B C D E, and n is the length of the path from node B to node C. The server responds with a YES/NO string based on the fact that path exists or not followed by an image for confirmation.

# Results
After passing the parameters as:
X=
|  | A | B | C | D | E |
| ------ | ------ | ------ |  ------ |  ------ |  ------ | 
| A | 0 | 1 | 1 | 0 | 1 |
| B | 1 | 0 | 0 | 1 | 0 |
| C | 0 | 0 | 0 | 1 | 0 |
| D | 0 | 0 | 0 | 0 | 0 |
| E | 1 | 0 | 0 | 0 | 0 |


n = 2

node1 = A

node2 = D

Graph Formed:

![Alt text](/GraphImageFrame1606259566149.png?raw=true "GraphImage")
