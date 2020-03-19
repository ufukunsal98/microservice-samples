package com.ufuk.accountprovider.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Table
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users implements Serializable {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column
    @NotNull
    private String username;

    @Column
    @NotNull
    private String companyId;

    @Column
    @NotNull
    private String password;

    @NotNull
    private Boolean isEnabled;

    @Column
    private Boolean isVisible;
}
