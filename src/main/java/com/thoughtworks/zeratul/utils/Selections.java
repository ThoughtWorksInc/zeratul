package com.thoughtworks.zeratul.utils;

public class Selections {
    private boolean distinct;
    private Iterable<SelectionGenerator> selectionGenerators;

    public Selections(boolean distinct, Iterable<SelectionGenerator> selectionGenerators) {
        this.distinct = distinct;
        this.selectionGenerators = selectionGenerators;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public Iterable<SelectionGenerator> getSelectionGenerators() {
        return selectionGenerators;
    }

    public void setSelectionGenerators(Iterable<SelectionGenerator> selectionGenerators) {
        this.selectionGenerators = selectionGenerators;
    }
}
