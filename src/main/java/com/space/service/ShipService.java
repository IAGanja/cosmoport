package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.List;

public interface ShipService {
    List<Ship> getAll(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating,
            ShipOrder order
    );

    List<Ship> getAllByPage(Integer pageNumber, Integer pageSize, List<Ship> ships);

    Ship createNew(Ship ship);

    Ship updateById(Ship newCharacteristics, Long id);

    void deleteById(Long id);

    Ship getById(Long id);
}
