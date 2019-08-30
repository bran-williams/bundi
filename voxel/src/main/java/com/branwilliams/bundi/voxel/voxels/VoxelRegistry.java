package com.branwilliams.bundi.voxel.voxels;

import com.branwilliams.bundi.voxel.builder.VoxelBuilder;
import com.branwilliams.bundi.voxel.voxels.model.VoxelProperties;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Brandon
 * @since August 11, 2019
 */
public class VoxelRegistry {

    private final VoxelBuilder voxelBuilder = new VoxelBuilder();

    /**
     * Voxel ID to properties.
     * */
    private Map<String, VoxelProperties> voxelProperties;

    /**
     * Mapping of voxel ID to voxel objects.
     * */
    private Map<String, Voxel> voxels;

    /***
     * List of all voxels, sorted alphabetically by identifier.
     */
    private List<String> sortedVoxelIdentifiers;

    public VoxelRegistry(Map<String, VoxelProperties> voxelProperties) {
        this.voxelProperties = voxelProperties;
        this.voxels = new HashMap<>();
        this.sortedVoxelIdentifiers = new ArrayList<>();
    }

    /**
     * Initializes the mapping of voxel ids to voxel objects.
     * */
    public void initialize() {
        for (Map.Entry<String, VoxelProperties> voxelProperty : voxelProperties.entrySet()) {
            if (!voxels.containsKey(voxelProperty.getKey())) {
                Voxel voxel = voxelBuilder.buildVoxel(voxelProperty.getKey(), voxelProperty.getValue());
                voxels.put(voxelProperty.getKey(), voxel);
            }
        }
        Voxels.initializeVoxels(voxels);

        sortedVoxelIdentifiers = voxels.keySet()
                .stream()
                .filter((v) -> !v.equalsIgnoreCase(VoxelIdentifiers.AIR.normalized()))
                .sorted()
                .collect(Collectors.toList());

    }

    public VoxelProperties getVoxelProperties(VoxelIdentifier voxelId) {
        return getVoxelProperties(voxelId.id());
    }

    public VoxelProperties getVoxelProperties(String voxelId) {
        return voxelProperties.get(voxelId);
    }

    public Voxel getVoxel(VoxelIdentifier voxelIdentifier) {
        return getVoxel(voxelIdentifier.normalized());
    }

    public Voxel getVoxel(String voxelIdentifier) {
        return voxels.get(voxelIdentifier);
    }

    public Set<String> getVoxelIdentifiers() {
        return voxelProperties.keySet();
    }

    public Map<String, VoxelProperties> getVoxelProperties() {
        return voxelProperties;
    }

    public List<String> getSortedVoxelIdentifiers() {
        return sortedVoxelIdentifiers;
    }
}
