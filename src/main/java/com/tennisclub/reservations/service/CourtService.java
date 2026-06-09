package com.tennisclub.reservations.service;

import com.tennisclub.reservations.model.entity.Court;

public interface CourtService extends CrudService<Court> {

    Court create(Court newCourt);

    Court findByNumber(int number);
}
