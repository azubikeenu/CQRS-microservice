package com.azubike.ellpsis.core.data;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

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
