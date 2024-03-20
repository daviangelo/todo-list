package com.lessa.todolist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeDescriptionDto {

    @NotBlank(message = "the description must not be empty")
    private String description;



}
