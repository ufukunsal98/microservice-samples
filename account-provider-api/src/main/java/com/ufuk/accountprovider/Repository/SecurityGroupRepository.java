package com.ufuk.accountprovider.Repository;


import com.ufuk.accountprovider.Domain.SecurityGroup;

import java.util.List;

@N1qlPrimaryIndexed
@ViewIndexed(designDoc = "securityGroup")
public interface SecurityGroupRepository extends
        CouchbasePagingAndSortingRepository<SecurityGroup, String> {


    @Query("#{#n1ql.selectEntity} where #{#n1ql.filter} and companyId = $1 and removed = false " +
            " AND ARRAY_CONTAINS(users, $2) ")
    List<SecurityGroup> listUserGroups(String companyId, String userId);
}
