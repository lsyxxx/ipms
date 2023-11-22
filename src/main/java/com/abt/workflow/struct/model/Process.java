package com.abt.workflow.struct.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 流程
 */
@Data
@Accessors(chain = true)
public class Process {
    private String id;
    private String name;
    private List<Layer> layers = new ArrayList<>();
    public Process add(Node ...nodes) {
        layers.add(Layer.build().add(nodes));
        return this;
    }

    public void print() {
        System.out.println("|-- Process: [" + this.id + ", " + this.name + "]");
        layers.forEach(i -> {
            i.print();
        });
    }

    public Node findNode(String id) {
        this.layers.forEach(layer -> {
            layer.getNodes().forEach(node -> {
                
            });
        });
        return null;
    }

}
