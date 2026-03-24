package com.example.munichway.mappers;

import com.example.munichway.DTO.TripResponse;
import com.example.munichway.models.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface TripMapper {


    @Mapping(source = "id", target = "tripId")
    @Mapping(source = "scooter.modelName", target = "scooterModel")
    TripResponse toDto(Trip trip);
}