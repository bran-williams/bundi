package com.branwilliams.bundi.engine.ecs;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;

import java.util.*;

/**
 * Manages entities and systems.
 * Created by Brandon Williams on 6/24/2018.
 */
public class EntitySystemManager implements Destructible {

    private final List<ISystem> systems = new ArrayList<>();

    private final List<IEntity> entities = new ArrayList<>();

    // Each systems matcher is put into this list. It contains every matcher which needs it's own list of entities.
    private final List<IComponentMatcher> matchers = new ArrayList<>();

    // Each system has a matcher that will determine whether or not an entity should belong to it. The entities are
    // sorted into separate lists based on these matchers.
    private final Map<IComponentMatcher, List<IEntity>> entitiesGrouped = new HashMap<>();

    // Incremented every time an entity is created.
    private int entityIdCounter = 0;

    // Keep track of the system whose update function is running. Queue whichever tasks needed.
    private ISystem runningSystem = null;
    private final List<Runnable> queuedTasks = new ArrayList<>();
    
    private List<EntityListener> entityListeners = new ArrayList<>();

    /**
     * Listens for the creation and destruction of entities within an {@link EntitySystemManager}.
     * */
    public interface EntityListener {
        /**
         * Invoked when an entity is built.
         * */
        void onEntityAdd(IEntity entity);

        /**
         * Invoked when an entity is removed.
         * */
        void onEntityRemove(IEntity entity);
    }

    public EntitySystemManager() {
    }

    /**
     * Initializes the systems.
     * */
    public void initSystems(Engine engine, Window window) {
        for (ISystem system : systems) {
            system.init(engine, this, window);
        }
    }

    /**
     * Updates each system.
     * */
    public void update(Engine engine, double interval) {
        for (int i = 0; i < systems.size(); i++) {
            runningSystem = systems.get(i);
            runningSystem.update(engine, this, interval);
            runningSystem = null;

            if (!queuedTasks.isEmpty()) {
                queuedTasks.forEach(Runnable::run);
                queuedTasks.clear();
            }
        }
    }

    /**
     * Updates each system.
     * */
    public void fixedUpdate(Engine engine, double interval) {
        for (int i = 0; i < systems.size(); i++) {
            runningSystem = systems.get(i);
            runningSystem.fixedUpdate(engine, this, interval);
            runningSystem = null;

            if (!queuedTasks.isEmpty()) {
                queuedTasks.forEach(Runnable::run);
                queuedTasks.clear();
            }
        }
    }

    /**
     * Used by the update function to queue any tasks which could cause any issues.
     * */
    protected boolean queueTask(Runnable task) {
        if (runningSystem != null) {
            return queuedTasks.add(task);
        }
        return false;
    }

    /**
     * @return A List of entities which meet the requirements for the provided matcher. An empty list if no entities
     * meet the requirements.
     * */
    public List<IEntity> getEntities(IComponentMatcher matcher) {
        List<IEntity> entities = entitiesGrouped.get(matcher);
        return entities == null ? Collections.emptyList() : entities;
    }

    public IEntity getEntity(String name) {
        Optional<IEntity> o = entities.stream().filter((e) -> e.getName().equals(name)).findFirst();
        return o.orElse(null);
    }

    /**
     * @return A List of entities which meet the requirements for the provided system. An empty list if no entities
     * meet the requirements.
     * */
    public List<IEntity> getEntities(ISystem system) {
        return getEntities(system.getMatcher());
    }

    /**
     * Begin the creation of an entity and automatically assign an id to it.
     * */
    public EntityBuilder entity() {
        return new EntityBuilder(this, entityIdCounter++);
    }

    /**
     * Begin the creation of an entity and automatically assign an id to it.
     * The only argument is a name for the entity.
     * */
    public EntityBuilder entity(String name) {
        EntityBuilder builder = entity();
        return builder.name(name);
    }

    /**
     * Creates a component matcher based on the component classes provided. The matcher is automatically added to this
     * manager.
     * */
    public IComponentMatcher matcher(Class<?>... components) {
        IComponentMatcher componentMatcher = new ClassComponentMatcher(components);
        this.addMatcher(componentMatcher);
        this.updateGrouping(componentMatcher);
        return componentMatcher;
    }

    /**
     * Adds the entity to this manager. If any systems match the entity, they will be added to their grouping.
     * If this is invoked from within the update function of a system, the event will be queued until after the system
     * is done updating.
     * */
    public boolean addEntity(IEntity entity) {
        if (runningSystem != null) {
            return queueTask(() -> addEntity_(entity));
        } else {
            return addEntity_(entity);
        }
    }

    private boolean addEntity_(IEntity entity) {
        boolean added = entities.add(entity);
        if (added) {
            for (IComponentMatcher componentMatcher : matchers) {
                if (componentMatcher.matches(entity)) {
                    entitiesGrouped.computeIfAbsent(componentMatcher, (m) -> new ArrayList<>())
                            .add(entity);
                }
            }

            entityListeners.forEach((l) -> l.onEntityAdd(entity));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes the entity from this manager. It will be removed from all grouping and if that grouping is empty without
     * it, the grouping will deleted as well. If this is invoked from within the update function of a system, the event
     * will be queued until after the system is done updating.
     * */
    public boolean removeEntity(IEntity entity) {
        if (runningSystem != null) {
            return queuedTasks.add(() -> removeEntity_(entity));
        } else {
            return removeEntity_(entity);
        }
    }

    private boolean removeEntity_(IEntity entity) {
        boolean removed = entities.remove(entity);
        if (removed) {
            for (IComponentMatcher componentMatcher : matchers) {
                List<IEntity> mappedEntities;
                if (componentMatcher.matches(entity) && (mappedEntities = entitiesGrouped.get(componentMatcher)) != null) {
                    mappedEntities.remove(entity);

                    // If the list is empty, remove it to ensure that a matcher will be mapped to nothing when no
                    // entities exist for that system.
                    if (mappedEntities.isEmpty()) {
                        entitiesGrouped.remove(componentMatcher);
                    }
                }
            }
            entityListeners.forEach((l) -> l.onEntityRemove(entity));
        }
        return removed;
    }

    /**
     * Removes all entities from this manager.
     * */
    public void clearEntities() {
        entities.clear();
        entitiesGrouped.clear();
    }

    /**
     * @return All entities within this manager.
     * */
    public List<IEntity> getEntities() {
        return entities;
    }

    /**
     * Adds the provided matcher to this managers list of matchers which need a group of entities.
     * */
    public boolean addMatcher(IComponentMatcher componentMatcher) {
        return matchers.add(componentMatcher);
    }

    /**
     * Groups entities based on the component matcher provided. This is useful for when a matcher
     * */
    public boolean removeMatcher(IComponentMatcher componentMatcher) {
        boolean removed = matchers.remove(componentMatcher);
        if (removed) {
            entitiesGrouped.remove(componentMatcher);
            return true;
        }
        return false;
    }

    /**
     * Updates the grouping of entities for this component matcher.
     * */
    private boolean updateGrouping(IComponentMatcher componentMatcher) {
        List<IEntity> foundEntities = new ArrayList<>();
        this.entities.stream()
                .filter(componentMatcher::matches)
                .forEach(foundEntities::add);

        if (!foundEntities.isEmpty()) {
            entitiesGrouped.put(componentMatcher, foundEntities);
            return true;
        }
        return false;
    }

    /**
     * Adds the given system to this entity-component-system. If this system has a component matcher that is different
     * from the other systems, then the entities will be sorted accordingly.
     * */
    public boolean addSystem(ISystem system) {
        boolean added = systems.add(system);
        if (added) {
            if (!matchers.contains(system.getMatcher())) {
                matchers.add(system.getMatcher());
                updateGrouping(system.getMatcher());
            }
            system.setEs(this);
        }
        return added;
    }

    /**
     * Removes the given system from this entity-component-system. If no other systems have the same component
     * requirements, then the entity mapping associated with this system will also be removed.
     * */
    public boolean removeSystem(ISystem system) {
        boolean removed = systems.remove(system);
        if (removed) {
            matchers.remove(system.getMatcher());
            if (systems.stream().noneMatch((other) -> other.getMatcher().equals(system.getMatcher()))) {
                entitiesGrouped.remove(system.getMatcher());
            }
            system.setEs(null);
        }
        return removed;
    }

    /**
     * Invoked whenever an entity has a component added or removed. Determines if an entity belongs within any group.
     * */
    void updateEntity(IEntity entity) {
        for (IComponentMatcher matcher : matchers) {
            // Update the grouped entities lists if needed.
            if (matcher.matches(entity)) {
                List<IEntity> entities = entitiesGrouped.computeIfAbsent(matcher, (m) -> new ArrayList<>());
                if (!entities.contains(entity)) {
                    entities.add(entity);
                }

                // Remove the list if it's empty.
            } else if (entitiesGrouped.containsKey(matcher)) {
                List<IEntity> entities = entitiesGrouped.get(matcher);
                entities.remove(entity);
                if (entities.isEmpty())
                    entitiesGrouped.remove(matcher);
            }
        }
    }


    @Override
    public void destroy() {
        systems.stream()
               .filter((s) -> s instanceof Destructible)
               .map((s) -> (Destructible) s)
               .forEach(Destructible::destroy);

        entities.forEach(Destructible::destroy);
    }

    public void addListener(EntityListener listener) {
        entityListeners.add(listener);
    }

    public boolean removeListener(EntityListener listener) {
        return entityListeners.remove(listener);
    }

    public void clearListeners() {
        entityListeners.clear();
    }

    /**
     * Removes all systems from this manager.
     * */
    public void clearSystems() {
        this.systems.clear();
        this.entitiesGrouped.clear();
    }

    /**
     * @return All systems from this manager.
     * */
    public List<ISystem> getSystems() {
        return systems;
    }


    public int getEntityCount() {
        return entities.size();
    }

    public int getSystemCount() {
        return systems.size();
    }
}