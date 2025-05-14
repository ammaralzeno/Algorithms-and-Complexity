class DisjointSet:
    def __init__(self, n):
        self.parent = list(range(n))
        self.rank = [0] * n

    def find(self, item):
        if self.parent[item] != item:
            self.parent[item] = self.find(self.parent[item])
        return self.parent[item]

    def union(self, x, y):
        xroot = self.find(x)
        yroot = self.find(y)
        if self.rank[xroot] < self.rank[yroot]:
            self.parent[xroot] = yroot
        elif self.rank[xroot] > self.rank[yroot]:
            self.parent[yroot] = xroot
        else:
            self.parent[yroot] = xroot
            self.rank[xroot] += 1

def lunar_transport_network(n, connections, D, V, L, R):
   
    all_connections = []
    for i, j in connections:
        all_connections.append((V(i, j), i, j, 'V'))
        all_connections.append((L(i, j), i, j, 'L'))
        all_connections.append((R(i, j), i, j, 'R'))

    
    all_connections.sort()

    
    ds = DisjointSet(n)

    
    result = []
    total_cost = 0

    
    for cost, i, j, transport_type in all_connections:
        if ds.find(i-1) != ds.find(j-1): 
            ds.union(i-1, j-1)
            result.append((i, j, transport_type))
            total_cost += cost

        if len(result) == n - 1:  
            break

    return result, total_cost

