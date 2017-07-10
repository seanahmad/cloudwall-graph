/*
 * (C) Copyright 2017 Kyle F. Downey.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cloudwall.graph;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdjacencyListGraphTest {
    /*
     * The acyclic tests uses this graph:
     *
     *                  A
     *                /  \
     *               B   C
     *             /   / |  \
     *            D   E  F   G
     *               /        \
     *              H          I
     *
     * while the with-cycles cases use this:
     *
     *                  A
     *                /  \
     *               B----C
     *             /   / |  \
     *            D---E--F---G
     *               /        \
     *              H----------I
     *
     */

    private Map<Object,LightweightVertex> vertexMap = new HashMap<>();


    @Before
    public void buildVertexMap() {
        for (String id : ImmutableList.of("A", "B", "C", "D", "E", "F", "G", "H", "I")) {
            LightweightVertex v = new LightweightVertex(id);
            vertexMap.put(v.getVertexId(), v);
        }
    }

    @Test
    public void bfsAcyclicGraph() {
        List<LightweightVertex> vertices = bfs(aDirectAcyclicGraph());
        assertEquals(createVertices("A", "B", "C", "D", "E", "F", "G", "H", "I"), vertices);
    }

    @Test
    public void bfsGraphWithCycles() {
        List<LightweightVertex> vertices = bfs(aGraphWithCycles());
        assertEquals(createVertices("A", "B", "C", "D", "E", "F", "G", "H", "I"), vertices);
    }

    @Test
    public void dfsAcyclicGraph() {
        List<LightweightVertex> vertices = dfs(aDirectAcyclicGraph());
        assertEquals(createVertices("A", "C", "G", "I", "F", "E", "H", "B", "D"), vertices);
    }

    @Test
    public void dfsAGraphWithCycles() {
        List<LightweightVertex> vertices = dfs(aGraphWithCycles());
        assertEquals(createVertices("A", "C", "G", "F", "E", "D", "B", "H", "I"), vertices);
    }

    @Test
    public void iterateAllVertices() {
        Set<LightweightVertex> vertices = new HashSet<>();
        aDirectAcyclicGraph().forEachVertex(v -> {
            vertices.add(v);
            System.out.println(v);
        });

        assertEquals(9, vertices.size());
        assertTrue("every vertex present", vertexMap.values().containsAll(vertices));
    }

    @Test
    public void iterateAllEdges() {
        Set<DirectedEdge<LightweightVertex>> edges = new HashSet<>();
        AtomicInteger numAdjacentToC = new AtomicInteger();

        aDirectAcyclicGraph().forEachEdge(e -> {
            edges.add(e);
            if (e.getFrom().getVertexId().equals("C")) {
                numAdjacentToC.incrementAndGet();
            }
            System.out.println(e);
        });
        
        assertEquals(3, numAdjacentToC.get());
        assertEquals(8, edges.size());

    }

    private List<LightweightVertex> bfs(Graph<LightweightVertex,DirectedEdge> graph) {
        List<LightweightVertex> vertices = new ArrayList<>();
        graph.visitBreadthFirstFrom(vertexMap.get("A"), v -> {
            vertices.add(v);
            System.out.println(v.getVertexId());
        });
        assertEquals(9, vertices.size());
        return vertices;
    }

    private List<LightweightVertex> dfs(Graph<LightweightVertex,DirectedEdge> graph) {
        List<LightweightVertex> vertices = new ArrayList<>();
        graph.visitDepthFirstFrom(vertexMap.get("A"), v -> {
            vertices.add(v);
            System.out.println(v.getVertexId());
        });
        return vertices;
    }

    private Graph<LightweightVertex,DirectedEdge> aDirectAcyclicGraph() {
        MutableGraph<LightweightVertex,DirectedEdge> graph = new AdjacencyListGraph<>();

        for (String id : ImmutableList.of("A", "B", "C", "D", "E", "F", "G", "H", "I")) {
            graph.addVertex(vertexMap.get(id));
        }
        graph.addEdge(new DirectedEdge<>(vertexMap.get("A"), vertexMap.get("B")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("A"), vertexMap.get("C")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("B"), vertexMap.get("D")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("C"), vertexMap.get("E")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("C"), vertexMap.get("F")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("C"), vertexMap.get("G")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("E"), vertexMap.get("H")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("G"), vertexMap.get("I")));

        return graph;
    }

    private List<LightweightVertex> createVertices(String... vertexIds) {
        List<LightweightVertex> vertices = new ArrayList<>();
        for (String id : vertexIds) {
            vertices.add(vertexMap.get(id));
        }
        return vertices;
    }

    private Graph<LightweightVertex,DirectedEdge> aGraphWithCycles() {
        MutableGraph<LightweightVertex,DirectedEdge> graph = new AdjacencyListGraph<>();

        for (String id : ImmutableList.of("A", "B", "C", "D", "E", "F", "G", "H", "I")) {
            graph.addVertex(vertexMap.get(id));
        }
        graph.addEdge(new DirectedEdge<>(vertexMap.get("A"), vertexMap.get("B")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("A"), vertexMap.get("C")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("B"), vertexMap.get("D")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("B"), vertexMap.get("C")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("C"), vertexMap.get("E")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("C"), vertexMap.get("F")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("C"), vertexMap.get("G")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("E"), vertexMap.get("H")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("G"), vertexMap.get("I")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("D"), vertexMap.get("E")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("E"), vertexMap.get("F")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("F"), vertexMap.get("G")));
        graph.addEdge(new DirectedEdge<>(vertexMap.get("H"), vertexMap.get("I")));

        return graph;
    }
}