package com.utn.frm.desarrollosoftware.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.Objects;

@MappedSuperclass
public class EntityId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    public EntityId() {
    }

    public Long getId() {
        return id;
    }

    /*
    @Override
    public String toString(){
        return String.format(
                "EntityId{id='%d'}%n",
                getId()
        );
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EntityId otra = (EntityId) obj;
        return id == otra.id && Objects.equals(getId(), otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

     */
}
