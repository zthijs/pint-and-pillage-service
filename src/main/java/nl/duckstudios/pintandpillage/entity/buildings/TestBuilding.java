package nl.duckstudios.pintandpillage.entity.buildings;

import nl.duckstudios.pintandpillage.helper.ResourceManager;

import javax.persistence.Entity;

@Entity
public class TestBuilding extends Building {
    @Override
    public void updateBuilding() {
        super.setConstructionTimeSeconds(10);
    }

    public void setResourceManager(ResourceManager resourceManager) {
        super.resourceManager = resourceManager;
    }
}
