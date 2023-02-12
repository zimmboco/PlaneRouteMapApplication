package com.example.planeroutemap.repository;

import com.example.planeroutemap.model.Airplane;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AirplaneRepository extends MongoRepository<Airplane, ObjectId> {
}
