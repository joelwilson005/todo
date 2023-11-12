package com.joel.todo.util;

import com.joel.todo.dto.ActionResponseDto;
import com.joel.todo.dto.CreateActionDto;
import com.joel.todo.dto.UpdateActionDto;
import com.joel.todo.model.Action;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ActionMapper {

    void mapCreateActionDtoToAction(CreateActionDto createActionDto, @MappingTarget Action action);

    void mapActionToActionResponseDto(Action action, @MappingTarget ActionResponseDto actionResponseDto);

    void mapUpdateActionDtoToAction(UpdateActionDto updateActionDto, @MappingTarget Action action);

    void mapActionPartialToAction(Action actionPartial, @MappingTarget Action destinyAction);


}
