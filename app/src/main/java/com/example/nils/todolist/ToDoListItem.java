package com.example.nils.todolist;

import java.io.Serializable;

public class ToDoListItem implements Serializable {

    String item;
    Boolean completed;
    int id;

    public ToDoListItem(String item) {
        this.item = item;
        this.completed = false;
    }

    public ToDoListItem(String item, Boolean completed, int id) {
        this.item = item;
        this.completed = completed;
        this.id = id;
    }

    public void changeState() {
        completed ^= true;
    }

}
