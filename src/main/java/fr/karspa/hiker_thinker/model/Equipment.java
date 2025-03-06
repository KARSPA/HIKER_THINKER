package fr.karspa.hiker_thinker.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Builder
@ToString
@Document(collection = "equipments")
public class Equipment {

    @MongoId
    private String id;

    private String name;
    private String description;
    private String brand;

    private Float weight;

    private String category;
}
