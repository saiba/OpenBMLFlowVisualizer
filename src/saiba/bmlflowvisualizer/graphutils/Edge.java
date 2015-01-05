package saiba.bmlflowvisualizer.graphutils;

/**
 * Stores an edge between vertex start and end
 * @author Herwin
 * 
 * @param <V> vertex type/id
 */

public class Edge<V>
{
    private final V start;
    private final V end;

    public Edge(V start, V end)
    {
        this.start = start;
        this.end = end;
    }

    public V getStart()
    {
        return start;
    }

    public V getEnd()
    {
        return end;
    }
}
