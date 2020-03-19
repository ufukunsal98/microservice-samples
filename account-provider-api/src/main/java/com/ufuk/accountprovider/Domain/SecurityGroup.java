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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityGroup  implements Serializable {

    @Id
    private String id = UUID.randomUUID().toString();

    @NotNull
    @Column
    private String name;

    @Column
    private String description;

    @NotNull
    @Column
    private String companyId;

//    @Column
//    private List<User> users = new ArrayList<>();

    @Column
    private boolean removed = false;
}
