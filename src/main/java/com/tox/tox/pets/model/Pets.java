package com.tox.tox.pets.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Getter
@Setter
@ToString
public class Pets implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String species;

    private String name;

    private String breed;

    private LocalDate birthday;

    /**
     * 记录创建时间
     */
    private LocalDateTime createdAt;
}
