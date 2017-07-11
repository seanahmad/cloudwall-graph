/*
 * (C) Copyright 2017 Kyle F. Downey.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cloudwall.graph.io.text;

import cloudwall.graph.*;
import org.jooq.lambda.tuple.Tuple2;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Ultra-simple data model for various text formats that are just lists of edges. 
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 * @see TextFormat
 */
public class EdgeListModel implements GraphModel {
    private final List<Tuple2<Long, Long>> edges = new ArrayList<>();

    private boolean supportComments = true;

    void addEdge(long vid1, long vid2) {
        edges.add(new Tuple2<>(vid1, vid2));
    }

    void write(DataSource dataOut) throws IOException {
        try (Writer w = new OutputStreamWriter(dataOut.getOutputStream())) {
            if (supportComments) {
                w.write("# Generated by Cloudwall Graph library\n");
            }
            for (Tuple2<Long, Long> edge : edges) {
                w.write(String.valueOf(edge.v1()));
                w.write(" ");
                w.write(String.valueOf(edge.v2()));
                w.write("\n");
            }
        }
    }

    /**
     * Converts this model to a uniform representation for analysis.
     */
    @SuppressWarnings("WeakerAccess")
    public Graph<LightweightVertex, HeavyweightDirectedEdge<LightweightVertex>> compile() {
        AdjacencyListGraph<LightweightVertex, HeavyweightDirectedEdge<LightweightVertex>> graph = new AdjacencyListGraph<>();
        for (Tuple2<Long, Long> edge : edges) {
            edge.forEach(vid -> {
                if (graph.getVertex(vid) == null) {
                    graph.addVertex(new LightweightVertex(vid));
                }
            });
            LightweightVertex vid1 = graph.getVertex(edge.v1());
            LightweightVertex vid2 = graph.getVertex(edge.v2());

            assert vid1 != null;
            assert vid2 != null;

            graph.addEdge(new HeavyweightDirectedEdge<>(vid1, vid2));
        }

        return graph;
    }

    public void setSupportComments(boolean supportComments) {
        this.supportComments = supportComments;
    }
}
