package com.lolmaxlevel.oneclone_backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lolmaxlevel.oneclone_backend.types.CompanyType;
import com.lolmaxlevel.oneclone_backend.types.DocumentType;
import com.lolmaxlevel.oneclone_backend.types.CountryType;
import com.lolmaxlevel.oneclone_backend.types.WorkPositionType;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employees", uniqueConstraints = @UniqueConstraint(columnNames = {"companyType", "companySpecificId"}))
@ToString
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Nonnull
    private String surname;

    @Nonnull
    private String name;

    private String secondName;
    @Nonnull
    private String bankAccount;

    private String inn;
    @Column(columnDefinition = "DATE")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.ORDINAL)
    @Nonnull
    private DocumentType documentType;

    @Nonnull
    private String documentSeries;

    @Nonnull
    private String documentNumber;

    @Nonnull
    private String documentIssuedBy;

    @Nonnull
    @Column(columnDefinition = "DATE")
    private LocalDate documentIssuedDate;

    @Nonnull
    private CountryType nationality;

    @Nonnull
    private CountryType birthPlace;

    @Nonnull
    private String workObject;

    @Nonnull
    private String workAddress;

    @Nonnull
    private WorkPositionType workPosition;

    @Nonnull
    private String omvd;

    @Nonnull
    @Column(name = "companySpecificId")
    private Long companySpecificId;

    @Nonnull
    @Enumerated(EnumType.ORDINAL)
    private CompanyType companyType;

    @JsonManagedReference
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<File> files;

    @JsonManagedReference
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Document> documents;
}



