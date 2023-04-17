package com.example.todospringboot;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private Long id;

    @Column(unique = true)
    @Getter @Setter
    private String title;

    @Column(name = "ordering")
    @Getter @Setter
    private int order;

    @Getter @Setter
    private String url;

    @Getter @Setter
    public boolean completed;

    public Todo() {
    }
}
