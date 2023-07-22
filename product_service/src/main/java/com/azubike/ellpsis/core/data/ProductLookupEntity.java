package com.azubike.ellpsis.core.data;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/*
This class is used to set base consistency between the query database and the event store
since the productId and the title are unique identifiers in the db , they are persisted
on product creation instance , this entity is only exclusive to the command module
And primarily serves to check if a product with the same id or title already exists in the database
**/
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "productLookup")
public class ProductLookupEntity implements Serializable {
    @Id
    private String productId;
    @Column(unique = true)
    private String title;
}
