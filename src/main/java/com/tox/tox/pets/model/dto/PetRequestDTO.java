package com.tox.tox.pets.model.dto;

import com.tox.tox.pets.model.Pets;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PetRequestDTO extends Pets {
}
