package com.thoughtworks.zeratul.utils;

import java.util.List;

public class Selections {
    private boolean distinct;
    private List<SelectionGenerator> selectionGenerators;

    public Selections(boolean distinct, List<SelectionGenerator> selectionGenerators) {
        this.distinct = distinct;
        this.selectionGenerators = selectionGenerators;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public List<SelectionGenerator> getSelectionGenerators() {
        return selectionGenerators;
    }

    public void setSelectionGenerators(List<SelectionGenerator> selectionGenerators) {
        this.selectionGenerators = selectionGenerators;
    }
}
