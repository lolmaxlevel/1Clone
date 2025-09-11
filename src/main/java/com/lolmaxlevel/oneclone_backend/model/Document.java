package com.lolmaxlevel.oneclone_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lolmaxlevel.oneclone_backend.types.ActType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "documents")
public class Document {
    // Document entity representing a document in the system
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    // Номер документа == id человека
    @Column(name = "number", nullable = false)
    private int number;

    //порядковый номер документа (для договоров будет добавляться после -, прим. №17-1,
    // для других просто сам номер как порядковый)
    @Column(name = "seq_number", nullable = false)
    private int seqNumber;

    @Column(name = "archive", nullable = false)
    private boolean archive = false;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ActType type;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "date_from", nullable = false)
    private LocalDate dateFrom;

    @Column(name = "date_to", nullable = false)
    private LocalDate dateTo;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", referencedColumnName = "id")
    private Employee owner;


}