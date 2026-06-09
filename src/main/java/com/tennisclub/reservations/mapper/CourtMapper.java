package com.tennisclub.reservations.mapper;

import com.tennisclub.reservations.model.dto.CourtDto;
import com.tennisclub.reservations.model.dto.create.CourtCreateDto;
import com.tennisclub.reservations.model.entity.Court;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourtMapper extends GenericMapper<Court, CourtDto, CourtCreateDto, CourtDto> {
}
