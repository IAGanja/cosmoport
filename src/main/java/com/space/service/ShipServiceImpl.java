package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exception.BadRequestException;
import com.space.exception.PageNotFoundException;
import com.space.model.Ship;
import com.space.model.ShipDate;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ShipServiceImpl implements ShipService{

    @Autowired
    ShipRepository shipRepository;

    private static final int LEFT_YEAR = 2800;
    private static final int RIGHT_YEAR = 3019;
    private static final int VARCHAR_LENGTH = 50;

    @Override
    public List<Ship> getAll(            String name,
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
    ) {
        List<Ship> allShips = shipRepository.findAll();

        allShips = filter(name, s -> s.getName().contains(name), allShips);
        allShips = filter(planet, s -> s.getPlanet().contains(planet), allShips);
        allShips = filter(shipType, s -> s.getShipType().equals(shipType), allShips);
        allShips = filter(after, ship -> ship.getProdDate().getTime() >= after, allShips);
        allShips = filter(before, ship -> ship.getProdDate().getTime() <= before, allShips);
        allShips = filter(isUsed, ship -> ship.getUsed() == isUsed, allShips);
        allShips = filter(minSpeed, ship -> Double.compare(ship.getSpeed(), minSpeed) >= 0, allShips);
        allShips = filter(maxSpeed, ship -> Double.compare(ship.getSpeed(), maxSpeed) <= 0, allShips);
        allShips = filter(minCrewSize, ship -> ship.getCrewSize() >= minCrewSize, allShips);
        allShips = filter(maxCrewSize, ship -> ship.getCrewSize() <= maxCrewSize, allShips);
        allShips = filter(minRating, ship -> Double.compare(ship.getRating(), minRating) >= 0, allShips);
        allShips = filter(maxRating, ship -> Double.compare(ship.getRating(), maxRating) <= 0, allShips);

        if (order != null) {
            allShips.sort(getComparator(order));
        }
        return allShips;
    }

    @Override
    public List<Ship> getAllByPage(Integer pageNumber, Integer pageSize, List<Ship> ships) {
        int skip = pageNumber * pageSize;
        List<Ship> result = new ArrayList<>();
        for (int i = skip; i < Math.min(skip + pageSize, ships.size()); i++) {
            result.add(ships.get(i));
        }
        return result;
    }

    @Override
    public Ship createNew(Ship ship) {
        String name = ship.getName(), planet = ship.getPlanet();
        ShipType shipType = ship.getShipType();
        Date prodDate = ship.getProdDate();
        Double speed = ship.getSpeed();
        Integer crewSize = ship.getCrewSize();

        if (name == null || planet == null || shipType == null || prodDate == null || speed == null || crewSize == null) {
            throw new BadRequestException();
        }
        checkStringValue(name);
        checkStringValue(planet);
        checkSpeed(speed);
        checkCrewSize(crewSize);

        Date date = ship.getProdDate();
        checkDate(date);
        ship.setUsed(ship.getUsed() != null && ship.getUsed());
        ship.setRating(ShipDate.rate(ship));
        return shipRepository.save(ship);
    }

    @Override
    public Ship updateById(Ship newShip, Long id) {
        checkId(id);

        if (!shipRepository.existsById(id)) {
            throw new PageNotFoundException();
        }

        Ship oldShip = getById(id);
        String name = newShip.getName();

        if (name != null) {
            checkStringValue(name);
            oldShip.setName(name);
        }

        String planet = newShip.getPlanet();

        if (planet != null) {
            checkStringValue(planet);
            oldShip.setPlanet(planet);
        }

        if (newShip.getShipType() != null)
            oldShip.setShipType(newShip.getShipType());

        if (newShip.getUsed() != null) {
            oldShip.setUsed(newShip.getUsed());
        }

        Date date = newShip.getProdDate();
        if (date != null) {
            checkDate(date);
            oldShip.setProdDate(date);
        }

        Double speed = newShip.getSpeed();

        if (speed != null) {
            checkSpeed(speed);
            oldShip.setSpeed(speed);
        }

        Integer crewSize = newShip.getCrewSize();

        if (crewSize != null) {
            checkCrewSize(crewSize);
            oldShip.setCrewSize(crewSize);
        }
        oldShip.setRating(ShipDate.rate(oldShip));
        return oldShip;
    }

    @Override
    public void deleteById(Long id) {
        checkId(id);

        if (!shipRepository.existsById(id)) {
            throw new PageNotFoundException();
        }
        shipRepository.deleteById(id);
    }

    @Override
    public Ship getById(Long id) {
        checkId(id);

        if (!shipRepository.existsById(id)) {
            throw new PageNotFoundException();
        }
        return shipRepository.findById(id).orElse(null);
    }

    private void checkId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException();
        }
    }

    private void checkStringValue(String value) {
        if (value.length() > 50 || value.isEmpty()) {
            throw new BadRequestException();
        }
    }

    private void checkDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int prodDate = cal.get(Calendar.YEAR);

        if (prodDate < 2800 || prodDate > 3019) {
            throw new BadRequestException();
        }
    }

    private void checkSpeed(Double speed) {
        if (speed < 0.01d || speed > 0.99d) {
            throw new BadRequestException();
        }
    }

    private void checkCrewSize(Integer crewSize) {
        if (crewSize < 1 || crewSize > 9999) {
            throw new BadRequestException();
        }
    }

    private <T, U> List<T> filter(U value, Predicate<T> predicate, List<T> list) {
        if (value == null) return list;
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    private Comparator<Ship> getComparator(ShipOrder order) {
        switch (order) {
            case SPEED:
                return Comparator.comparing(Ship::getSpeed);
            case DATE:
                return Comparator.comparing(Ship::getProdDate);
            case RATING:
                return Comparator.comparing(Ship::getRating);
            default:
                return Comparator.comparing(Ship::getId);
        }
    }

}
