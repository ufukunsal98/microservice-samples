package com.ufuk.accountprovider.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity
@Table
public class Users  implements Serializable {

    @Id
    private String id;

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