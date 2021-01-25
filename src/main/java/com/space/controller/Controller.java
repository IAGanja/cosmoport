package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/ships")
public class Controller {

    @Autowired
    private ShipService shipService;

    @GetMapping
    public List<Ship> getShipsAll(@RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String planet,
                                                  @RequestParam(required = false) ShipType shipType,
                                                  @RequestParam(required = false) Long after,
                                                  @RequestParam(required = false) Long before,
                                                  @RequestParam(required = false) Boolean isUsed,
                                                  @RequestParam(required = false) Double minSpeed,
                                                  @RequestParam(required = false) Double maxSpeed,
                                                  @RequestParam(required = false) Integer minCrewSize,
                                                  @RequestParam(required = false) Integer maxCrewSize,
                                                  @RequestParam(required = false) Double minRating,
                                                  @RequestParam(required = false) Double maxRating,
                                                  @RequestParam(required = false) ShipOrder order,
                                                  @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                                  @RequestParam(required = false, defaultValue = "3") Integer pageSize
    ) {

        List<Ship> list = shipService.getAll(name, planet, shipType,
                after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating, order);
        return shipService.getAllByPage(pageNumber, pageSize, list);
    }


    @GetMapping("/count")
    public Integer getShipsCount(       @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String planet,
                                        @RequestParam(required = false) ShipType shipType,
                                        @RequestParam(required = false) Long after,
                                        @RequestParam(required = false) Long before,
                                        @RequestParam(required = false) Boolean isUsed,
                                        @RequestParam(required = false) Double minSpeed,
                                        @RequestParam(required = false) Double maxSpeed,
                                        @RequestParam(required = false) Integer minCrewSize,
                                        @RequestParam(required = false) Integer maxCrewSize,
                                        @RequestParam(required = false) Double minRating,
                                        @RequestParam(required = false) Double maxRating
    ) {
        return shipService.getAll(name, planet, shipType,
                after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating, null).size();
    }

    @GetMapping("/{id}")
    public Ship getById(@PathVariable Long id) {
        return shipService.getById(id);
    }

    @PostMapping
    public Ship createNew(@RequestBody Ship newShip) {
        return shipService.createNew(newShip);
    }

    @PostMapping("/{id}")
    public Ship updateById(@RequestBody Ship newShip, @PathVariable Long id) {
        return shipService.updateById(newShip, id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        shipService.deleteById(id);
    }

}
