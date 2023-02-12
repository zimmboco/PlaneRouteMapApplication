package com.example.planeroutemap.model;

import java.util.List;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document
@Data
public class Airplane {
    private @MongoId ObjectId id;
    private AirplaneCharacteristics airplaneCharacteristics;
    private TemporaryPoint temporaryPoint;
    private List<Flight> flights;

}
