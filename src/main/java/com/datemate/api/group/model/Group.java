package com.datemate.api.group.model;

import com.datemate.common.model.AbstractTimestampEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


@Getter
@Setter
@Entity
@Table(name = "dm_group_master")
public class Group extends AbstractTimestampEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer groupId;
    private String groupName;
    private String groupOwner;
    private String groupDesc;
}
